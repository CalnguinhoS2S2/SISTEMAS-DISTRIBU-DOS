# TweetApp – visão geral do projeto

Aplicação Spring Boot desenvolvida para o trabalho de **Sistemas Distribuídos (NT2)**. O objetivo é expor uma API REST para cadastrar usuários, autenticar por `@handle` + senha e publicar tweets, com uma página HTML simples para testes rápidos.

---

## Estrutura de pastas

### Raiz do repositório

- `pom.xml` – POM Maven que centraliza dependências (Spring Boot, SpringDoc, H2 etc.), define Java 17 e configura o plugin Spring Boot Maven.
- `README.md` – Este documento com visão geral, instruções e mapeamento das pastas.
- `bin/` – Saída compilada gerada pelo STS/Eclipse. Não é versionada nem necessária para builds Maven (`mvn clean` remove sem impacto).

### Código de produção (`src/main/java`)

| Caminho | Descrição detalhada |
| --- | --- |
| `TweetappApplication.java` | Classe `@SpringBootApplication` que inicializa o contexto Spring; ponto de entrada (`main`). |
| `config/OpenApiConfig.java` | Declara um `@Bean` `OpenAPI` preenchendo título, descrição, contato e links externos do Swagger UI. |
| `controller/UserController.java` | Exposição REST para `/api/users`. Encapsula entradas/saídas com DTOs, define status HTTP, e delega ao `UserService`. |
| `controller/TweetController.java` | Endpoints `/api/tweets`. Usa validação `@Valid`, controla criação, listagem, busca por autor, atualização e exclusão. |
| `domain/User.java` | Entidade JPA `users` com atributos de perfil, relacionamento `@OneToMany` para `Tweet` e overrides de `equals`/`hashCode`. |
| `domain/Tweet.java` | Entidade `tweets` com timestamps e relacionamento `@ManyToOne` preguiçoso. Usa `@PrePersist`/`@PreUpdate` para manter `createdAt`/`updatedAt`. |
| `dto/UserRequest.java` | Record com anotações de validação para dados de cadastro/edição de usuário (handle, senha, displayName, imagem, bio). |
| `dto/UserResponse.java` | Record resposta contendo metadados públicos (`tweetCount` é derivado do tamanho da coleção). |
| `dto/TweetRequest.java` | Record de entrada para tweets: exige `authorHandle`, `password` e `content`, todos validados. |
| `dto/TweetResponse.java` | Record de saída com conteúdo, carimbos de tempo e informações do autor (ID, handle, displayName). |
| `exception/BusinessException.java` | Runtime exception para violações de regra (ex.: handle duplicado, senha incorreta). Inclui `serialVersionUID`. |
| `exception/ResourceNotFoundException.java` | Runtime exception para buscas que não retornam entidade. |
| `exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` que captura exceções de negócio, `ResourceNotFoundException` e erros de validação (`MethodArgumentNotValidException`), produzindo payload JSON padronizado. |
| `repository/UserRepository.java` | Interface Spring Data JPA com consultas auxiliares (`findByHandleIgnoreCase`, `existsByHandleIgnoreCase`). |
| `repository/TweetRepository.java` | Consultas customizadas (`findByAuthorIdOrderByCreatedAtDesc`, `findAllByOrderByCreatedAtDesc`). |
| `service/UserService.java` | Camada de negócio. Valida unicidade do handle, aplica DTOs à entidade, converte para resposta, remove usuários e contabiliza tweets. |
| `service/TweetService.java` | Regras para criação e atualização de tweets. Autentica autor por handle + senha, impede edição por terceiros, persiste e converte para DTO. |

### Recursos (`src/main/resources`)

- `application.properties` – Configura porta (`8080`), datasource H2 em memória (`jdbc:h2:mem:tweetapp`), credenciais, console H2, `spring.jpa` (DDL auto `update`, SQL formatado) e caminhos do Swagger `/api/docs` / `/swagger-ui.html`.
- `static/index.html` – Página SPA vanilla para consumo da API:
  * Formulário de cadastro de usuário.
  * Formulário de tweet com campos `authorHandle`, `password` e `content`.
  * Lista de usuários e tweets em tempo real via `fetch`.
  * Tratamento básico de erros (exibe mensagem retornada pela API).

### Testes (`src/test/java`)

- `TweetappApplicationTests.java` – Teste JUnit 5 com `@SpringBootTest`. Verifica se o contexto Spring Boot carrega integralmente (garante wiring mínimo).

---

## Tecnologias principais

- Java 17  
- Spring Boot 3.3.5  
- Spring Web, Spring Data JPA (H2 em memória)  
- Jakarta Bean Validation  
- SpringDoc OpenAPI (Swagger UI)

---

## Como executar

### Spring Tools Suite / Eclipse
1. `File > Import > Maven > Existing Maven Projects`.
2. Selecionar a pasta `trabalho_N2_Sistema_dis`.
3. Finalizar, aguardar o download das dependências.
4. Executar `TweetappApplication` como **Spring Boot App**.

### Linha de comando

```bash
mvn spring-boot:run
```

Ou, para build e testes:

```bash
mvn clean install
```

---

## Endpoints principais

- `POST /api/users` – cria usuário.  
- `GET /api/users` – lista todos.  
- `GET /api/users/{id}` – consulta por ID.  
- `PUT /api/users/{id}` – edita informações.  
- `DELETE /api/users/{id}` – remove cadastro.

- `POST /api/tweets` – cria tweet usando `authorHandle` + `password`.  
- `GET /api/tweets` – lista tweets mais recentes.  
- `GET /api/tweets/{id}` – busca por ID.  
- `GET /api/tweets/author/{authorId}` – lista tweets do autor.  
- `PUT /api/tweets/{id}` – atualiza conteúdo (exige handle/senha do autor).  
- `DELETE /api/tweets/{id}` – remove tweet.

Documentação interativa: `http://localhost:8080/swagger-ui.html`  
H2 Console: `http://localhost:8080/h2-console` (URL `jdbc:h2:mem:tweetapp`, usuário `sa`, senha vazia).  
Página estática: `http://localhost:8080/index.html`

---

## Testes

```bash
mvn test
```

Executa `TweetappApplicationTests`, garantindo que o contexto Spring Boot sobe corretamente. Adicione novos testes conforme evoluir a regra de negócio.
