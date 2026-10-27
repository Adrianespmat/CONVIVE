# Convive

Aplicación web para gestionar viviendas compartidas, gastos comunes, repartos y balances económicos. Proyecto individual de CFGS DAW.

## Estado actual

La Fase 2 está implementada en el backend: existe un backend Spring Boot con entidades JPA, validaciones, autenticación BCrypt/JWT, rutas protegidas, API REST inicial y pruebas de integración. Todavía no se han implementado la gestión de pisos, gastos, reparto, balances, frontend ni despliegue integrado.

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

La prueba automática de autenticación verifica el registro, el hash BCrypt, el login, la validación JWT, el acceso protegido, los correos duplicados y las credenciales incorrectas.

## Documentación

La documentación de requisitos, casos de uso, arquitectura y diagramas se encuentra en `docs/`. El estado detallado del desarrollo está en `docs/ESTADO_DESARROLLO.md` y `TODO.md`.
