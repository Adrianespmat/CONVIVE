<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAutenticacion } from '../stores/autenticacion'

const router = useRouter()
const { registrar, estado } = useAutenticacion()
const formulario = reactive({ nombre: '', correo: '', contrasena: '' })
const confirmacion = ref('')
const errorLocal = ref('')

async function enviar() {
  errorLocal.value = ''
  formulario.nombre = formulario.nombre.trim()
  formulario.correo = formulario.correo.trim()
  if (!formulario.nombre || !formulario.correo || !formulario.contrasena) { errorLocal.value = 'Completa todos los campos.'; return }
  if (formulario.contrasena.length < 8 || formulario.contrasena.length > 72) { errorLocal.value = 'La contraseña debe tener entre 8 y 72 caracteres.'; return }
  if (formulario.contrasena !== confirmacion.value) { errorLocal.value = 'Las contraseñas no coinciden.'; return }
  try { await registrar(formulario); router.push('/app') } catch { /* el store muestra el error */ }
}
</script>

<template>
  <main class="auth-shell">
    <section class="card auth-card p-4 p-md-5">
      <div class="mb-4"><span class="brand-mark">C</span><p class="text-uppercase small fw-bold text-teal mt-3 mb-1">Convive</p><h1 class="h3 mb-2">Crea tu cuenta</h1><p class="text-secondary mb-0">Empieza a organizar tu vivienda compartida.</p></div>
      <div v-if="estado.error || errorLocal" class="alert alert-danger small">{{ errorLocal || estado.error }}</div>
      <form @submit.prevent="enviar" novalidate>
        <label class="form-label" for="nombre">Nombre</label>
        <input id="nombre" v-model="formulario.nombre" class="form-control mb-3" maxlength="100" required />
        <label class="form-label" for="correo">Correo electrónico</label>
        <input id="correo" v-model="formulario.correo" class="form-control mb-3" type="email" autocomplete="email" required />
        <label class="form-label" for="contrasena">Contraseña</label>
        <input id="contrasena" v-model="formulario.contrasena" class="form-control mb-3" type="password" minlength="8" autocomplete="new-password" required />
        <label class="form-label" for="confirmacion">Repite la contraseña</label>
        <input id="confirmacion" v-model="confirmacion" class="form-control mb-4" type="password" minlength="8" autocomplete="new-password" required />
        <button class="btn btn-primary w-100" :disabled="estado.cargando">{{ estado.cargando ? 'Creando…' : 'Crear cuenta' }}</button>
      </form>
      <p class="small text-center text-secondary mt-4 mb-0">¿Ya tienes cuenta? <RouterLink to="/login" class="fw-semibold text-teal">Iniciar sesión</RouterLink></p>
    </section>
  </main>
</template>

<style scoped>
.brand-mark { display: inline-grid; place-items: center; width: 2.5rem; height: 2.5rem; border-radius: .8rem; background: #dff4ee; color: #0f766e; font-weight: 800; font-size: 1.25rem; }
</style>
