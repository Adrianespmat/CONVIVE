# Convive — Estado de desarrollo

**Fecha:** 2026-10-28
**Fase:** 6 — Frontend Vue 3 inicial
**Estado:** backend de dominio implementado y probado; frontend local Vue 3/Vite con Bootstrap, Axios, Vue Router, autenticación, layout protegido, panel, piso, gastos, balances y perfil creado y compilado; integración en ejecución y conexión real con MySQL pendientes.

## Verificado en el espacio de trabajo

- El proyecto partía sin código Java, Vue, Maven, Dockerfiles ni Compose.
- Se ha creado `convive-backend/` con configuración Spring Boot, entidades JPA y repositorios.
- Se han creado `.env.example`, `.gitignore`, `docker-compose.yml`, `README.md`, `plan.md` y `TODO.md`.
- El modelo contempla usuarios, pisos, membresías, gastos y participantes.
- Los importes usan `BigDecimal` y restricciones de precisión en la entidad de gasto y el reparto.
- Las contraseñas se almacenan mediante `BCryptPasswordEncoder` y nunca se devuelven en las respuestas.

## No verificado todavía

- Se ha ejecutado `mvn -B clean test` con JDK 17 y Maven 3.8.7.
- Resultado real: 14 pruebas, 0 fallos y 0 errores.
- `PruebaAutenticacionTest` verifica registro, BCrypt, JWT, identidad autenticada, correo duplicado, credenciales incorrectas y ruta protegida.
- `PruebaPisosMembresiasTest` verifica creación de piso, código de invitación, incorporación, ocultación del código a miembros, pertenencia única y permisos de administrador.
- `PruebaGastosTest` verifica creación, filtros, validación de pertenencia, permisos del propietario y reparto de `10,00 €` en `3,34 €`, `3,33 €` y `3,33 €`.
- `PruebaBalancesTest` verifica pagado, asumido, balance, filtros por periodo, intervalos inválidos y liquidación determinista.
- `PruebaPerfilPanelTest` verifica consulta y modificación exclusiva del nombre, protección JWT, total del panel, últimos gastos, balance personal y filtros por periodo.
- `npm install` y `npm run build` del frontend terminan correctamente.
- La pantalla de gastos incluye filtros de fecha/categoría, selección de integrantes, alta, edición propia, reparto visible y eliminación confirmada; `npm run build` vuelve a terminar correctamente tras estos cambios.
- La pantalla de balances incluye filtros de periodo, resumen económico, tabla de balances por integrante y vista de liquidación informativa con pagos sugeridos; `npm run build` termina correctamente tras estos cambios.
- Vite responde en el puerto 5173 y sirve `public/manus-routes.json` con las siete rutas declaradas.
- `PruebaMapeoPersistenciaTest` verifica el mapeo JPA de usuario, piso y membresía.
- Se ha ejecutado `mvn -B package -DskipTests` y se ha generado el JAR ejecutable en `convive-backend/target/`.
- No se ha iniciado MySQL porque Docker no está instalado.
- No se ha probado una conexión real con la base de datos.
- La prueba completa frontend-backend en ejecución, Docker integrado y la conexión real con MySQL siguen pendientes.

## Próximo paso

Levantar backend y frontend conjuntamente, probar los flujos desde navegador y completar las pantallas/validaciones pendientes. La conexión real con MySQL y Docker sigue pendiente.
