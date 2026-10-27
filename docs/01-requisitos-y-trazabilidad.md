# Convive — Requisitos y trazabilidad

**Estado:** diseño provisional para revisión  
**Fuentes revisadas:** `AnteProyectoAdrianEspinazoMateos.docx`, `Plantilla Memoria Proyecto (1).docx` y las instrucciones permanentes del proyecto.  
**Resultado de la comparación:** el anteproyecto confirma el alcance funcional descrito en esta matriz y no añade funcionalidades ni exclusiones distintas. Las decisiones técnicas no especificadas siguen marcadas para validación.

## 1. Alcance funcional identificado

Convive es una aplicación web para gestionar una vivienda compartida, sus integrantes, los gastos comunes, los repartos y los balances económicos. El frontend y el backend serán proyectos independientes comunicados mediante una API REST, con MySQL como base de datos.

### 1.1 Requisitos funcionales obligatorios

| ID | Requisito | Prioridad | Evidencia prevista | Casos de uso | Prueba mínima |
|---|---|---|---|---|---|
| RF-01 | Registrar una cuenta con nombre, correo y contraseña. | Obligatorio | Endpoint y pantalla de registro | CU-01 | Registro válido e inválido |
| RF-02 | Validar datos de registro y evitar correos duplicados. | Obligatorio | Bean Validation + restricción única | CU-01 | Duplicado y campos inválidos |
| RF-03 | Almacenar contraseñas con BCrypt. | Obligatorio | Configuración de seguridad | CU-01 | La contraseña no se guarda en claro |
| RF-04 | Iniciar sesión mediante credenciales y emitir JWT. | Obligatorio | Endpoint de autenticación | CU-02 | Credenciales válidas/incorrectas |
| RF-05 | Proteger las rutas privadas y autorizar en backend. | Obligatorio | Filtro JWT y reglas de servicio | Todos los privados | Acceso sin token y sin permiso |
| RF-06 | Crear un piso con nombre y dirección. | Obligatorio | Entidad, servicio y formulario | CU-03 | Creación válida/inválida |
| RF-07 | Asignar administrador al creador y generar código de invitación. | Obligatorio | Relación Usuario–Piso | CU-03 | Rol y código generados |
| RF-08 | Unirse a un piso mediante código válido. | Obligatorio | Endpoint de incorporación | CU-04 | Código válido, inválido y usado |
| RF-09 | Impedir más de un piso activo por usuario. | Obligatorio | Regla de servicio y restricción lógica | CU-03, CU-04, CU-06 | Segundo alta rechazada |
| RF-10 | Consultar datos del piso y sus integrantes. | Obligatorio | Endpoint de consulta | CU-05 | Solo miembros del piso |
| RF-11 | Administrar incorporaciones y bajas como administrador. | Obligatorio | Autorización por rol | CU-06 | Miembro no autorizado; admin autorizado |
| RF-12 | Mantener los rols administrador y miembro. | Obligatorio | Enumeración y asociación Membresia | CU-03, CU-04, CU-06 | Rols asignados correctamente |
| RF-13 | Registrar gastos con concepto, importe, fecha, categoría, pagador y participantes. | Obligatorio | API y formulario | CU-09 | Gasto válido/inválido |
| RF-14 | Usar categorías: alquiler, alimentación, electricidad, agua, internet y otros gastos domésticos. | Obligatorio | Enumeración/validación | CU-08, CU-09 | Categorías válidas |
| RF-15 | Consultar y filtrar gastos por fecha y/o categoría. | Obligatorio | Endpoint con parámetros | CU-08 | Filtros y listado vacío |
| RF-16 | Modificar y eliminar solo gastos propios. | Obligatorio | Comprobación de propietario | CU-10, CU-11 | Acceso a gasto ajeno rechazado |
| RF-17 | Validar importe, obligatorios, pagador y participantes pertenecientes al piso. | Obligatorio | Bean Validation + servicio | CU-09, CU-10 | Participante externo rechazado |
| RF-18 | Repartir cada gasto a partes iguales entre participantes. | Obligatorio | Servicio de reparto | CU-09, CU-10 | Reparto exacto |
| RF-19 | Distribuir céntimos sobrantes de forma determinista y sumar exactamente el total. | Obligatorio | `BigDecimal`/céntimos + pruebas | CU-09, CU-10 | 10,00/3 y casos similares |
| RF-20 | Calcular pagado, asumido y balance individual. | Obligatorio | Servicio de balances | CU-07, CU-12 | Balance = pagado − asumido |
| RF-21 | Actualizar balances al crear, modificar o eliminar gastos. | Obligatorio | Recalculación transaccional | CU-09, CU-10, CU-11 | Comparación antes/después |
| RF-22 | Mostrar propuesta informativa de liquidación con menos pagos. | Obligatorio | Servicio de liquidación | CU-12 | Acreedores/deudores y pagos |
| RF-23 | No ejecutar transferencias bancarias. | Obligatorio / exclusión operacional | Solo respuesta informativa | CU-12 | No existen integraciones de pago |
| RF-24 | Mostrar panel con total del periodo, últimos gastos, balance personal y accesos. | Obligatorio | Endpoint/resumen y pantalla | CU-07 | Periodo con y sin gastos |
| RF-25 | Consultar perfil y modificar el nombre. | Obligatorio | Endpoint de perfil | CU-13, CU-14 | Nombre válido/inválido |
| RF-26 | No modificar el correo mediante operación no prevista. | Obligatorio | DTO sin email editable | CU-14 | Intento rechazado/ignorado |
| RF-27 | Ofrecer pantallas de registro, login, alta/unión, panel, piso, miembros, gastos, balances y perfil. | Obligatorio | Rutas Vue | Todos | Navegación protegida |
| RF-28 | Interfaz responsive en español con Bootstrap y CSS propio. | Obligatorio | Componentes y estilos | Todos | Revisión visual en tamaños objetivo |

### 1.2 Requisitos técnicos y de calidad

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| RT-01 | Frontend con Vue 3, JavaScript, HTML5, CSS3, Bootstrap y Axios. | Obligatorio | `package.json` y revisión |
| RT-02 | Backend con Java 17, Spring Boot, Maven, Spring Web, Spring Data JPA, Hibernate, Spring Security, JWT, BCrypt y Bean Validation. | Obligatorio | `pom.xml` y configuración |
| RT-03 | Base de datos relacional MySQL. | Obligatorio | Configuración, migración y pruebas |
| RT-04 | Frontend y backend independientes mediante API REST. | Obligatorio | Estructura y contrato API |
| RT-05 | Arquitectura separada por responsabilidades. | Obligatorio | Paquetes y revisión de código |
| RT-06 | Variables de entorno para secretos y configuración sensible. | Obligatorio | `.env.example`, Docker Compose |
| RT-07 | Gestión centralizada de errores. | Obligatorio | Respuestas de error homogéneas |
| RT-08 | Integridad de base de datos, relaciones y restricciones adecuadas. | Obligatorio | Esquema y pruebas de integridad |
| RT-09 | No exponer contraseñas, tokens ni secretos en el repositorio. | Obligatorio | Revisión de configuración/Git |
| RT-10 | Pruebas unitarias y de integración de autenticación, permisos, pisos, gastos, céntimos y balances. | Obligatorio | Suite ejecutada y resultados |
| RT-11 | Dockerfiles y Docker Compose para MySQL, backend y frontend. | Obligatorio | Arranque en máquina limpia |
| RT-12 | Documentar configuración, ejecución, base de datos y publicación en Docker Hub. | Obligatorio | README/manual de despliegue |
| RT-13 | Documentar versiones exactas y mantener Java 17. | Obligatorio | Tabla de versiones |
| RT-14 | Git y GitHub como herramientas del proyecto. | Obligatorio | Repositorio/configuración |
| RT-15 | La fuente de verdad de gastos, repartos y balances es el backend. | Obligatorio | No calcular resultados definitivos solo en frontend |

### 1.3 Exclusiones identificadas

Las siguientes exclusiones sí aparecen expresamente en la documentación revisada:

| ID | Fuera de alcance |
|---|---|
| EX-01 | No se realizan transferencias bancarias desde la aplicación. |
| EX-02 | No se permite modificar el correo electrónico mediante la operación de perfil prevista. |
| EX-03 | No se permiten modificaciones o eliminaciones de gastos ajenos por parte de un miembro; tampoco se asignan al administrador permisos adicionales para gestionar gastos ajenos sin aprobación expresa. |
| EX-04 | No se permite pertenecer a más de un piso activo simultáneamente. |
| EX-05 | No se deben usar datos ficticios en el funcionamiento normal. |
| EX-06 | No se sustituyen las tecnologías obligatorias por React, Angular, MongoDB, Firebase u otras alternativas. |

**No incluidos en el alcance aprobado:** recuperación de contraseña, verificación de correo, cambio de contraseña, historial de pisos, invitaciones con caducidad, notificaciones, exportaciones, pagos externos, gastos recurrentes y división desigual. El anteproyecto no los contempla, por lo que no se incorporan.

## 2. Matriz de trazabilidad por entregables

| Área | Requisitos cubiertos | Diseño | Implementación futura | Pruebas futuras |
|---|---|---|---|---|
| Identidad y seguridad | RF-01–RF-05, RT-02, RT-06, RT-09 | CU-01–CU-02; paquetes `security` | AuthController, JWT, BCrypt, DTO | AuthServiceTest, AuthIntegrationTest |
| Pisos y miembros | RF-06–RF-12, RF-27 | CU-03–CU-06; diagrama de clases/E-R | PisoController, MembresiaService | PisoIntegrationTest, PermissionTest |
| Gastos | RF-13–RF-17, RF-27 | CU-08–CU-11 | GastoController, GastoService | GastoServiceTest, API tests |
| Reparto y céntimos | RF-18–RF-19 | Regla de dominio y `GastoSplit` | SplitCalculator | Tests parametrizados de céntimos |
| Balances y liquidación | RF-20–RF-23 | CU-07 y CU-12 | BalanceService | BalanceServiceTest |
| Panel y perfil | RF-24–RF-26, RF-27–RF-28 | CU-07, CU-13–CU-14 | DashboardController, ProfileController | Controller/integration tests |
| Calidad y despliegue | RT-01–RT-15 | Arquitectura y contrato API | Docker, CI/documentación | Smoke test de Compose |

## 3. Criterios de cierre del diseño

No se debe iniciar la implementación funcional tieneta: (1) recibir o localizar el anteproyecto aprobado; (2) confirmar las exclusiones y decisiones abiertas; (3) validar que este inventario coincide con el alcance académico; y (4) aprobar el contrato REST y el modelo de datos inicial.
