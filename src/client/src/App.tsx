import React from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router'
import { AuthProvider } from './context/AuthContext'
import { Login } from './pages/Login'
import { AdminClients } from './pages/AdminClients'
import { useAuth } from './context/useAuth'
import { Register } from './pages/Register'
import { Dashboard } from './pages/Dashboard'
import { LangProvider } from './context/LangContext'
import { AdminTariffs } from './pages/AdminTariffs'
import { AdminPromotions } from './pages/AdminPromotions'

// Компонент защиты
const ProtectedRoute: React.FC<{ children: React.ReactNode; requiredRole?: string }> = ({ children, requiredRole }) => {
  const { user } = useAuth()

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (requiredRole && !user.roles.includes(requiredRole)) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

function AppContent() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route
          path="/admin/clients"
          element={
            <ProtectedRoute requiredRole="ROLE_ADMIN">
              <AdminClients />
            </ProtectedRoute>
          }
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute requiredRole="ROLE_CLIENT">
              <Dashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/tariffs"
          element={
            <ProtectedRoute requiredRole="ROLE_ADMIN">
              <AdminTariffs />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/promotions"
          element={
            <ProtectedRoute requiredRole="ROLE_ADMIN">
              <AdminPromotions />
            </ProtectedRoute>
          }
        />

        {/* редирект */}
        <Route path="*" element={<Navigate to="/login" replace />} />

      </Routes>
    </BrowserRouter>
  )
}

export default function App() {
  return (
    <LangProvider>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </LangProvider>
  )
}
