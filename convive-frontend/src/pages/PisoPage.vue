<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { mensajeError } from '../services/api'
import { useAutenticacion } from '../stores/autenticacion'

const { estado } = useAutenticacion()
const piso = ref(null)
const integrantes = ref([])
const cargando = ref(true)
const guardando = ref(false)
const editando = ref(false)
const mensaje = ref('')
const error = ref('')
const crear = reactive({ nombre: '', direccion: '' })
const edicion = reactive({ nombre: '', direccion: '' })
const codigo = ref('')

const esAdministrador = computed(() => Boolean(piso.value?.piso?.administrador))
const idUsuarioActual = computed(() => Number(estado.usuario?.id))

async function cargar() {
  cargando.value = true
  error.value = ''
  try {
    piso.value = (await conviveApi.obtenerPiso()).data
    integrantes.value = (await conviveApi.obtenerIntegrantes()).data
  } catch (e) {
    if (e.response?.status !== 404) error.value = mensajeError(e)
  } finally {
    cargando.value = false
  }
}

async function crearPiso() {
  try {
    await conviveApi.crearPiso(crear)
    mensaje.value = 'Piso creado correctamente.'
    await cargar()
  } catch (e) {
    error.value = mensajeError(e)
  }
}

async function unirse() {
  try {
    await conviveApi.unirsePiso(codigo.value)
    codigo.value = ''
    mensaje.value = 'Te has unido al piso.'
    await cargar()
  } catch (e) {
    error.value = mensajeError(e, 'El código no es válido.')
  }
}

function comenzarEdicion() {
  edicion.nombre = piso.value?.piso?.nombre || ''
  edicion.direccion = piso.value?.piso?.direccion || ''
  editando.value = true
  mensaje.value = ''
  error.value = ''
}

function cancelarEdicion() {
  editando.value = false
}

async function guardarPiso() {
  guardando.value = true
  mensaje.value = ''
  error.value = ''
  try {
    const respuesta = await conviveApi.actualizarPiso({ ...edicion })
    piso.value = respuesta.data
    editando.value = false
    mensaje.value = 'Datos del piso actualizados correctamente.'
  } catch (e) {
    error.value = mensajeError(e)
  } finally {
    guardando.value = false
  }
}

async function expulsar(id) {
  if (!confirm('¿Dar de baja a este integrante?')) return
  try {
    await conviveApi.eliminarIntegrante(id)
    mensaje.value = 'Integrante dado de baja correctamente.'
    await cargar()
  } catch (e) {
    error.value = mensajeError(e)
  }
}

onMounted(cargar)
</script>

<template>
  <header class="mb-4">
    <p class="page-kicker mb-1">Organización</p>
    <h1 class="h2 mb-1">Mi piso</h1>
    <p class="text-secondary mb-0">Gestiona los datos y las personas que comparten la vivienda.</p>
  </header>

  <div v-if="mensaje" class="alert alert-success">{{ mensaje }}</div>
  <div v-if="error" class="alert alert-danger">{{ error }}</div>
  <div v-if="cargando" class="text-secondary">Cargando…</div>

  <template v-else-if="piso">
    <section class="card p-4 mb-4">
      <div v-if="!editando" class="d-flex justify-content-between align-items-start gap-3">
        <div>
          <div class="small text-secondary">Piso activo</div>
          <h2 class="h4">{{ piso.piso?.nombre || piso.nombre }}</h2>
          <p class="mb-0 text-secondary">{{ piso.piso?.direccion || piso.direccion }}</p>
        </div>
        <div class="d-flex align-items-start gap-2">
          <span class="badge bg-mint text-teal">Activo</span>
          <button v-if="esAdministrador" type="button" class="btn btn-sm btn-outline-primary" @click="comenzarEdicion">
            <i class="bi bi-pencil me-1"></i>Editar
          </button>
        </div>
      </div>

      <form v-else class="row g-3" @submit.prevent="guardarPiso">
        <div class="col-12 col-md-5">
          <label class="form-label" for="nombrePiso">Nombre del piso</label>
          <input id="nombrePiso" v-model.trim="edicion.nombre" class="form-control" maxlength="120" required />
        </div>
        <div class="col-12 col-md-5">
          <label class="form-label" for="direccionPiso">Dirección</label>
          <input id="direccionPiso" v-model.trim="edicion.direccion" class="form-control" maxlength="255" required />
        </div>
        <div class="col-12 col-md-2 d-flex align-items-end gap-2">
          <button class="btn btn-primary" type="submit" :disabled="guardando">
            <i class="bi bi-save me-1"></i>{{ guardando ? 'Guardando…' : 'Guardar' }}
          </button>
          <button class="btn btn-outline-secondary" type="button" :disabled="guardando" @click="cancelarEdicion">Cancelar</button>
        </div>
      </form>

      <div v-if="esAdministrador && !editando" class="mt-4">
        <label class="form-label small">Código de invitación</label>
        <div class="input-group" style="max-width: 360px">
          <input class="form-control" :value="piso.piso.codigoInvitacion" readonly />
          <button class="btn btn-outline-secondary" type="button" title="Copiar código" @click="navigator.clipboard?.writeText(piso.piso.codigoInvitacion)">
            <i class="bi bi-copy"></i>
          </button>
        </div>
      </div>
    </section>

    <section class="card p-4">
      <h2 class="h5 mb-3">Integrantes</h2>
      <div v-if="!integrantes.length" class="text-secondary">No hay integrantes para mostrar.</div>
      <ul v-else class="list-group list-group-flush">
        <li v-for="integrante in integrantes" :key="integrante.usuarioId" class="list-group-item px-0 d-flex justify-content-between align-items-start gap-3">
          <span>
            <strong>{{ integrante.nombre }}</strong>
            <small class="d-block text-secondary">{{ integrante.correo }}</small>
          </span>
          <span class="d-flex align-items-start gap-2">
            <span class="badge text-bg-light">{{ integrante.rol }}</span>
            <button
              v-if="esAdministrador && Number(integrante.usuarioId) !== idUsuarioActual"
              class="btn btn-sm btn-outline-danger"
              type="button"
              title="Dar de baja"
              @click="expulsar(integrante.usuarioId)"
            >
              <i class="bi bi-person-dash me-1"></i>Dar de baja
            </button>
          </span>
        </li>
      </ul>
    </section>
  </template>

  <section v-else class="row g-4">
    <div class="col-12 col-lg-6">
      <div class="card p-4">
        <h2 class="h5">Crea tu piso</h2>
        <p class="text-secondary small">Serás su administrador.</p>
        <form @submit.prevent="crearPiso">
          <input v-model="crear.nombre" class="form-control mb-3" placeholder="Nombre del piso" required />
          <input v-model="crear.direccion" class="form-control mb-3" placeholder="Dirección" required />
          <button class="btn btn-primary">Crear piso</button>
        </form>
      </div>
    </div>
    <div class="col-12 col-lg-6">
      <div class="card p-4">
        <h2 class="h5">Únete a un piso</h2>
        <p class="text-secondary small">Introduce el código que te ha compartido un administrador.</p>
        <form @submit.prevent="unirse">
          <input v-model="codigo" class="form-control mb-3" placeholder="Código de invitación" required />
          <button class="btn btn-outline-primary">Unirme</button>
        </form>
      </div>
    </div>
  </section>
</template>
