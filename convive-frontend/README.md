# Convive Frontend

Frontend local de Convive construido con Vue 3, JavaScript, Vite, Bootstrap, Bootstrap Icons, Vue Router y Axios.

## Desarrollo

Desde este directorio:

```bash
npm install
npm run dev
```

La aplicación se sirve en `http://localhost:5173` y redirige las peticiones `/api` al backend Spring Boot en `http://localhost:8080` mediante Vite.

## Build

```bash
npm run build
npm run preview
```

## Estructura

- `src/pages/`: pantallas de autenticación, panel, piso, gastos, balances y perfil.
- `src/layouts/`: layout protegido y navegación responsive.
- `src/services/`: cliente Axios y operaciones de la API.
- `src/stores/`: sesión JWT y usuario autenticado.
- `src/router/`: rutas públicas y privadas.
- `src/styles/`: Bootstrap y estilos propios de Convive.
- `public/manus-routes.json`: manifiesto de rutas de la aplicación.

El frontend es local y no utiliza servicios gestionados por Manus. El token JWT se guarda en `localStorage` para conectar las rutas privadas con el backend.
