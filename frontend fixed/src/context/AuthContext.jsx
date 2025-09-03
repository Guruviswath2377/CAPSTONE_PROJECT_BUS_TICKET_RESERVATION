import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { api } from '../api/client'
import { jwtDecode } from 'jwt-decode'
const AuthCtx = createContext(null)
export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [role, setRole] = useState(localStorage.getItem('role') || 'GUEST')
  const [user, setUser] = useState(null)
  useEffect(()=>{
    if (token) {
      try {
        const decoded = jwtDecode(token)
        const roles = decoded?.roles || decoded?.scope || decoded?.authorities || decoded?.role
        const r = Array.isArray(roles) ? roles[0] : roles || 'USER'
        setRole(String(r).toUpperCase().includes('ADMIN') ? 'ADMIN' : 'USER')
      } catch {}
    }
  }, [token])
  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    const t = data?.token || data?.jwt || data?.accessToken || data?.access_token
    if (!t) throw new Error('No token returned from backend')
    localStorage.setItem('token', t)
    let r = data?.role || data?.user?.role || 'USER'
    try {
      const decoded = jwtDecode(t)
      const roles = decoded?.roles || decoded?.scope || decoded?.authorities || decoded?.role
      const inferred = Array.isArray(roles) ? roles[0] : roles
      r = inferred || r
    } catch {}
    const norm = String(r).toUpperCase().includes('ADMIN') ? 'ADMIN' : 'USER'
    localStorage.setItem('role', norm)
    setToken(t); setRole(norm)
    return norm
  }
  const register = async (payload) => (await api.post('/auth/register', payload)).data
  const logout = () => { localStorage.clear(); location.href = '/login' }
  const value = useMemo(()=>({ token, role, user, setUser, login, register, logout, isAuthed: !!token }), [token, role, user])
  return <AuthCtx.Provider value={value}>{children}</AuthCtx.Provider>
}
export const useAuth = () => useContext(AuthCtx)