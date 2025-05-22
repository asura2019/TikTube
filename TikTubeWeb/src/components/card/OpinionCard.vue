<template>
  <v-card class="opinion-card pa-4" elevation="2" rounded="lg">
    <v-card-title class="text-h6 font-weight-bold d-flex align-center">
      <v-icon icon="mdi-flag" color="error" class="mr-2"></v-icon>
      {{ isReport ? '举报' : '意见反馈' }}
    </v-card-title>

    <v-card-text>
      <v-form ref="form" v-model="valid" lazy-validation>
        <!-- 举报类型选择 -->
        <v-card-title>
          <span v-if="isReport">举报：{{ targetTitle }}</span>
        </v-card-title>
    

        <!-- 举报/反馈内容 -->
        <v-textarea
          v-model="opinionData.userOpinion"
          :label="isReport ? '举报原因' : '反馈内容'"
          :placeholder="isReport ? '请详细描述您举报的原因...' : '请详细描述您的意见或建议...'"
          variant="outlined"
          counter="1000"
          :rules="[
            v => !!v || (isReport ? '请填写举报原因' : '请填写反馈内容'),
            v => v.length <= 1000 || '内容不能超过1000个字符'
          ]"
          rows="5"
          auto-grow
          class="mb-3"
        ></v-textarea>
      </v-form>
    </v-card-text>

    <v-card-actions class="px-4 pb-4">
      <v-spacer></v-spacer>
      <v-btn
        color="grey-darken-1"
        variant="text"
        @click="$emit('close')"
        :disabled="loading"
      >
        取消
      </v-btn>
      <v-btn
        color="error"
        variant="elevated"
        @click="submitOpinion"
        :loading="loading"
        :disabled="loading"
      >
        {{ isReport ? '提交举报' : '提交反馈' }}
      </v-btn>
    </v-card-actions>

    <!-- 提示消息 -->
    <v-snackbar
      v-model="showMessage"
      :color="messageType"
      location="top"
      :timeout="3000"
    >
      {{ message }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="showMessage = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>

    <!-- 未登录提示 -->
    <v-dialog v-model="showLoginDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6">需要登录</v-card-title>
        <v-card-text>
          您需要登录后才能进行{{ isReport ? '举报' : '反馈' }}操作
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="text" @click="showLoginDialog = false">
            取消
          </v-btn>
          <v-btn color="primary" variant="elevated" @click="goToLogin">
            去登录
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script>
import { useUserStore } from '@/stores/userStore'

export default {
  props: {
    targetId: {
      type: Number,
      required: true,
    },
    typeNum: {
      type: Number,
      required: true,
    },
    // 是否为举报模式，false则为意见反馈模式
    isReport: {
      type: Boolean,
      default: true
    },
    targetTitle: {
      type: String,
      required: true,
    }
  },
  emits: ['close', 'success'],
  data() {
    return {
      userInfo: useUserStore(),
      valid: false,
      loading: false,
      showMessage: false,
      message: '',
      messageType: 'success',
      showLoginDialog: false,
      opinionData: {
        targetId: this.targetId,
        type: this.isReport ? this.typeNum : 10, // 10为意见反馈类型
        userOpinion: '',
      },
      reportTypes: [
        { text: '稿件举报', value: 0 },
        { text: '评论举报', value: 1 },
        { text: '弹幕举报', value: 2 },
      ],
      feedbackTypes: [
        { text: '功能建议', value: 10 },
        { text: '内容问题', value: 10 },
        { text: '其他反馈', value: 10 },
      ]
    }
  },
  methods: {
    submitOpinion() {
      // 检查用户是否登录
      if (!this.userInfo.userData) {
        this.showLoginDialog = true
        return
      }

      // 表单验证
      if (!this.$refs.form.validate()) {
        return
      }

      if (this.opinionData.userOpinion.trim() === '') {
        this.messageType = 'error'
        this.message = this.isReport? '请填写举报原因' : '请填写反馈内容'
        this.showMessage = true
        return
      }

      this.loading = true
      
      // 设置目标ID
      this.opinionData.targetId = this.targetId

      // 发送请求
      this.httpPost('/opinion/save', this.opinionData, (json) => {
        this.loading = false
        
        if (json.status === 200) {
          this.messageType = 'success'
          this.message = this.isReport ? '举报已提交，我们会尽快处理' : '感谢您的反馈，我们会认真考虑您的建议'
          this.showMessage = true
          
          // 重置表单
          this.opinionData.userOpinion = ''
          this.$refs.form.reset()
          
          // 通知父组件提交成功
          this.$emit('success')
          
          // 延迟关闭
          setTimeout(() => {
            this.$emit('close')
          }, 3000)
        } else {
          this.messageType = 'error'
          this.message = json.message || '提交失败，请稍后再试'
          this.showMessage = true
          setTimeout(() => {
            this.$emit('close')
          }, 3000)
        }
      })
    },
    goToLogin() {
      this.showLoginDialog = false
      this.$router.push('/login?redirect=' + encodeURIComponent(this.$route.fullPath))
    }
  }
}
</script>

<style scoped>
.opinion-card {
  width: 50vh;
  margin: 0 auto;
}
</style>