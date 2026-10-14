// Small wrapper around fetch() for calling the JSON API.
async function api(path, options = {}) {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json' },
    credentials: 'same-origin',
    ...options,
  });
  const data = await res.json().catch(() => ({}));
  if (res.status === 401 && !path.endsWith('/login')) {
    window.location.href = 'index.html'; // session expired -> back to login
  }
  if (!res.ok) throw new Error(data.error || res.statusText);
  return data;
}

// Shows the user's name in the header and wires up the logout button.
async function setupHeader() {
  const me = await api('api/me');
  document.getElementById('who').textContent = `${me.fullName} (${me.role.toLowerCase()})`;
  document.getElementById('logout').onclick = async () => {
    await api('api/logout', { method: 'POST' });
    window.location.href = 'index.html';
  };
  return me;
}

// Escapes text before putting it in HTML (prevents XSS).
function esc(text) {
  const div = document.createElement('div');
  div.textContent = String(text);
  return div.innerHTML;
}

function renderSummary(el, s) {
  if (!s) { el.innerHTML = '<p>No grades yet.</p>'; return; }
  const items = [['Mean', s.mean], ['Median', s.median], ['Std dev', s.stdDev], ['Min', s.min], ['Max', s.max], ['Results', s.count]];
  el.innerHTML = items.map(([k, v]) => `<div class="stat">${k}<b>${esc(v)}</b></div>`).join('');
}
