document.getElementById('login-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const form = new FormData(e.target);
  try {
    const user = await api('api/login', {
      method: 'POST',
      body: JSON.stringify({ username: form.get('username'), password: form.get('password') }),
    });
    window.location.href = user.role === 'LECTURER' ? 'lecturer.html' : 'student.html';
  } catch (err) {
    document.getElementById('error').textContent = err.message;
  }
});
