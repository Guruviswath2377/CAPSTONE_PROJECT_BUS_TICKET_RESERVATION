import { Routes, Route } from 'react-router-dom'
import NavBar from './components/NavBar'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import SearchTrips from './pages/SearchTrips'
import TripDetails from './pages/TripDetails'
import Checkout from './pages/Checkout'
import MyTicket from './pages/MyTicket'
import CancelBooking from './pages/CancelBooking'
import NotFound from './pages/NotFound'
import AdminDashboard from './pages/admin/Dashboard'
import TripsAdmin from './pages/admin/Trips'
import Reports from './pages/admin/Reports'
import BusesRoutes from './pages/admin/BusesRoutes'
import ProtectedRoute from './components/ProtectedRoute'
export default function App(){
  return (
    <div className="min-h-screen">
      <NavBar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/search" element={<SearchTrips />} />
        <Route path="/trips/:id" element={<TripDetails />} />
        <Route path="/checkout/:bookingId" element={<Checkout />} />
        <Route path="/ticket" element={<MyTicket />} />
        <Route path="/cancel" element={<CancelBooking />} />
        <Route path="/admin" element={<ProtectedRoute requireAdmin><AdminDashboard /></ProtectedRoute>}>
          <Route index element={<TripsAdmin />} />
          <Route path="trips" element={<TripsAdmin />} />
          <Route path="buses-routes" element={<BusesRoutes />} />
          <Route path="reports" element={<Reports />} />
        </Route>
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  )
}