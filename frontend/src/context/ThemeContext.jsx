import { useEffect, useMemo, useState } from 'react';
import { ThemeContext } from './theme-context';

const THEME_KEY = 'rankitup.theme';

function getInitialTheme() {
  const stored = localStorage.getItem(THEME_KEY);
  if (stored === 'dark' || stored === 'light') {
    return stored;
  }
  return 'dark';
}

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState(getInitialTheme);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem(THEME_KEY, theme);
  }, [theme]);

  const value = useMemo(() => ({
    theme,
    isDark: theme === 'dark',
    toggleTheme: () => setTheme((current) => (current === 'dark' ? 'light' : 'dark')),
  }), [theme]);

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
}
