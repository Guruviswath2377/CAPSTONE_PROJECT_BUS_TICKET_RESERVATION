import { useEffect, useState } from 'react'
import { api } from '../../api/client'
export default function BusesRoutes(){
  const [buses, setBuses] = useState([])
  const [routes, setRoutes] = useState([])
  const [error, setError] = useState('')
  const load = async ()=>{
    try {
      const [b, r] = await Promise.all([ api.get('/buses'), api.get('/routes') ])
      setBuses(b.data || []); setRoutes(r.data || [])
    } catch (e) { setError(e?.response?.data?.message || 'Failed to load') }
  }
  useEffect(()=>{ load() }, [])
  return (
    <div className="grid md:grid-cols-2 gap-6">
      <div className="glass p-5">
        <h2 className="font-semibold">Buses</h2>
        <div className="mt-3 space-y-2 text-sm">
          {buses.map((b,i)=>(
            <div key={i} className="flex justify-between border border-white/10 rounded-lg p-2">
              <div>{b.name || b.number || b.busNumber || 'Bus'} <span className="text-white/60">#{b.id || b.busId}</span></div>
              {b.capacity && <div className="text-white/70">{b.capacity} seats</div>}
            </div>
          ))}
          {buses.length===0 && <div className="text-white/60">No buses</div>}
        </div>
      </div>
      <div className="glass p-5">
        <h2 className="font-semibold">Routes</h2>
        <div className="mt-3 space-y-2 text-sm">
          {routes.map((r,i)=>(
            <div key={i} className="flex justify-between border border-white/10 rounded-lg p-2">
              <div>{r.source || r.from} → {r.destination || r.to} <span className="text-white/60">#{r.id || r.routeId}</span></div>
              {r.distance && <div className="text-white/70">{r.distance} km</div>}
            </div>
          ))}
          {routes.length===0 && <div className="text-white/60">No routes</div>}
        </div>
      </div>
      {error && <div className="text-sm text-red-300">{error}</div>}
    </div>
  )
}