# Rank It Up! — Documentação da API

Base URL: `http://localhost:8090`

---

## Autenticação

A API usa **JWT (Bearer Token)**. Após o login, inclua o token em todas as requisições protegidas:

```
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

Rotas públicas (não precisam de token): `POST /api/usuarios/cadastro`, `POST /api/usuarios/login`, `GET /api/torneios`, `GET /api/inscricoes`, `GET /api/jogos`.

---

## Usuários

### Cadastrar usuário
`POST /api/usuarios/cadastro` — **público**

**Body:**
```json
{
  "email": "jogador@email.com",
  "senha": "<senha>",
  "perfil": "ROLE_USER",
  "nome": "João Silva",
  "nickname": "joaosilva"
}
```


**Respostas:**
| Status | Descrição |
|--------|-----------|
| 201 | Usuário criado com sucesso |
| 409 | E-mail já cadastrado |

---

### Login
`POST /api/usuarios/login` — **público**

**Body:**
```json
{
  "email": "jogador@email.com",
  "senha": "<senha>"
}
```

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Retorna o token JWT como string |
| 401 | E-mail não encontrado ou senha incorreta |

**Exemplo de resposta:**
```
eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2dhZG9yQGVtYWlsLmNvbSJ9...
```

---

### Listar todos os usuários
`GET /api/usuarios` — **admin**

**Resposta 200:**
```json
[
  {
    "idUsuario": 1,
    "email": "jogador@email.com",
    "perfil": "ROLE_USER",
    "nome": "João Silva",
    "nickname": "joaosilva",
    "fotoPerfil": null,
    "equipe": null
  }
]
```

---

### Buscar usuário por ID
`GET /api/usuarios/{id}` — **autenticado**

**Resposta 200:**
```json
{
  "idUsuario": 1,
  "email": "jogador@email.com",
  "perfil": "ROLE_USER",
  "nome": "João Silva",
  "nickname": "joaosilva",
  "fotoPerfil": null,
  "equipe": null
}
```

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Usuário encontrado |
| 404 | Usuário não encontrado |

---

### Atualizar usuário
`PUT /api/usuarios/{id}` — **admin ou o próprio usuário**

**Body** (todos os campos são opcionais):
```json
{
  "nome": "João Atualizado",
  "nickname": "joao_novo",
  "fotoPerfil": "https://url-da-foto.com/foto.jpg",
  "senha": "<nova-senha>"
}
```

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Usuário atualizado |
| 403 | Sem permissão para atualizar este usuário |
| 404 | Usuário não encontrado |

---

### Excluir usuário
`DELETE /api/usuarios/{id}` — **admin**

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | `"Usuário excluído com sucesso."` |
| 404 | Usuário não encontrado |

---

## Jogos

### Listar todos os jogos
`GET /api/jogos` — **público**

**Resposta 200:**
```json
[
  {
    "idJogo": 1,
    "titulo": "Counter-Strike 2",
    "genero": "FPS",
    "desenvolvedora": "Valve"
  }
]
```

---

### Cadastrar jogo
`POST /api/jogos` — **autenticado**

**Body:**
```json
{
  "titulo": "Counter-Strike 2",
  "genero": "FPS",
  "desenvolvedora": "Valve"
}
```

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Jogo criado |

---

## Torneios

### Listar todos os torneios
`GET /api/torneios` — **público**

**Resposta 200:**
```json
[
  {
    "idTorneio": 1,
    "nome": "Torneio Verão",
    "premiacaoTotal": 1000.00,
    "dataCriacao": "2026-06-05T10:00:00",
    "jogo": { "idJogo": 1, "titulo": "Counter-Strike 2", "genero": "FPS", "desenvolvedora": "Valve" }
  }
]
```

---

### Criar torneio
`POST /api/torneios` — **admin**

O campo `criador` é preenchido automaticamente com o admin do token — não precisa enviar.

**Body:**
```json
{
  "nome": "Torneio Verão",
  "premiacaoTotal": 1000.00,
  "jogo": { "idJogo": 1 }
}
```

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Torneio criado com criador vinculado automaticamente |
| 403 | Token não é de admin |

---

## Inscrições

### Listar todas as inscrições
`GET /api/inscricoes` — **público**

Serve como tabela de ranking geral.

**Resposta 200:**
```json
[
  {
    "idInscricao": 1,
    "torneio": { "idTorneio": 1, "nome": "Torneio Verão", "..." : "..." },
    "jogador": { "idUsuario": 4, "nickname": "joaosilva", "..." : "..." },
    "status": "APROVADO",
    "pontosAcumulados": 47,
    "vitoriasTotais": 3,
    "saldoKills": 0
  }
]
```

---

### Listar inscrições de um torneio por status
`GET /api/inscricoes/torneio/{idTorneio}?status=APROVADO` — **público**

Valores possíveis para `status`: `PENDENTE`, `APROVADO`, `REJEITADO`.

Default: `APROVADO` (se não passar o parâmetro, retorna só os aprovados).

---

### Inscrever jogador em torneio
`POST /api/inscricoes` — **admin**

A inscrição começa automaticamente como `PENDENTE`.

**Body:**
```json
{
  "torneio": { "idTorneio": 1 },
  "jogador": { "idUsuario": 4 }
}
```

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Inscrição criada com status PENDENTE |
| 500 | Jogador já inscrito neste torneio |

---

### Aprovar ou rejeitar inscrição
`PATCH /api/inscricoes/{id}/status?status=APROVADO` — **admin**

Valores possíveis: `APROVADO`, `REJEITADO`.

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Status atualizado |
| 404 | Inscrição não encontrada |

---

## Partidas

### Listar todas as partidas
`GET /api/partidas` — **admin**

---

### Criar partida
`POST /api/partidas` — **admin**

**Body:**
```json
{
  "torneio": { "idTorneio": 1 },
  "faseTorneio": "Fase de Grupos"
}
```

---

### Registrar resultado e atualizar Elo
`POST /api/partidas/{id}/resultado` — **admin (criador do torneio)**

O `resultadoA` é o resultado do jogador A. O jogador B recebe o inverso automaticamente.

**Body:**
```json
{
  "idInscricaoA": 1,
  "idInscricaoB": 2,
  "resultadoA": "VITORIA"
}
```

Valores possíveis para `resultadoA`: `VITORIA`, `DERROTA`, `EMPATE`.

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | `"Resultado registrado e rankings atualizados."` |
| 400 | Jogador sem inscrição aprovada ou jogador contra si mesmo |
| 403 | Apenas o criador do torneio pode registrar resultados |
| 404 | Partida ou inscrição não encontrada |

---

### Atualizar fase da partida
`PUT /api/partidas/{id}` — **admin (criador do torneio)**

**Body:**
```json
{
  "faseTorneio": "Semifinal"
}
```

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | Partida atualizada |
| 403 | Apenas o criador do torneio pode editar |
| 404 | Partida não encontrada |

---

### Excluir partida e reverter Elo
`DELETE /api/partidas/{id}` — **admin (criador do torneio)**

Ao excluir, o Elo de ambos os jogadores é revertido automaticamente.

**Body:**
```json
{
  "idInscricaoA": 1,
  "idInscricaoB": 2,
  "resultadoA": "VITORIA"
}
```

> Informe o resultado original da partida para que o Elo seja revertido corretamente.

**Respostas:**
| Status | Descrição |
|--------|-----------|
| 200 | `"Partida excluída e rankings revertidos."` |
| 403 | Apenas o criador do torneio pode excluir |
| 404 | Partida ou inscrição não encontrada |

---

## Formato de erro padrão

Todos os erros retornam no seguinte formato:

```json
{
  "timestamp": "2026-06-05T16:00:00.123",
  "status": 404,
  "erro": "Usuário não encontrado."
}
```

---

## Fluxo completo sugerido

```
1. POST /api/usuarios/cadastro        → criar admin e jogadores
2. POST /api/usuarios/login           → obter token JWT
3. POST /api/jogos                    → cadastrar o jogo
4. POST /api/torneios                 → criar torneio (criador vinculado pelo token)
5. POST /api/inscricoes               → inscrever jogadores (status: PENDENTE)
6. PATCH /api/inscricoes/{id}/status  → aprovar inscrições
7. POST /api/partidas                 → criar partida
8. POST /api/partidas/{id}/resultado  → registrar resultado e atualizar Elo
9. GET  /api/inscricoes               → consultar ranking
```
