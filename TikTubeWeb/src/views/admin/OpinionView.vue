<template>
  <v-container fluid>
    <v-card class="mx-auto w-100 mb-4" elevation="2" rounded="lg">
      <v-card-title class="text-h5 font-weight-bold py-4 px-6 bg-primary text-white">
        <v-icon icon="mdi-flag" class="mr-2"></v-icon>
        举报与意见反馈管理
      </v-card-title>

      <v-card-text class="pa-4">
        <!-- 筛选条件 -->
        <v-row>
          <v-col cols="12" sm="4" md="3">
            <v-select
              v-model="filterStatus"
              label="处理状态"
              :items="statusOptions"
              variant="outlined"
              density="comfortable"
              hide-details
              class="mb-4"
              @update:model-value="fetchOpinions"
            ></v-select>
          </v-col>
          <v-col cols="12" sm="4" md="3">
            <v-select
              v-model="filterType"
              label="举报类型"
              :items="typeOptions"
              variant="outlined"
              density="comfortable"
              hide-details
              class="mb-4"
              @update:model-value="fetchOpinions"
            ></v-select>
          </v-col>
          <v-col cols="12" sm="4" md="3">
            <v-text-field
              v-model="filterTarget"
              label="目标ID"
              variant="outlined"
              density="comfortable"
              hide-details
              class="mb-4"
              @keyup.enter="fetchOpinions"
            ></v-text-field>
          </v-col>
          <v-col cols="12" sm="12" md="3" class="d-flex align-center">
            <v-btn
              color="primary"
              variant="elevated"
              class="mr-2"
              @click="fetchOpinions"
              :loading="loading"
            >
              <v-icon icon="mdi-magnify" class="mr-1"></v-icon>
              查询
            </v-btn>
            <v-btn
              color="grey-darken-1"
              variant="elevated"
              @click="resetFilters"
              :disabled="loading"
            >
              <v-icon icon="mdi-refresh" class="mr-1"></v-icon>
              重置
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 数据表格 -->
    <v-card class="mx-auto w-100" elevation="2" rounded="lg">
      <v-data-table-server
        :headers="headers"
        :items="opinions"
        :items-per-page="pageSize"
        :items-length="totalCount"
        :hide-default-footer="true"
        :loading="loading"
        class="elevation-0 rounded-lg"
        mobile-breakpoint="md"
        :hide-default-header="$vuetify.display.smAndDown"
      >
        <!-- 举报类型 -->
        <template #[`item.type`]="{ item }">
          <v-chip :color="getTypeColor(item.type)" size="small" class="text-white">
            {{ getTypeText(item.type) }}
          </v-chip>
        </template>

        <!-- 举报内容 -->
        <template #[`item.userOpinion`]="{ item }">
          <div class="text-truncate" style="max-width: 300px">
            {{ item.userOpinion }}
          </div>
          <v-btn
            variant="text"
            color="primary"
            size="x-small"
            @click="viewDetail(item)"
            class="mt-1"
          >
            查看详情
          </v-btn>
        </template>

        <!-- 处理状态 -->
        <template #[`item.status`]="{ item }">
          <v-chip :color="getStatusColor(item.status)" size="small" class="text-white">
            {{ getStatusText(item.status) }}
          </v-chip>
        </template>

        <!-- 举报时间 -->
        <template #[`item.createTime`]="{ item }">
          {{ formatTime(item.createTime) }}
        </template>

        <!-- 处理时间 -->
        <template #[`item.opinionTime`]="{ item }">
          {{ item.opinionTime ? formatTime(item.opinionTime) : '未处理' }}
        </template>

        <!-- 操作 -->
        <template #[`item.actions`]="{ item }">
          <div class="d-flex">
            <v-btn
              v-if="item.status === 0"
              color="success"
              size="small"
              variant="text"
              class="mr-2"
              @click="handleOpinionFun(item, 1)"
            >
              <v-icon size="small" class="mr-1">mdi-check</v-icon>
              受理
            </v-btn>
            <v-btn
              v-if="item.status === 0"
              color="error"
              size="small"
              variant="text"
              @click="handleOpinionFun(item, 2)"
            >
              <v-icon size="small" class="mr-1">mdi-close</v-icon>
              不受理
            </v-btn>
            <v-btn
              v-if="item.status !== 0"
              color="info"
              size="small"
              variant="text"
              @click="viewDetail(item)"
            >
              <v-icon size="small" class="mr-1">mdi-eye</v-icon>
              查看
            </v-btn>
          </div>
        </template>
      </v-data-table-server>

      <!-- 分页 -->
      <div class="d-flex justify-center py-4">
        <v-pagination
          v-model="page"
          :length="totalPages"
          rounded="circle"
          @update:model-value="fetchOpinions"
        ></v-pagination>
      </div>
    </v-card>

    <!-- 详情对话框 -->
    <v-dialog v-model="detailDialog" max-width="700">
      <v-card>
        <v-card-title class="text-h5 font-weight-bold py-4 px-6 bg-primary text-white">
          <v-row class="mt-1 mb-1">
            <v-icon icon="mdi-flag" class="mr-2"></v-icon>
            举报详情
            <v-spacer></v-spacer>
            <v-btn
              icon="mdi-close"
              variant="text"
              color="white"
              @click="detailDialog = false"
            ></v-btn>
          </v-row>
        </v-card-title>
        <v-card-text class="pa-4">
          <v-list>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="primary" class="mr-2">mdi-identifier</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium">ID</v-list-item-title>
              <v-list-item-subtitle>{{ selectedOpinion.id || '无' }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="warning" class="mr-2">mdi-flag</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >举报类型</v-list-item-title
              >
              <v-list-item-subtitle>{{ getTypeText(selectedOpinion.type) }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="blue" class="mr-2">mdi-target</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >目标ID</v-list-item-title
              >
              <v-list-item-subtitle>{{ selectedOpinion.targetId || '无' }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="indigo" class="mr-2">mdi-account</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >举报人ID</v-list-item-title
              >
              <v-list-item-subtitle>{{ selectedOpinion.userId || '无' }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="indigo" class="mr-2">mdi-account</v-icon>
              </template>
              <div>
                {{ selectedOpinion.ip }} / {{ selectedOpinion.city }} / {{ selectedOpinion.ua }}
              </div>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="blue" class="mr-2">mdi-comment-text-outline</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >举报内容</v-list-item-title
              >
              <ShowMarkdown :markdown="selectedOpinion.userOpinion" :anchor="0"></ShowMarkdown>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="blue" class="mr-2">mdi-comment-text-outline</v-icon>
              </template>
              <div class="mt-2">{{ selectedOpinion.otherInfo || '无' }}</div>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon :color="getStatusColor(selectedOpinion.status)" class="mr-2">{{
                  getStatusIcon(selectedOpinion.status)
                }}</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >处理状态</v-list-item-title
              >
              <v-list-item-subtitle>{{
                getStatusText(selectedOpinion.status)
              }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item v-if="selectedOpinion.status !== 0">
              <template v-slot:prepend>
                <v-icon color="green" class="mr-2">mdi-account-check</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >处理人ID</v-list-item-title
              >
              <v-list-item-subtitle>{{ selectedOpinion.opinionUser || '无' }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item v-if="selectedOpinion.status !== 0">
              <template v-slot:prepend>
                <v-icon color="purple" class="mr-2">mdi-comment-check</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >处理意见</v-list-item-title
              >
              <ShowMarkdown :markdown="selectedOpinion.opinion" :anchor="0"></ShowMarkdown>
            </v-list-item>

            <v-list-item>
              <template v-slot:prepend>
                <v-icon color="grey" class="mr-2">mdi-clock-outline</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >举报时间</v-list-item-title
              >
              <v-list-item-subtitle>{{
                formatTime(selectedOpinion.createTime)
              }}</v-list-item-subtitle>
            </v-list-item>

            <v-list-item v-if="selectedOpinion.opinionTime">
              <template v-slot:prepend>
                <v-icon color="grey" class="mr-2">mdi-clock-check-outline</v-icon>
              </template>
              <v-list-item-title class="text-subtitle-1 font-weight-medium"
                >处理时间</v-list-item-title
              >
              <v-list-item-subtitle>{{
                formatTime(selectedOpinion.opinionTime)
              }}</v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- 处理对话框 -->
    <v-dialog v-model="handleDialog" width="60vh">
      <v-card>
        <v-card-title
          class="text-h5 font-weight-bold py-4 px-6"
          :class="handleStatus === 1 ? 'bg-success' : 'bg-error'"
          style="color: white"
        >
          <v-icon
            :icon="handleStatus === 1 ? 'mdi-check-circle' : 'mdi-close-circle'"
            class="mr-2"
          ></v-icon>
          {{ handleStatus === 1 ? '受理举报' : '不予受理' }}
          <span v-if="isAppealType(selectedOpinion.type)">
            {{ handleStatus === 1 ? '(申诉处理)' : '(申诉驳回)' }}
          </span>
        </v-card-title>
        <v-card-text class="pa-4 pt-6">
          <!-- 申诉处理结果选择 -->
          <v-select
            v-if="isAppealType(selectedOpinion.type) && handleStatus === 1"
            v-model="appealStatus"
            label="申诉处理结果"
            :items="appealStatusOptions"
            variant="outlined"
            density="comfortable"
            class="mb-4"
          ></v-select>

          <!-- 将v-textarea替换为Vditor组件 -->
          <Vditor
            ref="opinionVditor"
            :idname="`opinion-editor-${selectedOpinion.id || 'new'}`"
            :placeholder="getPlaceholderText()"
            :height="250"
            :hide="false"
            :cache="false"
            @vditor-input="handleVditorInput"
          />
        </v-card-text>
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="handleDialog = false"
            :disabled="submitting"
          >
            取消
          </v-btn>
          <v-btn
            :color="handleStatus === 1 ? 'success' : 'error'"
            variant="elevated"
            @click="submitHandleOpinion"
            :loading="submitting"
            :disabled="!handleOpinion || handleOpinion.trim() === ''"
          >
            确认
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 消息提示 -->
    <v-snackbar v-model="showMessage" :timeout="3000" location="top" :color="messageType">
      {{ message }}
      <template #actions>
        <v-btn color="white" variant="text" @click="showMessage = false">关闭</v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import Vditor from '@/components/vditor/VditorComponents.vue'
import ShowMarkdown from '@/components/vditor/ShowMarkdown.vue'
export default {
  name: 'OpinionView',
  components: {
    Vditor,
    ShowMarkdown,
  },
  data() {
    return {
      // 表格数据
      opinions: [],
      loading: false,
      page: 1,
      pageSize: 10,
      totalPages: 1,
      totalCount: 0,

      // 筛选条件
      filterStatus: '',
      filterType: '',
      filterTarget: '',

      // 对话框控制
      detailDialog: false,
      handleDialog: false,
      selectedOpinion: {},
      handleStatus: 1,
      handleOpinion: '',
      submitting: false,

      // 消息提示
      showMessage: false,
      message: '',
      messageType: 'info',

      // 表格列配置
      headers: [
        { title: 'ID', key: 'id', align: 'center', sortable: false, width: '80px' },
        { title: '类型', key: 'type', align: 'center', sortable: false, width: '100px' },
        { title: '举报内容', key: 'userOpinion', align: 'start', sortable: false },
        { title: '目标ID', key: 'targetId', align: 'center', sortable: false, width: '100px' },
        { title: '举报人ID', key: 'userId', align: 'center', sortable: false, width: '100px' },
        { title: '状态', key: 'status', align: 'center', sortable: false, width: '100px' },
        { title: '举报时间', key: 'createTime', align: 'center', sortable: false, width: '160px' },
        { title: 'IP', key: 'ip', align: 'center', sortable: false, width: '160px' },
        { title: 'City', key: 'city', align: 'center', sortable: false, width: '160px' },
        { title: '处理时间', key: 'opinionTime', align: 'center', sortable: false, width: '160px' },
        { title: '操作', key: 'actions', align: 'center', sortable: false, width: '150px' },
      ],

      // 选项数据
      statusOptions: [
        { title: '全部', value: '' },
        { title: '未处理', value: '0' },
        { title: '已处理', value: '1' },
        { title: '不予受理', value: '2' },
      ],
      typeOptions: [
        { title: '全部', value: '' },
        { title: '稿件举报', value: '0' },
        { title: '评论举报', value: '1' },
        { title: '弹幕举报', value: '2' },
        { title: '稿件申诉', value: '3' },
        { title: '评论申诉', value: '4' },
        { title: '弹幕申诉', value: '5' },
        { title: '意见反馈', value: '10' },
      ],
      typeMap: {
        0: { text: '稿件举报', color: 'error' },
        1: { text: '评论举报', color: 'warning' },
        2: { text: '弹幕举报', color: 'orange' },
        3: { text: '稿件申诉', color: 'error' },
        4: { text: '评论申诉', color: 'warning' },
        5: { text: '弹幕申诉', color: 'orange' },
        10: { text: '意见反馈', color: 'blue' },
      },
      statusMap: {
        0: { text: '未处理', color: 'grey', icon: 'mdi-clock-alert-outline' },
        1: { text: '已处理', color: 'success', icon: 'mdi-check-circle' },
        2: { text: '不予受理', color: 'error', icon: 'mdi-close-circle' },
      },
      appealStatus: 0,
      appealStatusOptions: [
        { title: '申诉成功', value: 0 },
        { title: '申诉失败', value: 1 },
      ],
    }
  },
  created() {
    this.fetchOpinions()
  },
  methods: {
    // 获取举报列表
    fetchOpinions() {
      this.loading = true

      const params = new URLSearchParams()
      params.append('page', this.page)
      params.append('limit', this.pageSize)

      if (this.filterStatus) {
        params.append('status', this.filterStatus)
      }

      if (this.filterType) {
        params.append('type', this.filterType)
      }

      if (this.filterTarget) {
        params.append('target', this.filterTarget)
      }

      this.httpGet(`/admin/opinion/list?${params.toString()}`, (json) => {
        this.loading = false

        if (json.status === 200) {
          this.opinions = json.data.list || []
          this.totalCount = json.data.totalCount || 0
          this.totalPages = json.data.totalPages || 1
        } else {
          this.showErrorMessage(json.message || '获取举报列表失败')
        }
      })
    },

    // 处理表格选项变更
    handleTableOptions(options) {
      this.page = options.page
      this.pageSize = options.itemsPerPage
      this.fetchOpinions()
    },

    // 重置筛选条件
    resetFilters() {
      this.filterStatus = ''
      this.filterType = ''
      this.filterTarget = ''
      this.page = 1
      this.fetchOpinions()
    },

    // 查看详情
    viewDetail(item) {
      this.selectedOpinion = { ...item }
      this.detailDialog = true
    },

    // 处理举报
    handleOpinionFun(item, status) {
      this.selectedOpinion = { ...item }
      this.handleStatus = status
      this.handleOpinion = ''
      this.appealStatus = 0 // 重置申诉状态
      this.handleDialog = true

      // 在对话框打开后，需要重置编辑器内容
      this.$nextTick(() => {
        if (this.$refs.opinionVditor) {
          this.$refs.opinionVditor.setTextValue('')
        }
      })
    },

    // 判断是否为申诉类型
    isAppealType(type) {
      return type === 3 || type === 4 || type === 5
    },

    // 获取输入框提示文本
    getPlaceholderText() {
      if (!this.isAppealType(this.selectedOpinion.type)) {
        return this.handleStatus === 1 ? '请输入处理意见...' : '请输入不予受理的原因...'
      }

      if (this.handleStatus === 1) {
        return this.appealStatus === 0
          ? '请输入申诉成功的处理意见...'
          : '请输入申诉失败的处理意见...'
      } else {
        return '请输入申诉驳回的原因...'
      }
    },

    // 处理Vditor输入
    handleVditorInput(content) {
      this.handleOpinion = content
    },

    // 提交处理意见
    submitHandleOpinion() {
      if (!this.handleOpinion || this.handleOpinion.trim() === '') {
        this.showErrorMessage('请填写处理意见')
        return
      }

      this.submitting = true

      const data = {
        id: this.selectedOpinion.id,
        status: this.handleStatus,
        opinion: this.handleOpinion,
      }

      // 如果是申诉类型，添加申诉处理结果
      if (this.isAppealType(this.selectedOpinion.type)) {
        data.appealStatus = this.appealStatus
      }

      this.httpPost('/admin/opinion/acceptance', data, (json) => {
        this.submitting = false

        if (json.status === 200) {
          let successMessage = this.handleStatus === 1 ? '举报已受理' : '举报已拒绝'

          // 如果是申诉类型，显示对应的成功消息
          if (this.isAppealType(this.selectedOpinion.type)) {
            if (this.handleStatus === 1) {
              successMessage = this.appealStatus === 0 ? '申诉已通过' : '申诉已驳回'
            } else {
              successMessage = '申诉已拒绝处理'
            }
          }

          this.showSuccessMessage(successMessage)
          this.handleDialog = false
          this.fetchOpinions()
        } else {
          this.showErrorMessage(json.message || '处理失败')
        }
      })
    },

    // 格式化时间
    formatTime(timestamp) {
      if (!timestamp) return '无'

      const date = new Date(timestamp)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(
        date.getDate()
      ).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(
        date.getMinutes()
      ).padStart(2, '0')}`
    },

    // 获取类型文本
    getTypeText(type) {
      return this.typeMap[type]?.text || '未知类型'
    },

    // 获取类型颜色
    getTypeColor(type) {
      return this.typeMap[type]?.color || 'grey'
    },

    // 获取状态文本
    getStatusText(status) {
      return this.statusMap[status]?.text || '未知状态'
    },

    // 获取状态颜色
    getStatusColor(status) {
      return this.statusMap[status]?.color || 'grey'
    },

    // 获取状态图标
    getStatusIcon(status) {
      return this.statusMap[status]?.icon || 'mdi-help-circle'
    },

    // 显示成功消息
    showSuccessMessage(msg) {
      this.message = msg
      this.messageType = 'success'
      this.showMessage = true
    },

    // 显示错误消息
    showErrorMessage(msg) {
      this.message = msg
      this.messageType = 'error'
      this.showMessage = true
    },
  },
}
</script>

<style scoped>
.text-pre-wrap {
  white-space: pre-wrap;
}
</style>