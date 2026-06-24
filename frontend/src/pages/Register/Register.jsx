import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../../assets/logo_nobg.png';
import { useAuth } from '../../context/useAuth';

function Register() {
  const navigate = useNavigate();
  const { login, register } = useAuth();
  const [form, setForm] = useState({ nome: '', nickname: '', email: '', senha: '', confirmarSenha: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  function updateField(event) {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    if (form.senha !== form.confirmarSenha) {
      setError('As senhas precisam ser iguais.');
      return;
    }

    setLoading(true);
    try {
      await register({
        nome: form.nome.trim(),
        nickname: form.nickname.trim(),
        email: form.email.trim(),
        senha: form.senha,
      });
      await login({ email: form.email.trim(), senha: form.senha });
      navigate('/dashboard', { replace: true });
    } catch (apiError) {
      setError(apiError.message || 'Nao foi possivel concluir o cadastro.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-intro">
        <img src={logo} alt="" />
        <p className="eyebrow">Novo jogador</p>
        <h1>Crie sua conta</h1>
        <p>Monte seu perfil competitivo e acompanhe sua evolucao nos torneios.</p>
      </section>

      <section className="auth-card">
        <p className="eyebrow">Cadastro</p>
        <h2>Dados da conta</h2>
        {error && <div className="notice error">{error}</div>}
        <form className="form-grid" onSubmit={handleSubmit}>
          <label>Nome<input name="nome" value={form.nome} onChange={updateField} required /></label>
          <label>Nickname<input name="nickname" value={form.nickname} onChange={updateField} required /></label>
          <label>E-mail<input name="email" type="email" value={form.email} onChange={updateField} required /></label>
          <label>Senha<input name="senha" type="password" value={form.senha} onChange={updateField} minLength={6} required /></label>
          <label>Confirmar senha<input name="confirmarSenha" type="password" value={form.confirmarSenha} onChange={updateField} minLength={6} required /></label>
          <button className="primary-button span-all" type="submit" disabled={loading}>
            {loading ? 'Criando...' : 'Criar conta'}
          </button>
        </form>
        <p className="auth-switch">Ja tem conta? <Link to="/login">Entrar</Link></p>
      </section>
    </main>
  );
}

export default Register;
