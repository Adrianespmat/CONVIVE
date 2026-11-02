<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { mensajeError } from '../services/api'

const datos = ref(null)
const liquidacion = ref(null)
const cargando = ref(true)
const error = ref('')
const pestana = ref('balances')
const filtros = reactive({ desde: '', hasta: '' })

const balancesOrdenados = computed(() => datos.value?.balances || [])
const acreedores = computed(() => balancesOrdenados.value.filter((balance) => Number(balance.balance) > 0))
const deudores = computed(() => balancesOrdenados.value.filter((balance) => Number(balance.balance) < 0))
const totalPagado = computed(() => balancesOrdenados.value.reduce((total, balance) => total + Number(balance.importePagado || 0), 0))
const totalAsumido = computed(() => balancesOrdenados.value.reduce((total, balance) => total + Number(balance.importeAsumido || 0), 0))
const hayFiltros = computed(() => Boolean(filtros.desde || filtros.hasta))

function parametrosFiltro() {
  return Object.fromEntries(Object.entries(filtros).filter(([, valor]) => valor !== ''))
}

function validarPeriodo() {
  if (filtros.desde && filtros.hasta && filtros.desde > filtros.hasta) {
    return 'La fecha inicial no puede ser posterior a la fecha final.'
  }
  return ''
}

async function cargar() {
  error.value = ''
  const validacion = validarPeriodo()
  if (validacion) {
    error.value = validacion
    return
  }
  cargando.value = true
  try {
    const parametros = parametrosFiltro()
    const [respuestaBalances, respuestaLiquidacion] = await Promise.all([
      conviveApi.obtenerBalances(parametros),
      conviveApi.obtenerLiquidacion(parametros)
    ])
    datos.value = respuestaBalances.data
    liquidacion.value = respuestaLiquidacion.data
  } catch (e) {
    error.value = mensajeError(e, 'No se han podido calcular los balances.')
  } finally {
    cargando.value = false
  }
}

function limpiarFiltros() {
  filtros.desde = ''
  filtros.hasta = ''
  cargar()
}

function formatoImporte(valor) {
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(Number(valor) || 0)
}

function formatoFecha(valor) {
  if (!valor) return 'Sin límite'
  return new Intl.DateTimeFormat('es-ES', { dateStyle: 'medium' }).format(new Date(`${valor}T12:00:00`))
}

function etiquetaBalance(valor) {
  if (Number(valor) > 0) return 'A favor'
  if (Number(valor) < 0) return 'Pendiente'
  return 'Equilibrado'
}

function claseBalance(valor) {
  if (Number(valor) > 0) return 'text-success'
  if (Number(valor) < 0) return 'text-danger'
  return 'text-secondary'
}

onMounted(cargar)
</script>

<template>
  <header class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
    <div>
      <p class="page-kicker mb-1">Equilibrio del piso</p>
      <h1 class="h2 mb-1">Balances y liquidación</h1>
      <p class="text-secondary mb-0">Comprueba quién ha adelantado dinero y cómo compensarlo.</p>
    </div>
    <button class="btn btn-outline-secondary btn-sm" type="button" :disabled="cargando" @click="cargar"><i class="bi bi-arrow-clockwise me-1"></i>Actualizar</button>
  </header>

  <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
    <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    <button type="button" class="btn-close" aria-label="Cerrar" @click="error = ''"></button>
  </div>

  <section class="card p-3 p-md-4 mb-4">
    <form class="row g-3 align-items-end" @submit.prevent="cargar">
      <div class="col-12 col-sm-5 col-lg-4"><label class="form-label small" for="desde">Desde</label><input id="desde" v-model="filtros.desde" class="form-control" type="date" /></div>
      <div class="col-12 col-sm-5 col-lg-4"><label class="form-label small" for="hasta">Hasta</label><input id="hasta" v-model="filtros.hasta" class="form-control" type="date" /></div>
      <div class="col-12 col-sm-2 col-lg-4 d-flex gap-2"><button class="btn btn-primary flex-grow-1" type="submit">Calcular</button><button v-if="hayFiltros" class="btn btn-outline-secondary" type="button" title="Limpiar periodo" @click="limpiarFiltros"><i class="bi bi-x-lg"></i></button></div>
    </form>
    <div class="small text-secondary mt-3"><i class="bi bi-calendar3 me-1"></i>Periodo: {{ formatoFecha(datos?.desde) }} — {{ formatoFecha(datos?.hasta) }}</div>
  </section>

  <div v-if="cargando" class="text-center text-secondary py-5"><span class="spinner-border spinner-border-sm me-2"></span>Calculando balances…</div>
  <template v-else-if="datos && liquidacion">
    <section class="row g-3 mb-4">
      <div class="col-12 col-md-4"><div class="card metric-card p-4 h-100"><div class="small text-secondary mb-2">Gasto asumido por el piso</div><div class="metric-value">{{ formatoImporte(totalAsumido) }}</div><div class="small text-secondary mt-2">Suma de las partes individuales</div></div></div>
      <div class="col-12 col-md-4"><div class="card metric-card p-4 h-100 bg-mint"><div class="small text-secondary mb-2">Pagado por integrantes</div><div class="metric-value text-teal">{{ formatoImporte(totalPagado) }}</div><div class="small text-secondary mt-2">Adelantos registrados en el periodo</div></div></div>
      <div class="col-12 col-md-4"><div class="card metric-card p-4 h-100"><div class="small text-secondary mb-2">Pagos sugeridos</div><div class="metric-value">{{ liquidacion.pagos.length }}</div><div class="small text-secondary mt-2">{{ formatoImporte(liquidacion.importeTotal) }} a compensar</div></div></div>
    </section>

    <div class="balance-tabs d-flex gap-2 mb-3" role="tablist" aria-label="Secciones de balances">
      <button :class="['btn', pestana === 'balances' ? 'btn-primary' : 'btn-outline-secondary']" type="button" @click="pestana = 'balances'"><i class="bi bi-bar-chart me-2"></i>Balances</button>
      <button :class="['btn', pestana === 'liquidacion' ? 'btn-primary' : 'btn-outline-secondary']" type="button" @click="pestana = 'liquidacion'"><i class="bi bi-arrow-left-right me-2"></i>Liquidación <span class="badge rounded-pill ms-1" :class="pestana === 'liquidacion' ? 'text-bg-light text-teal' : 'text-bg-secondary'">{{ liquidacion.pagos.length }}</span></button>
    </div>

    <section v-if="pestana === 'balances'" class="card p-3 p-md-4">
      <div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3"><div><h2 class="h5 mb-1">Balance por integrante</h2><p class="small text-secondary mb-0">Balance = importe pagado − importe que corresponde asumir.</p></div><span class="small text-secondary">{{ balancesOrdenados.length }} integrante{{ balancesOrdenados.length === 1 ? '' : 's' }}</span></div>
      <div v-if="!balancesOrdenados.length" class="empty-state text-center text-secondary py-5"><i class="bi bi-bar-chart display-6 d-block mb-3"></i>No hay integrantes o gastos en este periodo.</div>
      <div v-else class="table-responsive"><table class="table align-middle mb-0"><thead><tr><th>Integrante</th><th class="text-end">Ha pagado</th><th class="text-end">Debe asumir</th><th class="text-end">Balance</th></tr></thead><tbody><tr v-for="balance in balancesOrdenados" :key="balance.usuarioId"><td><strong>{{ balance.nombre }}</strong><span :class="['d-block small', claseBalance(balance.balance)]">{{ etiquetaBalance(balance.balance) }}</span></td><td class="text-end">{{ formatoImporte(balance.importePagado) }}</td><td class="text-end">{{ formatoImporte(balance.importeAsumido) }}</td><td :class="['text-end fw-bold fs-6', claseBalance(balance.balance)]">{{ Number(balance.balance) > 0 ? '+' : '' }}{{ formatoImporte(balance.balance) }}</td></tr></tbody></table></div>
      <div class="d-flex flex-wrap gap-3 small text-secondary mt-4"><span><i class="bi bi-circle-fill text-success me-1"></i>A favor: {{ acreedores.length }}</span><span><i class="bi bi-circle-fill text-danger me-1"></i>Pendientes: {{ deudores.length }}</span></div>
    </section>

    <section v-else class="card p-3 p-md-4">
      <div class="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3"><div><h2 class="h5 mb-1">Propuesta de liquidación</h2><p class="small text-secondary mb-0">{{ formatoImporte(liquidacion.importeTotal) }} distribuidos en {{ liquidacion.pagos.length }} pago{{ liquidacion.pagos.length === 1 ? '' : 's' }}.</p></div><span class="badge text-bg-warning"><i class="bi bi-info-circle me-1"></i>Informativa</span></div>
      <div class="alert alert-info small"><i class="bi bi-shield-check me-2"></i>Esta propuesta solo orienta la compensación entre compañeros. Convive no realiza transferencias bancarias.</div>
      <div v-if="!liquidacion.pagos.length" class="empty-state text-center text-secondary py-5"><i class="bi bi-check2-circle display-6 text-success d-block mb-3"></i>No hay pagos pendientes. El piso está equilibrado en este periodo.</div>
      <div v-else class="settlement-list"><article v-for="(pago, indice) in liquidacion.pagos" :key="`${pago.deudorUsuarioId}-${pago.acreedorUsuarioId}-${indice}`" class="settlement-item d-flex flex-wrap align-items-center justify-content-between gap-3"><div class="d-flex align-items-center gap-3"><div class="settlement-avatar bg-danger-subtle text-danger"><i class="bi bi-person"></i></div><div><strong>{{ pago.deudorNombre }}</strong><span class="text-secondary mx-2">paga a</span><strong>{{ pago.acreedorNombre }}</strong><div class="small text-secondary">Compensación sugerida</div></div></div><div class="fs-5 fw-bold text-teal">{{ formatoImporte(pago.importe) }}</div></article></div>
      <div v-if="liquidacion.pagos.length" class="small text-secondary mt-4"><i class="bi bi-lightbulb me-1"></i>La propuesta reduce el número de pagos agrupando los saldos positivos y negativos.</div>
    </section>
  </template>
</template>

<style scoped>
.metric-card { min-height: 145px; }
.balance-tabs .btn { border-radius: 999px; }
.empty-state { border: 1px dashed var(--convive-line); border-radius: .75rem; }
.settlement-list { display: grid; gap: .75rem; }
.settlement-item { padding: 1rem; border: 1px solid var(--convive-line); border-radius: .85rem; background: #fbfdfc; }
.settlement-avatar { display: grid; place-items: center; width: 2.5rem; height: 2.5rem; border-radius: 50%; flex: 0 0 auto; }
</style>
