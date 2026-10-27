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
