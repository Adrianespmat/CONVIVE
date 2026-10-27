# Convive — Actores y casos de uso

**Estado:** diseño provisional. Los casos se han extraído de la plantilla de memoria revisada.

## 1. Actores

| Actor | Descripción |
|---|---|
| Visitante | Persona no autenticada. Puede registrarse o iniciar sesión. |
| Usuario autenticado | Persona con sesión válida. Puede ejecutar operaciones permitidas según pertenencia a piso y permisos. |
| Administrador | Usuario autenticado con rol de administrador en un piso. Hereda las operaciones generales y gestiona información e integrantes del piso. |
| Miembro | Usuario autenticado con rol de miembro. Consulta el piso, registra gastos propios y consulta balances. |

La generalización conceptual es `Administrador` y `Miembro` ⟶ `Usuario autenticado` ⟶ `Visitante` solo a nivel de participación en el sistema; en la implementación el actor autenticado se determina mediante JWT y el rol mediante la pertenencia al piso.

## 2. Casos de uso

### CU-01 — Registrarse

- **Actor:** Visitante.
- **Precondiciones:** no necesita autenticación; dispone de nombre, correo y contraseña.
- **Flujo principal:** accede al registro; introduce datos; el sistema valida; comprueba que el correo no exista; almacena la cuenta con contraseña protegida; confirma el alta.
- **Alternativas/error:** campos vacíos → solicitar completar; formato o longitud inválidos → mostrar validaciones; correo duplicado → rechazar sin crear cuenta; error de persistencia → respuesta de error sin alta parcial.

### CU-02 — Iniciar sesión

- **Actor:** Visitante.
- **Precondiciones:** cuenta registrada y sesión no iniciada.
- **Flujo principal:** introduce correo y contraseña; el sistema verifica credenciales; emite JWT; comprueba pertenencia a piso activo; dirige a crear/unirse o al panel.
- **Alternativas/error:** credenciales incorrectas → error genérico; datos incompletos → validación; autenticación fallida → no se permite acceder a rutas privadas; token inválido/expirado → solicitar nueva sesión.

### CU-03 — Crear un piso

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida y ningún piso activo.
- **Flujo principal:** selecciona crear piso; introduce nombre y dirección; el sistema valida; crea el piso; crea su pertenencia con rol administrador; genera código de invitación; muestra información.
- **Alternativas/error:** usuario con piso activo → rechazar; datos inválidos → mostrar errores; conflicto de código o error de persistencia → no completar la operación; la creación y alta del administrador deben ser atómicas.

### CU-04 — Unirse a un piso

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida, sin piso activo y código disponible.
- **Flujo principal:** introduce el código; el sistema lo valida; confirma que no pertenece a otro piso; crea la pertenencia con rol miembro; confirma la incorporación.
- **Alternativas/error:** código vacío, inválido o inexistente → rechazar; ya pertenece a un piso → rechazar; piso no disponible → informar; error de persistencia → no crear incorporación parcial.

### CU-05 — Consultar la información del piso

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida y pertenencia a un piso activo.
- **Flujo principal:** accede a gestión del piso; el sistema recupera nombre, dirección, código de invitación según permiso y lista de integrantes; muestra la información.
- **Alternativas/error:** sin pertenencia o piso inexistente → impedir acceso; error de consulta → informar sin mostrar datos incompletos.

### CU-06 — Gestionar los miembros del piso

- **Actor principal:** Administrador.
- **Precondiciones:** sesión válida, pertenencia al piso y rol administrador.
- **Flujo principal:** accede a miembros; consulta integrantes; solicita incorporación o baja según las operaciones habilitadas; backend comprueba rol y restricciones; actualiza la pertenencia; devuelve lista actualizada.
- **Alternativas/error:** actor sin rol administrador → 403; intento de incumplir la unicidad de piso → rechazar; operación sobre otro piso → 403/404; error de persistencia → no confirmar; cualquier regla sobre baja del último administrador debe confirmarse antes de implementar.

### CU-07 — Consultar el panel principal

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida y piso activo.
- **Flujo principal:** accede al panel; selecciona o recibe un periodo; backend obtiene gastos del piso y calcula total, últimos gastos y balance personal; muestra accesos a gastos, balances, piso e integrantes.
- **Alternativas/error:** sin gastos → mostrar ceros y estado vacío; periodo inválido → validar; error parcial → mostrar mensaje y no presentar resultados económicos inconsistentes.

### CU-08 — Consultar y filtrar gastos

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida y piso activo.
- **Flujo principal:** accede al listado; ve gastos de su piso; filtra por fecha, categoría o ambos; backend aplica criterios; muestra coincidencias.
- **Alternativas/error:** criterios inválidos → solicitar corrección; sin coincidencias → estado vacío; intentar consultar otro piso → impedir; error de consulta → informar.

### CU-09 — Registrar un gasto

- **Actor:** Usuario autenticado (miembro o administrador).
- **Precondiciones:** sesión válida, piso activo y permiso para registrar gastos.
- **Flujo principal:** introduce concepto, importe, fecha y categoría; selecciona pagador y participantes; backend valida pertenencias y permisos; guarda gasto; calcula partes iguales en céntimos; persiste reparto; actualiza balances; confirma.
- **Alternativas/error:** obligatorios o importe inválidos → validar; participantes vacíos → rechazar; pagador/participante externo al piso → rechazar; usuario sin pertenencia → 403; error de persistencia → rollback transaccional.

### CU-10 — Modificar un gasto propio

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida, piso activo, gasto existente y propietario igual al usuario.
- **Flujo principal:** selecciona gasto propio; modifica datos; backend valida propietario, pertenencias y reglas; actualiza gasto y reparto; recalcula balances; confirma.
- **Alternativas/error:** gasto ajeno → 403; datos o participantes inválidos → rechazar; gasto de otro piso → impedir; error → rollback y no confirmar.

### CU-11 — Eliminar un gasto propio

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida, piso activo y gasto propio existente.
- **Flujo principal:** solicita eliminar; interfaz pide confirmación; usuario confirma; backend verifica propietario; elimina gasto y reparto; recalcula balances; confirma.
- **Alternativas/error:** cancelación → conservar; gasto ajeno → 403; inexistente → 404; error → no confirmar ni dejar balance parcialmente actualizado.

### CU-12 — Consultar balances y propuesta de liquidación

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida y piso activo.
- **Flujo principal:** accede a balances; backend recupera gastos/repartos; suma pagado y asumido por integrante; calcula `balance = pagado − asumido`; muestra balances; genera propuesta informativa reduciendo pagos.
- **Alternativas/error:** sin gastos → balances a cero; datos inconsistentes → no mostrar resultados potencialmente incorrectos; error de consulta/cálculo → informar; la propuesta nunca ejecuta transferencias.

### CU-13 — Consultar el perfil

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida.
- **Flujo principal:** accede al perfil; backend recupera sus datos; muestra nombre y correo.
- **Alternativas/error:** sesión inválida → solicitar login; error de consulta → informar; nunca devolver contraseña ni secreto.

### CU-14 — Modificar datos personales permitidos

- **Actor:** Usuario autenticado.
- **Precondiciones:** sesión válida y acceso al propio perfil.
- **Flujo principal:** modifica el nombre permitido; sistema valida; persiste; devuelve datos actualizados.
- **Alternativas/error:** nombre inválido → mostrar errores; error de persistencia → no confirmar; intento de modificar correo u otro campo no previsto → rechazar; nunca aceptar actualización de otro usuario.

## 3. Relaciones entre casos

- CU-02 conduce a CU-03 o CU-04 si el usuario no tiene piso, y a CU-07 si ya tiene uno.
- CU-07 ofrece accesos a CU-08, CU-12 y CU-05.
- CU-09 y CU-10 incluyen validación, reparto y actualización de balances.
- CU-11 incluye comprobación de propiedad y actualización de balances.
- CU-06 requiere autorización de administrador.
- Todas las operaciones privadas requieren autenticación y comprobación de pertenencia cuando afectan al piso.

## 4. Cobertura

Los casos CU-01 a CU-14 cubren RF-01–RF-28 relacionados con funcionalidad, seguridad de acceso, reparto, balances, perfil y pantallas. La trazabilidad técnica y de pruebas está en `01-requisitos-y-trazabilidad.md`.
