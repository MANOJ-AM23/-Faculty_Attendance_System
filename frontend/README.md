Frontend static assets

- Files are under `frontend/static/` (HTML + CSS). These are the client-side pages.
- During development you can serve the `frontend/static` folder using a simple HTTP server. Example (Python):

```powershell
python -m http.server 3000 --directory frontend/static
```

The frontend expects backend API endpoints to be available at the same host (e.g., `/api/*`) or you can configure a reverse proxy.
