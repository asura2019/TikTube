import { createApp } from 'vue'
import { createPinia } from 'pinia'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import App from './App.vue'
import router from './router'
import HttpFetch from './utils/fetch'

import './assets/css/Main.css'

const app = createApp(App)

app.config.globalProperties.SERVER_API_URL = '/api'

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
  },
})

console.log('%c不挂高数出品 https://www.buguagaoshu.com', 'color: #1976d2;font-size:2em')
console.log('%cPowered by buguagaosh', 'color: #1976d2;font-size:1em')

app.use(createPinia())
app.use(router)
app.use(vuetify)
app.use(HttpFetch)
app.mount('#app')
