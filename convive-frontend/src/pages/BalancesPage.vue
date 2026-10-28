<script setup>
import { onMounted, ref } from 'vue'
import { conviveApi } from '../services/convive'
import { mensajeError } from '../services/api'
const datos = ref(null); const liquidacion = ref(null); const cargando = ref(true); const error = ref('')
async function cargar() { cargando.value = true; try { const [balances, propuesta] = await Promise.all([conviveApi.obtenerBalances(), conviveApi.obtenerLiquidacion()]); datos.value = balances.data; liquidacion.value = propuesta.data } catch (e) { error.value = mensajeError(e) } finally { cargando.value = false } }
function formato(v) { return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(v || 0) }
onMounted(cargar)
</script>
<template>
  <header class="mb-4"><p class="page-kicker mb-1">Equilibrio del piso</p><h1 class="h2 mb-1">Balances</h1><p class="text-secondary mb-0">Consulta quién ha pagado de más y la propuesta de compensación.</p></header>
  <div v-if="error" class="alert alert-danger">{{ error }}</div><div v-if="cargando" class="text-secondary">Calculando balances…</div>
  <template v-else-if="datos"><section class="card p-3 p-md-4 mb-4"><div class="table-responsive"><table class="table align-middle mb-0"><thead><tr><th>Integrante</th><th>Pagado</th><th>Asumido</th><th class="text-end">Balance</th></tr></thead><tbody><tr v-for="balance in datos.balances" :key="balance.usuarioId"><td class="fw-semibold">{{ balance.nombre }}</td><td>{{ formato(balance.importePagado) }}</td><td>{{ formato(balance.importeAsumido) }}</td><td :class="['text-end fw-bold', balance.balance >= 0 ? 'text-success' : 'text-danger']">{{ formato(balance.balance) }}</td></tr></tbody></table></div></section><section class="card p-4"><h2 class="h5">Liquidación informativa</h2><p class="small text-secondary">Son pagos sugeridos; Convive no ejecuta transferencias.</p><div v-if="!liquidacion?.pagos?.length" class="text-secondary py-3">No hay pagos pendientes.</div><ul v-else class="list-group list-group-flush"><li v-for="(pago, i) in liquidacion.pagos" :key="i" class="list-group-item px-0 d-flex justify-content-between"><span><strong>{{ pago.deudorNombre }}</strong> paga a <strong>{{ pago.acreedorNombre }}</strong></span><span class="fw-bold text-teal">{{ formato(pago.importe) }}</span></li></ul></section></template>
</template>
