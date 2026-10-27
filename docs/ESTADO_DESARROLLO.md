# Convive — Estado de desarrollo

**Fecha:** 2026-10-27  
**Fase:** 1 — Preparación y base de datos  
**Estado:** estructura y código base creados; compilación, empaquetado y prueba de mapeo JPA superados; conexión real con MySQL pendiente.

## Verificado en el espacio de trabajo

- El proyecto partía sin código Java, Vue, Maven, Dockerfiles ni Compose.
- Se ha creado `convive-backend/` con configuración Spring Boot, entidades JPA y repositorios.
- Se han creado `.env.example`, `.gitignore`, `docker-compose.yml`, `README.md`, `plan.md` y `TODO.md`.
- El modelo contempla usuarios, pisos, membresías, gastos y participantes.
- Los importes usan `BigDecimal` y restricciones de precisión en la entidad de gasto y el reparto.
- Las contraseñas se modelan como tieneh (`contrasenaHash`), pero BCrypt aún pertenece a la Fase 2.

## No verificado todavía

- Se ha ejecutado `mvn -B test` con JDK 17 y Maven 3.8.7.
- Resultado real: compilación correcta y `PruebaMapeoPersistenciaTest` superada, con 1 prueba, 0 fallos y 0 errores.
- Se ha ejecutado `mvn -B package -DskipTests` y se ha generado el JAR ejecutable en `convive-backend/target/`.
- No se ha iniciado MySQL porque Docker no está instalado.
- No se ha probado una conexión real con la base de datos.
- No existen todavía endpoints REST, autenticación, frontend ni pruebas de integración.

## Próximo paso

Instalar o utilizar un entorno con JDK 17, Maven y Docker/MySQL para probar la conexión real y el arranque contra MySQL. Después se puede iniciar la Fase 2 de registro, login, JWT, BCrypt y Spring Security.
