package jogo;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class servidor {

    private static final int PORT = 50000;

    public static void main(String[] args) {
        Game game = new Game();
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Servidor aguardando na porta " + PORT + "...");
            while (true) {
                Socket s = server.accept();
                pool.submit(new ClientHandler(s, game));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ==================== Game (estado compartilhado) ==================== */
    static class Game {
        private final char[][] board = {
                {'.','.','.'},
                {'.','.','.'},
                {'.','.','.'}
        };
        private char turn = 'X';   // X começa
        private char status = 'I'; // I=In progress, X=venceu X, O=venceu O, D=empate
        private ClientHandler playerX;
        private ClientHandler playerO;

        synchronized char assignSymbol(ClientHandler ch, char desired) {
            desired = (desired=='O') ? 'O' : 'X';
            if (desired=='X') {
                if (playerX == null) { playerX = ch; ch.symbol='X'; }
                else if (playerO == null) { playerO = ch; ch.symbol='O'; }
                else return 0; // cheio
            } else { // quer O
                if (playerO == null) { playerO = ch; ch.symbol='O'; }
                else if (playerX == null) { playerX = ch; ch.symbol='X'; }
                else return 0; // cheio
            }
            // se agora temos os dois, envia estado inicial
            if (playerX != null && playerO != null) {
                // garante status/vez corretos e broadcasta
                if (status == 'I') broadcast();
            } else {
                ch.sendLine("INFO Aguardando oponente conectar...");
            }
            return ch.symbol;
        }

        synchronized void handleMove(ClientHandler ch, int r, int c) {
            if (status != 'I') { ch.sendLine("ERROR Jogo já terminou"); return; }
            if (ch.symbol == 0) { ch.sendLine("ERROR Sem símbolo"); return; }
            if ((playerX == null) || (playerO == null)) { ch.sendLine("ERROR Falta oponente"); return; }
            if (ch.symbol != turn) { ch.sendLine("ERROR Não é sua vez"); return; }
            if (r<0||r>2||c<0||c>2) { ch.sendLine("ERROR Fora do tabuleiro"); return; }
            if (board[r][c] != '.') { ch.sendLine("ERROR Posição ocupada"); return; }

            board[r][c] = turn;
            status = computeStatus(board);
            if (status == 'I') turn = (turn=='X') ? 'O' : 'X';
            broadcast();
        }

        synchronized void broadcast() {
            String s0 = new String(board[0]);
            String s1 = new String(board[1]);
            String s2 = new String(board[2]);
            String statusStr = switch (status) {
                case 'X' -> "X_WON";
                case 'O' -> "O_WON";
                case 'D' -> "DRAW";
                default -> "IN_PROGRESS";
            };
            String last = "STATE " + statusStr + " TURN " + turn;

            if (playerX != null) {
                playerX.sendLine("BOARD"); playerX.sendLine(s0); playerX.sendLine(s1); playerX.sendLine(s2); playerX.sendLine(last);
            }
            if (playerO != null) {
                playerO.sendLine("BOARD"); playerO.sendLine(s0); playerO.sendLine(s1); playerO.sendLine(s2); playerO.sendLine(last);
            }

            System.out.println("\n[Broadcast]");
            printBoard(board);
            System.out.println(last);
        }

        synchronized void opponentLeft(ClientHandler leaver) {
            ClientHandler other = (leaver == playerX) ? playerO : playerX;
            if (other != null) other.sendLine("ERROR Oponente desconectou. Fim.");
        }

        private static char computeStatus(char[][] b) {
            for (int i=0;i<3;i++){
                if (b[i][0] != '.' && b[i][0]==b[i][1] && b[i][1]==b[i][2]) return b[i][0];
                if (b[0][i] != '.' && b[0][i]==b[1][i] && b[1][i]==b[2][i]) return b[0][i];
            }
            if (b[0][0] != '.' && b[0][0]==b[1][1] && b[1][1]==b[2][2]) return b[0][0];
            if (b[0][2] != '.' && b[0][2]==b[1][1] && b[1][1]==b[2][0]) return b[0][2];
            boolean anyDot=false;
            for (int r=0;r<3;r++) for (int c=0;c<3;c++) if (b[r][c]=='.') anyDot=true;
            return anyDot ? 'I' : 'D';
        }

        private static void printBoard(char[][] b) {
            for (int i=0;i<3;i++){
                for (int j=0;j<3;j++){
                    System.out.print(" " + b[i][j] + " ");
                    if (j<2) System.out.print("|");
                }
                System.out.println();
                if (i<2) System.out.println("---+---+---");
            }
        }
    }

    /* ==================== ClientHandler (uma thread por cliente) ==================== */
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private final Game game;
        private BufferedReader in;
        private PrintWriter out;
        char symbol = 0; // 'X' ou 'O'

        ClientHandler(Socket socket, Game game) {
            this.socket = socket;
            this.game = game;
        }

        @Override
        public void run() {
            try (socket;
                 BufferedReader _in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter _out = new PrintWriter(socket.getOutputStream(), true)) {

                this.in = _in; this.out = _out;
                out.println("INFO Conectado. Envie: SYMBOL X|O");

                // Handshake de símbolo
                while (symbol == 0) {
                    String line = in.readLine();
                    if (line == null) return;
                    if (line.startsWith("SYMBOL ")) {
                        char desired = Character.toUpperCase(line.charAt(line.length()-1));
                        char assigned = game.assignSymbol(this, desired);
                        if (assigned == 0) { out.println("ERROR Sala cheia"); return; }
                        out.println("ASSIGNED " + assigned);
                    } else {
                        out.println("ERROR Primeiro envie: SYMBOL X|O");
                    }
                }

                // Loop principal: recebe MOVE r c
                while (true) {
                    String line = in.readLine();
                    if (line == null || line.equals(".")) {
                        game.opponentLeft(this);
                        return;
                    }
                    if (!line.startsWith("MOVE ")) { out.println("ERROR Use: MOVE r c"); continue; }
                    String[] t = line.split("\\s+");
                    if (t.length != 3) { out.println("ERROR Formato: MOVE r c"); continue; }
                    try {
                        int r = Integer.parseInt(t[1]);
                        int c = Integer.parseInt(t[2]);
                        game.handleMove(this, r, c);
                    } catch (NumberFormatException e) {
                        out.println("ERROR Use números inteiros");
                    }
                }

            } catch (IOException e) {
                // desconexão inesperada
                game.opponentLeft(this);
            }
        }

        void sendLine(String s) {
            if (out != null) out.println(s);
        }
    }
}
