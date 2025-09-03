import { useParams, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { api } from '../api/client'
export default function Checkout(){
  const { bookingId } = useParams()
  const nav = useNavigate()
  const [method, setMethod] = useState('CARD')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(null)
  const pay = async () => {
    setLoading(true); setError('')
    try {
      const body = { bookingId: Number(bookingId), method }
      const { data } = await api.post('/payments/checkout', body)
      setSuccess(data)
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Payment failed')
    } finally {
      setLoading(false)
    }
  }
  return (
    <main className="mx-auto max-w-lg px-4 py-10">
      <div className="glass p-6">
        <h1 className="text-xl font-semibold mb-4">Checkout</h1>
        <div className="text-sm text-white/70 mb-4">Booking ID: <span className="font-mono">{bookingId}</span></div>
        {error && <div className="text-sm text-red-300 mb-3">{error}</div>}
        {success ? (
          <div className="space-y-4">
            <div className="text-emerald-300">Payment successful.</div>
            <div className="flex gap-2">
              {success?.ticketId && <button className="btn btn-primary" onClick={()=> nav('/ticket?ticketId='+success.ticketId)}>View Ticket</button>}
              <button className="btn btn-ghost" onClick={()=> nav('/')}>Home</button>
            </div>
          </div>
        ) : (
          <>
            <label className="text-sm">Payment method</label>
            <select value={method} onChange={e=>setMethod(e.target.value)} className="w-full mt-1 mb-4 px-3 py-2 bg-white/5 border border-white/10 rounded-lg">
              <option value="CARD">Card</option>
              <option value="UPI">UPI</option>
              <option value="NETBANKING">Netbanking</option>
            </select>
            <button onClick={pay} disabled={loading} className="btn btn-primary w-full">{loading ? 'Processing…' : 'Pay now'}</button>
          </>
        )}
      </div>
    </main>
  )
}