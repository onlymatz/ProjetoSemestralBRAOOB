import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  authApi,
  clearStoredToken,
  decodeToken,
  getStoredToken,
  isTokenExpired,
  saveStoredToken,
} from '../services/api';
import { AuthContext } from './auth-context';

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => {
    const storedToken = getStoredToken();

    if (storedToken && isTokenExpired(storedToken)) {
      clearStoredToken();
      return null;
    }

    return storedToken;
  });

  const user = useMemo(() => {
    const payload = decodeToken(token);

    if (!payload) {
      return null;
    }

    return {
      email: payload.sub,
      perfil: payload.perfil,
      exp: payload.exp,
    };
  }, [token]);

  const logout = useCallback(() => {
    clearStoredToken();
    setToken(null);
  }, []);

  useEffect(() => {
    const payload = decodeToken(token);

    if (!payload?.exp) {
      return undefined;
    }

    const delay = Math.max(payload.exp * 1000 - Date.now(), 0);
    const timeout = window.setTimeout(logout, delay);

    return () => window.clearTimeout(timeout);
  }, [logout, token]);

  const login = useCallback(async (credentials) => {
    const nextToken = await authApi.login(credentials);
    saveStoredToken(nextToken);
    setToken(nextToken);
    return nextToken;
  }, []);

  const register = useCallback(async (data) => {
    return authApi.register(data);
  }, []);

  const value = useMemo(() => ({
    token,
    user,
    isAuthenticated: Boolean(token),
    login,
    logout,
    register,
  }), [token, user, login, logout, register]);

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
