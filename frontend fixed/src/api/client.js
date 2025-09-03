import axios from 'axios'
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'

export const api = axios.create({ baseURL })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  r => r,
  (error) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      if (!location.pathname.includes('/login')) location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  URL.revokeObjectURL(url)
  a.remove()
}

export function parseTrip(trip) {
  const iso = trip.departureTime || trip.departure_time || trip.startTime || trip.departure
  const dateIso = trip.date || trip.departureDate || (iso && String(iso).slice(0, 10))
  return {
    id: trip.id ?? trip.tripId ?? trip.trip_id ?? trip.uuid ?? trip.code,
    source: trip.source ?? trip.from ?? trip.origin ?? trip.route?.source ?? trip.route?.from,
    destination: trip.destination ?? trip.to ?? trip.target ?? trip.route?.destination ?? trip.route?.to,
    date: dateIso,
    time: trip.time ?? iso,
    price: trip.price ?? trip.fare ?? trip.ticketPrice ?? trip.cost,
    busName: trip.busName ?? trip.bus?.name ?? trip.bus?.busNumber ?? trip.bus?.number ?? 'Bus',
    availableSeats: trip.availableSeats ?? trip.freeSeats ?? trip.seatsAvailable ?? trip.availability ?? null,
  }
}

export function parseSeats(raw) {
  if (!raw) return []
  const arr = Array.isArray(raw) ? raw : (Array.isArray(raw?.seats) ? raw.seats : [])
  return arr.map((s, i) => {
    const id = s.id ?? s.seatId ?? (i + 1)
    const number = s.seatNumber ?? s.number ?? s.no ?? s.seatNo ?? s.seat ?? String(id)
    let available = true
    if (typeof s.booked === 'boolean')             available = !s.booked
    else if (typeof s.available === 'boolean')     available = s.available
    else if (typeof s.isAvailable === 'boolean')   available = s.isAvailable
    else if (typeof s.status === 'string')         available = s.status.toUpperCase() === 'AVAILABLE'
    return { id, number, available }
  })
}
