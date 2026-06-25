import { useCallback, useEffect, useMemo, useState } from 'react';
import { apiFetch } from '../../services/api';
import { useAuth } from '../../context/useAuth';

const initialGame = { titulo: '', genero: 'FPS', desenvolvedora: '' };
const initialTeam = { nomeEquipe: '', tagEquipe: '', paisOrigem: 'Brasil' };
const genres = ['FPS', 'MOBA', 'BATTLE_ROYALLE', 'SHOOTER', 'TABULEIRO'];

function Games() {
  const { user } = useAuth();
  const isAdmin = user?.perfil === 'ROLE_ADMIN';
  const [jogos, setJogos] = useState([]);
  const [equipes, setEquipes] = useState([]);
  const [jogadores, setJogadores] = useState([]);
  const [game, setGame] = useState(initialGame);
  const [team, setTeam] = useState(initialTeam);
  const [editingGameId, setEditingGameId] = useState(null);
  const [editingTeamId, setEditingTeamId] = useState(null);
  const [search, setSearch] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadData = useCallback(async () => {
    const [gamesData, teamsData, playersData] = await Promise.all([
      apiFetch('/api/jogos'),
      apiFetch('/api/equipes'),
      apiFetch('/api/jogadores'),
    ]);
    setJogos(Array.isArray(gamesData) ? gamesData : []);
    setEquipes(Array.isArray(teamsData) ? teamsData : []);
    setJogadores(Array.isArray(playersData) ? playersData : []);
  }, []);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      loadData().catch((apiError) => setError(apiError.message || 'Nao foi possivel carregar jogos e equipes.'));
    }, 0);
    return () => window.clearTimeout(timeout);
  }, [loadData]);

  const filteredGames = useMemo(() => {
    const term = search.trim().toLowerCase();
    return jogos.filter((item) => !term
      || item.titulo?.toLowerCase().includes(term)
      || item.genero?.toLowerCase().includes(term)
      || item.desenvolvedora?.toLowerCase().includes(term));
  }, [jogos, search]);

  async function submitGame(event) {
    event.preventDefault();
    setMessage('');
    setError('');
    try {
      const path = editingGameId ? `/api/jogos/${editingGameId}` : '/api/jogos';
      await apiFetch(path, { method: editingGameId ? 'PUT' : 'POST', body: game });
      setGame(initialGame);
      setEditingGameId(null);
      setMessage(editingGameId ? 'Jogo atualizado com sucesso.' : 'Jogo cadastrado com sucesso.');
      await loadData();
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel cadastrar o jogo.');
    }
  }

  async function submitTeam(event) {
    event.preventDefault();
    setMessage('');
    setError('');
    try {
      const path = editingTeamId ? `/api/equipes/${editingTeamId}` : '/api/equipes';
      await apiFetch(path, { method: editingTeamId ? 'PUT' : 'POST', body: team });
      setTeam(initialTeam);
      setEditingTeamId(null);
      setMessage(editingTeamId ? 'Equipe atualizada com sucesso.' : 'Equipe cadastrada com sucesso.');
      await loadData();
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel cadastrar a equipe.');
    }
  }

  function editGame(item) {
    setEditingGameId(item.idJogo);
    setGame({
      titulo: item.titulo || '',
      genero: item.genero || 'FPS',
      desenvolvedora: item.desenvolvedora || '',
    });
    setMessage('');
    setError('');
  }

  function editTeam(item) {
    setEditingTeamId(item.idEquipe);
    setTeam({
      nomeEquipe: item.nomeEquipe || '',
      tagEquipe: item.tagEquipe || '',
      paisOrigem: item.paisOrigem || '',
    });
    setMessage('');
    setError('');
  }

  async function deleteGame(id) {
    setMessage('');
    setError('');
    try {
      await apiFetch(`/api/jogos/${id}`, { method: 'DELETE' });
      setMessage('Jogo excluido com sucesso.');
      await loadData();
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel excluir o jogo. Verifique se ele nao esta vinculado a torneios.');
    }
  }

  async function deleteTeam(id) {
    setMessage('');
    setError('');
    try {
      await apiFetch(`/api/equipes/${id}`, { method: 'DELETE' });
      setMessage('Equipe excluida com sucesso.');
      await loadData();
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel excluir a equipe. Verifique se ela nao esta vinculada a inscricoes.');
    }
  }

  function cancelGameEdit() {
    setEditingGameId(null);
    setGame(initialGame);
  }

  function cancelTeamEdit() {
    setEditingTeamId(null);
    setTeam(initialTeam);
  }

  return (
    <div className="content-stack">
      <section className="page-heading compact-heading">
        <div>
          <p className="eyebrow">Catalogo competitivo</p>
          <h1>Jogos e equipes</h1>
          <p>Gerencie modalidades, desenvolvedoras e times usados nos torneios.</p>
        </div>
        <label className="search-field">Buscar<input value={search} onChange={(event) => setSearch(event.target.value)} /></label>
      </section>

      {message && <div className="notice success">{message}</div>}
      {error && <div className="notice error">{error}</div>}

      {isAdmin && (
        <section className="management-grid">
          <article className="panel">
            <div className="panel-title"><div><p className="eyebrow">Modalidade</p><h2>{editingGameId ? 'Editar jogo' : 'Novo jogo'}</h2></div></div>
            <form className="form-stack" onSubmit={submitGame}>
              <label>Titulo<input value={game.titulo} onChange={(event) => setGame({ ...game, titulo: event.target.value })} required /></label>
              <label>Genero<select value={game.genero} onChange={(event) => setGame({ ...game, genero: event.target.value })}>{genres.map((genre) => <option key={genre} value={genre}>{genre}</option>)}</select></label>
              <label>Desenvolvedora<input value={game.desenvolvedora} onChange={(event) => setGame({ ...game, desenvolvedora: event.target.value })} /></label>
              <div className="form-actions">
                <button type="submit" className="primary-button">{editingGameId ? 'Salvar jogo' : 'Cadastrar jogo'}</button>
                {editingGameId && <button type="button" className="ghost-button" onClick={cancelGameEdit}>Cancelar</button>}
              </div>
            </form>
          </article>

          <article className="panel">
            <div className="panel-title"><div><p className="eyebrow">Equipe</p><h2>{editingTeamId ? 'Editar time' : 'Novo time'}</h2></div></div>
            <form className="form-stack" onSubmit={submitTeam}>
              <label>Nome<input value={team.nomeEquipe} onChange={(event) => setTeam({ ...team, nomeEquipe: event.target.value })} required /></label>
              <label>Tag<input value={team.tagEquipe} onChange={(event) => setTeam({ ...team, tagEquipe: event.target.value.toUpperCase() })} maxLength={10} required /></label>
              <label>Pais<input value={team.paisOrigem} onChange={(event) => setTeam({ ...team, paisOrigem: event.target.value })} /></label>
              <div className="form-actions">
                <button type="submit" className="primary-button">{editingTeamId ? 'Salvar equipe' : 'Cadastrar equipe'}</button>
                {editingTeamId && <button type="button" className="ghost-button" onClick={cancelTeamEdit}>Cancelar</button>}
              </div>
            </form>
          </article>
        </section>
      )}

      <section className="split-grid">
        <article className="panel">
          <div className="panel-title"><div><p className="eyebrow">Jogos</p><h2>Modalidades cadastradas</h2></div><strong>{jogos.length}</strong></div>
          <div className="catalog-grid">
            {filteredGames.map((item) => (
              <div className="catalog-card" key={item.idJogo}>
                <span>{item.genero}</span>
                <strong>{item.titulo}</strong>
                <small>{item.desenvolvedora || 'Desenvolvedora nao informada'}</small>
                {isAdmin && (
                  <div className="row-actions">
                    <button className="ghost-button compact-button" type="button" onClick={() => editGame(item)}>Editar</button>
                    <button className="danger-button compact-button" type="button" onClick={() => deleteGame(item.idJogo)}>Excluir</button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </article>

        <article className="panel">
          <div className="panel-title"><div><p className="eyebrow">Times</p><h2>Equipes e jogadores</h2></div><strong>{equipes.length}</strong></div>
          <div className="team-list">
            {equipes.map((item) => (
              <div className="team-row" key={item.idEquipe}>
                <b>{item.tagEquipe}</b>
                <div><strong>{item.nomeEquipe}</strong><small>{item.paisOrigem || 'Pais nao informado'}</small></div>
                {isAdmin && (
                  <div className="row-actions">
                    <button className="ghost-button compact-button" type="button" onClick={() => editTeam(item)}>Editar</button>
                    <button className="danger-button compact-button" type="button" onClick={() => deleteTeam(item.idEquipe)}>Excluir</button>
                  </div>
                )}
              </div>
            ))}
          </div>
          <p className="panel-footnote">{jogadores.length} jogadores cadastrados para compor inscricoes e rankings.</p>
        </article>
      </section>
    </div>
  );
}

export default Games;
