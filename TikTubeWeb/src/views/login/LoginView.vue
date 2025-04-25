<template>
  <v-main class="login-view">
    <v-container fill-height>
      <v-row justify="center" align="center">
        <v-col cols="12" sm="8" md="6" lg="5">
          <v-card class="mx-auto elevation-6 rounded-lg" color="surface">
            <v-card-item>
              <v-row class="mt-2 mb-2" justify="start">
                <v-btn
                  color="primary"
                  variant="text"
                  prepend-icon="mdi-arrow-left"
                  @click="backHome"
                >
                  返回
                </v-btn>
              </v-row>
              <v-row justify="center" class="mb-4">
                <v-img :src="webInfo.logoUrl" height="40" class="mx-auto" contain></v-img>
              </v-row>
              <v-row justify="center" class="mb-6">
                <h1 class="text-h4 font-weight-bold primary--text text-center">
                  {{ webInfo.name }} {{ type }}
                </h1>
              </v-row>
            </v-card-item>

            <v-card-text>
              <LoginFrom v-if="showLogin" @login="userLogin" />
              <RegisterFrom v-else @register="register" />
            </v-card-text>

            <v-card-actions class="px-6 mb-3">
              <v-btn variant="text" color="primary"> 忘记密码 </v-btn>
              <v-spacer></v-spacer>
              <v-btn variant="text" color="primary" @click="moveRegister">
                {{ moveMessage }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
      <v-snackbar v-model="showMessage" :timeout="3000" color="error" location="top" rounded="pill">
        {{ message }}
        <template v-slot:actions>
          <v-btn color="white" variant="text" @click="showMessage = false"> 关闭 </v-btn>
        </template>
      </v-snackbar>

      <!-- 两步认证弹框 -->
      <v-dialog v-model="showTotpDialog" persistent max-width="400">
        <v-card>
          <v-card-title class="text-h5 font-weight-bold primary--text"> 两步验证 </v-card-title>
          <v-card-text>
            <p class="mb-4">请输入您的两步验证码以完成登录</p>
            <v-otp-input
              v-model="totpCode"
              length="6"
              label="验证码"
              type="text"
              variant="outlined"
              :rules="[(v) => !!v || '验证码不能为空']"
              maxlength="6"
              class="mb-2"
            ></v-otp-input>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="grey" variant="text" @click="cancelTotp"> 取消 </v-btn>
            <v-btn color="primary" @click="loginTOTP" :disabled="!totpCode"> 确认 </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </v-main>
</template>

<script>
import LoginFrom from '@/components/form/LoginForm.vue'
import RegisterFrom from '@/components/form/RegisterForm.vue'
import { useWebInfoStore } from '@/stores/webInfoStore'
import { useUserStore } from '@/stores/userStore'
import StringUtils from '@/utils/string-utils.vue'
export default {
  name: 'LoginView',
  components: {
    LoginFrom,
    RegisterFrom,
  },
  data() {
    return {
      verifyImageUrl: '/api/verifyImage',
      user: useUserStore(),
      type: '登录',
      moveMessage: '没有账号，创建账号',
      showLogin: true,
      message: '',
      showMessage: false,
      webInfo: {},
      totpCode: '',
      totpUserInfo: {},
      showTotpDialog: false,
    }
  },
  created() {
    this.webInfo = useWebInfoStore().webInfo
  },
  methods: {
    userLogin(value) {
      this.httpPost('/login', value, (json) => {
        if (json.status === 200) {
          const userData = json.data
          // 检查用户是否开启两步认证
          if (!userData.loginStatus && userData.otp === 1) {
            // 弹出两步认证验证码输入弹窗
            this.totpUserInfo = userData
            this.showTotpDialog = true
            this.totpCode = ''
          } else {
            // 保存用户
            this.user.setUserData(userData)
            // 检查是否有重定向参数
            const redirect = this.$route.query.redirect
            // 如果有重定向参数，则跳转到指定页面，否则跳转到首页
            this.$router.push(redirect || '/')
          }
        } else if (json.status === 4002) {
          this.message = StringUtils.dataErrorMessage(json.data)
          this.showMessage = true
        } else if (json.status === 1001) {
          this.message = json.data
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    loginTOTP() {
      const data = {
        code: this.totpCode,
        key: this.totpUserInfo.key,
      }
      this.httpPost('/login/totp', data, (json) => {
        if (json.status === 200) {
          const userData = json.data
          this.user.setUserData(userData)
          // 关闭弹窗
          this.showTotpDialog = false
          // 检查是否有重定向参数
          const redirect = this.$route.query.redirect
          // 如果有重定向参数，则跳转到指定页面，否则跳转到首页
          this.$router.push(redirect || '/')
        } else if (json.status === 4002) {
          this.message = StringUtils.dataErrorMessage(json.data)
          this.showMessage = true
        } else if (json.status === 1001) {
          this.message = json.data
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    cancelTotp() {
      // 取消两步验证，关闭弹窗
      this.showTotpDialog = false
      this.totpCode = ''
      this.totpUserInfo = {}
    },
    register(value) {
      this.httpPost('/register', value, (json) => {
        if (json.status === 200) {
          this.message = '注册成功，即将为你跳转到登录页面！'
          this.showMessage = true
          this.moveRegister()
        } else if (json.status === 4002) {
          this.message = StringUtils.dataErrorMessage(json.data)
          this.showMessage = true
        } else {
          this.message = json.message
          this.showMessage = true
        }
      })
    },
    moveRegister() {
      if (this.type === '登录') {
        this.type = '注册'
      } else {
        this.type = '登录'
      }
      if (this.moveMessage === '没有账号，创建账号') {
        this.moveMessage = '已有账号，我要登录'
      } else {
        this.moveMessage = '没有账号，创建账号'
      }
      this.showLogin = !this.showLogin
    },
    backHome() {
      this.$router.push('/')
    },
  },
}
</script>

<style>
.login-view {
  background-color: rgb(249, 249, 249);
}
</style>
