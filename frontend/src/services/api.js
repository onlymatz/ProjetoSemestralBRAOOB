const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8090';
const TOKEN_KEY = 'rankitup.token';

export class ApiError extends Error {
  constructor(message, status, payload) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.payload = payload;
  }
}

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function saveStoredToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearStoredToken() {
  localStorage.removeItem(TOKEN_KEY);
}

export function decodeToken(token) {
  if (!token) {
    return null;
  }

  try {
    const payload = token.split('.')[1];
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padding = '='.repeat((4 - (normalized.length % 4)) % 4);
    const decoded = atob(normalized + padding);
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}

export function isTokenExpired(token) {
  const decoded = decodeToken(token);

  if (!decoded?.exp) {
    return false;
  }

  return decoded.exp * 1000 <= Date.now();
}

export async function apiFetch(path, options = {}) {
  const token = getStoredToken();
  const headers = {
    Accept: 'application/json',
    ...(options.headers || {}),
  };

  let body = options.body;

  if (body !== undefined && !(body instanceof FormData)) {
    headers['Content-Type'] = headers['Content-Type'] || 'application/json';
    body = typeof body === 'string' ? body : JSON.stringify(body);
  }

  if (token && !headers.Authorization) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
    body,
  });

  const raw = await response.text();
  const contentType = response.headers.get('content-type') || '';
  let data = raw;

  if (raw && contentType.includes('application/json')) {
    try {
      data = JSON.parse(raw);
    } catch {
      data = raw;
    }
  }

  if (!response.ok) {
    const message = typeof data === 'string' && data
      ? data
      : data?.erro || data?.message || `Erro ${response.status} ao acessar a API.`;
    throw new ApiError(message, response.status, data);
  }

  return data || null;
}

export const authApi = {
  login: ({ email, senha }) => apiFetch('/api/usuarios/login', {
    method: 'POST',
    body: { email, senha },
  }),
  register: ({ email, senha, nome, nickname }) => apiFetch('/api/usuarios/cadastro', {
    method: 'POST',
    body: {
      email,
      senha,
      nome,
      nickname,
      perfil: 'ROLE_USER',
    },
  }),
};

export { API_BASE_URL };
