<script setup>
import { ref } from 'vue'
import { restaurarConfiguracion, useConfiguracion } from '../stores/configuracion'

const { configuracion } = useConfiguracion()
const mensaje = ref('')

function guardar() {
  mensaje.value = 'Preferencias guardadas en este navegador.'
  window.setTimeout(() => { mensaje.value = '' }, 3500)
}

function restaurar() {
  restaurarConfiguracion()
  mensaje.value = 'Preferencias restauradas.'
}
</script>

<template>
  <header class="mb-4">
    <p class="page-kicker mb-1">Personalización</p>
    <h1 class="h2 mb-1">Configuración</h1>
    <p class="text-secondary mb-0">Ajusta el comportamiento de Convive en este navegador.</p>
  </header>

  <div v-if="mensaje" class="alert alert-success"><i class="bi bi-check-circle me-2"></i>{{ mensaje }}</div>

  <section class="card p-4 mb-4" style="max-width: 760px">
    <div class="d-flex align-items-start gap-3 mb-4"><div class="setting-icon bg-mint text-teal"><i class="bi bi-sliders"></i></div><div><h2 class="h5 mb-1">Preferencias de la aplicación</h2><p class="small text-secondary mb-0">Estas preferencias se guardan solo en este navegador y no modifican los datos del piso.</p></div></div>
    <div class="setting-row py-3 border-top"><div><strong>Mostrar ayudas contextuales</strong><p class="small text-secondary mb-0">Muestra explicaciones breves junto a los formularios y acciones.</p></div><div class="form-check form-switch"><input id="mostrarAyudas" v-model="configuracion.mostrarAyudas" class="form-check-input" type="checkbox" role="switch" /><label class="visually-hidden" for="mostrarAyudas">Mostrar ayudas contextuales</label></div></div>
    <div class="setting-row py-3 border-top"><div><strong>Confirmar eliminaciones</strong><p class="small text-secondary mb-0">Pide confirmación antes de eliminar gastos u otros elementos.</p></div><div class="form-check form-switch"><input id="confirmarEliminaciones" v-model="configuracion.confirmarEliminaciones" class="form-check-input" type="checkbox" role="switch" /><label class="visually-hidden" for="confirmarEliminaciones">Confirmar eliminaciones</label></div></div>
    <div class="d-flex flex-wrap gap-2 mt-4"><button class="btn btn-primary" type="button" @click="guardar"><i class="bi bi-check2 me-2"></i>Guardar preferencias</button><button class="btn btn-outline-secondary" type="button" @click="restaurar"><i class="bi bi-arrow-counterclockwise me-2"></i>Restaurar</button></div>
  </section>

  <section class="card p-4" style="max-width: 760px"><div class="d-flex align-items-start gap-3"><div class="setting-icon bg-light text-secondary"><i class="bi bi-info-circle"></i></div><div><h2 class="h5 mb-1">Sobre la configuración</h2><p class="small text-secondary mb-0">El idioma de Convive es español y los importes se muestran en euros, según el alcance aprobado del proyecto. Las preferencias de cuenta y los datos económicos siempre proceden del backend.</p></div></div></section>
</template>

<style scoped>
.setting-icon { display: grid; place-items: center; width: 2.5rem; height: 2.5rem; border-radius: .75rem; flex: 0 0 auto; font-size: 1.1rem; }
.setting-row { display: flex; justify-content: space-between; align-items: center; gap: 1rem; }
.form-check-input { width: 2.6rem; height: 1.35rem; cursor: pointer; }
</style>
