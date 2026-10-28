<script setup>
import { onMounted, reactive, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { mensajeError } from '../services/api'

const panel = ref(null)
const cargando = ref(true)
const error = ref('')
const filtros = reactive({ desde: '', hasta: '' })

async function cargar() {
  cargando.value = true; error.value = ''
  try { panel.value = (await conviveApi.obtenerPanel(filtros)).data } catch (e) { error.value = mensajeError(e) } finally { cargando.value = false }
}
function formatoImporte(valor) { return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(valor || 0) }
function formatoFecha(valor) { return new Intl.DateTimeFormat('es-ES', { dateStyle: 'medium' }).format(new Date(`${valor}T12:00:00`)) }
onMounted(cargar)
</script>

<template>
  <header class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div><p class="text-uppercase small fw-bold text-teal mb-1">Resumen de tu vivienda</p><h1 class="h2 mb-1">Hola, {{ panel?.balancePersonal?.nombre || 'equipo' }}</h1><p class="text-secondary mb-0">Cada gasto en su sitio.</p></div>
    <div class="d-flex gap-2 align-items-end"><div><label class="form-label small mb-1">Desde</label><input v-model="filtros.desde" type="date" class="form-control form-control-sm" /></div><div><label class="form-label small mb-1">Hasta</label><input v-model="filtros.hasta" type="date" class="form-control form-control-sm" /></div><button class="btn btn-primary btn-sm" @click="cargar"><i class="bi bi-arrow-clockwise"></i></button></div>
  </header>
  <div v-if="error" class="alert alert-danger">{{ error }}</div>
  <div v-if="cargando" class="text-center py-5 text-secondary"><div class="spinner-border spinner-border-sm me-2"></div>Cargando tu panel…</div>
  <template v-else-if="panel">
    <section class="row g-3 mb-4">
      <div class="col-12 col-md-4"><div class="card p-4 h-100"><div class="small text-secondary mb-2">Gastos del periodo</div><div class="metric-value">{{ formatoImporte(panel.totalGastos) }}</div></div></div>
      <div class="col-12 col-md-4"><div class="card p-4 h-100 bg-mint"><div class="small text-secondary mb-2">Tu balance</div><div :class="['metric-value', panel.balancePersonal.balance >= 0 ? 'text-teal' : 'text-danger']">{{ formatoImporte(panel.balancePersonal.balance) }}</div><div class="small text-secondary mt-2">Pagado {{ formatoImporte(panel.balancePersonal.importePagado) }} · Asumido {{ formatoImporte(panel.balancePersonal.importeAsumido) }}</div></div></div>
      <div class="col-12 col-md-4"><div class="card p-4 h-100"><div class="small text-secondary mb-2">Actividad reciente</div><div class="metric-value">{{ panel.ultimosGastos.length }}</div><div class="small text-secondary mt-2">gastos en este periodo</div></div></div>
    </section>
    <section class="card p-3 p-md-4"><div class="d-flex justify-content-between align-items-center mb-3"><h2 class="h5 mb-0">Últimos gastos</h2><RouterLink to="/app/gastos" class="btn btn-sm btn-outline-secondary">Ver todos</RouterLink></div><div v-if="!panel.ultimosGastos.length" class="text-center text-secondary py-4">Todavía no hay gastos en este periodo.</div><div v-else class="table-responsive"><table class="table align-middle mb-0"><thead><tr><th>Concepto</th><th>Fecha</th><th>Pagador</th><th class="text-end">Importe</th></tr></thead><tbody><tr v-for="gasto in panel.ultimosGastos" :key="gasto.id"><td class="fw-semibold">{{ gasto.concepto }}<div class="small text-secondary">{{ gasto.categoria }}</div></td><td>{{ formatoFecha(gasto.fecha) }}</td><td>{{ gasto.pagadorNombre }}</td><td class="text-end fw-semibold">{{ formatoImporte(gasto.importe) }}</td></tr></tbody></table></div></section>
  </template>
</template>
