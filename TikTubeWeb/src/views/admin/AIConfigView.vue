<template>
  <v-container fluid>
    <v-card class="mx-auto w-100" elevation="2" rounded="lg">
      <v-toolbar color="green-lighten-1">
        <v-toolbar-title class="text-h5 font-weight-medium">
          <v-icon icon="mdi-robot" class="mr-2"></v-icon>
          AI配置管理
          <v-chip variant="flat" v-if="aiOpen" color="success" size="small" class="ml-2"> AI功能已启用 </v-chip>
          <v-chip variant="flat" v-else color="error" size="small" class="ml-2"> AI功能已禁用 </v-chip>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn prepend-icon="mdi-pen"  color="white" variant="tonal" v-if="aiOpen == false" @click="openAIConfig()" class="mr-2">
          开启 AI
        </v-btn>
        <v-btn prepend-icon="mdi-pen"  color="white" variant="tonal" v-if="aiOpen" @click="closeAIExamine()" class="mr-2">
          关闭 AI
        </v-btn>
        <v-btn
          prepend-icon="mdi-plus"
          color="white"
          variant="tonal"
          @click="openDialog(null)"
          class="mr-2"
        >
          新增配置
        </v-btn>
      </v-toolbar>

      <v-alert type="info" variant="tonal" border="start" density="comfortable" class="ma-4">
        AI配置可以同时启用多个，不同类型的AI配置用于不同的功能，请根据需要进行配置。
        启用AI功能后系统将使用AI进行内容审核，请确保配置正确，由于 TOKEN 计算不一定准确，请以 API 服务商数据为准。
        prompt 审核规则可以随意修改，但是请不要修改输出结果部分提示信息，否则将造成AI审核功能无法正常使用。
        当已用Token超过最大可用Token时，系统将自动切换到下一个可用配置。
      </v-alert>

      <!-- 配置列表 -->
      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="aiConfigList"
          :loading="loading"
          mobile-breakpoint="md"
          hide-default-footer
          :hide-default-header="$vuetify.display.smAndDown"
          hover
          class="elevation-0 rounded-lg"
        >
          <!-- 配置名称列 -->
          <template #[`item.name`]="{ item }">
            <v-chip :color="item.status === 1 ? 'success' : 'grey'" variant="outlined" size="small">
              {{ item.name }}
            </v-chip>
          </template>

          <!-- 类型列 -->
          <template #[`item.type`]="{ item }">
            <v-chip :color="getTypeColor(item.type)" size="small" text-color="white">
              {{ getTypeText(item.type) }}
            </v-chip>
          </template>

          <!-- 状态列 -->
          <template #[`item.status`]="{ item }">
            <v-chip :color="item.status === 1 ? 'success' : 'grey'" size="small" text-color="white">
              {{ item.status === 1 ? '启用' : '禁用' }}
            </v-chip>
          </template>

          <!-- 使用Token列 -->
          <template #[`item.useTokens`]="{ item }">
            <v-chip :color="item.useTokens && item.maxTokens && item.useTokens >= item.maxTokens ? 'error' : 'blue-lighten-4'" size="small">
              {{ item.useTokens || 0 }} / {{ item.maxTokens || '无限制' }}
            </v-chip>
          </template>

          <!-- 操作列 -->
          <template #[`item.actions`]="{ item }">
            <div class="d-flex justify-center">
              <v-tooltip location="top" text="编辑">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    variant="text"
                    size="small"
                    color="primary"
                    @click="openDialog(item)"
                    class="mr-1"
                  >
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip location="top" text="删除">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    variant="text"
                    size="small"
                    color="error"
                    @click="deleteConfig(item.id)"
                    class="mr-1"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <!-- <v-tooltip location="top" text="测试">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    variant="text"
                    size="small"
                    color="success"
                    @click="testConfig(item)"
                  >
                    <v-icon>mdi-check-circle</v-icon>
                  </v-btn>
                </template>
              </v-tooltip> -->
            </div>
          </template>

          <template #no-data>
            <div class="d-flex flex-column align-center pa-4">
              <v-icon size="large" color="grey-lighten-1" class="mb-2">mdi-emoticon-sad</v-icon>
              <span class="text-body-1 text-grey">暂无配置数据</span>
              <v-btn color="primary" class="mt-4" @click="initialize">刷新数据</v-btn>
            </div>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- 编辑对话框 -->
    <v-dialog v-model="dialog" max-width="800px">
      <v-card>
        <v-card-title class="text-h5 bg-primary text-white">
          <v-row class="mt-2 mb-2">
            <v-icon class="mr-2">{{ editedItem.id ? 'mdi-pencil' : 'mdi-plus' }}</v-icon>
            {{ editedItem.id ? '编辑配置' : '新增配置' }}
            <v-spacer></v-spacer>
            <v-btn icon="mdi-close" variant="text" color="white" @click="dialog = false"></v-btn>
          </v-row>
        </v-card-title>

        <v-card-text class="pt-4">
          <v-form ref="form" v-model="valid">
            <v-container>
              <v-row>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedItem.name"
                    label="配置名称"
                    :rules="[(v) => !!v || '配置名称不能为空']"
                    required
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-tag"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-select
                    v-model="editedItem.type"
                    :items="typeOptions"
                    label="配置类型"
                    :rules="[(v) => v !== null || '配置类型不能为空']"
                    required
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-format-list-bulleted"
                    @update:model-value="handleTypeChange"
                  ></v-select>
                </v-col>
                <v-col cols="12">
                  <v-text-field
                    v-model="editedItem.apiUrl"
                    label="API地址"
                    :rules="[(v) => !!v || 'API地址不能为空']"
                    required
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-web"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedItem.apiKey"
                    label="API密钥"
                    :rules="[(v) => !!v || 'API密钥不能为空']"
                    required
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-key"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedItem.model"
                    label="模型名称"
                    :rules="[(v) => !!v || '模型名称不能为空']"
                    required
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-brain"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-textarea
                    v-model="editedItem.prompt"
                    label="Prompt内容"
                    rows="3"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-text-box"
                    hint="请不要修改输出结果部分提示信息，否则会影响输出结果"
                    persistent-hint
                  ></v-textarea>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-select
                    v-model="editedItem.status"
                    :items="[
                      { title: '启用', value: 1 },
                      { title: '禁用', value: 0 },
                    ]"
                    label="状态"
                    required
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-toggle-switch"
                  ></v-select>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedItem.maxTokens"
                    label="最大可用Token"
                    type="number"
                    hint="设置为0表示无限制"
                    persistent-hint
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-counter"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="editedItem.useTokens"
                    label="已用Token"
                    hint="由于系统统计数据不一定准确，你可以通过修改这个值来校准使用情况"
                    type="number"
                    variant="outlined"
                    density="comfortable"
                    prepend-inner-icon="mdi-counter"
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-container>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4 bg-grey-lighten-5">
          <v-spacer></v-spacer>
          <v-btn color="error" variant="text" @click="dialog = false" prepend-icon="mdi-close">
            取消
          </v-btn>
          <v-btn color="primary" variant="text" @click="saveConfig" prepend-icon="mdi-content-save">
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 消息提示 -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="2000">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script>
export default {
  name: 'AIConfigView',
  data: () => ({
    loading: false,
    dialog: false,
    valid: false,
    aiOpen: false,
    headers: [
      { title: '配置名称', key: 'name', align: 'start', sortable: false },
      { title: '类型', key: 'type', align: 'center', sortable: false },
      { title: 'API地址', key: 'apiUrl', align: 'start', sortable: false },
      { title: '模型', key: 'model', align: 'start', sortable: false },
      { title: 'Token使用情况', key: 'useTokens', align: 'center', sortable: false },
      { title: '状态', key: 'status', align: 'center', sortable: false },
      { title: '操作', key: 'actions', align: 'center', sortable: false },
    ],
    aiConfigList: [],
    editedItem: {
      id: null,
      name: '',
      apiUrl: '',
      apiKey: '',
      model: '',
      prompt: '',
      type: 0,
      status: 1,
      useTokens: 0,
      maxTokens: 0,
    },
    defaultItem: {
      id: null,
      name: '',
      apiUrl: '',
      apiKey: '',
      model: '',
      prompt: '',
      type: 0,
      status: 1,
      useTokens: 0,
      maxTokens: 0,
    },
    snackbar: {
      show: false,
      text: '',
      color: 'success',
    },
    typeOptions: [
      { title: '默认', value: 0 },
      { title: '评论弹幕审核', value: 1 },
      { title: '文本稿件摘要生成', value: 2 },
    ],
    promptMap: {},
    promptHint: '',
  }),

  created() {
    this.checkAIOpen()
    this.initialize()
    this.getPromptOptions()
  },

  methods: {
    // 初始化数据
    initialize() {
      this.loading = true
      this.httpGet('/admin/ai/list', (json) => {
        if (json.status === 200) {
          this.aiConfigList = json.data
        }
        this.loading = false
      })
    },
    openAIConfig() {
      this.httpPost('/admin/ai/open', {}, (json) => {
        if (json.status === 200) {
          this.aiOpen = true
        } else {
          this.showMessage(json.message || '操作失败', 'error')
        }
      })
    },

    // 检查AI功能是否开启
    checkAIOpen() {
      this.httpGet('/admin/setting/info', (json) => {
        this.aiOpen = json.data.openAIConfig
      })
    },

    // 获取Prompt选项
    getPromptOptions() {
      this.httpPost('/admin/ai/prompt', {}, (json) => {
        if (json.status === 200) {
          this.promptMap = json.data
        }
      })
    },

    // 打开编辑对话框
    openDialog(item) {
      this.dialog = true
      if (item) {
        this.editedItem = { ...item }
      } else {
        this.editedItem = { ...this.defaultItem }
      }

      // 设置对应类型的prompt提示
      this.updatePromptHint()
    },

    // 处理类型变更
    handleTypeChange() {
      // 根据类型设置默认prompt
      if (this.promptMap[this.editedItem.type]) {
        this.editedItem.prompt = this.promptMap[this.editedItem.type]
      } else {
        this.editedItem.prompt = ''
      }

      // 更新提示信息
      this.updatePromptHint()
    },

    // 更新prompt提示信息
    updatePromptHint() {
      if (this.promptMap[this.editedItem.type]) {
        this.promptHint = `系统已为该类型提供默认Prompt，您可以根据需要修改`
      } else {
        this.promptHint = `系统未提供该类型的默认Prompt，请自行填写`
      }
    },

    closeAIExamine() {
      this.httpPost('/admin/ai/close', {}, (json) => {
        if (json.status === 200) {
          this.aiOpen = false
        } else {
          this.showMessage(json.message || '操作失败', 'error')
        }
      })
    },

    // 保存配置
    async saveConfig() {
      if (!this.$refs.form.validate()) return

      const url = this.editedItem.id ? '/admin/ai/update' : '/admin/ai/save'
      this.httpPost(url, this.editedItem, (json) => {
        if (json.status === 200) {
          this.showMessage('操作成功', 'success')
          this.dialog = false
          this.initialize()
          this.checkAIOpen()
        } else {
          this.showMessage(json.message || '操作失败', 'error')
        }
      })
    },

    // 删除配置
    deleteConfig(id) {
      if (confirm('确定要删除该配置吗？')) {
        const deleteItem = { id: id }
        this.httpPost('/admin/ai/delete', deleteItem, (json) => {
          if (json.status === 200) {
            this.showMessage('删除成功', 'success')
            this.initialize()
            this.checkAIOpen()
          } else {
            this.showMessage(json.message || '删除失败', 'error')
          }
        })
      }
    },
    // 获取类型文本
    getTypeText(type) {
      switch (type) {
        case 0:
          return '默认'
        case 1:
          return '评论弹幕审核'
        default:
          return '未知类型'
      }
    },

    // 获取类型颜色
    getTypeColor(type) {
      switch (type) {
        case 0:
          return 'blue'
        case 1:
          return 'purple'
        default:
          return 'grey'
      }
    },

    // 显示消息提示
    showMessage(text, color) {
      this.snackbar.text = text
      this.snackbar.color = color
      this.snackbar.show = true
    },
  },
}
</script>

<style scoped>
.title-wrap {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>