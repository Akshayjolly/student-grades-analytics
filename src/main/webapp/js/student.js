(async function () {
  await setupHeader();
  const r = await api('api/student/report');
  renderSummary(document.getElementById('summary'), r.overall);
  if (!r.overall) return;

  drawChart('moduleChart', 'bar', Object.keys(r.moduleAverages), Object.values(r.moduleAverages), 'Average');
  drawChart('distChart', 'bar', Object.keys(r.distribution), Object.values(r.distribution), 'Results');
  drawChart('trendChart', 'line', Object.keys(r.semesterAverages).map(s => 'Semester ' + s),
            Object.values(r.semesterAverages), 'Average');
  document.getElementById('prediction').textContent = r.predictedNextSemester == null
    ? 'Not enough semesters for a prediction yet.'
    : `Predicted next semester average (linear trend): ${r.predictedNextSemester}%`;

  document.getElementById('grades').innerHTML =
    '<tr><th>Semester</th><th>Module</th><th>Assessment</th><th>Score</th></tr>' +
    r.grades.map(g => `<tr><td>${g.semester}</td><td>${esc(g.moduleCode)}</td><td>${esc(g.assessment)}</td><td>${g.score}</td></tr>`).join('');
})();
