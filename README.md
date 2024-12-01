
Integrantes: Andressa Komoda - 12522128490, Gustavo Yamada - 12524120449, Lucas Rolim 12522220623, Rafael Almeida 12523144660



# Sistema Hospitalar com Estruturas de Dados

## Descrição Geral

Este projeto é um sistema hospitalar desenvolvido para gerenciar filas de atendimento com base em prioridades e organizar os registros de médicos e pacientes. Ele utiliza estruturas de dados como filas e listas encadeadas, com funcionalidades voltadas para diferentes tipos de atendimento (Consulta, Coleta e Emergência).

O sistema é construído em Java com Spring Boot e inclui uma interface web que permite interação de pacientes e médicos.

## Requisitos do Sistema

- **Java**: Versão 17 ou superior
- **Spring Boot**: 3 ou superior
- **Banco de Dados**: MySQL ou H2 (em memória)
- **Node.js**: Para gerenciar dependências de frontend (opcional)
- **Ferramentas**: Maven para build e IntelliJ IDEA ou VS Code para desenvolvimento

## Instalação

1. **Clone o repositório**:
   ```bash
   git clone <URL_DO_REPOSITÓRIO>
   cd Estrutura-de-dados-A3
   ```

2. **Configure o banco de dados**:
   - Atualize o arquivo `src/main/resources/application.properties` com as credenciais do seu banco de dados.

3. **Execute o backend**:
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse o frontend**:
   Abra o arquivo `index.html` em `src/main/resources/static/frontend` ou configure um servidor para servir os arquivos.

## Uso

### Página Inicial
Contém três opções principais para os pacientes:
- **Consulta**: Registra um paciente na fila de consulta.
- **Coleta**: Registra um paciente na fila de coleta de exames.
- **Emergência**: Registra um paciente na fila de emergência com prioridade.

### Área do Médico
- Visualiza a lista de pacientes em espera.
- Possui um botão "Chamar Próximo Paciente" para atualizar o status de atendimento.
- Permite edição dos registros de pacientes.

### Login e Cadastro
- **Médicos**: Cadastro e login com email, CRM e senha.
- **Pacientes**: Registro automático ao selecionar uma fila de atendimento.

## Arquitetura do Sistema

1. **Backend**:
   - **Controller**: Gerencia requisições HTTP e interações do usuário.
   - **Service**: Contém a lógica de negócios.
   - **Repository**: Acessa o banco de dados.

2. **Frontend**:
   - Arquivos HTML/CSS para páginas estáticas.
   - JavaScript para interatividade.
   - Para acesso da pagina colocar localhost:8081 para pagina principal
   - Para acesso de Banco de Dados localhost:8081/h2-console para banco de dados 

3. **Banco de Dados**:
   - Estrutura definida em `data.sql` para inicialização.
   - Utiliza filas para gerenciar prioridades.

## Funcionalidades

- **Gestão de Fila**:
  - Emergências possuem prioridade máxima.
  - Ordem FIFO para Coleta e Consulta.
- **Autenticação**:
  - Login e senha para médicos.
- **Registro**:
  - Criação de registros para pacientes e médicos.
- **Administração**:
  - Médicos podem visualizar e modificar a lista de pacientes.

## Testes

Os testes estão localizados em `src/test/java` e cobrem:
- Controllers
- Services
- Integração com o banco de dados

Para rodar os testes:
```bash
mvn test
```
