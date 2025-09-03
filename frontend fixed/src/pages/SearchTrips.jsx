import { useState } from 'react'
import { api } from '../api/client'
import TripCard from '../components/TripCard'
import { useNavigate } from 'react-router-dom'

const normalizeCity = (s) => {
  const map = {
    bengaluru: 'Bengaluru',
    bangalore: 'Bengaluru',
    mumbai: 'Mumbai',
    bombay: 'Mumbai',
    chennai: 'Chennai',
    madras: 'Chennai',
  }
  const k = (s || '').trim().toLowerCase()
  return map[k] || (s || '').trim()
}

const dateOnly = (s) => {
  if (!s) return ''
  if (/^\d{4}-\d{2}-\d{2}$/.test(s)) return s
  try {
    if (typeof s === 'string' && s.includes('T')) return s.slice(0, 10)
    const d = new Date(s)
    if (!isNaN(d)) {
      const yyyy = d.getFullYear()
      const mm = String(d.getMonth() + 1).padStart(2, '0')
      const dd = String(d.getDate()).padStart(2, '0')
      return `${yyyy}-${mm}-${dd}`
    }
  } catch {}
  return s
}

export default function SearchTrips() {
  const nav = useNavigate()
  const [form, setForm] = useState({ source: '', destination: '', date: '' })
  const [trips, setTrips] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const onSearch = async (e) => {
    e?.preventDefault()
    setLoading(true); setError('')
    try {
      const from = normalizeCity(form.source)
      const to   = normalizeCity(form.destination)
      const dt   = dateOnly(form.date) 

      const { data } = await api.get('/trips/search', {
        params: { source: from, destination: to, date: dt }
      })
      setTrips(Array.isArray(data) ? data : (data?.content || []))
    } catch (e) {
      setError(e?.response?.data?.message || `Search failed (${e?.response?.status || 'network'})`)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="mx-auto max-w-6xl px-4 py-8">
      <div className="glass p-5 mb-6">
        <form onSubmit={onSearch} className="grid sm:grid-cols-4 gap-3">
          <input
            placeholder="From"
            value={form.source}
            onChange={e=>setForm({...form, source: e.target.value})}
            className="px-3 py-2 rounded-lg bg-white/5 border border-white/10 outline-none focus:ring-2 focus:ring-brand-500"
          />
          <input
            placeholder="To"
            value={form.destination}
            onChange={e=>setForm({...form, destination: e.target.value})}
            className="px-3 py-2 rounded-lg bg-white/5 border border-white/10 outline-none focus:ring-2 focus:ring-brand-500"
          />
          <input
            type="date"
            value={form.date}
            onChange={e=>setForm({...form, date: e.target.value})}
            className="px-3 py-2 rounded-lg bg-white/5 border border-white/10 outline-none focus:ring-2 focus:ring-brand-500"
          />
          <button className="btn btn-primary" disabled={loading}>
            {loading ? 'Searching…' : 'Search'}
          </button>
        </form>
        {error && <div className="text-sm text-red-300 mt-2">{error}</div>}
      </div>

      <div className="space-y-3">
        {trips.length === 0 && !loading && (
          <div className="text-white/70">No trips yet. Try a different query.</div>
        )}
        {trips.map((t,i)=> (
          <TripCard
            key={t.id ?? i}
            data={t}
            onOpen={()=> nav(`/trips/${t.id ?? t.tripId ?? i}`)}
          />
        ))}
      </div>
    </main>
  )
}
