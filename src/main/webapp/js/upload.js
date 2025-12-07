document.getElementById('uploadBtn').addEventListener('click', async () => {
  const input = document.getElementById('file');
  if (!input.files.length) return alert('Choose file');
  const form = new FormData();
  form.append('file', input.files[0]);

  const res = await fetch('/upload', { method: 'POST', body: form });
  const text = await res.text();
  alert(text);
});

document.getElementById('loginForm').addEventListener('submit', async (ev) => {
  ev.preventDefault();
  const form = new FormData(ev.target);
  const params = new URLSearchParams();
  params.append('email', form.get('email'));
  params.append('password', form.get('password'));
  const res = await fetch('/login', { method: 'POST', body: params });
  alert(await res.text());
});
