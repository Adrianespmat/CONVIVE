import { reactive, watch } from 'vue'

const CLAVE = 'convive.configuracion'
const predeterminada = { mostrarAyudas: true, confirmarEliminaciones: true }

function cargar() {
  try {
    return { ...predeterminada, ...JSON.parse(localStorage.getItem(CLAVE) || '{}') }
  } catch {
    return { ...predeterminada }
  }
}

const configuracion = reactive(cargar())
watch(configuracion, (valor) => localStorage.setItem(CLAVE, JSON.stringify(valor)), { deep: true })

export function restaurarConfiguracion() {
  Object.assign(configuracion, predeterminada)
}

export function useConfiguracion() {
  return { configuracion, predeterminada }
}
