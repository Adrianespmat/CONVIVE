import { createRouter, createWebHistory } from 'vue-router'
import { useAutenticacion } from '../stores/autenticacion'

const rutas = [
  { path: '/', redirect: '/app' },
  { path: '/login', component: () => import('../pages/LoginPage.vue'), meta: { publica: true } },
  { path: '/registro', component: () => import('../pages/RegistroPage.vue'), meta: { publica: true } },
  {
    path: '/app',
    component: () => import('../layouts/AplicacionLayout.vue'),
    children: [
      { path: '', redirect: '/app/panel' },
      { path: 'panel', component: () => import('../pages/PanelPage.vue') },
      { path: 'piso', component: () => import('../pages/PisoPage.vue') },
      { path: 'gastos', component: () => import('../pages/GastosPage.vue') },
      { path: 'balances', component: () => import('../pages/BalancesPage.vue') },
      { path: 'perfil', component: () => import('../pages/PerfilPage.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/app' }
]

const router = createRouter({ history: createWebHistory(), routes: rutas })

router.beforeEach((to) => {
  const { autenticado } = useAutenticacion()
  if (!to.meta.publica && !autenticado.value) return '/login'
  if (to.meta.publica && autenticado.value) return '/app'
})

export default router
