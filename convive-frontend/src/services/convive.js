import { api } from './api'

export const conviveApi = {
  obtenerPanel(params = {}) { return api.get('/dashboard', { params }) },
  obtenerPiso() { return api.get('/pisos/mio') },
  crearPiso(datos) { return api.post('/pisos', datos) },
  unirsePiso(codigoInvitacion) { return api.post('/pisos/unirse', { codigoInvitacion }) },
  actualizarPiso(datos) { return api.patch('/pisos/mio', datos) },
  obtenerIntegrantes() { return api.get('/pisos/mio/integrantes') },
  eliminarIntegrante(id) { return api.delete(`/pisos/mio/integrantes/${id}`) },
  obtenerGastos(params = {}) { return api.get('/gastos', { params }) },
  crearGasto(datos) { return api.post('/gastos', datos) },
  actualizarGasto(id, datos) { return api.put(`/gastos/${id}`, datos) },
  eliminarGasto(id) { return api.delete(`/gastos/${id}`) },
  obtenerBalances(params = {}) { return api.get('/balances', { params }) },
  obtenerLiquidacion(params = {}) { return api.get('/balances/liquidacion', { params }) },
  obtenerPerfil() { return api.get('/users/me') },
  actualizarPerfil(datos) { return api.patch('/users/me', datos) }
}
