# Convive Backend

Backend Spring Boot de Convive. Esta fase contiene la configuración Maven, las entidades JPA/Hibernate, validaciones, repositorios Spring Data, autenticación con BCrypt/JWT y la configuración de MySQL.

## Versiones

- Java: 17.
- Maven: 3.8.7 utilizado en la verificación del entorno.
- Spring Boot: 3.4.5.
- MySQL Connector/J: gestionado por Spring Boot 3.4.5.
- Hibernate: gestionado por Spring Boot 3.4.5.

## Comprobación ejecutada

Desde este directorio:

```bash
mvn -B test
```

Resultado verificado el 27 de octubre de 2026: compilación correcta y 4 pruebas superadas, con 0 fallos y 0 errores. `PruebaAutenticacionTest` comprueba registro, BCrypt, login, JWT, identidad autenticada, duplicados y rutas protegidas. `PruebaMapeoPersistenciaTest` comprueba la persistencia de usuario, piso y membresía con H2 en memoria.

## Endpoints de autenticación

- `POST /api/autenticacion/registro`: recibe `nombre`, `correo` y `contrasena`.
- `POST /api/autenticacion/login`: recibe `correo` y `contrasena`.
- `GET /api/autenticacion/yo`: requiere `Authorization: Bearer <JWT>`.

El secreto JWT se configura mediante `JWT_SECRET` y debe tener al menos 32 bytes. No se deben guardar secretos reales en Git.

La conexión real con MySQL queda pendiente hasta disponer de Docker/MySQL activo.
