import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/useAuth';
import { apiFetch } from '../../services/api';

const emptyData = { torneios: [], inscricoes: [], jogos: [], jogadores: [], equipes: [], partidas: [] };
const list = (value) => (Array.isArray(value) ? value : []);
const playerName = (item) => item?.jogador?.nickname || item?.jogador?.nome || item?.jogador?.email || 'Jogador';
const money = (value) => Number(value || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

function StatCard({ label, value, detail }) {
  return (
    <article className="stat-card">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{detail}</small>
    </article>
  );
}

function Dashboard() {
  const { user } = useAuth();
  const isAdmin = user?.perfil === 'ROLE_ADMIN';
  const [data, setData] = useState(emptyData);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let mounted = true;

    async function loadData() {
      setLoading(true);
      const entries = [
        ['torneios', '/api/torneios'],
        ['inscricoes', '/api/inscricoes'],
        ['jogos', '/api/jogos'],
        ['jogadores', '/api/jogadores'],
        ['equipes', '/api/equipes'],
        ['partidas', '/api/partidas'],
      ];

      const results = await Promise.allSettled(entries.map(([, path]) => apiFetch(path)));
      if (!mounted) return;

      setData(entries.reduce((acc, [key], index) => ({
        ...acc,
        [key]: results[index].status === 'fulfilled' ? list(results[index].value) : [],
      }), emptyData));

      if (results.every((result) => result.status === 'rejected')) {
        setError('Nao foi possivel carregar os dados do painel.');
      }
      setLoading(false);
    }

    loadData();
    return () => { mounted = false; };
  }, []);

  const ranking = useMemo(() => (
    [...data.inscricoes].sort((a, b) => (
      (b.pontosAcumulados || 0) - (a.pontosAcumulados || 0)
      || (b.vitoriasTotais || 0) - (a.vitoriasTotais || 0)
    ))
  ), [data.inscricoes]);

  const recentTournaments = [...data.torneios]
    .sort((a, b) => new Date(b.dataCriacao || 0) - new Date(a.dataCriacao || 0))
    .slice(0, 4);

  return (
    <div className="content-stack">
      <section className="page-heading hero-panel">
        <div>
          <p className="eyebrow">Rank It Up</p>
          <h1>Painel competitivo</h1>
          <p>Acompanhe torneios, jogadores, partidas e ranking com dados atualizados.</p>
        </div>
        <div className="hero-actions">
          {isAdmin && <Link className="primary-button" to="/matches">Registrar partida</Link>}
          <Link className={isAdmin ? 'ghost-button' : 'primary-button'} to="/tournaments">Ver torneios</Link>
        </div>
      </section>

      {error && <div className="notice error">{error}</div>}

      <section className="stats-grid">
        <StatCard label="Torneios" value={loading ? '...' : data.torneios.length} detail="cadastrados" />
        <StatCard label="Jogos" value={loading ? '...' : data.jogos.length} detail="modalidades" />
        <StatCard label="Inscricoes" value={loading ? '...' : data.inscricoes.length} detail="aprovadas e pendentes" />
        <StatCard label="Partidas" value={loading ? '...' : data.partidas.length} detail={`${data.jogadores.length} jogadores`} />
      </section>

      <section className="workflow-grid">
        <Link className="workflow-card" to="/tournaments"><span>Torneios</span><strong>Campeonatos ativos</strong><small>Consulte jogos, premios e inscricoes.</small></Link>
        {isAdmin && <Link className="workflow-card" to="/matches"><span>Resultados</span><strong>Registrar partidas</strong><small>Adicione confrontos e atualize o ranking.</small></Link>}
        <Link className="workflow-card" to="/leaderboards"><span>Ranking</span><strong>Classificacao</strong><small>Acompanhe pontuacoes por torneio.</small></Link>
        {isAdmin && <Link className="workflow-card" to="/games"><span>Catalogo</span><strong>Jogos e equipes</strong><small>Gerencie modalidades e times.</small></Link>}
      </section>

      <section className="dashboard-grid">
        <article className="panel">
          <div className="panel-title">
            <div><p className="eyebrow">Ranking</p><h2>Melhores pontuacoes</h2></div>
            <Link to="/leaderboards">Ver todos</Link>
          </div>
          <div className="rank-list">
            {ranking.slice(0, 5).map((item, index) => (
              <div className="rank-row" key={item.idInscricao}>
                <span className="rank-badge">#{index + 1}</span>
                <div><strong>{playerName(item)}</strong><small>{item.torneio?.nome}</small></div>
                <b>{item.pontosAcumulados || 0} pts</b>
              </div>
            ))}
          </div>
        </article>

        <article className="panel">
          <div className="panel-title">
            <div><p className="eyebrow">Agenda</p><h2>Torneios recentes</h2></div>
            <Link to="/tournaments">Abrir</Link>
          </div>
          <div className="tournament-list">
            {recentTournaments.map((torneio) => (
              <div className="tournament-row" key={torneio.idTorneio}>
                <div><strong>{torneio.nome}</strong><small>{torneio.jogo?.titulo || 'Jogo'}</small></div>
                <span>{money(torneio.premiacaoTotal)}</span>
              </div>
            ))}
          </div>
        </article>
      </section>
    </div>
  );
}

export default Dashboard;
