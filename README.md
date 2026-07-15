# Cadastro ATLANTICO — Aplicacao Web em 3 Camadas

Sistema web de candidaturas profissionais adaptado para os requisitos do exame:

- **Modelo:** JavaBeans + DAO + PostgreSQL
- **Controlador:** Jakarta Servlets
- **Visao:** JSP/JSTL renderizada em HTML
- **SDK obrigatorio:** Java 17
- **Empacotamento:** WAR para Tomcat 10.1+

## 1. Funcionalidades

- registo completo de uma candidatura profissional
- lado publico para o candidato submeter candidatura sem login
- confirmacao visual depois da submissao, sem expor a lista de candidatos
- login administrativo com utilizador `admin` e senha `admin`
- painel administrativo com listagem das 100 candidaturas mais recentes
- resumo no painel com a quantidade de candidatos por cada area de estudo/licenciatura
- pesquisa administrativa por nome, B.I./Passaporte, e-mail ou telefone
- consulta detalhada protegida por sessao de administrador
- areas de estudo e interesse com relacionamento muitos-para-muitos
- validacao no navegador, no Servlet/Service e na base de dados
- validacao segura de URLs de LinkedIn e portfolio
- interface responsiva para computador, tablet e telemovel

## 2. Tecnologias e versoes

| Tecnologia | Versao/uso |
|---|---|
| Java SDK | **17** (`maven.compiler.release=17`) |
| Jakarta Servlet | 6.0 |
| JSP/JSTL | 3.0 |
| PostgreSQL JDBC | 42.7.9 |
| PostgreSQL | 14 ou superior |
| Servidor recomendado | Tomcat 10.1 ou superior |
| Maven | 3.8 ou superior |

> Atencao: Tomcat 9 usa os pacotes antigos `javax.servlet` e nao e compativel com esta aplicacao. Use Tomcat 10.1+, que suporta `jakarta.servlet`.

## 3. Arquitectura em tres camadas

```text
Navegador
   │ HTTP
   ▼
Servlets (Controlador)
   │ cria/consulta Beans e chama servicos
   ▼
Service + JavaBean (Modelo e regras)
   │ chama o DAO
   ▼
DAO com JDBC e PreparedStatement
   │ SQL parametrizado
   ▼
PostgreSQL

Servlet ──forward──► JSP/JSTL (Visao) ──► HTML/CSS/JavaScript
```

### Ficheiros principais

- Bean: `src/main/java/ao/co/atlantico/candidaturas/model/Candidatura.java`
- DAO: `src/main/java/ao/co/atlantico/candidaturas/dao/CandidaturaDAO.java`
- conexao: `src/main/java/ao/co/atlantico/candidaturas/dao/ConnectionFactory.java`
- regras e validacao: `src/main/java/ao/co/atlantico/candidaturas/service/CandidaturaService.java`
- controlador publico: `src/main/java/ao/co/atlantico/candidaturas/web/CandidaturaServlet.java`
- controlador admin: `src/main/java/ao/co/atlantico/candidaturas/web/AdminServlet.java`
- visoes: `src/main/webapp/WEB-INF/views/`
- esquema SQL: `src/main/resources/db/schema.sql`
- interface: `src/main/webapp/assets/`

## 4. Base de dados PostgreSQL

O esquema possui tres tabelas normalizadas:

```text
candidatura 1 ────── N candidatura_area N ────── 1 area
```

- `candidatura`: dados pessoais, academicos e profissionais
- `area`: catalogo de areas, separado por `ESTUDO` e `INTERESSE`
- `candidatura_area`: tabela associativa muitos-para-muitos

Existem chaves primarias, estrangeiras, `UNIQUE`, `CHECK` e indices para pesquisa. O DAO grava a candidatura e as suas areas dentro da mesma transacao; qualquer falha provoca `rollback`.

### Criar a base manualmente

```sql
CREATE USER cadastro_app WITH PASSWORD 'cadastro123';
CREATE DATABASE cadastro_atlantico OWNER cadastro_app;
```

As tabelas e os catalogos sao criados automaticamente pelo `schema.sql` quando a aplicacao arranca.

### Criar com Docker (opcional)

```bash
docker compose up -d db
```

## 5. Configuracao da conexao

A aplicacao le a configuracao de variaveis de ambiente. Os valores abaixo sao os valores padrao:

```bash
export DB_URL='jdbc:postgresql://localhost:5432/cadastro_atlantico'
export DB_USER='cadastro_app'
export DB_PASSWORD='cadastro123'
```

Nunca grave uma senha real no repositorio. Em alternativa, podem ser usadas propriedades da JVM:

```bash
-Ddb.url=jdbc:postgresql://localhost:5432/cadastro_atlantico
-Ddb.user=cadastro_app
-Ddb.password=cadastro123
```

## 6. Compilar obrigatoriamente com Java 17

### Neste computador

O JDK 17 local do projecto pode ser activado no terminal sem `sudo`:

```bash
source scripts/use-java17.sh
```

Depois disso, `java --version`, `javac --version` e `mvn -version` devem mostrar `17.0.19`. O VS Code tambem esta configurado para usar esse JDK por defeito neste workspace.

Para instalar Java 17 globalmente no Ubuntu, operacao que exige a palavra-passe do utilizador:

```bash
sudo apt install openjdk-17-jdk
sudo update-alternatives --config java
sudo update-alternatives --config javac
```

Confirme primeiro o ambiente:

```bash
java -version
mvn -version
```

As duas saidas devem indicar Java 17. Depois execute:

```bash
mvn clean test
mvn package
```

O resultado sera:

```text
target/cadastro-atlantico.war
```

O `pom.xml` usa `maven.compiler.release=17`, que impede acidentalmente a utilizacao de APIs de Java 18, 21 ou 25, mesmo que outro JDK esteja activo na maquina de desenvolvimento.

## 7. Executar no Tomcat 10.1+

1. crie a base `cadastro_atlantico`
2. configure `DB_URL`, `DB_USER` e `DB_PASSWORD`
3. compile com `mvn clean package`
4. copie `target/cadastro-atlantico.war` para a pasta `webapps` do Tomcat
5. inicie o Tomcat
6. abra `http://localhost:8080/cadastro-atlantico/`
7. para administrar, aceda a `http://localhost:8080/cadastro-atlantico/admin/login`
   com utilizador `admin` e senha `admin`

No NetBeans:

1. abra o projecto como projecto Maven
2. em **Java Platform**, seleccione **JDK 17**
3. adicione um servidor **Tomcat 10.1+**
4. associe o projecto ao servidor e use **Run**

## 8. Rotas da aplicacao

| Metodo | Rota | Responsabilidade |
|---|---|---|
| GET | `/` | redirecciona para o formulario publico |
| GET | `/candidaturas/nova` | apresenta o formulario publico sem login |
| POST | `/candidaturas/nova` | valida e grava no PostgreSQL |
| GET | `/candidaturas/sucesso?id=1` | confirma a submissao ao candidato |
| GET | `/admin/login` | apresenta o login administrativo |
| POST | `/admin/login` | autentica o administrador (`admin`/`admin`) |
| GET | `/admin/logout` | termina a sessao administrativa |
| GET | `/admin/candidaturas` | lista e pesquisa candidaturas no painel admin |
| GET | `/admin/candidaturas/detalhe?id=1` | consulta um registo pelo ID, protegido por login |

As JSP ficam dentro de `WEB-INF`, portanto nao podem ser chamadas directamente. O acesso passa sempre pelo controlador Servlet.

## 9. Robustez e seguranca

A aplicacao implementa:

- campos obrigatorios e limites de tamanho
- e-mail, telefone, documento e datas validados no servidor
- pais/provincia e opcoes de catalogo validados contra listas permitidas
- URLs aceites apenas com `http://` ou `https://`, host valido e sem credenciais embutidas
- `PreparedStatement` em todas as consultas contra SQL Injection
- `<c:out>` nas JSP contra XSS
- token CSRF na submissao
- token CSRF no login administrativo
- separacao de responsabilidades entre candidato publico e administrador autenticado
- cabecalhos CSP, `nosniff`, `DENY` e politica de permissoes
- transacao, `commit` e `rollback` no DAO
- mensagens amigaveis sem expor detalhes internos da base de dados
- restricoes `UNIQUE`, `CHECK` e chaves estrangeiras no PostgreSQL
- padrao POST/Redirect/GET para impedir submissao duplicada ao actualizar a pagina

## 10. Relacao com os criterios de avaliacao

| Criterio | Evidencia para mostrar |
|---|---|
| Funcionalidade | candidato submete sem login; admin entra, lista, pesquisa e consulta detalhes |
| GUI/UX | telas separadas para candidato e admin, layout responsivo, feedback de erros, estado vazio e confirmacao |
| Robustez | validacao dupla, URLs, CSRF, SQL parametrizado e transacao |
| Esquema da BD | tres tabelas normalizadas, PK/FK, indices e constraints |
| Codificacao | MVC, JavaBeans, DAO, Service, Servlet e JSP separados |

## 11. Roteiro recomendado para a defesa

1. mostrar o `pom.xml` e destacar `release 17` e `packaging war`
2. explicar o diagrama das tres camadas
3. abrir o Bean `Candidatura`
4. abrir o DAO e mostrar `PreparedStatement`, transacao e `rollback`
5. abrir o Servlet e explicar GET, POST, forward e redirect
6. abrir uma JSP e mostrar que apenas renderiza a informacao
7. mostrar as tres tabelas no PostgreSQL
8. submeter uma candidatura valida ao vivo como candidato sem login
9. entrar em `/admin/login` com `admin`/`admin`
10. pesquisar e abrir o detalhe da candidatura no painel administrativo
11. tentar um URL invalido ou telefone curto para demonstrar robustez

Para a alteracao surpresa de uma hora, dividam o grupo por camadas: uma pessoa no Bean/BD, uma no DAO/Service, uma no Servlet e outra na JSP/CSS. No fim, reservem pelo menos 10 minutos para integrar e testar juntos.

## 12. Testes

Os testes automatizados verificam uma candidatura valida e rejeitam:

- URL com protocolo perigoso
- data futura
- opcao forjada fora do catalogo
- gravacao e leitura real via JDBC (quando `TEST_DATABASE_URL` estiver configurada)

Execute:

```bash
mvn test
```

Para incluir o teste de integracao PostgreSQL:

```bash
TEST_DATABASE_URL='jdbc:postgresql://localhost:5432/cadastro_atlantico' mvn test
```
