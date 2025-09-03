import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate, Link } from 'react-router-dom'
export default function Register(){
  const nav = useNavigate()
  const { register } = useAuth()
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [loading, setLoading] = useState(false)
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')
  const onSubmit = async (e)=>{
    e.preventDefault()
    setLoading(true); setError(''); setMsg('')
    try {
      await register(form)
      setMsg('Registered. You can now sign in.')
      setTimeout(()=> nav('/login'), 800)
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }
  return (
    <main className="mx-auto max-w-md px-4 py-12">
      <div className="glass p-8">
        <h1 className="text-2xl font-semibold mb-6">Create account</h1>
        {msg && <div className="mb-4 text-sm text-emerald-300">{msg}</div>}
        {error && <div className="mb-4 text-sm text-red-300">{error}</div>}
        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="text-sm">Full name</label>
            <input value={form.name} onChange={e=>setForm({...form, name:e.target.value})} className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg outline-none focus:ring-2 focus:ring-brand-500" required />
          </div>
          <div>
            <label className="text-sm">Email</label>
            <input type="email" value={form.email} onChange={e=>setForm({...form, email:e.target.value})} className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg outline-none focus:ring-2 focus:ring-brand-500" required />
          </div>
          <div>
            <label className="text-sm">Password</label>
            <input type="password" value={form.password} onChange={e=>setForm({...form, password:e.target.value})} className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg outline-none focus:ring-2 focus:ring-brand-500" required />
          </div>
          <button disabled={loading} className="btn btn-primary w-full">{loading ? 'Creating…' : 'Create account'}</button>
        </form>
        <div className="text-sm mt-4 text-white/70">Already have an account? <Link to="/login" className="underline">Sign in</Link></div>
      </div>
    </main>
  )
}