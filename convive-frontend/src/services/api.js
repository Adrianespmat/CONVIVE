import axios from 'axios'

export const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

export const TOKEN_KEY = 'convive.jwt'

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      window.dispatchEvent(new CustomEvent('convive:sesion-expirada'))
    }
    return Promise.reject(error)
  }
)

export function mensajeError(error, fallback = 'No se ha podido completar la operación.') {
  const datos = error.response?.data
  const campos = datos?.campos ? Object.values(datos.campos).filter(Boolean) : []
  return campos.length ? campos.join(' ') : (datos?.mensaje || datos?.message || fallback)
}
