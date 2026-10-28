# Convive — Arquitectura y API REST prevista

**Estado:** diseño provisional; el alcance funcional ha sido contrastado con el anteproyecto aprobado y los endpoints siguen siendo un contrato técnico inicial.

## 1. Arquitectura general

Se propone una arquitectura cliente-servidor por capas, con dos aplicaciones independientes:

```text
Navegador
  └─ Vue 3 + Bootstrap + Axios
       └─ HTTPS/HTTP en desarrollo → API REST JSON
             └─ Spring Boot 3.x + Java 17
                  ├─ Controller: HTTP, DTO y códigos de respuesta
                  ├─ Service: casos de uso, autorización y transacciones
                  ├─ Repository: Spring Data JPA
                  ├─ Domain/entity: modelo persistente
                  └─ Security: JWT, BCrypt y autorización
                       └─ MySQL
```

El backend es la fuente de verdad de pertenencias, gastos, repartos y balances. El frontend solo presenta datos y gestiona el estado de sesión; no sustituye las comprobaciones del backend.

## 2. Estructura inicial propuesta

### Backend `convive-backend/src/main/java/.../convive`

| Paquete | Responsabilidad |
|---|---|
| `config` | Configuración general, CORS y propiedades. |
| `security` | Filtro JWT, proveedor de tokens, `UsuarioDetailsService`, BCrypt y reglas HTTP. |
| `controller` | Controladores REST y códigos HTTP. |
| `dto.auth` | Peticiones/respuestas de registro y login. |
| `dto.user` | Perfil y actualización de nombre. |
| `dto.house` | Creación, consulta y unión a piso. |
| `dto.membership` | Miembros, rols e incorporación/baja. |
| `dto.expense` | Gasto, filtros y reparto. |
| `dto.balance` | Resumen, balances y liquidación informativa. |
| `entity` | Entidades JPA y enumeraciones. |
| `repository` | Interfaces de acceso a datos. |
| `service` | Lógica de casos de uso y reglas de autorización. |
| `exception` | Excepciones de dominio y `@RestControllerAdvice`. |

### Frontend `convive-frontend/src`

| Carpeta | Responsabilidad |
|---|---|
| `router` | Rutas públicas y privadas. |
| `views` | Pantallas de registro, login, alta/unión, panel, piso, miembros, gastos, balances y perfil. |
| `components` | Navegación, formularios, tablas, alertas, estados vacíos y confirmación destructiva. |
| `services` | Cliente Axios y módulos de API. |
| `stores` | Estado de autenticación y usuario/piso actual, mediante solución Vue compatible. |
| `composables` | Lógica reutilizable de sesión, validación y filtros. |
| `assets` / `styles` | CSS propio y recursos. |

La elección concreta de la librería de estado global y de la estrategia de almacenamiento del JWT debe confirmarse antes de implementar seguridad del frontend. Como decisión provisional se evita añadir dependencias no exigidas.

## 3. Modelo de dominio inicial

- `Usuario`: identidad, nombre, correo único y contraseña BCrypt.
- `Piso`: nombre, dirección y código de invitación.
- `Membresia`: asociación de usuario y piso, rol (`ADMINISTRADOR` o `MIEMBRO`) y estado activo.
- `Gasto`: propietario que registra, concepto, importe, fecha, categoría, pagador y piso.
- `ParticipanteGasto`: relación entre gasto y usuario participante, con importe exacto asignado.
- `CategoriaGasto`: alquiler, alimentación, electricidad, agua, internet y otros gastos domésticos.

La pertenencia se modela como entidad propia para expresar el rol y el estado activo sin duplicar datos. El gasto conserva el usuario que lo registra para hacer cumplir la regla de que solo su propietario puede modificarlo o eliminarlo. El pagador y los participantes son usuarios del mismo piso.

## 4. Contrato REST inicial

Todos los endpoints `/api/**` salvo registro y login requieren `Authorization: Bearer <JWT>`.

| Método | Ruta | Actor | Uso |
|---|---|---|---|
| `POST` | `/api/autenticacion/registro` | Público | Crear cuenta, aplicar BCrypt y devolver JWT/resumen de usuario. |
| `POST` | `/api/autenticacion/login` | Público | Validar credenciales y devolver JWT/resumen de usuario. |
| `GET` | `/api/autenticacion/yo` | Autenticado | Obtener la identidad del usuario autenticado. |
| `GET` | `/api/users/me` | Autenticado | Consultar perfil. |
| `PATCH` | `/api/users/me` | Autenticado | Modificar nombre permitido. |
| `GET` | `/api/pisos/mio` | Miembro | Consultar piso, código solo para administrador e integrantes. |
| `POST` | `/api/pisos` | Autenticado sin piso activo | Crear piso y alta de administrador. |
| `POST` | `/api/pisos/unirse` | Autenticado sin piso activo | Unirse mediante código válido. |
| `PATCH` | `/api/pisos/mio` | Administrador | Modificar nombre y dirección del piso. |
| `GET` | `/api/pisos/mio/integrantes` | Miembro | Listar integrantes del piso. |
| `DELETE` | `/api/pisos/mio/integrantes/{usuarioId}` | Administrador | Dar de baja a un integrante distinto del propio administrador. |
| `GET` | `/api/gastos` | Miembro | Listar gastos; filtros opcionales `desde`, `hasta`, `categoria`. |
| `POST` | `/api/gastos` | Miembro | Registrar gasto con pagador y participantes. |
| `GET` | `/api/gastos/{gastoId}` | Miembro | Consultar detalle de gasto del piso. |
| `PUT` | `/api/gastos/{gastoId}` | Propietario | Modificar gasto propio y recalcular reparto. |
| `DELETE` | `/api/gastos/{gastoId}` | Propietario | Eliminar gasto propio. |
| `GET` | `/api/dashboard?from=&to=` | Miembro | Total del periodo, últimos gastos y balance personal. |
| `GET` | `/api/balances?desde=&hasta=` | Miembro | Pagado, asumido y balance por integrante, opcionalmente por periodo. |
| `GET` | `/api/balances/liquidacion?desde=&hasta=` | Miembro | Propuesta informativa de liquidación por periodo opcional. `/settlement` se mantiene como alias. |

### 4.1 Convenciones de respuestas

- `200 OK` para consultas y actualizaciones correctas.
- `201 Created` para registro de usuario, creación de piso y creación de gasto.
- `204 No Content` para eliminación correcta.
- `400 Bad Request` para datos inválidos o filtros incorrectos.
- `401 Unauthorized` para ausencia o invalidez de JWT.
- `403 Forbidden` para falta de rol, propiedad o pertenencia.
- `404 Not Found` cuando el recurso no existe dentro del ámbito permitido.
- `409 Conflict` para correo duplicado, piso activo existente o conflictos de unicidad.

El formato de error debe ser homogéneo y no filtrar información sensible. No se devolverán contraseñas, hashes BCrypt ni secretos.

### 4.2 Ejemplo conceptual de creación de gasto

```json
{
  "concepto": "Compra semanal",
  "importe": "60.00",
  "fecha": "2026-10-27",
  "categoria": "ALIMENTACION",
  "pagadorUsuarioId": 12,
  "participantUsuarioIds": [12, 15, 18]
}
```

Los importes se transportarán como decimal textual o estructura equivalente para evitar pérdida de precisión. La decisión definitiva de DTO debe mantenerse consistente con `BigDecimal` en Java.

## 5. Reglas de negocio transversales

Cada servicio que opere sobre un piso debe obtener el usuario autenticado del contexto de seguridad y comprobar su `Membresia` activa. El administrador no obtiene permisos sobre gastos ajenos salvo que el anteproyecto lo apruebe expresamente. El usuario no puede tener dos pertenencias activas.

El cálculo de reparto convertirá el total a céntimos, dividirá entre el número de participantes y distribuirá el resto de forma determinista siguiendo el orden estable de los participantes definido en el diseño. La suma debe igualar el total. Los balances se calculan como pagado menos asumido.

Las operaciones de creación, modificación y eliminación del gasto, sus participantes y el recalculo asociado deben ejecutarse dentro de una transacción. La propuesta de liquidación es solo información de deudores, acreedores y pagos sugeridos.

## 6. Decisiones técnicas aún no especificadas

| Decisión | Propuesta provisional | Requiere confirmación |
|---|---|---|
| Versión exacta de Spring Boot y Node/Vue | Backend fijado provisionalmente en Spring Boot 3.4.5 y Java 17; frontend pendiente de crear. | Frontend pendiente. |
| Formato y duración del JWT | JWT firmado con secreto de entorno y expiración configurable. | Sí: duración y estrategia de renovación. |
| Almacenamiento del JWT en frontend | No fijar aún; priorizar una solución segura y sencilla. | Sí, antes de implementar login. |
| Códigos HTTP/nombres exactos | Contrato de esta sección. | Sí. |
| Edición de datos del piso | La documentación exige gestión, pero no especifica campos ni operación exacta. | Sí. |
| Baja de miembros | Se exige administración de bajas, pero no se aclara si puede darse de baja el administrador, ni efectos sobre sus gastos. | Sí. |
| Regeneración/caducidad del código | Solo se exige generar y usar un código válido; no se añade caducidad ni regeneración. | Confirmar si el anteproyecto lo trata. |
| Periodo del panel | Se exige periodo seleccionado, pero no se define el valor inicial ni los límites. | Sí. |
| Orden de reparto de céntimos | Propuesta: orden estable por identificador de usuario ascendente, documentado y probado. | Sí. |
| Propuesta de liquidación | Propuesta: algoritmo de deudores/acreedores con emparejamiento determinista. | Sí si el anteproyecto exige otro criterio. |
| Migraciones de base de datos | Elegir herramienta compatible y documentarla; no añadirla al alcance funcional. | Sí, decisión técnica. |
| Paginación de listados | No especificada; no se incorpora de momento. | Confirmar si el anteproyecto exige límites. |

## 7. Bloqueos para implementación

El bloqueo principal es la ausencia del anteproyecto aprobado independiente en el espacio de trabajo. También deben confirmarse las decisiones de la tabla anterior que afectan a seguridad, API o comportamiento de negocio. No se recomienda iniciar la implementación de esos puntos tieneta disponer de la confirmación.
