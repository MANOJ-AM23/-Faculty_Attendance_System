// UI helpers: sidebar toggle, toast, filter, modal, password toggle
function toggleSidebar(){ const sb = document.getElementById('sidebar'); if(!sb) return; sb.classList.toggle('hidden'); }

function showToast(msg, type){ let t = document.getElementById('toast'); if(!t){ t = document.createElement('div'); t.id='toast'; document.body.appendChild(t); Object.assign(t.style,{position:'fixed',right:'16px',bottom:'18px',padding:'10px 14px',borderRadius:'10px',color:'#fff',zIndex:1000,transition:'all .18s ease',opacity:0}); }
  t.textContent = msg; if(type==='error') t.style.background='#ef4444'; else if(type==='info') t.style.background='#374151'; else t.style.background='#111827'; t.style.opacity='1'; clearTimeout(t._hide); t._hide=setTimeout(()=>{ t.style.opacity='0'; },3500);
}

function filterTable(tableId, q){ const t=document.getElementById(tableId); if(!t) return; const rows=t.querySelectorAll('tbody tr'); const s=(q||'').toLowerCase(); rows.forEach(r=>{ r.style.display = Array.from(r.cells).some(c=>c.textContent.toLowerCase().includes(s))? 'table-row':'none'; }); }

function togglePassword(inputId, btnId){ const inp=document.getElementById(inputId); const btn=document.getElementById(btnId); if(!inp||!btn) return; if(inp.type==='password'){ inp.type='text'; btn.innerText='Hide'; } else { inp.type='password'; btn.innerText='Show'; } }

// Modal helpers
function openModal(onConfirm){ const m=document.getElementById('confirmModal'); if(!m) return; m.classList.remove('hidden'); m.classList.add('flex'); const btn=document.getElementById('confirmBtn'); btn.onclick = ()=>{ onConfirm(); }; }
function closeModal(){ const m=document.getElementById('confirmModal'); if(!m) return; m.classList.add('hidden'); m.classList.remove('flex'); }

function scrollToSection(id){ const el=document.getElementById(id); if(el) el.scrollIntoView({behavior:'smooth',block:'start'}); }

function escapeHtml(s){ return String(s||'').replace(/[&<>"']/g,function(c){return{'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"}[c];}); }

window.addEventListener('resize', ()=>{ const sb=document.getElementById('sidebar'); if(!sb) return; if(window.innerWidth<900) sb.classList.add('hidden'); else sb.classList.remove('hidden'); });
document.addEventListener('DOMContentLoaded', ()=>{ if(window.innerWidth<900){ const sb=document.getElementById('sidebar'); if(sb) sb.classList.add('hidden'); } });
