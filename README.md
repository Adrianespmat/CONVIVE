# Convive

Aplicación web para gestionar viviendas compartidas, gastos comunes, repartos y balances económicos. Proyecto individual de CFGS DAW.

## Estado actual

La Fase 1 está implementada a nivel de estructura y modelo de persistencia: existe un backend Spring Boot con entidades JPA, validaciones, relaciones y repositorios, además de la configuración de MySQL. El backend ha compilado con JDK 17 y la prueba de mapeo JPA con H2 ha pasado. Todavía no se han implementado autenticación, API REST, frontend, reparto, balances ni despliegue integrado.

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

No existen todavía endpoints funcionales que probar. La Fase 2 añadirá registro, login, JWT, BCrypt y autorización.

## Documentación

La documentación de requisitos, casos de uso, arquitectura y diagramas se encuentra en `docs/`. El estado detallado del desarrollo está en `docs/ESTADO_DESARROLLO.md` y `TODO.md`.
