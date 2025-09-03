import { Link, Outlet, NavLink } from 'react-router-dom'
export default function AdminDashboard(){
  const Tab = ({ to, children }) => (
    <NavLink to={to} className={({isActive})=>'px-3 py-2 rounded-lg hover:bg-white/10 ' + (isActive?'bg-white/10':'')}>{children}</NavLink>
  )
  return (
    <main className="mx-auto max-w-6xl px-4 py-8">
      <div className="glass p-5 mb-4 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Admin</h1>
          <div className="text-sm text-white/70">Manage trips, buses, routes, and reports</div>
        </div>
        <Link to="/" className="btn btn-ghost">Back</Link>
      </div>
      <div className="mb-4 flex gap-2">
        <Tab to="/admin/trips">Trips</Tab>
        <Tab to="/admin/buses-routes">Buses & Routes</Tab>
        <Tab to="/admin/reports">Reports</Tab>
      </div>
      <Outlet />
    </main>
  )
}