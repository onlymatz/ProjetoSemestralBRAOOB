import { useEffect, useMemo, useState } from 'react';
import { apiFetch } from '../../services/api';

const playerName = (item) => item?.jogador?.nickname || item?.jogador?.nome || item?.jogador?.email || 'Jogador';

function Leaderboards() {
  const [inscricoes, setInscricoes] = useState([]);
  const [torneios, setTorneios] = useState([]);
  const [selectedTournament, setSelectedTournament] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([apiFetch('/api/inscricoes'), apiFetch('/api/torneios')])
      .then(([registrations, tournaments]) => {
        setInscricoes(Array.isArray(registrations) ? registrations : []);
        setTorneios(Array.isArray(tournaments) ? tournaments : []);
      })
      .catch((apiError) => setError(apiError.message || 'Nao foi possivel carregar o ranking.'));
  }, []);

  const ranking = useMemo(() => [...inscricoes]
    .filter((item) => !selectedTournament || String(item.torneio?.idTorneio) === String(selectedTournament))
    .sort((a, b) => (
      (b.pontosAcumulados || 0) - (a.pontosAcumulados || 0)
      || (b.vitoriasTotais || 0) - (a.vitoriasTotais || 0)
    )), [inscricoes, selectedTournament]);

  const podium = ranking.slice(0, 3);

  return (
    <div className="content-stack">
      <section className="page-heading compact-heading">
        <div>
          <p className="eyebrow">Classificacao</p>
          <h1>Ranking global</h1>
          <p>Posicoes ordenadas por pontos, vitorias e ranking isolado por torneio.</p>
        </div>
        <label className="search-field">Torneio
          <select value={selectedTournament} onChange={(event) => setSelectedTournament(event.target.value)}>
            <option value="">Todos</option>
            {torneios.map((torneio) => <option key={torneio.idTorneio} value={torneio.idTorneio}>{torneio.nome}</option>)}
          </select>
        </label>
      </section>
      {error && <div className="notice error">{error}</div>}

      <section className="podium-grid">
        {podium.map((item, index) => (
          <article className="podium-card" key={item.idInscricao}>
            <span>#{index + 1}</span>
            <h2>{playerName(item)}</h2>
            <p>{item.torneio?.nome || 'Torneio'}</p>
            <strong>{item.pontosAcumulados || 0} pts</strong>
          </article>
        ))}
      </section>

      <section className="table-panel">
        <table className="data-table">
          <thead>
            <tr><th>#</th><th>Jogador</th><th>Equipe</th><th>Torneio</th><th>Status</th><th>Pontos</th><th>Vitorias</th></tr>
          </thead>
          <tbody>
            {ranking.map((item, index) => (
              <tr key={item.idInscricao}>
                <td><span className="rank-badge">#{index + 1}</span></td>
                <td><strong>{playerName(item)}</strong><small>{item.jogador?.email}</small></td>
                <td>{item.equipe?.tagEquipe || item.equipe?.nomeEquipe || 'Sem equipe'}</td>
                <td>{item.torneio?.nome || '-'}</td>
                <td><span className={`status-pill status-${item.status}`}>{item.status}</span></td>
                <td>{item.pontosAcumulados || 0}</td>
                <td>{item.vitoriasTotais || 0}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}

export default Leaderboards;
