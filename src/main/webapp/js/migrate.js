document.getElementById('migrateBtn').addEventListener('click', async () => {
  const res = await fetch('/migrate', { method: 'POST' });
  const txt = await res.text();
  document.getElementById('out').textContent = txt;
});
