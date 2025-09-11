package jogo;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class cliente {

    private static final int PORT = 50000;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", PORT)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);

            // símbolo desejado
            System.out.print("Você quer ser X ou O? ");
            char desired = Character.toUpperCase(sc.nextLine().trim().charAt(0));
            if (desired!='X' && desired!='O') desired='X';
            out.println("SYMBOL " + desired);

            // aguarda confirmação
            char my = 0;
            while (my == 0) {
                String line = in.readLine();
                if (line == null) return;
                if (line.startsWith("ASSIGNED ")) {
                    my = line.charAt(line.length()-1);
                    System.out.println("Você é: " + my);
                } else if (line.startsWith("INFO ")) {
                    System.out.println(line);
                } else if (line.startsWith("ERROR ")) {
                    System.out.println(line);
                }
            }

            // loop principal: aguarda BOARD; se for sua vez, envia MOVE
            while (true) {
                String header = in.readLine();
                if (header == null) break;

                if ("BOARD".equals(header)) {
                    String r0 = in.readLine();
                    String r1 = in.readLine();
                    String r2 = in.readLine();
                    String state = in.readLine(); // "STATE ... TURN X|O"

                    System.out.println("\nTabuleiro:");
                    printBoard(r0, r1, r2);
                    System.out.println(state);

                    if (!state.startsWith("STATE IN_PROGRESS")) {
                        System.out.println("Fim de jogo!");
                        break;
                    }

                    char turn = state.charAt(state.length()-1);
                    if (turn == my) {
                        while (true) {
                            System.out.print("Sua vez! Digite linha e coluna (0..2) ou '.' para sair: ");
                            String s = sc.nextLine().trim();
                            if (s.equals(".")) { out.println("."); return; }
                            String[] t = s.split("\\s+");
                            if (t.length != 2) { System.out.println("Ex: 1 2"); continue; }
                            try {
                                int r = Integer.parseInt(t[0]);
                                int c = Integer.parseInt(t[1]);
                                out.println("MOVE " + r + " " + c);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Use números inteiros.");
                            }
                        }
                    }
                } else if (header.startsWith("INFO ") || header.startsWith("ERROR ")) {
                    System.out.println(header);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printBoard(String r0, String r1, String r2) {
        String[] L = { r0, r1, r2 };
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                System.out.print(" " + L[i].charAt(j) + " ");
                if (j<2) System.out.print("|");
            }
            System.out.println();
            if (i<2) System.out.println("---+---+---");
        }
    }
}
