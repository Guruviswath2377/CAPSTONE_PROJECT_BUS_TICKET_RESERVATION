import { parseTrip } from '../api/client'
export default function TripCard({ data, onOpen }) {
  const t = parseTrip(data)
  return (
    <div className="glass p-4 flex items-center justify-between">
      <div className="space-y-1">
        <div className="text-lg font-semibold">{t.source} → {t.destination}</div>
        <div className="text-sm text-white/70">{t.date || t.time} · {t.busName}</div>
      </div>
      <div className="flex items-center gap-6">
        <div className="text-right">
          <div className="text-2xl font-bold">₹{t.price ?? '--'}</div>
          {t.availableSeats != null && <div className="text-xs text-white/60">{t.availableSeats} seats left</div>}
        </div>
        {onOpen && <button className="btn btn-primary" onClick={onOpen}>View</button>}
      </div>
    </div>
  )
}