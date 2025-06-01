<template>
  <v-container>
    <v-tabs v-model="show">
      <v-tab :value="0" @click="setShow(0)">视频</v-tab>
      <!-- <v-tab @click="setShow(1)">图片</v-tab>
        <v-tab @click="setShow(3)">音乐</v-tab> -->
        <v-tab :value="2" @click="setShow(2)">文章</v-tab> 
    </v-tabs>

    <VideoUpload v-if="show == 0" />
    <UploadText v-if="show == 2" />
  </v-container>
</template>
  
  <script>
import VideoUpload from '@/components/upload/UploadVideo.vue'
import UploadText from '@/components/upload/UploadText.vue'
export default {
  name: 'UploadView',
  components: {
    VideoUpload,
    UploadText
  },
  data() {
    return {
      show: 0,
    }
  },
  created() {
    this.show = 0
    // 获取路由show参数
    let show = parseInt(this.$route.query.show)
    if (show == 0 || show == 2) {
      this.show = show
    } else {
      this.show = 0
    }
  },
  methods: {
    setShow(value) {
      this.show = value
    },
  },
}
</script>
  
  <style>
</style>
  