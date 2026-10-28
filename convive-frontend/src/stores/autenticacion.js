import { computed, reactive } from 'vue'
import { api, TOKEN_KEY, mensajeError } from '../services/api'

const usuarioGuardado = localStorage.getItem('convive.usuario')
const estado = reactive({
  token: localStorage.getItem(TOKEN_KEY),
  usuario: usuarioGuardado ? JSON.parse(usuarioGuardado) : null,
  cargando: false,
  error: ''
})

function guardarSesion(respuesta) {
  estado.token = respuesta.token
  estado.usuario = respuesta.usuario
  localStorage.setItem(TOKEN_KEY, respuesta.token)
  localStorage.setItem('convive.usuario', JSON.stringify(respuesta.usuario))
}

async function registrar(datos) {
  estado.cargando = true
  estado.error = ''
  try {
    const { data } = await api.post('/autenticacion/registro', datos)
    guardarSesion(data)
    return data
  } catch (error) {
    estado.error = mensajeError(error, 'No se ha podido crear la cuenta.')
    throw error
  } finally {
    estado.cargando = false
  }
}

async function iniciarSesion(datos) {
  estado.cargando = true
  estado.error = ''
  try {
    const { data } = await api.post('/autenticacion/login', datos)
    guardarSesion(data)
    return data
  } catch (error) {
    estado.error = mensajeError(error, 'El correo o la contraseña no son correctos.')
    throw error
  } finally {
    estado.cargando = false
  }
}

function cerrarSesion() {
  estado.token = null
  estado.usuario = null
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem('convive.usuario')
}

async function actualizarUsuario(datos) {
  const { data } = await api.patch('/users/me', datos)
  estado.usuario = data
  localStorage.setItem('convive.usuario', JSON.stringify(data))
  return data
}

window.addEventListener('convive:sesion-expirada', cerrarSesion)

export function useAutenticacion() {
  return {
    estado,
    autenticado: computed(() => Boolean(estado.token)),
    registrar,
    iniciarSesion,
    cerrarSesion,
    actualizarUsuario
  }
}
