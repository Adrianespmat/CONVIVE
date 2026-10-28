<script setup>
import { onMounted, reactive, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { useAutenticacion } from '../stores/autenticacion'
import { mensajeError } from '../services/api'
const { estado, actualizarUsuario } = useAutenticacion(); const formulario = reactive({ nombre: '' }); const cargando = ref(true); const guardando = ref(false); const mensaje = ref(''); const error = ref('')
async function cargar() { try { const { data } = await conviveApi.obtenerPerfil(); formulario.nombre = data.nombre } catch (e) { error.value = mensajeError(e) } finally { cargando.value = false } }
async function guardar() { guardando.value = true; error.value = ''; mensaje.value = ''; try { await actualizarUsuario({ nombre: formulario.nombre }); mensaje.value = 'Perfil actualizado.' } catch (e) { error.value = mensajeError(e) } finally { guardando.value = false } }
onMounted(cargar)
</script>
<template>
  <header class="mb-4"><p class="page-kicker mb-1">Tu cuenta</p><h1 class="h2 mb-1">Perfil</h1><p class="text-secondary mb-0">Actualiza tu nombre sin cambiar tu correo de acceso.</p></header>
  <div v-if="mensaje" class="alert alert-success">{{ mensaje }}</div><div v-if="error" class="alert alert-danger">{{ error }}</div><section v-if="!cargando" class="card p-4" style="max-width: 640px"><form @submit.prevent="guardar"><label class="form-label" for="nombre">Nombre</label><input id="nombre" v-model="formulario.nombre" class="form-control mb-3" maxlength="100" required /><label class="form-label" for="correo">Correo electrónico</label><input id="correo" class="form-control mb-1" :value="estado.usuario?.correo" disabled readonly /><div class="form-text mb-4">El correo es el identificador de acceso y no se modifica desde el perfil.</div><button class="btn btn-primary" :disabled="guardando">{{ guardando ? 'Guardando…' : 'Guardar cambios' }}</button></form></section>
</template>
