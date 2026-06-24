import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/useAuth';
import { apiFetch } from '../../services/api';

const money = (value) => Number(value || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
const date = (value) => (value ? new Intl.DateTimeFormat('pt-BR').format(new Date(value)) : 'Em aberto');

function Tournaments() {
  const { user } = useAuth();
  const isAdmin = user?.perfil === 'ROLE_ADMIN';
  const [torneios, setTorneios] = useState([]);
  const [jogos, setJogos] = useState([]);
  const [inscricoes, setInscricoes] = useState([]);
  const [search, setSearch] = useState('');
  const [form, setForm] = useState({ nome: '', jogoId: '', premiacaoTotal: '' });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadData = useCallback(async () => {
    const [torneiosData, jogosData, inscricoesData] = await Promise.all([
      apiFetch('/api/torneios'),
      apiFetch('/api/jogos'),
      apiFetch('/api/inscricoes'),
    ]);
    setTorneios(Array.isArray(torneiosData) ? torneiosData : []);
    setJogos(Array.isArray(jogosData) ? jogosData : []);
    setInscricoes(Array.isArray(inscricoesData) ? inscricoesData : []);
  }, []);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      loadData().catch((apiError) => setError(apiError.message || 'Nao foi possivel carregar os torneios.'));
    }, 0);
    return () => window.clearTimeout(timeout);
  }, [loadData]);

  const countByTournament = useMemo(() => inscricoes.reduce((acc, item) => {
    const id = item.torneio?.idTorneio;
    return id ? { ...acc, [id]: (acc[id] || 0) + 1 } : acc;
  }, {}), [inscricoes]);

  const filtered = torneios.filter((torneio) => {
    const term = search.trim().toLowerCase();
    return !term || torneio.nome?.toLowerCase().includes(term) || torneio.jogo?.titulo?.toLowerCase().includes(term);
  });

  async function handleCreate(event) {
    event.preventDefault();
    setMessage('');
    setError('');
    try {
      await apiFetch('/api/torneios', {
        method: 'POST',
        body: {
          nome: form.nome.trim(),
          premiacaoTotal: Number(form.premiacaoTotal || 0),
          jogo: { idJogo: Number(form.jogoId) },
        },
      });
      setForm({ nome: '', jogoId: '', premiacaoTotal: '' });
      setMessage('Torneio criado com sucesso.');
      await loadData();
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel criar o torneio.');
    }
  }

  return (
    <div className="content-stack">
      <section className="page-heading">
        <div>
          <p className="eyebrow">Campeonatos</p>
          <h1>Torneios</h1>
          <p>Consulte campeonatos ativos, jogos e inscricoes.</p>
        </div>
        <label className="search-field">Buscar<input value={search} onChange={(event) => setSearch(event.target.value)} /></label>
      </section>

      {message && <div className="notice success">{message}</div>}
      {error && <div className="notice error">{error}</div>}

      {isAdmin && (
        <section className="panel">
          <div className="panel-title"><div><p className="eyebrow">Organizacao</p><h2>Novo torneio</h2></div></div>
          <form className="inline-form" onSubmit={handleCreate}>
            <label>Nome<input value={form.nome} onChange={(event) => setForm({ ...form, nome: event.target.value })} required /></label>
            <label>Jogo<select value={form.jogoId} onChange={(event) => setForm({ ...form, jogoId: event.target.value })} required><option value="">Selecione</option>{jogos.map((jogo) => <option key={jogo.idJogo} value={jogo.idJogo}>{jogo.titulo}</option>)}</select></label>
            <label>Premiacao<input type="number" min="0" step="0.01" value={form.premiacaoTotal} onChange={(event) => setForm({ ...form, premiacaoTotal: event.target.value })} /></label>
            <button className="primary-button" type="submit">Criar torneio</button>
          </form>
        </section>
      )}

      <section className="tournament-grid">
        {filtered.map((torneio) => (
          <article className="tournament-card" key={torneio.idTorneio}>
            <div className="card-topline"><span>{torneio.jogo?.genero || 'JOGO'}</span><b>{countByTournament[torneio.idTorneio] || 0} inscricoes</b></div>
            <h2>{torneio.nome}</h2>
            <p>{torneio.jogo?.titulo || 'Modalidade'}</p>
            <dl className="meta-grid">
              <div><dt>Premiacao</dt><dd>{money(torneio.premiacaoTotal)}</dd></div>
              <div><dt>Criado em</dt><dd>{date(torneio.dataCriacao)}</dd></div>
            </dl>
            <Link className="ghost-button full-width" to="/leaderboards">Ver ranking</Link>
          </article>
        ))}
      </section>
    </div>
  );
}

export default Tournaments;
