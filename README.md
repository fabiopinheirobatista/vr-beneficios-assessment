# Mini Autorizador - VR Benefícios (Assessment)

Descrição
---------
Este repositório contém a implementação de um *Mini Autorizador* em Java 17 usando Spring Boot 3.5.5. A aplicação expõe uma API REST para:
- criar cartões (com saldo inicial);
- consultar saldo de cartões;
- processar transações (autorização de débito) com regras de negócio.

O objetivo do projeto é demonstrar boas práticas de arquitetura, design patterns, tratamento de erros e cobertura de testes automatizados.

Tecnologias e versões
---------------------
- Java 17
- Spring Boot 3.5.5
- Maven
- Spring Data JPA (Hibernate)
- Flyway (migrações SQL)
- H2 (padrão para desenvolvimento/testes)
- MySQL (opção em Docker)
- Spring Security (Basic Auth)
- Spring Actuator (health endpoint)
- JaCoCo (relatório de cobertura)

Arquitetura e modelos de desenvolvimento
---------------------------------------
- Arquitetura orientada a camadas (Controller → Service → Repository → Domain).
- Princípios de Clean Architecture e Separation of Concerns:
  - Controllers: camada de entrada (REST); tratam contratos HTTP.
  - Services: orquestra regras de negócio.
  - Repositories: abstrações JPA para persistência.
  - Domain: entidades JPA (Cartão, Transação).
  - DTOs: objetos de transferência para requests/responses.
- Padrões de projeto utilizados:
  - Repository Pattern (Spring Data JPA).
  - Service Layer.
  - Exception Handler global (ControllerAdvice) para mapear exceções de negócio a respostas HTTP apropriadas.
  - Uso de Optional/functional style para evitar estruturas if (conforme requisito do assessment).
- Concorrência e consistência:
  - O débito do saldo é feito com uma atualização atômica via query JPA (UPDATE com condição de saldo >= valor), garantindo que duas transações concorrentes não ultrapassem o saldo disponível mesmo em instâncias distintas.
  - Transações ACID são garantidas pela configuração padrão do JPA/Hibernate.

Regras de negócio (casos de uso)
--------------------------------
- Criar cartão
  - Todo cartão criado inicia com saldo R$ 500,00.
  - Se o cartão já existir, o retorno será 422 com o body contendo os dados do cartão (conforme contrato).
- Consultar saldo
  - Retorna o saldo atual do cartão (200) ou 404 se o cartão não existir.
- Autorizar transação
  - Regras para autorização:
    1. O cartão deve existir.
    2. A senha deve ser correta.
    3. O cartão deve possuir saldo suficiente.
  - Se autorizada: 201 e body `OK`.
  - Se barrada por regra: 422 com corpo `SALDO_INSUFICIENTE|SENHA_INVALIDA|CARTAO_INEXISTENTE` (conforme a regra que falhou).
  - As tentativas (mesmo de cartão inexistente) são persistidas na tabela `transacoes` para auditoria.

Contrato da API (endpoints)
---------------------------
Base URL: `http://localhost:8081` (quando em Docker, ver seção Docker) ou `http://localhost:8080` para execução local padrão.

1) Criar novo cartão
- Method: POST
- URL: /cartoes
- Body JSON:
```json
{
  "numeroCartao": "6549873025634501",
  "senha": "1234"
}
```
- Respostas:
  - 201: criação com sucesso (body retorna JSON com numeroCartao e senha).
  - 422: cartão já existe (body retorna os dados do cartão).

2) Obter saldo do cartão
- Method: GET
- URL: /cartoes/{numeroCartao}
- Respostas:
  - 200: body contém o saldo (ex.: `495.15`).
  - 404: cartão não existe.

3) Realizar transação
- Method: POST
- URL: /transacoes
- Body JSON:
```json
{
  "numeroCartao": "6549873025634501",
  "senhaCartao": "1234",
  "valor": 10.00
}
```
- Respostas:
  - 201: `OK` (transação aprovada e saldo debitado).
  - 422: `SALDO_INSUFICIENTE` | `SENHA_INVALIDA` | `CARTAO_INEXISTENTE` (quando aplicável).

Autenticação
------------
- Basic Auth (HTTP Basic)
- Credenciais padrão usadas no projeto para testes:
  - usuário: `username`
  - senha: `password`
- O endpoint `/actuator/health` está liberado sem autenticação para permitir probes de readiness.

Banco de dados e migrações
-------------------------
- As migrations Flyway estão em `src/main/resources/db/migration/`.
- Por padrão, a aplicação usa H2 em memória (bom para testes locais).
- Em Docker, a stack usa MySQL (configurado no `docker-compose.yml`).

Testes automatizados e cobertura
--------------------------------
- Testes JUnit (unitários e integração) em `src/test/java/vr_backend_assessment/`.
- JaCoCo foi configurado — relatório gerado em `target/site/jacoco/index.html` após `mvn test`.
- Objetivo do projeto: cobertura mínima de 80%.

Postman collection
------------------
- Uma collection pronta para importar no Postman está em `postman/mini-autorizador-collection.json`.
- A collection contém cenários encadeados:
  1. criar cartão
  2. consultar saldo
  3. realizar várias transações verificando saldo após cada uma até SALDO_INSUFICIENTE
  4. transação com senha inválida
  5. transação com cartão inexistente
  6. casos extras (número inválido, transações diversas, large transaction)
- A collection usa variáveis de coleção (base_url, numeroCartao, senhaCartao) e Basic Auth no nível da collection.

Executando a aplicação localmente (sem Docker)
-----------------------------------------------
Pré-requisitos:
- Java 17 instalado
- Maven instalado

Comandos:
1. Buildar o projeto:
   mvn clean package

2. Rodar via Maven:
   mvn spring-boot:run
   (ou)
   java -jar target/api-assessment-0.0.1-SNAPSHOT.jar

- Endpoints locais (padrão): http://localhost:8080 (apenas se você não alterar as variáveis de ambiente). Em desenvolvimento, o projeto está configurado para usar H2 se SPRING_DATASOURCE_URL não for informado.

Executando via Docker (docker-compose)
--------------------------------------
Pré-requisitos:
- Docker e Docker Compose instalados

Como subir a stack:
1. Subir em background:
   docker-compose up --build -d

2. Ver logs do aplicativo:
   docker-compose logs -f app

Portas padrão mapeadas pelo compose:
- App (host -> container): 8081 -> 8080 (acesso externo em http://localhost:8081)
- MySQL (host -> container): 3307 -> 3306 (acesso externo em localhost:3307)

Observações:
- O serviço app usa um healthcheck HTTP que consulta `/actuator/health` internamente.
- Se precisar reexecutar as migrations do banco (quando o volume já existe), execute:
  docker-compose down --volumes
  docker-compose up --build

Como usar a collection do Postman
--------------------------------
1. No Postman: Import -> selecionar `postman/mini-autorizador-collection.json`.
2. Verificar variáveis: base_url (por exemplo `http://localhost:8080`), username/password (Basic Auth).
3. Executar as requisições na ordem (a collection já está ordenada). Use o Collection Runner para executar todo o fluxo.

Boas práticas e decisões de design
---------------------------------
- Código organizado por camadas e responsabilidades claras.
- Uso de DTOs para desacoplar API de entidades internas.
- Tratamento centralizado de exceções para padronizar respostas de erro.
- Evitando uso de `if` em pontos críticos: adotado estilo funcional com Optional/streams onde aplicável (requisito do assessment).
- Débito atômico via JPQL `UPDATE ... WHERE saldo >= :valor` para evitar condições de corrida entre instâncias.
- Persistência de todas as tentativas de transação (incluindo falhas) para facilitar auditoria.

Observações finais e contato
---------------------------
- Para qualquer ajuste de credenciais, portas ou comportamento, altere as variáveis de ambiente no `docker-compose.yml` ou no `application.properties`.
- Relatório de cobertura: `target/site/jacoco/index.html` após executar `mvn test`.

Agradecimentos
--------------
Implementado seguindo boas práticas de Clean Architecture e orientado a testes. Qualquer dúvida ou ajuste, posso ajudar a adaptar o projeto às suas necessidades.

