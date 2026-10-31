<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { useAutenticacion } from '../stores/autenticacion'
import { mensajeError } from '../services/api'

const { estado, actualizarUsuario } = useAutenticacion()
const formulario = reactive({ nombre: '' })
const cargando = ref(true)
const guardando = ref(false)
const mensaje = ref('')
const error = ref('')
const nombreInicial = ref('')
const nombreVisible = computed(() => formulario.nombre.trim() || 'Usuario')
const iniciales = computed(() => nombreVisible.value.split(/\s+/).slice(0, 2).map((parte) => parte[0]).join('').toUpperCase())
const cambiosPendientes = computed(() => formulario.nombre.trim() !== nombreInicial.value)

async function cargar() {
  error.value = ''
  try {
    const { data } = await conviveApi.obtenerPerfil()
    formulario.nombre = data.nombre
    nombreInicial.value = data.nombre
    estado.usuario = data
    localStorage.setItem('convive.usuario', JSON.stringify(data))
  } catch (e) {
    error.value = mensajeError(e, 'No se ha podido cargar el perfil.')
  } finally {
    cargando.value = false
  }
}

async function guardar() {
  error.value = ''
  mensaje.value = ''
  const nombre = formulario.nombre.trim()
  if (!nombre) {
    error.value = 'El nombre es obligatorio.'
    return
  }
  if (nombre.length > 100) {
    error.value = 'El nombre no puede superar los 100 caracteres.'
    return
  }
  guardando.value = true
  try {
    const usuario = await actualizarUsuario({ nombre })
    formulario.nombre = usuario.nombre
    nombreInicial.value = usuario.nombre
    mensaje.value = 'Perfil actualizado correctamente.'
  } catch (e) {
    error.value = mensajeError(e, 'No se ha podido actualizar el perfil.')
  } finally {
    guardando.value = false
  }
}

function restaurarCambios() {
  formulario.nombre = nombreInicial.value
  error.value = ''
  mensaje.value = ''
}

onMounted(cargar)
</script>

<template>
  <header class="mb-4"><p class="page-kicker mb-1">Tu cuenta</p><h1 class="h2 mb-1">Perfil</h1><p class="text-secondary mb-0">Consulta tus datos y actualiza el nombre que verán tus compañeros.</p></header>
  <div v-if="mensaje" class="alert alert-success alert-dismissible fade show" role="alert"><i class="bi bi-check-circle me-2"></i>{{ mensaje }}<button type="button" class="btn-close" aria-label="Cerrar" @click="mensaje = ''"></button></div>
  <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert"><i class="bi bi-exclamation-triangle me-2"></i>{{ error }}<button type="button" class="btn-close" aria-label="Cerrar" @click="error = ''"></button></div>
  <div v-if="cargando" class="text-secondary py-4"><span class="spinner-border spinner-border-sm me-2"></span>Cargando perfil…</div>
  <template v-else>
    <section class="card profile-hero p-4 mb-4"><div class="d-flex flex-wrap align-items-center gap-3"><div class="avatar">{{ iniciales }}</div><div><div class="small text-secondary">Usuario de Convive</div><h2 class="h4 mb-1">{{ nombreVisible }}</h2><div class="text-secondary">{{ estado.usuario?.correo }}</div></div></div></section>
    <section class="card p-4" style="max-width: 760px"><div class="mb-4"><h2 class="h5 mb-1">Datos personales</h2><p class="small text-secondary mb-0">El correo electrónico es tu identificador de acceso y no se modifica desde el perfil.</p></div><form @submit.prevent="guardar" novalidate><label class="form-label" for="nombre">Nombre</label><input id="nombre" v-model="formulario.nombre" class="form-control mb-3" maxlength="100" autocomplete="name" required /><label class="form-label" for="correo">Correo electrónico</label><input id="correo" class="form-control mb-1" :value="estado.usuario?.correo" disabled readonly autocomplete="email" /><div class="form-text mb-4">Este campo es de solo lectura.</div><div class="d-flex flex-wrap gap-2"><button class="btn btn-primary" :disabled="guardando || !cambiosPendientes" type="submit"><i class="bi bi-save me-2"></i>{{ guardando ? 'Guardando…' : 'Guardar cambios' }}</button><button v-if="cambiosPendientes" class="btn btn-outline-secondary" type="button" @click="restaurarCambios">Cancelar</button></div></form></section>
  </template>
</template>

<style scoped>
.profile-hero { max-width: 760px; background: linear-gradient(135deg, #ffffff 0%, #effaf6 100%); }
.avatar { display: grid; place-items: center; width: 4rem; height: 4rem; border-radius: 1.25rem; background: var(--convive-teal); color: white; font-size: 1.25rem; font-weight: 800; }
</style>
