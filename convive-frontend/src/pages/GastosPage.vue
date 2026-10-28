<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { mensajeError } from '../services/api'

const gastos = ref([])
const integrantes = ref([])
const cargando = ref(true)
const guardando = ref(false)
const error = ref('')
const mensaje = ref('')
const gastoEditando = ref(null)
const detalleAbierto = ref(null)

const filtros = reactive({ desde: '', hasta: '', categoria: '' })
const categorias = [
  { valor: 'ALQUILER', etiqueta: 'Alquiler' },
  { valor: 'ALIMENTACION', etiqueta: 'Alimentación' },
  { valor: 'ELECTRICIDAD', etiqueta: 'Electricidad' },
  { valor: 'AGUA', etiqueta: 'Agua' },
  { valor: 'INTERNET', etiqueta: 'Internet' },
  { valor: 'OTROS', etiqueta: 'Otros' }
]

function formularioInicial() {
  return {
    concepto: '',
    importe: '',
    fecha: new Date().toISOString().slice(0, 10),
    categoria: 'OTROS',
    pagadorUsuarioId: '',
    participantesUsuarioIds: []
  }
}

const formulario = reactive(formularioInicial())
const esEdicion = computed(() => gastoEditando.value !== null)
const participantesSeleccionados = computed(() => formulario.participantesUsuarioIds.length)
const importePorPersona = computed(() => {
  const importe = Number(formulario.importe)
  return participantesSeleccionados.value && importe > 0 ? importe / participantesSeleccionados.value : 0
})

function limpiarMensajes() {
  error.value = ''
  mensaje.value = ''
}

function parametrosFiltro() {
  return Object.fromEntries(Object.entries(filtros).filter(([, valor]) => valor !== ''))
}

async function cargar() {
  cargando.value = true
  limpiarMensajes()
  try {
    const [respuestaGastos, respuestaPiso] = await Promise.all([
      conviveApi.obtenerGastos(parametrosFiltro()),
      conviveApi.obtenerPiso()
    ])
    gastos.value = respuestaGastos.data
    integrantes.value = respuestaPiso.data.integrantes || []
    prepararPagadorPorDefecto()
  } catch (e) {
    if (e.response?.status === 404) {
      integrantes.value = []
      gastos.value = []
    } else {
      error.value = mensajeError(e)
    }
  } finally {
    cargando.value = false
  }
}

function prepararPagadorPorDefecto() {
  if (!formulario.pagadorUsuarioId && integrantes.value.length) {
    formulario.pagadorUsuarioId = integrantes.value[0].usuarioId
  }
}

function seleccionarTodos() {
  formulario.participantesUsuarioIds = integrantes.value.map((integrante) => integrante.usuarioId)
}

function limpiarParticipantes() {
  formulario.participantesUsuarioIds = []
}

function nombreIntegrante(id) {
  return integrantes.value.find((integrante) => integrante.usuarioId === Number(id))?.nombre || `Usuario ${id}`
}

function datosFormulario() {
  return {
    concepto: formulario.concepto.trim(),
    importe: Number(formulario.importe),
    fecha: formulario.fecha,
    categoria: formulario.categoria,
    pagadorUsuarioId: Number(formulario.pagadorUsuarioId),
    participantesUsuarioIds: formulario.participantesUsuarioIds.map(Number)
  }
}

function validarFormulario() {
  if (!formulario.concepto.trim()) return 'El concepto es obligatorio.'
  if (!Number(formulario.importe) || Number(formulario.importe) <= 0) return 'El importe debe ser mayor que cero.'
  if (!formulario.fecha) return 'La fecha es obligatoria.'
  if (!formulario.pagadorUsuarioId) return 'Selecciona quién ha pagado el gasto.'
  if (!formulario.participantesUsuarioIds.length) return 'Selecciona al menos un participante.'
  return ''
}

async function guardar() {
  limpiarMensajes()
  const validacion = validarFormulario()
  if (validacion) {
    error.value = validacion
    return
  }
  guardando.value = true
  try {
    const datos = datosFormulario()
    if (esEdicion.value) {
      await conviveApi.actualizarGasto(gastoEditando.value.id, datos)
      mensaje.value = 'Gasto actualizado y reparto recalculado.'
    } else {
      await conviveApi.crearGasto(datos)
      mensaje.value = 'Gasto registrado y repartido correctamente.'
    }
    cancelarEdicion()
    await cargar()
  } catch (e) {
    error.value = mensajeError(e)
  } finally {
    guardando.value = false
  }
}

function editar(gasto) {
  limpiarMensajes()
  gastoEditando.value = gasto
  formulario.concepto = gasto.concepto
  formulario.importe = Number(gasto.importe)
  formulario.fecha = gasto.fecha
  formulario.categoria = gasto.categoria
  formulario.pagadorUsuarioId = gasto.pagadorUsuarioId
  formulario.participantesUsuarioIds = gasto.participantes.map((participante) => participante.usuarioId)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function cancelarEdicion() {
  gastoEditando.value = null
  Object.assign(formulario, formularioInicial())
  prepararPagadorPorDefecto()
}

async function eliminar(gasto) {
  if (!confirm(`¿Quieres eliminar el gasto “${gasto.concepto}”? Esta acción no se puede deshacer.`)) return
  limpiarMensajes()
  try {
    await conviveApi.eliminarGasto(gasto.id)
    mensaje.value = 'Gasto eliminado.'
    if (detalleAbierto.value?.id === gasto.id) detalleAbierto.value = null
    await cargar()
  } catch (e) {
    error.value = mensajeError(e)
  }
}

function alternarDetalle(gasto) {
  detalleAbierto.value = detalleAbierto.value?.id === gasto.id ? null : gasto
}

function etiquetaCategoria(valor) {
  return categorias.find((categoria) => categoria.valor === valor)?.etiqueta || valor
}

function formatoImporte(valor) {
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(Number(valor) || 0)
}

function formatoFecha(valor) {
  return new Intl.DateTimeFormat('es-ES', { dateStyle: 'medium' }).format(new Date(`${valor}T12:00:00`))
}

onMounted(cargar)
</script>

<template>
  <header class="mb-4">
    <p class="page-kicker mb-1">Economía compartida</p>
    <h1 class="h2 mb-1">Gastos</h1>
    <p class="text-secondary mb-0">Registra, reparte y revisa los gastos comunes de tu piso.</p>
  </header>

  <div v-if="mensaje" class="alert alert-success alert-dismissible fade show" role="alert">
    <i class="bi bi-check-circle me-2"></i>{{ mensaje }}
    <button type="button" class="btn-close" aria-label="Cerrar" @click="mensaje = ''"></button>
  </div>
  <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
    <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    <button type="button" class="btn-close" aria-label="Cerrar" @click="error = ''"></button>
  </div>

  <section class="card p-4 mb-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
      <div>
        <h2 class="h5 mb-1">{{ esEdicion ? 'Editar gasto' : 'Registrar gasto' }}</h2>
        <p class="small text-secondary mb-0">{{ esEdicion ? 'Solo puedes modificar tus propios gastos.' : 'El reparto se calcula automáticamente a partes iguales.' }}</p>
      </div>
      <button v-if="esEdicion" class="btn btn-sm btn-outline-secondary" type="button" @click="cancelarEdicion">Cancelar edición</button>
    </div>

    <form class="row g-3" @submit.prevent="guardar" novalidate>
      <div class="col-12 col-md-6">
        <label class="form-label" for="concepto">Concepto</label>
        <input id="concepto" v-model="formulario.concepto" class="form-control" maxlength="180" placeholder="Compra semanal" required />
      </div>
      <div class="col-6 col-md-3">
        <label class="form-label" for="importe">Importe</label>
        <div class="input-group"><input id="importe" v-model="formulario.importe" class="form-control" type="number" min="0.01" step="0.01" placeholder="0,00" required /><span class="input-group-text">€</span></div>
      </div>
      <div class="col-6 col-md-3">
        <label class="form-label" for="fecha">Fecha</label>
        <input id="fecha" v-model="formulario.fecha" class="form-control" type="date" required />
      </div>
      <div class="col-12 col-md-5">
        <label class="form-label" for="categoria">Categoría</label>
        <select id="categoria" v-model="formulario.categoria" class="form-select" required>
          <option v-for="categoria in categorias" :key="categoria.valor" :value="categoria.valor">{{ categoria.etiqueta }}</option>
        </select>
      </div>
      <div class="col-12 col-md-7">
        <label class="form-label" for="pagador">Quién ha pagado</label>
        <select id="pagador" v-model="formulario.pagadorUsuarioId" class="form-select" required>
          <option value="" disabled>Selecciona un integrante</option>
          <option v-for="integrante in integrantes" :key="integrante.usuarioId" :value="integrante.usuarioId">{{ integrante.nombre }}</option>
        </select>
      </div>
      <div class="col-12">
        <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-2">
          <label class="form-label mb-0">Participantes</label>
          <div class="d-flex gap-2"><button type="button" class="btn btn-sm btn-link p-0" @click="seleccionarTodos">Seleccionar todos</button><button type="button" class="btn btn-sm btn-link text-secondary p-0" @click="limpiarParticipantes">Limpiar</button></div>
        </div>
        <div v-if="!integrantes.length" class="alert alert-warning small mb-0">Necesitas pertenecer a un piso para seleccionar participantes.</div>
        <div v-else class="participant-grid">
          <label v-for="integrante in integrantes" :key="integrante.usuarioId" class="participant-option">
            <input v-model="formulario.participantesUsuarioIds" class="form-check-input mt-0" type="checkbox" :value="integrante.usuarioId" />
            <span><strong>{{ integrante.nombre }}</strong><small class="d-block text-secondary">{{ integrante.rol }}</small></span>
          </label>
        </div>
        <div class="form-text mt-2">{{ participantesSeleccionados }} participante{{ participantesSeleccionados === 1 ? '' : 's' }} seleccionado{{ participantesSeleccionados === 1 ? '' : 's' }}<span v-if="importePorPersona"> · Aproximadamente {{ formatoImporte(importePorPersona) }} por persona</span></div>
      </div>
      <div class="col-12"><button class="btn btn-primary" :disabled="guardando || !integrantes.length"><i :class="['bi me-2', esEdicion ? 'bi-save' : 'bi-plus-lg']"></i>{{ guardando ? 'Guardando…' : esEdicion ? 'Guardar cambios' : 'Registrar gasto' }}</button></div>
    </form>
  </section>

  <section class="card p-3 p-md-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3">
      <div><h2 class="h5 mb-1">Historial de gastos</h2><p class="small text-secondary mb-0">Consulta el reparto de cada gasto o filtra el periodo.</p></div>
      <button class="btn btn-sm btn-outline-secondary" type="button" @click="cargar"><i class="bi bi-arrow-clockwise me-1"></i>Actualizar</button>
    </div>
    <form class="row g-2 align-items-end mb-4" @submit.prevent="cargar">
      <div class="col-12 col-sm-4"><label class="form-label small" for="filtroDesde">Desde</label><input id="filtroDesde" v-model="filtros.desde" class="form-control form-control-sm" type="date" /></div>
      <div class="col-12 col-sm-4"><label class="form-label small" for="filtroHasta">Hasta</label><input id="filtroHasta" v-model="filtros.hasta" class="form-control form-control-sm" type="date" /></div>
      <div class="col-12 col-sm-4"><label class="form-label small" for="filtroCategoria">Categoría</label><select id="filtroCategoria" v-model="filtros.categoria" class="form-select form-select-sm"><option value="">Todas</option><option v-for="categoria in categorias" :key="categoria.valor" :value="categoria.valor">{{ categoria.etiqueta }}</option></select></div>
      <div class="col-12 d-flex gap-2"><button class="btn btn-sm btn-primary" type="submit">Aplicar filtros</button><button class="btn btn-sm btn-outline-secondary" type="button" @click="Object.assign(filtros, { desde: '', hasta: '', categoria: '' }); cargar()">Limpiar filtros</button></div>
    </form>

    <div v-if="cargando" class="text-center text-secondary py-4"><span class="spinner-border spinner-border-sm me-2"></span>Cargando gastos…</div>
    <div v-else-if="!gastos.length" class="empty-state text-center text-secondary py-5"><i class="bi bi-receipt display-6 d-block mb-3"></i>No hay gastos que coincidan con los filtros.</div>
    <div v-else class="table-responsive">
      <table class="table align-middle mb-0">
        <thead><tr><th>Concepto</th><th>Fecha</th><th>Categoría</th><th>Pagador</th><th class="text-end">Importe</th><th class="text-end">Acciones</th></tr></thead>
        <tbody>
          <template v-for="gasto in gastos" :key="gasto.id">
            <tr>
              <td><button class="btn btn-link p-0 text-start fw-semibold text-decoration-none" type="button" @click="alternarDetalle(gasto)">{{ gasto.concepto }} <i :class="['bi small ms-1', detalleAbierto?.id === gasto.id ? 'bi-chevron-up' : 'bi-chevron-down']"></i></button></td>
              <td>{{ formatoFecha(gasto.fecha) }}</td><td><span class="badge text-bg-light">{{ etiquetaCategoria(gasto.categoria) }}</span></td><td>{{ gasto.pagadorNombre }}</td><td class="text-end fw-semibold">{{ formatoImporte(gasto.importe) }}</td>
              <td class="text-end text-nowrap"><button class="btn btn-sm btn-outline-secondary me-1" type="button" title="Editar" @click="editar(gasto)"><i class="bi bi-pencil"></i></button><button class="btn btn-sm btn-outline-danger" type="button" title="Eliminar" @click="eliminar(gasto)"><i class="bi bi-trash"></i></button></td>
            </tr>
            <tr v-if="detalleAbierto?.id === gasto.id" class="expense-detail-row"><td colspan="6"><div class="p-3"><div class="small fw-semibold text-secondary mb-2">Reparto entre participantes</div><div class="row g-2"><div v-for="participante in gasto.participantes" :key="participante.usuarioId" class="col-12 col-sm-6 col-lg-4"><div class="d-flex justify-content-between bg-white border rounded p-2"><span>{{ participante.nombre }}</span><strong>{{ formatoImporte(participante.importeAsignado) }}</strong></div></div></div></div></td></tr>
          </template>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.participant-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(190px, 1fr)); gap: .65rem; }
.participant-option { display: flex; align-items: center; gap: .65rem; padding: .75rem; border: 1px solid var(--convive-line); border-radius: .75rem; cursor: pointer; transition: background .15s, border-color .15s; }
.participant-option:has(input:checked) { background: var(--convive-mint); border-color: #9bd9ca; }
.participant-option:hover { border-color: var(--convive-teal); }
.expense-detail-row > td { background: #f7fbf9; }
.empty-state { border: 1px dashed var(--convive-line); border-radius: .75rem; }
</style>
