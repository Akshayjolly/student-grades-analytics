const moduleSelect = document.getElementById('module');

async function loadModule() {
  const r = await api('api/lecturer/module?code=' + encodeURIComponent(moduleSelect.value));
  const summary = document.getElementById('summary');
  renderSummary(summary, r.summary);
  if (!r.summary) return;
  summary.insertAdjacentHTML('beforeend', `<div class="stat">At risk (&lt;40)<b>${r.studentsAtRisk}</b></div>`);
  drawChart('distChart', 'bar', Object.keys(r.distribution), Object.values(r.distribution), 'Results');
  drawChart('trendChart', 'line', Object.keys(r.semesterAverages).map(s => 'Semester ' + s),
            Object.values(r.semesterAverages), 'Average');
}

(async function () {
  await setupHeader();
  const modules = await api('api/lecturer/modules');
  moduleSelect.innerHTML = modules.map(m => `<option>${esc(m)}</option>`).join('');
  moduleSelect.onchange = loadModule;

  const students = await api('api/lecturer/students');
  document.getElementById('students').innerHTML =
    students.map(s => `<option value="${s.id}">${esc(s.fullName)}</option>`).join('');
  await loadModule();
})();

document.getElementById('grade-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const f = new FormData(e.target);
  const msg = document.getElementById('form-msg');
  try {
    await api('api/lecturer/grades', {
      method: 'POST',
      body: JSON.stringify({
        studentId: Number(f.get('studentId')), moduleCode: moduleSelect.value,
        assessment: f.get('assessment'), semester: Number(f.get('semester')), score: Number(f.get('score')),
      }),
    });
    msg.textContent = 'Saved!';
    e.target.reset();
    await loadModule();
  } catch (err) {
    msg.textContent = 'Error: ' + err.message;
  }
});
