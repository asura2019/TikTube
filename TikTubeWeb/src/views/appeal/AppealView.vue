<template>
  <v-container class="d-flex justify-center align-center">
    <v-card width="80vh" class="appeal-card pa-4" elevation="1" rounded="lg">
      <v-card-title class="text-h6 font-weight-bold d-flex align-center">
        <v-icon icon="mdi-gavel" color="primary" class="mr-2"></v-icon>
        内容申诉
      </v-card-title>

      <v-card-text>
        <v-form ref="form" v-model="valid" lazy-validation>
          <!-- 申诉类型显示 -->
          <v-card-title>
            <span>申诉：{{ getAppealTypeText() }}</span>
          </v-card-title>

          <!-- 申诉内容 -->
          <v-textarea
            v-model="appeal.content"
            label="申诉理由"
            placeholder="请详细描述您的申诉理由，以便我们更好地审核..."
            variant="outlined"
            counter="1000"
            :rules="[
              v => !!v || '请填写申诉理由',
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
          @click="goBack"
          :disabled="loading"
        >
          取消
        </v-btn>
        <v-btn
          color="primary"
          variant="elevated"
          @click="submitAppeal"
          :loading="loading"
          :disabled="loading"
        >
          提交申诉
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
            您需要登录后才能进行申诉操作
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
  </v-container>
</template>

<script>
import { useUserStore } from '@/stores/userStore'

export default {
  data() {
    return {
      userInfo: useUserStore(),
      valid: false,
      loading: false,
      showMessage: false,
      message: '',
      messageType: 'success',
      showLoginDialog: false,
      // 申诉
      appeal: {
        // 申诉内容
        targetId: 0,
        type: 0,
        userOpinion: '',
      },
      // 申诉类型映射
      appealTypes: {
        'article': 3,  // 稿件申诉
        'comment': 4,  // 评论申诉
        'danmaku': 5   // 弹幕申诉
      },
      // 申诉类型文本
      appealTypeTexts: {
        'article': '稿件',
        'comment': '评论',
        'danmaku': '弹幕'
      }
    }
  },
  created() {
    // 初始化
    this.appeal.targetId = parseInt(this.$route.query.id)
    if (this.appeal.targetId == 0 || this.appeal.targetId == 'undefined' || isNaN(this.appeal.targetId)) {
      this.$router.push('/')
      return
    }
    
    // 设置申诉类型
    const targetType = this.$route.query.type
    if (!targetType || !this.appealTypes[targetType]) {
      this.$router.push('/')
      return
    }
    
    this.appeal.type = this.appealTypes[targetType]
  },
  methods: {
    // 获取申诉类型文本
    getAppealTypeText() {
      const targetType = this.$route.query.type
      return this.appealTypeTexts[targetType] || '未知内容'
    },
    
    // 提交申诉
    submitAppeal() {
      // 检查用户是否登录
      if (!this.userInfo.userData) {
        this.showLoginDialog = true
        return
      }

      // 表单验证
      if (!this.$refs.form.validate()) {
        return
      }

      this.loading = true
      
      // 准备提交数据
      const appealData = {
        targetId: this.appeal.targetId,
        type: this.appeal.type,
        userOpinion: this.appeal.userOpinion
      }
      
      // 将表单内容赋值给userOpinion字段
      appealData.userOpinion = this.appeal.content
      
      // 发送请求
      this.httpPost('/opinion/save', appealData, (json) => {
        this.loading = false
        
        if (json.status === 200) {
          this.messageType = 'success'
          this.message = '申诉已提交，我们会尽快处理'
          this.showMessage = true
          
          // 重置表单
          this.appeal.content = ''
          this.$refs.form.reset()
        } else {
          this.messageType = 'error'
          this.message = '你已经提交过申诉或无权申诉此内容，即将返回上一页！'
          this.showMessage = true
        }
        // 延迟返回
        setTimeout(() => {
            this.goBack()
          }, 2000)
      })
    },
    
    // 返回上一页
    goBack() {
      this.$router.go(-1)
    },
    
    // 跳转到登录页
    goToLogin() {
      this.showLoginDialog = false
      this.$router.push('/login?redirect=' + encodeURIComponent(this.$route.fullPath))
    }
  }
}
</script>

<style scoped>
.appeal-card {
  width: 50vh;
  margin: 0 auto;
}
</style>