import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import TripCard from '../../components/TripCard'

export default function TripsAdmin() {
  const [trips, setTrips] = useState([])
  const [error, setError] = useState('')
  const [form, setForm] = useState({ busId: '', routeId: '', departureTime: '', price: '' })
  const [loading, setLoading] = useState(false)
  const [lists, setLists] = useState({ buses: [], routes: [] })

  const load = async () => {
    try {
      const [t, b, r] = await Promise.all([api.get('/trips'), api.get('/buses'), api.get('/routes')])
      setTrips(Array.isArray(t.data) ? t.data : (t.data?.content || []))
      setLists({ buses: b.data || [], routes: r.data || [] })
    } catch (e) {
      setError(e?.response?.data?.message || 'Failed to load')
    }
  }

  useEffect(() => { load() }, [])
const cleanCity = (s) => (s || '').trim();
const toIsoDateParam = (s) => {
  if (!s) return '';
  if (/^\d{4}-\d{2}-\d{2}$/.test(s)) return s;
  const m = s.match(/^(\d{2})[-/](\d{2})[-/](\d{4})$/);
  if (m) return `${m[3]}-${m[2]}-${m[1]}`;
  const d = new Date(s);
  return isNaN(d) ? '' : d.toISOString().slice(0, 10);
};
const onSearch = async (e) => {
  e.preventDefault();
  setError('');
  setLoading(true);
  try {
    const source = cleanCity(form.source);
    const destination = cleanCity(form.destination);
    const date = toIsoDateParam(form.date); 

    if (!source || !destination || !date) {
      throw new Error('Please fill From, To and Date');
    }

    const { data } = await api.get('/trips/search', { params: { source, destination, date } });
    setTrips(Array.isArray(data) ? data : (data?.content || []));
  } catch (e) {
    setTrips([]);
    setError(e?.response?.data?.message || `Search failed (${e?.response?.status || 'network'})`);
  } finally {
    setLoading(false);
  }
};

  const createTrip = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      if (!form.busId || !form.routeId || !form.departureTime || !form.price) {
        throw new Error('Please fill all fields')
      }

      const depLocal = new Date(form.departureTime)
      if (isNaN(depLocal.getTime())) throw new Error('Invalid departure time')
      const departureTime = depLocal.toISOString()

      const ARRIVAL_HOURS = 6
      const arrivalTime = new Date(depLocal.getTime() + ARRIVAL_HOURS * 60 * 60 * 1000).toISOString()

      const body = {
        busId: Number(form.busId),
        routeId: Number(form.routeId),
        departureTime,
        arrivalTime,
        fare: Number(form.price)
      }

      await api.post('/trips', body)
      setForm({ busId: '', routeId: '', departureTime: '', price: '' })
      await load()
    } catch (e) {
      setError(e?.response?.data?.message || e?.message || 'Failed to create trip')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="grid lg:grid-cols-5 gap-6">
      <div className="lg:col-span-3 space-y-3">
        {trips.map((t, i) => <TripCard key={i} data={t} />)}
        {trips.length === 0 && <div className="text-white/70">No trips yet</div>}
      </div>

      <div className="lg:col-span-2">
        <div className="glass p-5">
          <h2 className="font-semibold mb-3">Create new trip</h2>
          {error && <div className="text-sm text-red-300 mb-2">{error}</div>}

          <form onSubmit={createTrip} className="space-y-3">
            <div>
              <label className="text-sm">Bus</label>
              <select
                value={form.busId}
                onChange={e => setForm({ ...form, busId: e.target.value })}
                className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg"
                required
              >
                <option value="">Select bus</option>
                {lists.buses.map(b =>
                  <option key={b.id || b.busId} value={b.id || b.busId}>
                    {b.name || b.number || b.busNumber || 'Bus'}
                  </option>
                )}
              </select>
            </div>

            <div>
              <label className="text-sm">Route</label>
              <select
                value={form.routeId}
                onChange={e => setForm({ ...form, routeId: e.target.value })}
                className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg"
                required
              >
                <option value="">Select route</option>
                {lists.routes.map(r =>
                  <option key={r.id || r.routeId} value={r.id || r.routeId}>
                    {(r.source || r.from)} → {(r.destination || r.to)}
                  </option>
                )}
              </select>
            </div>

            <div>
              <label className="text-sm">Departure time</label>
              <input
                type="datetime-local"
                value={form.departureTime}
                onChange={e => setForm({ ...form, departureTime: e.target.value })}
                className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg"
                required
              />
            </div>

            <div>
              <label className="text-sm">Fare</label>
              <input
                type="number"
                value={form.price}
                onChange={e => setForm({ ...form, price: e.target.value })}
                className="w-full mt-1 px-3 py-2 bg-white/5 border border-white/10 rounded-lg"
                min="0"
                step="1"
                required
              />
            </div>

            <button disabled={loading} className="btn btn-primary w-full">
              {loading ? 'Creating…' : 'Create trip'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
