// Helpers that create (or replace) Chart.js charts.
const charts = {};

function drawChart(id, type, labels, data, label) {
  if (charts[id]) charts[id].destroy();
  charts[id] = new Chart(document.getElementById(id), {
    type,
    data: { labels, datasets: [{ label, data, backgroundColor: '#4a78c2', borderColor: '#1f3b73', tension: 0.2 }] },
    options: { plugins: { legend: { display: false } }, scales: type === 'line' || type === 'bar' ? { y: { beginAtZero: true } } : {} },
  });
}
