# Convive — Revisión de cobertura del diseño

**Fecha de revisión:** 2026-10-27  
**Resultado:** el alcance funcional queda confirmado por el anteproyecto; el diseño técnico sigue provisional en las decisiones no especificadas.

## 1. Comprobaciones realizadas

| Comprobación | Resultado |
|---|---|
| Documentación local inventariada | Revisados `AnteProyectoAdrianEspinazoMateos.docx`, `Plantilla Memoria Proyecto (1).docx` e instrucciones del proyecto. |
| Requisitos funcionales identificados | 28 requisitos, RF-01 a RF-28. |
| Requisitos técnicos y de calidad identificados | 15 requisitos, RT-01 a RT-15. |
| Exclusiones explícitas registradas | 6 exclusiones, EX-01 a EX-06. |
| Casos de uso documentados | 14, CU-01 a CU-14. |
| Actores documentados | Visitante, usuario autenticado, administrador y miembro. |
| Arquitectura frontend/backend | Definida por capas y con responsabilidades separadas. |
| Entidades y relaciones | Definidas en el diagrama de clases y en el modelo E-R. |
| Endpoints REST previstos | 18 operaciones iniciales. |
| Diagramas editables | Tres fuentes `.mmd` creadas. |
| Renderizado de diagramas | Correcto; se generaron tres PNG en `docs/render/`. |
| Código funcional | No iniciado, tal como se solicitó. |
| Pruebas de aplicación | No ejecutadas porque todavía no existe implementación funcional. |
| Planificación aprobada | Incorporada desde el anteproyecto en `08-planificacion-aprobada.md`. |

## 2. Cobertura por área

La autenticación cubre registro, duplicidad, BCrypt, login, JWT y protección de rutas. La gestión de pisos cubre creación, rol inicial, código, unión, consulta y unicidad de piso activo. La gestión de miembros cubre rols y operaciones administrativas, dejando abiertas las reglas concretas de baja.

La gestión de gastos cubre creación, consulta, filtros, edición/eliminación del propietario, validaciones de pertenencia y categorías. El reparto se modela con cantidades asignadas persistibles, cálculo exacto en céntimos y orden determinista. Los balances cubren pagado, asumido, fórmula de balance, actualización tras cambios y propuesta informativa de liquidación.

El panel, el perfil y las pantallas requeridas tienen casos de uso, rutas previstas y trazabilidad. La calidad técnica queda reflejada en la arquitectura, seguridad, variables de entorno, errores centralizados, Docker, MySQL y matriz de pruebas futura.

## 3. Coherencia entre artefactos

El modelo E-R contiene las entidades necesarias para los casos de uso: usuarios, pisos, pertenencias, gastos y participantes. El modelo de clases añade los objetos de lectura de balances y liquidación sin convertirlos automáticamente en tablas, evitando persistencia redundante. La API prevista opera sobre esas entidades y aplica la pertenencia al piso como frontera de autorización.

La matriz de requisitos enlaza cada área con casos de uso, diseño y pruebas futuras. Los casos de uso describen los flujos funcionales y el documento de arquitectura concreta cómo se expondrán mediante REST. Los diagramas reflejan el mismo núcleo de dominio.

## 4. Bloqueos y decisiones que requieren revisión

El anteproyecto aprobado ya ha sido revisado y confirma el alcance funcional utilizado para este diseño. No existe un bloqueo documental para continuar con la preparación técnica.

Antes de implementar deben confirmarse, como mínimo, la versión exacta de frameworks, la gestión y duración del JWT, el almacenamiento del token en el frontend, los nombres definitivos de endpoints, la edición del piso, las reglas de baja de miembros, el periodo inicial del panel, el criterio de reparto de céntimos, el algoritmo de liquidación y la estrategia de migraciones.

También debe resolverse cómo se trata la baja del administrador y qué ocurre con gastos existentes si la documentación académica exige una regla concreta. No se ha inventado una funcionalidad para cubrir esos huecos.

## 5. Decisión de paso a implementación

**Se puede pasar a la implementación inicial**, comenzando por la configuración base del backend, la base de datos y las pruebas de dominio del reparto. Antes de implementar cada área deben cerrarse las decisiones técnicas abiertas que le afecten, especialmente seguridad JWT, contrato REST, migraciones y reglas concretas de baja de miembros. No se han declarado pruebas funcionales superadas porque todavía no existe código de aplicación.
