# Plan de implementación de Convive

## Estado de partida

El proyecto contenía únicamente documentación y diagramas. La implementación comienza por la Fase 1 del anteproyecto: estructura, backend Spring Boot, modelo relacional, repositorios, validaciones y configuración de MySQL.

## Decisiones técnicas

Se utilizará Spring Boot 3.4.5 con Java 17 como nivel de compilación, Maven, Spring Data JPA/Hibernate, MySQL y Bean Validation. Las entidades se mantendrán con Java sencillo, sin Lombok, para facilitar la comprensión académica. La relación entre usuario y piso se representará mediante `Membresia`, porque contiene el rol y el estado activo. Los importes se almacenarán con `BigDecimal` y precisión decimal.

La configuración sensible se leerá de variables de entorno. La base de datos se llamará `convive` en desarrollo y se preparará un servicio MySQL 8.4 en Docker Compose. La autenticación JWT y la API se implementarán en la Fase 2; en esta fase se dejan las dependencias previstas y se prepara el modelo.

## Estructura

- `convive-backend/`: aplicación Spring Boot, entidades, repositorios y configuración.
- `convive-frontend/`: se creará en la fase de frontend.
- `docs/`: requisitos, diseño, diagramas, decisiones y estado.
- `docker-compose.yml`: MySQL para desarrollo y, posteriormente, servicios integrados.
- `.env.example`: variables sin secretos reales.
- `README.md`: instalación, ejecución y estado verificable.

## Secuencia

1. Preparación y modelo persistente.
2. Registro, login, JWT, BCrypt y autorización.
3. Pisos y miembros.
4. Gastos y permisos.
5. Reparto, balances y liquidación.
6. Frontend Vue 3.
7. Panel principal.
8. Pruebas y correcciones.
9. Docker, documentación y publicación.

Cada fase se considera completada solo cuando el código correspondiente compila y existe evidencia de las comprobaciones realizadas.

## Diseño de interfaz

La interfaz futura seguirá una estética clara y doméstica: fondo neutro, acentos verde azulado para acciones de confianza y estados económicos diferenciados con prudencia. Se priorizarán formularios legibles, tablas sencillas, estados vacíos explícitos y navegación responsive; no se añadirán elementos decorativos sin función.

## Decisiones de la fase frontend

Se desarrollará `convive-frontend/` como proyecto local independiente dentro de este repositorio, sin inicialización ni publicación mediante servicios gestionados. Se utilizarán Vue 3, JavaScript, Vite, Vue Router, Bootstrap, Bootstrap Icons y Axios. Axios usará la ruta relativa `/api` y Vite hará proxy local hacia el backend, evitando duplicar configuración de CORS durante el desarrollo.

La sesión JWT se guardará en `localStorage` bajo una clave propia de Convive. Un interceptor de Axios añadirá el token a las peticiones privadas y reaccionará ante respuestas 401. Las rutas privadas exigirán sesión; las páginas de autenticación quedarán separadas del layout principal.

Se consideraron tres estilos de interfaz: **panel doméstico sobrio** (0,06), **bento financiero colorido** (0,08) y **cuaderno comunitario cálido** (0,04). Se elige **panel doméstico sobrio**: fondo neutro, tarjetas con jerarquía clara, verde azulado como color propio, estados económicos diferenciados y formularios directos.

Principios: claridad antes que decoración, datos económicos legibles, acciones destructivas confirmadas y responsive desde móvil. La navegación principal usará una barra lateral en escritorio y una barra compacta en pantallas pequeñas. La voz será directa y útil, por ejemplo: “Tu piso, claro y al día” y “Cada gasto en su sitio”.

La primera entrega del frontend incluye autenticación, layout protegido, panel, piso, gastos, balances y perfil. La validación de integración contra el backend arrancado y la revisión visual completa quedan como siguiente paso antes de marcar la fase frontend como terminada.
