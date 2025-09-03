NeoBus Frontend v2 – tolerant search + fallbacks.

- Search now sends multiple param aliases and falls back to GET /trips + client-side filtering.
- Admin create trip sends { price, fare } so either field works.
- Trip details seat hold sends multiple seat field names.

Quick start:
1) copy .env.example .env and set VITE_API_BASE_URL to your backend
2) npm install
3) npm run dev
