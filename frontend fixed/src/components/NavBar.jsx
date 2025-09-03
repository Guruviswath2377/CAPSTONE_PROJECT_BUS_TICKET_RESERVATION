import { Link, NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Bus, Ticket, Search, LogIn, LogOut, LayoutDashboard } from 'lucide-react'
export default function NavBar() {
  const { isAuthed, role, logout } = useAuth()
  const Item = ({ to, children }) => (
    <NavLink to={to} className={({isActive})=>'px-3 py-2 rounded-lg hover:bg-white/10 ' + (isActive?'bg-white/10':'')}>{children}</NavLink>
  )
  return (
    <header className="sticky top-0 z-50 border-b border-white/10 bg-gradient-to-r from-slate-900/80 to-slate-800/60 backdrop-blur">
      <nav className="mx-auto max-w-6xl px-4 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2 font-semibold tracking-wide">
          <div className="w-9 h-9 rounded-xl bg-brand-500 flex items-center justify-center shadow-glass"><Bus size={18}/></div>
          <span className="hidden sm:block">NeoBus</span>
        </Link>
        <div className="flex items-center gap-1 text-sm">
          <Item to="/search"><div className="flex items-center gap-1"><Search size={16}/> Search</div></Item>
          <Item to="/ticket"><div className="flex items-center gap-1"><Ticket size={16}/> Ticket</div></Item>
          <Item to="/cancel">Cancel</Item>
          {isAuthed && role==='ADMIN' && <Item to="/admin"><div className="flex items-center gap-1"><LayoutDashboard size={16}/> Admin</div></Item>}
        </div>
        <div className="flex items-center gap-2">
          {!isAuthed ? (
            <>
              <Link to="/login" className="btn btn-ghost flex items-center gap-2"><LogIn size={16}/> Login</Link>
              <Link to="/register" className="btn btn-primary">Create account</Link>
            </>
          ) : (
            <button onClick={logout} className="btn btn-ghost flex items-center gap-2"><LogOut size={16}/> Logout</button>
          )}
        </div>
      </nav>
    </header>
  )
}