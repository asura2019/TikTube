<template>
  <!-- 弹窗公告  persistent-->
  <v-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    max-width="600"
    persistent
    content-class="notice-dialog"
  >
    <v-card
      v-if="notice"
      class="notice-card"
      :class="{ 'has-image': notice.imageUrl }"
      :style="
        notice.imageUrl
          ? `background-image: linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)), url(${notice.imageUrl}); background-size: cover; background-position: center;`
          : ''
      "
    >
      <!-- 关闭按钮 -->
      <v-btn
        icon="mdi-close"
        variant="text"
        color="white"
        size="small"
        class="close-btn"
        @click="closeDialog"
      ></v-btn>

      <v-container class="pa-6">
        <v-row>
          <!-- 标题区域 -->
          <v-col cols="12" class="pb-0">
            <h2 class="text-h4 font-weight-bold mb-2" :class="{ 'text-white': notice.imageUrl }">
              {{ notice.title }}
            </h2>
          </v-col>

          <!-- 视频区域 -->
          <v-col v-if="notice.videoUrl" cols="12" class="py-2">
            <v-sheet rounded elevation="2" class="overflow-hidden">
              <video controls width="100%" height="auto" class="video-player">
                <source :src="notice.videoUrl" type="video/mp4" />
                您的浏览器不支持视频播放
              </video>
            </v-sheet>
          </v-col>

          <!-- 内容区域 -->
          <v-col cols="12" class="pt-2">
            <v-card-text class="pa-0" :class="{ 'text-white': notice.imageUrl }">
              <div v-if="notice.url" class="content-text">
                <a
                  :href="notice.url"
                  target="_blank"
                  class="text-decoration-none text-h6"
                  :class="{ 'white--text': notice.imageUrl }"
                >
                  {{ notice.content }}
                  <v-icon icon="mdi-open-in-new" size="small" class="ms-1"></v-icon>
                </a>
              </div>
              <div v-else class="content-text text-body-1">
                {{ notice.content }}
              </div>
            </v-card-text>
          </v-col>

          <!-- 按钮区域 -->
          <v-col cols="12" class="pt-4 d-flex justify-end">
            <v-btn
              color="primary"
              variant="elevated"
              rounded="pill"
              size="large"
              class="px-6"
              @click="closeDialog"
            >
              我知道了
            </v-btn>
          </v-col>
        </v-row>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: 'NoticeDialogCard',
  props: {
    // 使用 modelValue 作为 v-model 的绑定值
    modelValue: {
      type: Boolean,
      default: false,
    },
    // 公告信息对象
    notice: {
      type: Object,
      default: null,
    },
    closedPopupId: {
      type: String,
      default: 'closedPopupId',
    },
  },
  emits: ['update:modelValue'],
  methods: {
    // 关闭对话框
    closeDialog() {
      // 如果有公告信息，则保存到 localStorage
      if (this.notice && this.notice.id) {
        // 保存已关闭的弹窗ID到本地存储
        localStorage.setItem(this.closedPopupId, this.notice.id.toString())
      }
      this.httpPost(`/web/notice/click/${this.notice.id}`, {}, (json) => {
        // console.log(json)
      })

      // 通知父组件更新状态
      this.$emit('update:modelValue', false)
    },
  },
}
</script>

<style scoped>
.notice-dialog {
  backdrop-filter: blur(8px);
}

.notice-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.notice-card.has-image {
  color: white;
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
}

.video-player {
  border-radius: 8px;
  display: block;
}

.content-text {
  line-height: 1.6;
  margin-top: 8px;
}

a {
  display: inline-flex;
  align-items: center;
  color: #3ea6ff;
  font-weight: 500;
  transition: color 0.2s;
}

a:hover {
  color: #65b7ff;
  text-decoration: underline !important;
}

a.white--text {
  color: white;
}

a.white--text:hover {
  color: #e0e0e0;
}
</style>