import { useEffect, useState } from 'react'
import { api, downloadBlob } from '../../api/client'
export default function Reports(){
  const [payments, setPayments] = useState([])
  const [bookings, setBookings] = useState([])
  const [error, setError] = useState('')
  const load = async ()=>{
    try {
      const [p,b] = await Promise.all([ api.get('/reports/payments'), api.get('/reports/bookings') ])
      setPayments(p.data || []); setBookings(b.data || [])
    } catch (e) { setError(e?.response?.data?.message || 'Failed to load reports') }
  }
  useEffect(()=>{ load() }, [])
  const download = async (kind) => {
    const path = kind === 'payments' ? '/reports/payments/pdf' : '/reports/bookings/pdf'
    const res = await api.get(path, { responseType: 'blob' })
    downloadBlob(res.data, `${kind}-report.pdf`)
  }
  return (
    <div className="space-y-6">
      {error && <div className="text-sm text-red-300">{error}</div>}
      <div className="glass p-5">
        <div className="flex items-center justify-between">
          <h2 className="font-semibold">Payments</h2>
          <button onClick={()=>download('payments')} className="btn btn-ghost">Download PDF</button>
        </div>
        <div className="mt-3 overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-white/70"><tr><th className="text-left p-2">ID</th><th className="text-left p-2">Booking</th><th className="text-left p-2">Amount</th><th className="text-left p-2">Method</th></tr></thead>
            <tbody>
              {payments.map((p,i)=>(
                <tr key={i} className="border-t border-white/10">
                  <td className="p-2">{p.id || p.paymentId}</td>
                  <td className="p-2">{p.bookingId || p.booking?.id}</td>
                  <td className="p-2">₹{p.amount || p.total}</td>
                  <td className="p-2">{p.method || p.mode}</td>
                </tr>
              ))}
              {payments.length===0 && <tr><td className="p-2 text-white/60" colSpan="4">No data</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
      <div className="glass p-5">
        <div className="flex items-center justify-between">
          <h2 className="font-semibold">Bookings</h2>
          <button onClick={()=>download('bookings')} className="btn btn-ghost">Download PDF</button>
        </div>
        <div className="mt-3 overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="text-white/70"><tr><th className="text-left p-2">ID</th><th className="text-left p-2">Trip</th><th className="text-left p-2">Seats</th><th className="text-left p-2">Status</th></tr></thead>
            <tbody>
              {bookings.map((b,i)=>(
                <tr key={i} className="border-t border-white/10">
                  <td className="p-2">{b.id || b.bookingId}</td>
                  <td className="p-2">{b.tripId || b.trip?.id}</td>
                  <td className="p-2">{Array.isArray(b.seats) ? b.seats.join(', ') : b.seatNumbers || '-'}</td>
                  <td className="p-2">{b.status || b.state}</td>
                </tr>
              ))}
              {bookings.length===0 && <tr><td className="p-2 text-white/60" colSpan="4">No data</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}