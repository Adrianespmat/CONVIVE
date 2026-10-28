# Convive

Aplicación web para gestionar viviendas compartidas, gastos comunes, repartos y balances económicos. Proyecto individual de CFGS DAW.

## Estado actual

La Fase 7 está implementada en el backend: existe un backend Spring Boot con entidades JPA, validaciones, autenticación BCrypt/JWT, gestión de pisos, membresías, gastos, reparto exacto de céntimos, balances, liquidación informativa, perfil, panel principal, rutas protegidas y pruebas de integración. Todavía no se han implementado el frontend ni el despliegue integrado.

La conexión real con MySQL está pendiente porque Docker no está instalado en este entorno. No se afirma que esa conexión esté probada.

## Requisitos previstos

- Java 17 y Maven.
- MySQL 8.4 o Docker Compose.
- Node.js se necesitará al crear el frontend.

## Configuración local

1. Copiar `.env.example` a `.env` y sustituir los valores locales.
2. Arrancar MySQL con `docker compose up -d mysql` cuando Docker esté instalado.
3. Ejecutar el backend desde `convive-backend/` con Maven cuando Maven esté disponible.

En Windows PowerShell, los comandos equivalentes serán:

```powershell
Copy-Item .env.example .env
docker compose up -d mysql
cd convive-backend
mvn spring-boot:run
```

Endpoints de autenticación disponibles:

- `POST /api/autenticacion/registro`
- `POST /api/autenticacion/login`
- `GET /api/autenticacion/yo` con `Authorization: Bearer <JWT>`

Endpoints de pisos y membresías disponibles:

- `POST /api/pisos`
- `POST /api/pisos/unirse`
- `GET /api/pisos/mio`
- `PATCH /api/pisos/mio`
- `GET /api/pisos/mio/integrantes`
- `DELETE /api/pisos/mio/integrantes/{usuarioId}`

Endpoints de gastos disponibles:

- `POST /api/gastos`
- `GET /api/gastos?desde=&hasta=&categoria=`
- `GET /api/gastos/{gastoId}`
- `PUT /api/gastos/{gastoId}`
- `DELETE /api/gastos/{gastoId}`

El reparto se realiza en céntimos con `BigDecimal`, ordenando los participantes por identificador y distribuyendo los céntimos sobrantes de forma determinista.

Endpoints de balances y liquidación disponibles:

- `GET /api/balances?desde=&hasta=`
- `GET /api/balances/liquidacion?desde=&hasta=`

La liquidación es únicamente informativa: propone pagos entre deudores y acreedores, pero no ejecuta transferencias bancarias. El alias `/api/balances/settlement` también está disponible para compatibilidad con el diseño inicial.

Endpoints de perfil y panel:

- `GET /api/users/me`
- `PATCH /api/users/me` con `{ "nombre": "Nuevo nombre" }`.
- `GET /api/dashboard?desde=&hasta=`

El correo electrónico del perfil no se modifica. El panel devuelve el total del periodo, los cinco gastos más recientes y el balance personal.

La prueba automática de autenticación verifica el registro, el hash BCrypt, el login, la validación JWT, el acceso protegido, los correos duplicados y las credenciales incorrectas.

## Documentación

La documentación de requisitos, casos de uso, arquitectura y diagramas se encuentra en `docs/`. El estado detallado del desarrollo está en `docs/ESTADO_DESARROLLO.md` y `TODO.md`.
