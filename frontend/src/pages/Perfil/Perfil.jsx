import { useEffect, useMemo, useState } from 'react';
import { useAuth } from '../../context/useAuth';
import { apiFetch } from '../../services/api';

function Perfil() {
  const { user } = useAuth();
  const [jogadores, setJogadores] = useState([]);

  useEffect(() => {
    apiFetch('/api/jogadores')
      .then((data) => setJogadores(Array.isArray(data) ? data : []))
      .catch(() => setJogadores([]));
  }, []);

  const jogador = useMemo(() => jogadores.find((item) => item.email === user?.email), [jogadores, user?.email]);
  const name = jogador?.nome || jogador?.nickname || user?.email || 'Jogador';

  return (
    <div className="content-stack">
      <section className="profile-hero">
        <div className="profile-avatar">{name.slice(0, 2).toUpperCase()}</div>
        <div>
          <p className="eyebrow">Perfil</p>
          <h1>{name}</h1>
          <p>{user?.email}</p>
        </div>
      </section>

      <section className="profile-grid">
        <article className="profile-item"><span>Nickname</span><strong>{jogador?.nickname || 'Sem nickname'}</strong></article>
        <article className="profile-item"><span>Acesso</span><strong>{user?.perfil === 'ROLE_ADMIN' ? 'Administrador' : 'Jogador'}</strong></article>
        <article className="profile-item"><span>Equipe</span><strong>{jogador?.equipe?.nomeEquipe || 'Sem equipe vinculada'}</strong></article>
        <article className="profile-item"><span>Status</span><strong>Conta ativa</strong></article>
      </section>
    </div>
  );
}

export default Perfil;
