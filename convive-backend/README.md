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

## Endpoints de pisos y membresías

- `POST /api/pisos`: crea un piso y asigna `ADMINISTRADOR` al creador.
- `POST /api/pisos/unirse`: incorpora al usuario mediante un código válido.
- `GET /api/pisos/mio`: consulta el piso activo y sus integrantes.
- `PATCH /api/pisos/mio`: modifica nombre y dirección como administrador.
- `GET /api/pisos/mio/integrantes`: lista los integrantes activos.
- `DELETE /api/pisos/mio/integrantes/{usuarioId}`: permite al administrador dar de baja a otro integrante.

Se impide que un usuario tenga más de un piso activo. El código de invitación se devuelve únicamente al administrador.

## Endpoints de gastos

- `POST /api/gastos`: crea un gasto y calcula el reparto.
- `GET /api/gastos?desde=&hasta=&categoria=`: lista con filtros opcionales.
- `GET /api/gastos/{gastoId}`: consulta el detalle dentro del piso activo.
- `PUT /api/gastos/{gastoId}`: modifica un gasto creado por el usuario autenticado.
- `DELETE /api/gastos/{gastoId}`: elimina un gasto creado por el usuario autenticado.

El importe se convierte a céntimos mediante `longValueExact()`. Los céntimos sobrantes se asignan por identificador de usuario ascendente, garantizando que la suma de las participaciones coincida con el importe total.

El secreto JWT se configura mediante `JWT_SECRET` y debe tener al menos 32 bytes. No se deben guardar secretos reales en Git.

La conexión real con MySQL queda pendiente hasta disponer de Docker/MySQL activo.
