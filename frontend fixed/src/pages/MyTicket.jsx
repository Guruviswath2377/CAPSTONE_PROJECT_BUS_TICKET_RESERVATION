import { useState } from 'react'
import { api, downloadBlob } from '../api/client'
export default function MyTicket(){
  const url = new URL(window.location.href)
  const initId = url.searchParams.get('ticketId') || ''
  const [id, setId] = useState(initId)
  const [ticket, setTicket] = useState(null)
  const [error, setError] = useState('')
  const fetchTicket = async () => {
    setError('')
    try {
      const { data } = await api.get(`/tickets/${id}`)
      setTicket(data)
    } catch (e) {
      setError(e?.response?.data?.message || 'Failed to fetch ticket'); setTicket(null)
    }
  }
  const downloadPdf = async () => {
    try {
      const res = await api.get(`/tickets/${id}/pdf`, { responseType: 'blob' })
      downloadBlob(res.data, `ticket-${id}.pdf`)
    } catch (e) {
      setError('Failed to download PDF')
    }
  }
  return (
    <main className="mx-auto max-w-xl px-4 py-10">
      <div className="glass p-6 space-y-3">
        <h1 className="text-xl font-semibold">Find your ticket</h1>
        <div className="flex gap-2">
          <input placeholder="Ticket ID" value={id} onChange={e=>setId(e.target.value)} className="flex-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg" />
          <button onClick={fetchTicket} className="btn btn-primary">Fetch</button>
        </div>
        {error && <div className="text-sm text-red-300">{error}</div>}
        {ticket && (
          <div className="mt-4 text-sm">
            <div className="font-medium">Ticket #{id}</div>
            <pre className="mt-2 text-xs bg-black/30 p-3 rounded-lg overflow-auto">{JSON.stringify(ticket, null, 2)}</pre>
            <div className="mt-3 flex gap-2">
              <button onClick={downloadPdf} className="btn btn-ghost">Download PDF</button>
            </div>
          </div>
        )}
      </div>
    </main>
  )
}