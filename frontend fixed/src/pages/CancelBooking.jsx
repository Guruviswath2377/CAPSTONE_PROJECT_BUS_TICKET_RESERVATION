import { useState } from 'react'
import { api } from '../api/client'
export default function CancelBooking(){
  const [id, setId] = useState('')
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')
const cancel = async () => {
  setMsg(''); setError('');
  try {
    await api.delete(`/tickets/${id}`);
    setMsg('Cancelled');
  } catch (e) {
    setError(e?.response?.data?.message || 'Cancel failed');
  }
};
  return (
    <main className="mx-auto max-w-xl px-4 py-10">
      <div className="glass p-6 space-y-3">
        <h1 className="text-xl font-semibold">Cancel booking</h1>
        <div className="flex gap-2">
          <input placeholder="Booking ID" value={id} onChange={e=>setId(e.target.value)} className="flex-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg" />
          <button onClick={cancel} className="btn btn-primary">Cancel</button>
        </div>
        {msg && <div className="text-sm text-emerald-300">{msg}</div>}
        {error && <div className="text-sm text-red-300">{error}</div>}
      </div>
    </main>
  )
}