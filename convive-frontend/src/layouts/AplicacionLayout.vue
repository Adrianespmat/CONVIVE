<script setup>
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAutenticacion } from '../stores/autenticacion'

const router = useRouter()
const { estado, cerrarSesion } = useAutenticacion()
const menuAbierto = ref(false)

const enlaces = [
  { to: '/app/panel', icono: 'bi-grid-1x2-fill', texto: 'Panel' },
  { to: '/app/gastos', icono: 'bi-receipt', texto: 'Gastos' },
  { to: '/app/balances', icono: 'bi-arrow-left-right', texto: 'Balances' },
  { to: '/app/piso', icono: 'bi-house', texto: 'Mi piso' },
  { to: '/app/perfil', icono: 'bi-person', texto: 'Perfil' }
]

function salir() {
  cerrarSesion()
  router.push('/login')
}
</script>

<template>
  <div class="app-shell d-flex flex-column flex-md-row">
    <aside class="sidebar p-3 p-md-4 d-flex flex-column">
      <div class="d-flex align-items-center justify-content-between mb-4">
        <RouterLink to="/app/panel" class="text-white text-decoration-none d-flex align-items-center gap-2">
          <span class="brand-mark">C</span><strong class="fs-4">Convive</strong>
        </RouterLink>
        <button class="btn btn-sm btn-outline-light d-md-none" @click="menuAbierto = !menuAbierto"><i class="bi bi-list"></i></button>
      </div>
      <nav :class="['nav flex-column gap-1', { 'd-none d-md-flex': !menuAbierto }]">
        <RouterLink v-for="enlace in enlaces" :key="enlace.to" :to="enlace.to" class="nav-link px-3 py-2" @click="menuAbierto = false">
          <i :class="['bi me-2', enlace.icono]"></i>{{ enlace.texto }}
        </RouterLink>
      </nav>
      <div class="mt-auto pt-4 d-none d-md-block">
        <div class="small opacity-75 mb-2">Sesión iniciada como</div>
        <div class="text-white text-truncate mb-3">{{ estado.usuario?.nombre }}</div>
        <button class="btn btn-sm btn-outline-light w-100" @click="salir"><i class="bi bi-box-arrow-right me-2"></i>Cerrar sesión</button>
      </div>
    </aside>
    <main class="main-content flex-grow-1 p-3 p-lg-5">
      <div class="container-fluid px-0" style="max-width: 1180px"><router-view /></div>
    </main>
  </div>
</template>

<style scoped>
.brand-mark { display: inline-grid; place-items: center; width: 2rem; height: 2rem; border-radius: .7rem; background: #b7eee0; color: #173b3a; font-weight: 800; }
</style>
