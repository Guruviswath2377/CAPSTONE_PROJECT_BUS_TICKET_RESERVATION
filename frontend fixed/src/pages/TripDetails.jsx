import { useEffect, useMemo, useState } from 'react'
import { useParams, useNavigate, useLocation } from 'react-router-dom'
import { api, parseSeats, parseTrip } from '../api/client'
import SeatGrid from '../components/SeatGrid'
import { useAuth } from '../context/AuthContext'
export default function TripDetails(){
  const { id } = useParams()
  const nav = useNavigate()
  const loc = useLocation()
  const { isAuthed } = useAuth()
  const [trip, setTrip] = useState(null)
  const [seats, setSeats] = useState([])
  const [selected, setSelected] = useState([])
  const [loading, setLoading] = useState(true)
  const [holdLoading, setHoldLoading] = useState(false)
  const [error, setError] = useState('')
  useEffect(()=>{
    const run = async () => {
      setLoading(true); setError('')
      try {
        const [t, s] = await Promise.all([
          api.get(`/trips/${id}`).catch(()=>({data:null})),
          api.get(`/trips/${id}/seats`)
        ])
        setTrip(t?.data || { id })
        setSeats(parseSeats(s?.data))
      } catch (e) {
        const st = e?.response?.status
        if (st === 401 || st === 403) { nav(`/login?next=${encodeURIComponent(loc.pathname)}`); return }
        setError(e?.response?.data?.message || 'Failed to load trip')
      } finally {
        setLoading(false)
      }
    }
    run()
  }, [id])
  const columns = useMemo(()=>{
    const layout = trip?.bus?.seatLayout || trip?.bus?.layout
    if (!layout) return 8
    const m = String(layout).match(/(\d+)\s*[*xX+]\s*(\d+)/)
    return m ? Number(m[1]) + Number(m[2]) : 8
  }, [trip])
  const holdSeats = async () => {
    if (!isAuthed) { nav(`/login?next=${encodeURIComponent(loc.pathname)}`); return }
    setHoldLoading(true); setError('')
    try {
      const body = { tripId: Number(id), seatIds: selected }
      const { data } = await api.post('/bookings/hold', body)
      const bookingId = data?.id || data?.bookingId || data?.booking?.id || data
      if (!bookingId) throw new Error('No bookingId returned')
      nav(`/checkout/${bookingId}`)
    } catch (e) {
      const st = e?.response?.status
      if (st===401 || st===403) { nav(`/login?next=${encodeURIComponent(loc.pathname)}`); return }
      setError(e?.response?.data?.message || e?.message || 'Hold failed')
    } finally {
      setHoldLoading(false)
    }
  }
  if (loading) return <main className="p-8">Loading…</main>
  const info = parseTrip(trip || {})
  return (
    <main className="mx-auto max-w-6xl px-4 py-8 space-y-6">
      <div className="glass p-5 flex items-center justify-between">
        <div>
          <div className="text-lg font-semibold">Trip #{info.id}</div>
          <div className="text-white/70 text-sm">{info.source} → {info.destination} · {info.date || info.time}</div>
        </div>
        <div className="text-right">
          <div className="text-2xl font-bold">₹{info.price ?? '--'}</div>
        </div>
      </div>
      {error && <div className="text-sm text-red-300">{error}</div>}
      <div className="glass p-5">
        <h2 className="font-semibold mb-3">Pick your seats</h2>
        <SeatGrid seats={seats} selected={selected} setSelected={setSelected} columns={columns} />
        <div className="flex items-center justify-between mt-4">
          <div className="text-sm text-white/70">{selected.length} selected</div>
          <button disabled={selected.length===0 || holdLoading} onClick={holdSeats} className="btn btn-primary">
            {holdLoading ? 'Holding…' : 'Hold & Continue'}
          </button>
        </div>
      </div>
    </main>
  )
}