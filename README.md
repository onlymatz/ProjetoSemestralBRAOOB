"""# 🏆 Rank It Up! - Sistema de Gestão de Torneios de e-Sports

![Rank It Up Logo](./frontend/src/assets/logo.png)

## 📖 Sobre o Projeto

A organização e o acompanhamento de campeonatos de e-Sports exigem um nível de precisão no registro de resultados e na atualização de classificações que ferramentas manuais já não conseguem suprir. Historicamente, a gestão baseada em processos humanos ou planilhas estáticas apresenta vulnerabilidades críticas, como a suscetibilidade a erros de digitação, a perda de integridade referencial e a dificuldade de promover transparência em tempo real para o público. No atual cenário de alta competitividade tecnológica, a automação dessas tarefas por meio de sistemas de software robustos é a solução essencial para garantir agilidade e confiabilidade na administração de dados competitivos.

Diante desse contexto, o projeto **Rank It Up!** propõe o desenvolvimento de um **Sistema de Gestão de Torneios** focado na eficiência arquitetural e na usabilidade para modalidades eletrônicas. O objetivo central é automatizar o núcleo operacional de uma competição, permitindo o registro de partidas entre múltiplos competidores e a aplicação de uma lógica de ranqueamento ponderada. Diferente de sistemas convencionais, a solução busca implementar algoritmos inspirados no **Sistema de Rating Elo**, tratando a pontuação não apenas como uma soma aritmética, mas como um reflexo dinâmico do nível de habilidade dos participantes.

Para garantir a viabilidade técnica dentro do semestre letivo, a arquitetura do projeto prioriza funcionalidades que conferem o caráter de um ecossistema completo e escalável. Destacam-se a implementação de controle de acesso rigoroso para organizadores — garantindo a exclusividade na manipulação de placares e a integridade das tabelas de classificação — e a capacidade de gerenciamento de múltiplos torneios de forma independente sob uma mesma base de dados.

---

## 👥 Integrantes da Equipe

- **Eduardo Soares Lourenço Araujo** - BP3062643
- **João Victor de Moraes Sant'Anna** - BP3062601
- **João Vitor Oliveira Durães** - BP3061353
- **Matheus da Silva Marcondes** - BP3061493
- **Raul Ramos Cirilo da Silva** - BP3061558

---

## 🛠️ Tecnologias Utilizadas

O ecossistema do sistema está dividido em duas camadas principais totalmente desacopladas:

**Backend (API RESTful):**
- **Java 17 / Spring Boot:** Framework base para construção da API estável e escalável.
- **Spring Security & JWT:** Controle rigoroso de autenticação e autorização por tokens para os organizadores.
- **Spring Data JPA / Hibernate:** Abstração e persistência normalizada de dados.
- **MySQL:** Banco de dados relacional para manutenção da integridade referencial.
- **Maven:** Gerenciador de dependências e build.

**Frontend (Interface do Usuário):**
- **React.js & Vite:** Ferramentas modernas para uma interface de alta performance e SPA (Single Page Application).
- **Axios:** Cliente HTTP para consumo seguro dos endpoints da API.
- **HTML5 / CSS3 / JavaScript (ES6+):** Estruturação e estilização moderna da interface.

---

## ⚙️ Arquitetura e Detalhamento da API

A API do **Rank It Up!** foi projetada seguindo as restrições arquiteturais do modelo REST, garantindo recursos bem definidos, respostas em formato JSON e o uso correto dos métodos HTTP (GET, POST, PUT, DELETE). O núcleo da API está mapeado através dos seguintes controladores principais:

1. **Autenticação e Usuários (`UsuarioController`)**
   - Gerencia o ciclo de vida dos usuários (Organizadores e Administradores).
   - Realiza o cadastro seguro e o endpoint de login, emitindo tokens **JWT (JSON Web Tokens)** para validar as requisições subsequentes e proteger rotas críticas.

2. **Gerenciamento de Torneios e Inscrições (`TorneioController` & `InscricaoController`)**
   - Criação, edição e listagem de múltiplos campeonatos simultâneos e independentes.
   - Gerenciamento do status das inscrições das equipes em torneios específicos, mantendo o histórico e os critérios de validação de vagas.

3. **Cadastro de Jogos, Equipes e Atletas (`JogoController`, `EquipeController` & `JogadorController`)**
   - Mapeamento das modalidades de e-Sports disponíveis (Gênero do jogo, regras base).
   - Gerenciamento de entidades competitivas, permitindo a formação de elencos (lineups) ao vincular jogadores às suas respectivas equipes.

4. **Partidas, Desempenhos e Atualização de Ranking (`PartidaController` & `DesempenhoPartidaController`)**
   - Registro detalhado de confrontos dentro de um torneio, capturando placares e dados brutos de desempenho.
   - **Mecanismo de Rating Elo:** Ao finalizar uma partida, o backend calcula automaticamente as mudanças na pontuação dos envolvidos de forma dinâmica. A variação considera a probabilidade de vitória com base no histórico de habilidade atual, aplicando uma lógica ponderada que atualiza instantaneamente a tabela de classificação geral.

	Link para as informações referentes a API/backend: 
	https://github.com/onlymatz/ProjetoSemestralBRAOOB/blob/main/rankitup-api-docs.md
---

## 🚀 Como Executar o Projeto

### Pré-requisitos
Certifique-se de ter as seguintes ferramentas instaladas:
- [Java Development Kit (JDK) 17 ou superior](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Node.js (v18+) e npm](https://nodejs.org/)
