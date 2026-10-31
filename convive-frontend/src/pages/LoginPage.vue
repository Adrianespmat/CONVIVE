<script setup>
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAutenticacion } from '../stores/autenticacion'

const router = useRouter()
const { iniciarSesion, estado } = useAutenticacion()
const formulario = reactive({ correo: '', contrasena: '' })
const errorLocal = ref('')

async function enviar() {
  errorLocal.value = ''
  formulario.correo = formulario.correo.trim()
  if (!formulario.correo || !formulario.contrasena) { errorLocal.value = 'Introduce el correo y la contraseña.'; return }
  try { await iniciarSesion(formulario); router.push('/app') } catch { /* el store muestra el error */ }
}
</script>

<template>
  <main class="auth-shell">
    <section class="card auth-card p-4 p-md-5">
      <div class="mb-4"><span class="brand-mark">C</span><p class="text-uppercase small fw-bold text-teal mt-3 mb-1">Convive</p><h1 class="h3 mb-2">Tu piso, claro y al día</h1><p class="text-secondary mb-0">Inicia sesión para consultar tus gastos y balances.</p></div>
      <div v-if="errorLocal || estado.error" class="alert alert-danger small">{{ errorLocal || estado.error }}</div>
      <form @submit.prevent="enviar" novalidate>
        <label class="form-label" for="correo">Correo electrónico</label>
        <input id="correo" v-model="formulario.correo" class="form-control mb-3" type="email" autocomplete="email" required />
        <label class="form-label" for="contrasena">Contraseña</label>
        <input id="contrasena" v-model="formulario.contrasena" class="form-control mb-4" type="password" autocomplete="current-password" required />
        <button class="btn btn-primary w-100" :disabled="estado.cargando">{{ estado.cargando ? 'Entrando…' : 'Iniciar sesión' }}</button>
      </form>
      <p class="small text-center text-secondary mt-4 mb-0">¿Todavía no tienes cuenta? <RouterLink to="/registro" class="fw-semibold text-teal">Crear cuenta</RouterLink></p>
    </section>
  </main>
</template>

<style scoped>
.brand-mark { display: inline-grid; place-items: center; width: 2.5rem; height: 2.5rem; border-radius: .8rem; background: #dff4ee; color: #0f766e; font-weight: 800; font-size: 1.25rem; }
</style>
