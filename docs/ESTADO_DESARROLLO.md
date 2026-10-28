# Convive — Estado de desarrollo

**Fecha:** 2026-10-28
**Fase:** 5 — Balances y liquidación informativa
**Estado:** registro, BCrypt, login, JWT, pisos, membresías, gastos, reparto exacto, balances, liquidación informativa, rutas protegidas y tratamiento de errores implementados y probados; conexión real con MySQL pendiente.

## Verificado en el espacio de trabajo

- El proyecto partía sin código Java, Vue, Maven, Dockerfiles ni Compose.
- Se ha creado `convive-backend/` con configuración Spring Boot, entidades JPA y repositorios.
- Se han creado `.env.example`, `.gitignore`, `docker-compose.yml`, `README.md`, `plan.md` y `TODO.md`.
- El modelo contempla usuarios, pisos, membresías, gastos y participantes.
- Los importes usan `BigDecimal` y restricciones de precisión en la entidad de gasto y el reparto.
- Las contraseñas se almacenan mediante `BCryptPasswordEncoder` y nunca se devuelven en las respuestas.

## No verificado todavía

- Se ha ejecutado `mvn -B clean test` con JDK 17 y Maven 3.8.7.
- Resultado real: 12 pruebas, 0 fallos y 0 errores.
- `PruebaAutenticacionTest` verifica registro, BCrypt, JWT, identidad autenticada, correo duplicado, credenciales incorrectas y ruta protegida.
- `PruebaPisosMembresiasTest` verifica creación de piso, código de invitación, incorporación, ocultación del código a miembros, pertenencia única y permisos de administrador.
- `PruebaGastosTest` verifica creación, filtros, validación de pertenencia, permisos del propietario y reparto de `10,00 €` en `3,34 €`, `3,33 €` y `3,33 €`.
- `PruebaBalancesTest` verifica pagado, asumido, balance, filtros por periodo, intervalos inválidos y liquidación determinista.
- `PruebaMapeoPersistenciaTest` verifica el mapeo JPA de usuario, piso y membresía.
- Se ha ejecutado `mvn -B package -DskipTests` y se ha generado el JAR ejecutable en `convive-backend/target/`.
- No se ha iniciado MySQL porque Docker no está instalado.
- No se ha probado una conexión real con la base de datos.
- El frontend, el perfil, el panel principal y la conexión real con MySQL siguen pendientes.

## Próximo paso

Implementar el perfil y el panel principal; después crear el frontend Vue 3 y probar la integración completa. La conexión real con MySQL y Docker sigue pendiente.
