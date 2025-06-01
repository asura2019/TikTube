<template>
  <v-container fluid>
    <v-card class="mx-auto w-100" elevation="2" rounded="lg">
      <v-toolbar color="indigo">
        <v-toolbar-title class="text-h5 font-weight-medium">
          <v-icon icon="mdi-bullhorn" class="mr-2"></v-icon>
          广告公告管理
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn
          prepend-icon="mdi-plus"
          color="white"
          variant="tonal"
          @click="openDialog()"
          class="mr-2"
        >
          新增广告公告
        </v-btn>
      </v-toolbar>

      <v-alert type="info" variant="tonal" border="start" density="comfortable" class="ma-4">
        首页公告与首页弹窗广告仅支持一条，如有多条只显示第一条内容，只建议在视频贴片广告中加入视频，别的地方不建议。新闻轮播因为目前没有找到一个合适的投放位置，所以暂不生效。访问量统计目前仅支持贴片广告和弹窗广告！
      </v-alert>

      <!-- 筛选区域 -->
      <div class="search-options pa-4 bg-grey-lighten-5">
        <v-row>
          <v-col cols="12" sm="5">
            <v-select
              v-model="queryParams.type"
              :items="adTypeOptions"
              label="广告类型"
              variant="outlined"
              density="comfortable"
              hide-details
              prepend-inner-icon="mdi-filter-variant"
              @update:model-value="fetchAdvertisements"
            ></v-select>
          </v-col>
          <v-col cols="12" sm="5">
            <v-select
              v-model="queryParams.status"
              :items="statusOptions"
              label="状态"
              variant="outlined"
              density="comfortable"
              hide-details
              prepend-inner-icon="mdi-check-circle-outline"
              @update:model-value="fetchAdvertisements"
            ></v-select>
          </v-col>
          <v-col cols="12" sm="2" class="d-flex align-center">
            <v-btn
              color="primary"
              variant="text"
              prepend-icon="mdi-refresh"
              @click="fetchAdvertisements"
              block
            >
              刷新
            </v-btn>
          </v-col>
        </v-row>
      </div>

      <!-- 数据表格 -->
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="advertisements"
          :loading="loading"
          :hide-default-footer="true"
          hover
          class="elevation-0 rounded-lg"
          mobile-breakpoint="sm"
          :hide-default-header="$vuetify.display.xs"
        >
          <template #[`item.title`]="{ item }">
            <div class="d-flex flex-column">
              <div class="text-subtitle-1 font-weight-medium text-truncate">
                {{ item.title }}
              </div>
              <div class="text-caption text-grey d-flex align-center mt-1">
                <v-icon size="small" class="mr-1">mdi-calendar</v-icon>
                {{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}
              </div>
            </div>
          </template>

          <template #[`item.type`]="{ item }">
            <v-chip :color="getTypeColor(item.type)" text-color="white" size="small">
              {{ getTypeText(item.type) }}
            </v-chip>
          </template>

          <template #[`item.status`]="{ item }">
            <v-chip
              :color="item.status === 1 ? 'success' : 'error'"
              text-color="white"
              size="small"
            >
              {{ item.status === 1 ? '启用' : '禁用' }}
            </v-chip>
          </template>

          <template #[`item.media`]="{ item }">
            <div class="d-flex flex-column">
              <div v-if="item.imageUrl" class="d-flex align-center mb-1">
                <v-icon size="small" class="mr-1">mdi-image</v-icon>
                <span class="text-caption text-truncate">有图片</span>
              </div>
              <div v-if="item.videoUrl" class="d-flex align-center">
                <v-icon size="small" class="mr-1">mdi-video</v-icon>
                <span class="text-caption text-truncate">有视频</span>
              </div>
              <div v-if="!item.imageUrl && !item.videoUrl" class="text-caption text-grey">
                无媒体
              </div>
            </div>
          </template>

          <template #[`item.actions`]="{ item }">
            <div class="d-flex justify-center">
              <v-tooltip location="top" text="编辑">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    size="small"
                    color="primary"
                    variant="text"
                    class="mr-1"
                    @click="openDialog(item)"
                  >
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip location="top" text="预览">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    size="small"
                    color="info"
                    variant="text"
                    class="mr-1"
                    @click="previewAd(item)"
                  >
                    <v-icon>mdi-eye</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip location="top" :text="item.status === 1 ? '禁用' : '启用'">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    :icon="item.status === 1 ? 'mdi-close' : 'mdi-check'"
                    size="small"
                    :color="item.status === 1 ? 'error' : 'success'"
                    variant="text"
                    @click="toggleStatus(item)"
                  >
                  </v-btn>
                </template>
              </v-tooltip>
            </div>
          </template>

          <template #no-data>
            <div class="d-flex flex-column align-center pa-4">
              <v-icon size="large" color="grey-lighten-1" class="mb-2">mdi-emoticon-sad</v-icon>
              <span class="text-body-1 text-grey">暂无广告数据</span>
              <v-btn color="primary" class="mt-4" @click="fetchAdvertisements">刷新数据</v-btn>
            </div>
          </template>
        </v-data-table>
      </v-card-text>

      <!-- 分页 -->
      <div class="d-flex justify-center pa-4">
        <v-pagination
          v-model="queryParams.page"
          :length="totalPage"
          :total-visible="7"
          @update:model-value="fetchAdvertisements"
          rounded
          density="comfortable"
        ></v-pagination>
      </div>
    </v-card>

    <!-- 新增/编辑对话框 -->
    <v-dialog v-model="dialog" max-width="800px" persistent>
      <v-card>
        <v-card-title class="text-h5 bg-primary text-white">
          <v-row class="mt-1 mb-1">
            <v-icon class="mr-2">{{ isEdit ? 'mdi-pencil' : 'mdi-plus-circle' }}</v-icon>
            {{ isEdit ? '编辑广告公告' : '新增广告公告' }}
            <v-spacer></v-spacer>
            <v-btn icon="mdi-close" variant="text" color="white" @click="closeDialog"></v-btn>
          </v-row>
        </v-card-title>
        <v-card-text class="pt-4">
          <v-form ref="form" v-model="valid">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="currentAd.title"
                  label="标题"
                  required
                  :rules="[(v) => !!v || '标题不能为空']"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-format-title"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="currentAd.type"
                  :items="adTypeOptions"
                  label="广告类型"
                  required
                  :rules="[(v) => v !== null || '请选择广告类型']"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-tag"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="currentAd.url"
                  label="链接地址"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-link"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="currentAd.status"
                  :items="statusOptions"
                  label="状态"
                  required
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-toggle-switch"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="currentAd.imageUrl"
                  label="图片地址"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-image"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="currentAd.videoUrl"
                  label="视频地址"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-video"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="startTimeStr"
                  label="开始时间"
                  type="datetime-local"
                  required
                  :rules="[(v) => !!v || '开始时间不能为空']"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-calendar-start"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="endTimeStr"
                  label="结束时间"
                  type="datetime-local"
                  required
                  :rules="[
                    (v) => !!v || '结束时间不能为空',
                    (v) =>
                      !startTimeStr ||
                      new Date(v) > new Date(startTimeStr) ||
                      '结束时间必须大于开始时间',
                  ]"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-calendar-end"
                ></v-text-field>
              </v-col>
              <v-col cols="12">
                <v-textarea
                  v-model="currentAd.content"
                  label="内容描述"
                  rows="4"
                  variant="outlined"
                  density="comfortable"
                  prepend-inner-icon="mdi-text-box"
                ></v-textarea>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions class="pa-4 bg-grey-lighten-5">
          <v-spacer></v-spacer>
          <v-btn color="error" variant="text" @click="closeDialog" prepend-icon="mdi-close">
            取消
          </v-btn>
          <v-btn
            color="primary"
            :loading="saving"
            @click="saveAdvertisement"
            prepend-icon="mdi-content-save"
          >
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 预览对话框 -->
    <v-dialog v-model="previewDialog" max-width="700px">
      <v-card>
        <v-card-title class="text-h5 bg-primary text-white">
          <v-icon class="mr-2">mdi-eye</v-icon>
          广告预览
          <v-spacer></v-spacer>
          <v-btn
            icon="mdi-close"
            variant="text"
            color="white"
            @click="previewDialog = false"
          ></v-btn>
        </v-card-title>
        <v-card-text class="pa-4">
          <v-row v-if="previewItem">
            <v-col cols="12">
              <v-card variant="outlined" class="pa-4">
                <h3 class="text-h6 mb-2 d-flex align-center">
                  <v-icon class="mr-2" color="primary">mdi-bullhorn</v-icon>
                  {{ previewItem.title }}
                </h3>
                <v-chip
                  :color="getTypeColor(previewItem.type)"
                  text-color="white"
                  size="small"
                  class="mb-3"
                >
                  {{ getTypeText(previewItem.type) }}
                </v-chip>

                <div v-if="previewItem.imageUrl" class="mb-3">
                  <v-img
                    :src="previewItem.imageUrl"
                    max-height="300"
                    contain
                    class="bg-grey-lighten-3 rounded-lg"
                  ></v-img>
                </div>

                <div v-if="previewItem.videoUrl" class="mb-3">
                  <video controls width="100%" max-height="300" class="rounded-lg">
                    <source :src="previewItem.videoUrl" type="video/mp4" />
                    您的浏览器不支持视频播放
                  </video>
                </div>

                <div
                  v-if="previewItem.content"
                  class="mb-3 text-body-1 pa-2 bg-grey-lighten-5 rounded"
                >
                  {{ previewItem.content }}
                </div>

                <div v-if="previewItem.url" class="mb-3">
                  <v-btn
                    :href="previewItem.url"
                    target="_blank"
                    color="primary"
                    variant="text"
                    prepend-icon="mdi-open-in-new"
                  >
                    {{ previewItem.url }}
                  </v-btn>
                </div>

                <v-divider class="my-3"></v-divider>

                <div class="d-flex justify-space-between text-caption">
                  <v-chip size="x-small" color="primary" variant="outlined">
                    <v-icon start size="x-small">mdi-calendar-start</v-icon>
                    {{ formatTime(previewItem.startTime) }}
                  </v-chip>
                  <v-chip size="x-small" color="primary" variant="outlined">
                    <v-icon start size="x-small">mdi-calendar-end</v-icon>
                    {{ formatTime(previewItem.endTime) }}
                  </v-chip>
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- 消息提示 -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="3000" location="top">
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn variant="text" color="white" @click="snackbar.show = false"> 关闭 </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      // 查询参数
      queryParams: {
        page: 1,
        limit: 10,
        type: null,
        status: null,
      },
      // 广告列表
      advertisements: [],
      // 加载状态
      loading: false,
      // 保存状态
      saving: false,
      // 总页数
      totalPage: 0,
      // 总记录数
      totalCount: 0,
      // 对话框显示状态
      dialog: false,
      // 预览对话框
      previewDialog: false,
      // 预览项
      previewItem: null,
      // 是否编辑模式
      isEdit: false,
      // 表单验证
      valid: false,
      // 当前编辑的广告
      currentAd: {
        id: null,
        title: '',
        url: '',
        imageUrl: '',
        content: '',
        videoUrl: '',
        status: 1,
        type: 0,
        startTime: null,
        endTime: null,
        position: 0,
      },
      // 开始时间字符串
      startTimeStr: '',
      // 结束时间字符串
      endTimeStr: '',
      // 表格头
      headers: [
        { title: 'ID', key: 'id', sortable: false, width: '80px' },
        { title: '标题', key: 'title', sortable: false, width: '200px' },
        { title: '内容', key: 'content', sortable: false, width: '300px' },
        { title: '类型', key: 'type', sortable: false, width: '120px' },
        { title: '访问量', key: 'viewCount', sortable: false, width: '120px' },
        { title: '状态', key: 'status', sortable: false, width: '100px' },
        { title: '操作', key: 'actions', sortable: false, width: '120px', align: 'center' },
      ],
      // 广告类型选项
      adTypeOptions: [
        { title: '全部类型', value: null },
        { title: '首页公告', value: 0 },
        { title: '首页弹窗广告', value: 1 },
        { title: '视频贴片广告', value: 2 },
        { title: '轮播新闻', value: 3 },
      ],
      // 状态选项
      statusOptions: [
        { title: '全部状态', value: null },
        { title: '启用', value: 1 },
        { title: '禁用', value: 0 },
      ],
      // 消息提示
      snackbar: {
        show: false,
        text: '',
        color: 'success',
      },
    }
  },
  created() {
    // 初始加载数据
    this.fetchAdvertisements()
  },
  methods: {
    // 获取广告列表
    fetchAdvertisements() {
      this.loading = true

      // 构建查询参数
      const params = {
        page: this.queryParams.page,
        limit: this.queryParams.limit,
      }

      // 添加类型过滤
      if (this.queryParams.type !== null) {
        params.type = this.queryParams.type
      }

      // 添加状态过滤
      if (this.queryParams.status !== null) {
        params.status = this.queryParams.status
      }

      // 构建URL查询参数
      const queryString = Object.entries(params)
        .filter(([_, value]) => value !== null && value !== undefined)
        .map(([key, value]) => `${key}=${value}`)
        .join('&')

      this.httpGet(`/admin/ads/list?${queryString}`, (json) => {
        if (json.status === 200) {
          this.advertisements = json.data.list
          this.totalCount = json.data.totalCount
          this.totalPage = json.data.totalPage
        } else {
          this.showMessage(json.message || '获取广告列表失败', 'error')
        }
        this.loading = false
      })
    },

    // 打开对话框
    openDialog(item) {
      this.resetForm()

      if (item) {
        // 编辑模式
        this.isEdit = true
        this.currentAd = { ...item }

        // 转换时间戳为日期字符串
        if (this.currentAd.startTime) {
          this.startTimeStr = this.formatDateTimeForInput(this.currentAd.startTime)
        }

        if (this.currentAd.endTime) {
          this.endTimeStr = this.formatDateTimeForInput(this.currentAd.endTime)
        }
      } else {
        // 新增模式
        this.isEdit = false

        // 设置默认值
        const now = new Date()
        const tomorrow = new Date()
        tomorrow.setDate(now.getDate() + 7)

        this.startTimeStr = this.formatDateTimeForInput(now.getTime())
        this.endTimeStr = this.formatDateTimeForInput(tomorrow.getTime())
        this.currentAd.startTime = now.getTime()
        this.currentAd.endTime = tomorrow.getTime()
      }

      this.dialog = true
    },

    // 关闭对话框
    closeDialog() {
      this.dialog = false
      this.resetForm()
    },

    // 重置表单
    resetForm() {
      this.currentAd = {
        id: null,
        title: '',
        url: '',
        imageUrl: '',
        content: '',
        videoUrl: '',
        status: 1,
        type: 0,
        startTime: null,
        endTime: null,
        position: 0,
      }
      this.startTimeStr = ''
      this.endTimeStr = ''
      this.isEdit = false

      if (this.$refs.form) {
        this.$refs.form.reset()
      }
    },

    // 保存广告
    saveAdvertisement() {
      // 表单验证
      if (!this.$refs.form.validate()) {
        return
      }

      // 转换时间
      if (this.startTimeStr) {
        this.currentAd.startTime = new Date(this.startTimeStr).getTime()
      }

      if (this.endTimeStr) {
        this.currentAd.endTime = new Date(this.endTimeStr).getTime()
      }

      this.saving = true

      // 根据是否编辑模式选择接口
      const url = this.isEdit ? '/admin/ads/update' : '/admin/ads/save'

      this.httpPost(url, this.currentAd, (json) => {
        if (json.status === 200) {
          this.showMessage(this.isEdit ? '更新成功' : '添加成功', 'success')
          this.dialog = false
          this.fetchAdvertisements()
        } else {
          this.showMessage(json.message || (this.isEdit ? '更新失败' : '添加失败'), 'error')
        }
        this.saving = false
      })
    },

    // 切换状态
    toggleStatus(item) {
      const newStatus = item.status === 1 ? 0 : 1
      const statusText = newStatus === 1 ? '启用' : '禁用'

      const updateData = {
        id: item.id,
        status: newStatus,
        type: item.type,
      }

      this.httpPost('/admin/ads/update', updateData, (json) => {
        if (json.status === 200) {
          this.showMessage(`${statusText}成功`, 'success')
          // 更新本地数据
          item.status = newStatus
        } else {
          this.showMessage(json.message || `${statusText}失败`, 'error')
        }
      })
    },

    // 预览广告
    previewAd(item) {
      this.previewItem = { ...item }
      this.previewDialog = true
    },

    // 格式化时间
    formatTime(timestamp) {
      if (!timestamp) return '未设置'

      const date = new Date(timestamp)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
      })
    },

    // 格式化日期时间为输入框格式
    formatDateTimeForInput(timestamp) {
      if (!timestamp) return ''

      const date = new Date(timestamp)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')

      return `${year}-${month}-${day}T${hours}:${minutes}`
    },

    // 获取类型文本
    getTypeText(type) {
      if (type == 0) return '首页公告'
      else if (type == 1) return '首页弹窗'
      else if (type == 2) return '视频贴片'
      else if (type == 3) return '轮播新闻'
      else return '未知类型'
    },
    // 获取类型颜色
    getTypeColor(type) {
      switch (type) {
        case 0:
          return 'primary'
        case 1:
          return 'warning'
        case 2:
          return 'error'
        case 3:
          return 'success'
        default:
          return 'grey'
      }
    },

    // 显示消息
    showMessage(text, color = 'success') {
      this.snackbar.text = text
      this.snackbar.color = color
      this.snackbar.show = true
    },
  },
}
</script>

<style>
.search-options {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.v-btn--size-small {
  text-transform: none;
  letter-spacing: normal;
}

.v-pagination {
  margin-top: 8px;
}
</style>