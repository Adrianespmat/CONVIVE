<script setup>
import { useRouter } from 'vue-router'
import { useAutenticacion } from './stores/autenticacion'

const router = useRouter()
const { cerrarSesion } = useAutenticacion()

function salir() {
  cerrarSesion()
  router.push('/login')
}
</script>

<template>
  <router-view v-slot="{ Component }">
    <component :is="Component" @salir="salir" />
  </router-view>
</template>
