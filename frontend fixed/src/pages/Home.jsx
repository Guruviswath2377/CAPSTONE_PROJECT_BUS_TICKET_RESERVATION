import { Link } from 'react-router-dom'
export default function Home(){
  return (
    <main className="mx-auto max-w-6xl px-4 pt-12 pb-24">
      <section className="relative overflow-hidden rounded-2xl border border-white/10 p-10 bg-gradient-to-br from-brand-700/30 to-slate-900/50">
        <div className="max-w-2xl space-y-4">
          <h1 className="text-4xl sm:text-5xl font-bold leading-tight">Find your next ride with a futuristic touch</h1>
          <p className="text-white/80">Search, pick seats, pay, and get your e-ticket instantly. Admins can manage trips and pull reports.</p>
          <div className="flex flex-wrap gap-3 pt-2">
            <Link to="/search" className="btn btn-primary">Search trips</Link>
            <Link to="/login" className="btn btn-ghost">Sign in</Link>
          </div>
        </div>
        <div className="absolute -right-20 -top-20 w-96 h-96 bg-brand-500/30 blur-3xl rounded-full"></div>
      </section>
    </main>
  )
}