import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
export default function ProtectedRoute({ children, requireAdmin=false }) {
  const { isAuthed, role } = useAuth()
  if (!isAuthed) return <Navigate to="/login" replace />
  if (requireAdmin && role !== 'ADMIN') return <Navigate to="/" replace />
  return children
}