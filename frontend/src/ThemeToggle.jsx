import { useTheme } from './context/useTheme';

function ThemeToggle() {
  const { isDark, toggleTheme } = useTheme();

  return (
    <button
      type="button"
      className="theme-toggle"
      onClick={toggleTheme}
      aria-label={isDark ? 'Ativar tema claro' : 'Ativar tema escuro'}
      title={isDark ? 'Tema claro' : 'Tema escuro'}
    >
      <span className="theme-toggle-track">
        <span className="theme-toggle-thumb" />
      </span>
      <span>{isDark ? 'Escuro' : 'Claro'}</span>
    </button>
  );
}

export default ThemeToggle;
