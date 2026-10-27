# Convive Backend

Backend Spring Boot de Convive. Esta primera fase contiene la configuración Maven, las entidades JPA/Hibernate, validaciones básicas, repositorios Spring Data y la configuración de MySQL.

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

Resultado verificado el 27 de octubre de 2026: compilación correcta y `PruebaMapeoPersistencia` superada, con 1 prueba, 0 fallos y 0 errores. La prueba utiliza H2 en memoria y comprueba la persistencia de usuario, piso y membresía.

La conexión real con MySQL queda pendiente hasta disponer de Docker/MySQL activo.
