import { createApp } from 'vue'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import './styles/base.css'
import './styles/utilidades.css'
import App from './App.vue'
import router from './router'

createApp(App).use(router).mount('#app')
