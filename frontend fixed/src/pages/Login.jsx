import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
export default function Login(){
  const nav = useNavigate()
  const [sp] = useSearchParams()
  const { login } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const onSubmit = async (e)=>{
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const role = await login(email, password)
      const next = sp.get('next')
      if (next) nav(next)
      else nav(role === 'ADMIN' ? '/admin' : '/')
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }
  return (
    <main className="mx-auto max-w-md px-4 py-12">
      <div className="glass p-8">
        <h1 className="text-2xl font-semibold mb-6">Welcome back</h1>
        {error && <div className="mb-4 text-sm text-red-300">{error}</div>}
        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="text-sm">Email</label>
            <input type="email" value={email} onChange={e=>setEmail(e.target.value)} className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg outline-none focus:ring-2 focus:ring-brand-500" required />
          </div>
          <div>
            <label className="text-sm">Password</label>
            <input type="password" value={password} onChange={e=>setPassword(e.target.value)} className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg outline-none focus:ring-2 focus:ring-brand-500" required />
          </div>
          <button disabled={loading} className="btn btn-primary w-full">{loading ? 'Signing in…' : 'Sign in'}</button>
        </form>
        <div className="text-sm mt-4 text-white/70">No account? <Link to="/register" className="underline">Create one</Link></div>
      </div>
    </main>
  )
}