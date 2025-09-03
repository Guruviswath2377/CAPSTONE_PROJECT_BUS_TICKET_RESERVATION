export default function SeatGrid({ seats, selected, setSelected, columns=8 }) {
  const toggle = (id, available) => {
    if (!available) return
    if (selected.includes(id)) setSelected(selected.filter(s=>s!==id))
    else setSelected([...selected, id])
  }
  return (
    <div className="grid" style={{gridTemplateColumns:`repeat(${columns},minmax(0,1fr))`}}>
      {(seats||[]).map(({id, number, available}, idx)=>{
        const isSel = selected.includes(id)
        const cls = isSel
          ? 'bg-indigo-500/80 border-indigo-300'
          : available ? 'bg-white/10 border-white/20 hover:bg-white/20' : 'bg-red-500/30 border-red-400/40 cursor-not-allowed'
        return (
          <button key={id || number || idx} onClick={()=>toggle(id, available)}
            className={'m-1 h-10 rounded-lg border text-xs ' + cls}>
            {number}
          </button>
        )
      })}
    </div>
  )
}