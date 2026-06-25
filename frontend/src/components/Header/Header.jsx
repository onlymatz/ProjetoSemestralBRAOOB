import { NavLink, useNavigate } from 'react-router-dom';
import logo from '../../assets/logo_nobg.png';
import ThemeToggle from '../../ThemeToggle';
import { useAuth } from '../../context/useAuth';

const userLinks = [
  { to: '/dashboard', label: 'Visao geral' },
  { to: '/leaderboards', label: 'Ranking' },
  { to: '/tournaments', label: 'Torneios' },
  { to: '/perfil', label: 'Perfil' },
];

const adminLinks = [
  { to: '/dashboard', label: 'Visao geral' },
  { to: '/leaderboards', label: 'Ranking' },
  { to: '/tournaments', label: 'Torneios' },
  { to: '/games', label: 'Jogos' },
  { to: '/matches', label: 'Partidas' },
  { to: '/perfil', label: 'Perfil' },
];

function Header() {
  const navigate = useNavigate();
  const { logout, user } = useAuth();
  const links = user?.perfil === 'ROLE_ADMIN' ? adminLinks : userLinks;

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  return (
    <header className="global-header">
      <NavLink to="/dashboard" className="brand-lockup" aria-label="Rank It Up">
        <img src={logo} alt="" />
        <strong>Rank It Up!</strong>
      </NavLink>

      <nav className="primary-nav" aria-label="Navegacao principal">
        {links.map((link) => (
          <NavLink key={link.to} to={link.to} className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>
            {link.label}
          </NavLink>
        ))}
      </nav>

      <div className="header-actions">
        <ThemeToggle />
        <button type="button" className="ghost-button" onClick={handleLogout}>Sair</button>
      </div>
    </header>
  );
}

export default Header;
