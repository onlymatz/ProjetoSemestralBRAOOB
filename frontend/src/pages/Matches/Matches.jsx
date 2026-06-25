import { useCallback, useEffect, useMemo, useState } from 'react';
import { apiFetch } from '../../services/api';
import { useAuth } from '../../context/useAuth';

const initialMatch = { torneioId: '', faseTorneio: '', inscricaoA: '', inscricaoB: '', resultadoA: 'VITORIA' };
const resultLabels = { VITORIA: 'Vitoria', DERROTA: 'Derrota', EMPATE: 'Empate' };

function playerLabel(item) {
  return item?.jogador?.nickname || item?.jogador?.nome || item?.jogador?.email || 'Jogador';
}

function countApprovedByTournament(registrations) {
  return registrations.reduce((acc, item) => {
    if (item.status !== 'APROVADO' || !item.torneio?.idTorneio) {
      return acc;
    }
    const id = String(item.torneio.idTorneio);
    return { ...acc, [id]: (acc[id] || 0) + 1 };
  }, {});
}

function Matches() {
  const { user } = useAuth();
  const isAdmin = user?.perfil === 'ROLE_ADMIN';
  const [torneios, setTorneios] = useState([]);
  const [inscricoes, setInscricoes] = useState([]);
  const [partidas, setPartidas] = useState([]);
  const [match, setMatch] = useState(initialMatch);
  const [selectedTournament, setSelectedTournament] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadData = useCallback(async () => {
    const requests = [
      apiFetch('/api/torneios'),
      apiFetch('/api/inscricoes'),
      isAdmin ? apiFetch('/api/partidas') : Promise.resolve([]),
    ];
    const [tournamentsData, registrationsData, matchesData] = await Promise.all(requests);
    const tournaments = Array.isArray(tournamentsData) ? tournamentsData : [];
    const registrations = Array.isArray(registrationsData) ? registrationsData : [];
    const approvedCount = countApprovedByTournament(registrations);
    const firstPlayable = tournaments.find((item) => (approvedCount[String(item.idTorneio)] || 0) >= 2);
    const fallbackTournament = firstPlayable || tournaments[0];
    const fallbackId = String(fallbackTournament?.idTorneio || '');

    setTorneios(tournaments);
    setInscricoes(registrations);
    setPartidas(Array.isArray(matchesData) ? matchesData : []);
    setSelectedTournament((current) => current || fallbackId);
    setMatch((current) => (current.torneioId ? current : { ...current, torneioId: fallbackId }));
  }, [isAdmin]);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      loadData().catch((apiError) => setError(apiError.message || 'Nao foi possivel carregar partidas.'));
    }, 0);
    return () => window.clearTimeout(timeout);
  }, [loadData]);

  const tournamentId = match.torneioId || selectedTournament;
  const approvedRegistrations = useMemo(() => inscricoes.filter((item) => (
    item.status === 'APROVADO' && String(item.torneio?.idTorneio) === String(tournamentId)
  )), [inscricoes, tournamentId]);

  const pendingRegistrations = useMemo(() => inscricoes.filter((item) => item.status === 'PENDENTE'), [inscricoes]);
  const approvedCountByTournament = useMemo(() => countApprovedByTournament(inscricoes), [inscricoes]);
  const visibleMatches = useMemo(() => partidas.filter((item) => (
    !selectedTournament || String(item.torneio?.idTorneio) === String(selectedTournament)
  )), [partidas, selectedTournament]);

  function handleTournamentFilterChange(value) {
    setSelectedTournament(value);
    setMatch((current) => ({
      ...current,
      torneioId: value,
      inscricaoA: '',
      inscricaoB: '',
    }));
  }

  async function approveRegistration(id, status) {
    setMessage('');
    setError('');
    try {
      await apiFetch(`/api/inscricoes/${id}/status?status=${status}`, { method: 'PATCH' });
      setMessage(status === 'APROVADO' ? 'Inscricao aprovada.' : 'Inscricao rejeitada.');
      await loadData();
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel atualizar a inscricao.');
    }
  }

  async function submitMatch(event) {
    event.preventDefault();
    setMessage('');
    setError('');

    if (match.inscricaoA === match.inscricaoB) {
      setError('Escolha dois jogadores diferentes.');
      return;
    }

    try {
      const created = await apiFetch('/api/partidas', {
        method: 'POST',
        body: {
          torneio: { idTorneio: Number(match.torneioId) },
          faseTorneio: match.faseTorneio.trim(),
        },
      });
      await apiFetch(`/api/partidas/${created.idPartida}/resultado`, {
        method: 'POST',
        body: {
          idInscricaoA: Number(match.inscricaoA),
          idInscricaoB: Number(match.inscricaoB),
          resultadoA: match.resultadoA,
        },
      });
      setSelectedTournament(match.torneioId);
      setMatch(initialMatch);
      setMessage('Partida registrada e ranking recalculado.');
      await loadData();
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel registrar a partida.');
    }
  }

  return (
    <div className="content-stack">
      <section className="page-heading compact-heading">
        <div>
          <p className="eyebrow">Resultados</p>
          <h1>Partidas</h1>
          <p>Crie confrontos, aprove jogadores e processe a pontuacao Elo automaticamente.</p>
        </div>
        <label className="search-field">Torneio
          <select value={selectedTournament} onChange={(event) => handleTournamentFilterChange(event.target.value)}>
            <option value="">Todos</option>
            {torneios.map((torneio) => (
              <option key={torneio.idTorneio} value={torneio.idTorneio}>
                {torneio.nome} ({approvedCountByTournament[String(torneio.idTorneio)] || 0} jogadores)
              </option>
            ))}
          </select>
        </label>
      </section>

      {message && <div className="notice success">{message}</div>}
      {error && <div className="notice error">{error}</div>}
      <section className="management-grid">
        <article className="panel">
          <div className="panel-title"><div><p className="eyebrow">Registro de partida</p><h2>Novo resultado</h2></div></div>
          <form className="form-stack" onSubmit={submitMatch}>
            <label>Torneio
              <select value={match.torneioId} onChange={(event) => handleTournamentFilterChange(event.target.value)} required>
                <option value="">Selecione</option>
                {torneios.map((torneio) => (
                  <option key={torneio.idTorneio} value={torneio.idTorneio}>
                    {torneio.nome} ({approvedCountByTournament[String(torneio.idTorneio)] || 0} jogadores)
                  </option>
                ))}
              </select>
            </label>
            {match.torneioId && approvedRegistrations.length < 2 && (
              <div className="notice error">Este torneio ainda nao tem 2 jogadores aprovados.</div>
            )}
            <label>Fase<input value={match.faseTorneio} onChange={(event) => setMatch({ ...match, faseTorneio: event.target.value })} placeholder="Final, Rodada 1..." required /></label>
            <label>Jogador A
              <select value={match.inscricaoA} onChange={(event) => setMatch({ ...match, inscricaoA: event.target.value })} required>
                <option value="">Selecione</option>
                {approvedRegistrations.map((item) => <option key={item.idInscricao} value={item.idInscricao}>{playerLabel(item)}</option>)}
              </select>
            </label>
            <label>Jogador B
              <select value={match.inscricaoB} onChange={(event) => setMatch({ ...match, inscricaoB: event.target.value })} required>
                <option value="">Selecione</option>
                {approvedRegistrations.map((item) => <option key={item.idInscricao} value={item.idInscricao}>{playerLabel(item)}</option>)}
              </select>
            </label>
            <label>Resultado do jogador A
              <select value={match.resultadoA} onChange={(event) => setMatch({ ...match, resultadoA: event.target.value })}>
                {Object.entries(resultLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
              </select>
            </label>
            <button className="primary-button" type="submit" disabled={!isAdmin || approvedRegistrations.length < 2}>Registrar e recalcular</button>
          </form>
        </article>

        <article className="panel">
          <div className="panel-title"><div><p className="eyebrow">Convites</p><h2>Inscricoes pendentes</h2></div><strong>{pendingRegistrations.length}</strong></div>
          <div className="approval-list">
            {pendingRegistrations.length === 0 && <p className="empty-state">Nenhuma inscricao pendente.</p>}
            {pendingRegistrations.map((item) => (
              <div className="approval-row" key={item.idInscricao}>
                <div><strong>{playerLabel(item)}</strong><small>{item.torneio?.nome} - {item.equipe?.tagEquipe || 'Sem equipe'}</small></div>
                <div className="row-actions">
                  <button className="ghost-button" type="button" disabled={!isAdmin} onClick={() => approveRegistration(item.idInscricao, 'REJEITADO')}>Rejeitar</button>
                  <button className="primary-button" type="button" disabled={!isAdmin} onClick={() => approveRegistration(item.idInscricao, 'APROVADO')}>Aprovar</button>
                </div>
              </div>
            ))}
          </div>
        </article>
      </section>

      <section className="table-panel">
        <table className="data-table">
          <thead>
            <tr><th>#</th><th>Torneio</th><th>Jogo</th><th>Fase</th><th>Registro</th></tr>
          </thead>
          <tbody>
            {visibleMatches.map((item) => (
              <tr key={item.idPartida}>
                <td>{item.idPartida}</td>
                <td><strong>{item.torneio?.nome}</strong><small>{item.torneio?.jogo?.genero || 'Torneio'}</small></td>
                <td>{item.torneio?.jogo?.titulo || '-'}</td>
                <td>{item.faseTorneio || 'Sem fase'}</td>
                <td>{item.dataRegistro ? new Intl.DateTimeFormat('pt-BR').format(new Date(item.dataRegistro)) : '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}

export default Matches;
