<template>
  <v-app>
    <router-view />
  </v-app>
</template>

<script>
import { useWebInfoStore } from '@/stores/webInfoStore'
export default {
  name: 'App',

  data: () => ({
    //
  }),
  mounted() {
    this.httpGet('/web/info', (json) => {
      const webInfoStore = useWebInfoStore()
      webInfoStore.updateWebInfo(json.data)
      const metaDesc = document.querySelector('meta[name="description"]');
      if (metaDesc) {
        metaDesc.setAttribute('content', json.data.webDescribe);
      }
    })
  },
}
</script>

<style>
a {
  text-decoration: none;
}
</style>
