import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { useAuth } from './context/useAuth';
import Header from './components/Header/Header';
import Dashboard from './pages/Dashboard/Dashboard';
import Games from './pages/Games/Games';
import Leaderboards from './pages/Leaderboards/Leaderboards';
import Login from './pages/Login/Login';
import Matches from './pages/Matches/Matches';
import Perfil from './pages/Perfil/Perfil';
import Register from './pages/Register/Register';
import Studio from './pages/Studio/Studio';
import Tournaments from './pages/Tournaments/Tournaments';
import './App.css';

function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

function AppLayout({ children }) {
  return (
    <>
      <Header />
      <main className="page-shell">{children}</main>
    </>
  );
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Register />} />
      <Route path="/register" element={<Navigate to="/cadastro" replace />} />
      <Route
        path="/dashboard"
        element={(
          <ProtectedRoute>
            <AppLayout><Dashboard /></AppLayout>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/leaderboards"
        element={(
          <ProtectedRoute>
            <AppLayout><Leaderboards /></AppLayout>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/games"
        element={(
          <ProtectedRoute>
            <AppLayout><Games /></AppLayout>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/matches"
        element={(
          <ProtectedRoute>
            <AppLayout><Matches /></AppLayout>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/tournaments"
        element={(
          <ProtectedRoute>
            <AppLayout><Tournaments /></AppLayout>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/perfil"
        element={(
          <ProtectedRoute>
            <AppLayout><Perfil /></AppLayout>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/studio"
        element={(
          <ProtectedRoute>
            <AppLayout><Studio /></AppLayout>
          </ProtectedRoute>
        )}
      />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="app-shell">
          <div className="parallax-layer parallax-grid" />
          <div className="parallax-layer parallax-art" />
          <AppRoutes />
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
