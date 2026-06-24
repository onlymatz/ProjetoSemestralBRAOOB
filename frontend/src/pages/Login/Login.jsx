import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../../assets/logo_nobg.png';
import { useAuth } from '../../context/useAuth';

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: '', senha: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  function updateField(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login(form);
      navigate('/dashboard', { replace: true });
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel entrar.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-intro">
        <img src={logo} alt="" />
        <p className="eyebrow">Rank It Up</p>
        <h1>Entre no painel competitivo</h1>
        <p>Acesse torneios, ranking, partidas e dados dos jogadores.</p>
      </section>

      <section className="auth-card">
        <p className="eyebrow">Acesso</p>
        <h2>Login</h2>
        {error && <div className="notice error">{error}</div>}
        <form className="form-stack" onSubmit={handleSubmit}>
          <label>
            E-mail
            <input name="email" type="email" value={form.email} onChange={updateField} required autoComplete="email" />
          </label>
          <label>
            Senha
            <input name="senha" type="password" value={form.senha} onChange={updateField} required autoComplete="current-password" />
          </label>
          <button className="primary-button" type="submit" disabled={loading}>
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
        <p className="auth-switch">Nao tem conta? <Link to="/cadastro">Criar cadastro</Link></p>
      </section>
    </main>
  );
}

export default Login;
