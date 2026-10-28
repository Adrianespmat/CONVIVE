<script setup>
import { onMounted, reactive, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { mensajeError } from '../services/api'

const piso = ref(null); const integrantes = ref([]); const cargando = ref(true); const mensaje = ref(''); const error = ref('')
const crear = reactive({ nombre: '', direccion: '' }); const codigo = ref('')
async function cargar() { cargando.value = true; error.value = ''; try { piso.value = (await conviveApi.obtenerPiso()).data; integrantes.value = (await conviveApi.obtenerIntegrantes()).data } catch (e) { if (e.response?.status !== 404) error.value = mensajeError(e) } finally { cargando.value = false } }
async function crearPiso() { try { await conviveApi.crearPiso(crear); mensaje.value = 'Piso creado correctamente.'; await cargar() } catch (e) { error.value = mensajeError(e) } }
async function unirse() { try { await conviveApi.unirsePiso(codigo.value); codigo.value = ''; mensaje.value = 'Te has unido al piso.'; await cargar() } catch (e) { error.value = mensajeError(e, 'El código no es válido.') } }
async function expulsar(id) { if (!confirm('¿Dar de baja a este integrante?')) return; try { await conviveApi.eliminarIntegrante?.(id); await cargar() } catch (e) { error.value = mensajeError(e) } }
onMounted(cargar)
</script>

<template>
  <header class="mb-4"><p class="page-kicker mb-1">Organización</p><h1 class="h2 mb-1">Mi piso</h1><p class="text-secondary mb-0">Gestiona los datos y las personas que comparten la vivienda.</p></header>
  <div v-if="mensaje" class="alert alert-success">{{ mensaje }}</div><div v-if="error" class="alert alert-danger">{{ error }}</div><div v-if="cargando" class="text-secondary">Cargando…</div>
  <template v-else-if="piso">
    <section class="card p-4 mb-4"><div class="d-flex justify-content-between align-items-start gap-3"><div><div class="small text-secondary">Piso activo</div><h2 class="h4">{{ piso.piso?.nombre || piso.nombre }}</h2><p class="mb-0 text-secondary">{{ piso.piso?.direccion || piso.direccion }}</p></div><span class="badge bg-mint text-teal">Activo</span></div><div v-if="piso.piso?.codigoInvitacion" class="mt-4"><label class="form-label small">Código de invitación</label><div class="input-group" style="max-width: 360px"><input class="form-control" :value="piso.piso.codigoInvitacion" readonly /><button class="btn btn-outline-secondary" @click="navigator.clipboard?.writeText(piso.piso.codigoInvitacion)"><i class="bi bi-copy"></i></button></div></div></section>
    <section class="card p-4"><h2 class="h5 mb-3">Integrantes</h2><div v-if="!integrantes.length" class="text-secondary">No hay integrantes para mostrar.</div><ul v-else class="list-group list-group-flush"><li v-for="integrante in integrantes" :key="integrante.usuarioId" class="list-group-item px-0 d-flex justify-content-between"><span><strong>{{ integrante.nombre }}</strong><small class="d-block text-secondary">{{ integrante.correo }}</small></span><span class="badge text-bg-light align-self-start">{{ integrante.rol }}</span></li></ul></section>
  </template>
  <section v-else class="row g-4"><div class="col-12 col-lg-6"><div class="card p-4"><h2 class="h5">Crea tu piso</h2><p class="text-secondary small">Serás su administrador.</p><form @submit.prevent="crearPiso"><input v-model="crear.nombre" class="form-control mb-3" placeholder="Nombre del piso" required /><input v-model="crear.direccion" class="form-control mb-3" placeholder="Dirección" required /><button class="btn btn-primary">Crear piso</button></form></div></div><div class="col-12 col-lg-6"><div class="card p-4"><h2 class="h5">Únete a un piso</h2><p class="text-secondary small">Introduce el código que te ha compartido un administrador.</p><form @submit.prevent="unirse"><input v-model="codigo" class="form-control mb-3" placeholder="Código de invitación" required /><button class="btn btn-outline-primary">Unirme</button></form></div></div></section>
</template>
