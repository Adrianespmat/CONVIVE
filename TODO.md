# Convive — Estado de trabajo

## Fase 1 — Preparación y base de datos

### Estructura inicial

- [x] Crear `convive-backend/` con estructura Maven/Spring Boot.
- [x] Mantener `docs/` como fuente de requisitos, diseño y decisiones.
- [x] Crear `.env.example`, `.gitignore`, `README.md` y `docker-compose.yml` sin secretos.

### Modelo persistente

- [x] Definir entidades para usuarios, pisos, membresías, gastos y participantes.
- [x] Definir roles y categorías aprobados por el anteproyecto.
- [x] Definir relaciones JPA, claves únicas y validaciones básicas.
- [x] Crear repositorios Spring Data JPA.
- [x] Ejecutar `mvn -B test` con JDK 17 y comprobar que compila.
- [x] Ejecutar la prueba de mapeo JPA con H2: 1 prueba, 0 fallos y 0 errores.
- [x] Generar el JAR con `mvn -B package -DskipTests`.
- [ ] Ejecutar conexión real contra MySQL.

## Fases siguientes

- [x] Registro, login, JWT, BCrypt y autorización backend.
- [x] Gestión de pisos, invitaciones y miembros.
- [x] Gastos, filtros y permisos de propietario.
- [x] Reparto exacto en céntimos, balances y liquidación informativa.
- [x] Perfil de usuario: consulta y modificación del nombre.
- [x] Panel principal: total del periodo, últimos gastos y balance personal.
- [x] Crear frontend local independiente con Vue 3, JavaScript y Vite; organizar vistas, componentes, rutas, servicios Axios y gestión de sesión.
- [x] Integrar Bootstrap y CSS propio con interfaz en español, navegación responsive inicial, formularios, estados vacíos, mensajes y confirmaciones destructivas.
- [x] Configurar Axios con proxy local `/api`, añadir el JWT a las peticiones privadas y proteger las rutas privadas del frontend.
- [x] Crear las vistas iniciales de registro, inicio de sesión, creación/incorporación a piso, panel, piso e integrantes, gastos, balances, liquidación y perfil.
- [x] Crear el panel principal y la navegación responsive inicial con total del periodo, últimos gastos, balance personal y accesos principales.
- [x] Completar la gestión avanzada de gastos en Vue: filtros por fecha y categoría, selección de pagador y participantes, edición de gastos propios, recálculo del reparto, detalle de cantidades asignadas y eliminación confirmada.
- [ ] Probar los flujos completos del frontend contra el backend arrancado y revisar visualmente todos los estados en navegador.
- [x] Pruebas de integración principales ejecutadas con evidencias en H2.
- [ ] Docker integrado para backend/frontend/MySQL.
- [ ] Documentación de despliegue y publicación real en Docker Hub.

## Limitaciones verificadas

En el entorno de desarrollo actual Maven y JDK 17 ya están instalados y la prueba de persistencia ha pasado. Docker no está instalado, por lo que la conexión real contra MySQL sigue pendiente. El proyecto fija `java.version=17`.
