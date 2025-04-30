<template>
  <v-container>
    <v-card class="mb-6">
      <v-card-title class="text-h5 font-weight-bold">
        <v-icon icon="mdi-cloud-upload" class="mr-2" color="primary"></v-icon>
        文件上传测试
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <v-file-input
              v-model="uploadFile"
              label="选择文件"
              prepend-icon="mdi-file"
              show-size
              truncate-length="15"
              accept="*/*"
              :loading="uploading"
              variant="outlined"
            ></v-file-input>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="bucketName"
              label="存储桶名称"
              placeholder="默认为test"
              hint="不填写则使用默认值test"
              variant="outlined"
              prepend-icon="mdi-folder"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-btn
          color="primary"
          block
          :loading="uploading"
          :disabled="!uploadFile"
          @click="handleUpload"
          class="mt-2"
        >
          <v-icon left>mdi-cloud-upload</v-icon>
          上传文件
        </v-btn>
      </v-card-text>

      <v-expand-transition>
        <div v-if="uploadResult">
          <v-divider></v-divider>
          <v-card-text>
            <v-alert
              type="success"
              variant="tonal"
              title="上传成功"
              class="mb-3"
            >
              文件已成功上传到对象存储服务
            </v-alert>
            <v-list>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-link" color="primary"></v-icon>
                </template>
                <v-list-item-title>文件URL</v-list-item-title>
                <v-list-item-subtitle class="text-truncate">
                  <a :href="uploadResult.url" target="_blank">{{ uploadResult.url }}</a>
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-folder" color="primary"></v-icon>
                </template>
                <v-list-item-title>存储桶</v-list-item-title>
                <v-list-item-subtitle>{{ uploadResult.bucketName }}</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-file" color="primary"></v-icon>
                </template>
                <v-list-item-title>对象名称</v-list-item-title>
                <v-list-item-subtitle>{{ uploadResult.objectName }}</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-file-document" color="primary"></v-icon>
                </template>
                <v-list-item-title>内容类型</v-list-item-title>
                <v-list-item-subtitle>{{ uploadResult.contentType }}</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-weight" color="primary"></v-icon>
                </template>
                <v-list-item-title>文件大小</v-list-item-title>
                <v-list-item-subtitle>{{ formatFileSize(uploadResult.size) }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
            <v-row class="mt-2">
              <v-col>
                <v-btn
                  color="info"
                  block
                  @click="copyToClipboard(uploadResult.url)"
                >
                  <v-icon left>mdi-content-copy</v-icon>
                  复制URL
                </v-btn>
              </v-col>
              <v-col>
                <v-btn
                  color="warning"
                  block
                  @click="fillGetUrlForm(uploadResult.bucketName, uploadResult.objectName)"
                >
                  <v-icon left>mdi-link-variant</v-icon>
                  获取URL
                </v-btn>
              </v-col>
              <v-col>
                <v-btn
                  color="error"
                  block
                  @click="fillDeleteForm(uploadResult.bucketName, uploadResult.objectName)"
                >
                  <v-icon left>mdi-delete</v-icon>
                  删除文件
                </v-btn>
              </v-col>
            </v-row>
          </v-card-text>
        </div>
      </v-expand-transition>
    </v-card>

    <v-card class="mb-6">
      <v-card-title class="text-h5 font-weight-bold">
        <v-icon icon="mdi-link-variant" class="mr-2" color="info"></v-icon>
        获取文件URL测试
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="getUrlForm.bucketName"
              label="存储桶名称"
              required
              variant="outlined"
              prepend-icon="mdi-folder"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="getUrlForm.objectName"
              label="对象名称"
              required
              variant="outlined"
              prepend-icon="mdi-file"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="getUrlForm.duration"
              label="过期时间"
              type="number"
              hint="默认为7"
              variant="outlined"
              prepend-icon="mdi-timer"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-select
              v-model="getUrlForm.timeUnit"
              :items="timeUnits"
              label="时间单位"
              hint="默认为天(DAYS)"
              variant="outlined"
              prepend-icon="mdi-clock"
            ></v-select>
          </v-col>
        </v-row>
        <v-btn
          color="info"
          block
          :loading="gettingUrl"
          :disabled="!getUrlForm.bucketName || !getUrlForm.objectName"
          @click="handleGetUrl"
          class="mt-2"
        >
          <v-icon left>mdi-link-variant</v-icon>
          获取URL
        </v-btn>
      </v-card-text>

      <v-expand-transition>
        <div v-if="urlResult">
          <v-divider></v-divider>
          <v-card-text>
            <v-alert
              type="info"
              variant="tonal"
              title="获取URL成功"
              class="mb-3"
            >
              已成功获取文件的临时访问URL
            </v-alert>
            <v-list>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-link" color="info"></v-icon>
                </template>
                <v-list-item-title>文件URL</v-list-item-title>
                <v-list-item-subtitle class="text-truncate">
                  <a :href="urlResult.url" target="_blank">{{ urlResult.url }}</a>
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-folder-open" color="info"></v-icon>
                </template>
                <v-list-item-title>存储路径</v-list-item-title>
                <v-list-item-subtitle>{{ urlResult.storagePath }}</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-timer" color="info"></v-icon>
                </template>
                <v-list-item-title>过期时间</v-list-item-title>
                <v-list-item-subtitle>{{ urlResult.duration }} {{ getTimeUnitText(urlResult.timeUnit) }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
            <v-btn
              color="info"
              block
              @click="copyToClipboard(urlResult.url)"
              class="mt-2"
            >
              <v-icon left>mdi-content-copy</v-icon>
              复制URL
            </v-btn>
          </v-card-text>
        </div>
      </v-expand-transition>
    </v-card>

    <v-card>
      <v-card-title class="text-h5 font-weight-bold">
        <v-icon icon="mdi-delete" class="mr-2" color="error"></v-icon>
        删除文件测试
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="deleteForm.bucketName"
              label="存储桶名称"
              required
              variant="outlined"
              prepend-icon="mdi-folder"
            ></v-text-field>
          </v-col>
          <v-col cols="12" md="6">
            <v-text-field
              v-model="deleteForm.objectName"
              label="对象名称"
              required
              variant="outlined"
              prepend-icon="mdi-file"
            ></v-text-field>
          </v-col>
        </v-row>
        <v-btn
          color="error"
          block
          :loading="deleting"
          :disabled="!deleteForm.bucketName || !deleteForm.objectName"
          @click="confirmDelete"
          class="mt-2"
        >
          <v-icon left>mdi-delete</v-icon>
          删除文件
        </v-btn>
      </v-card-text>

      <v-expand-transition>
        <div v-if="deleteResult">
          <v-divider></v-divider>
          <v-card-text>
            <v-alert
              type="success"
              variant="tonal"
              title="删除成功"
              class="mb-3"
            >
              {{ deleteResult.message }}
            </v-alert>
            <v-list>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-folder" color="error"></v-icon>
                </template>
                <v-list-item-title>存储桶</v-list-item-title>
                <v-list-item-subtitle>{{ deleteResult.bucketName }}</v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon icon="mdi-file" color="error"></v-icon>
                </template>
                <v-list-item-title>对象名称</v-list-item-title>
                <v-list-item-subtitle>{{ deleteResult.objectName }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
          </v-card-text>
        </div>
      </v-expand-transition>
    </v-card>

    <!-- 确认删除对话框 -->
    <v-dialog v-model="deleteDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h5">确认删除</v-card-title>
        <v-card-text>
          您确定要删除以下文件吗？此操作不可撤销。
          <v-list>
            <v-list-item>
              <v-list-item-title>存储桶：{{ deleteForm.bucketName }}</v-list-item-title>
            </v-list-item>
            <v-list-item>
              <v-list-item-title>对象名称：{{ deleteForm.objectName }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" variant="text" @click="deleteDialog = false">取消</v-btn>
          <v-btn color="error" variant="text" @click="handleDelete">确认删除</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 消息提示 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="top"
    >
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn variant="text" @click="snackbar.show = false">关闭</v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
export default {
  name: 'TestOSS',
  data() {
    return {
      // 上传相关
      uploadFile: null,
      bucketName: '',
      uploading: false,
      uploadResult: null,
      
      // 获取URL相关
      getUrlForm: {
        bucketName: '',
        objectName: '',
        duration: '7',
        timeUnit: 'DAYS'
      },
      gettingUrl: false,
      urlResult: null,
      timeUnits: [
        { title: '秒', value: 'SECONDS' },
        { title: '分钟', value: 'MINUTES' },
        { title: '小时', value: 'HOURS' },
        { title: '天', value: 'DAYS' }
      ],
      
      // 删除相关
      deleteForm: {
        bucketName: '',
        objectName: ''
      },
      deleting: false,
      deleteResult: null,
      deleteDialog: false,
      
      // 消息提示
      snackbar: {
        show: false,
        text: '',
        color: 'info'
      }
    }
  },
  methods: {
    // 上传文件
    handleUpload() {
      if (!this.uploadFile) {
        this.showMessage('请选择要上传的文件', 'error');
        return;
      }
      
      this.uploading = true;
      
      const formData = new FormData();
      formData.append('file', this.uploadFile);
      
      if (this.bucketName) {
        formData.append('bucketName', this.bucketName);
      }
      
      this.uploadFiles('/oss/test/upload', formData, (response) => {
        this.uploading = false;
        
        if (response.status === 200) {
          this.uploadResult = response.data;
          this.showMessage('文件上传成功', 'success');
        } else {
          this.showMessage(`上传失败: ${response.message}`, 'error');
        }
      });
    },
    
    // 获取文件URL
    handleGetUrl() {
      if (!this.getUrlForm.bucketName || !this.getUrlForm.objectName) {
        this.showMessage('请填写存储桶名称和对象名称', 'error');
        return;
      }
      
      this.gettingUrl = true;
      
      let url = `/oss/test/url?bucketName=${this.getUrlForm.bucketName}&objectName=${this.getUrlForm.objectName}`;
      
      if (this.getUrlForm.duration) {
        url += `&duration=${this.getUrlForm.duration}`;
      }
      
      if (this.getUrlForm.timeUnit) {
        url += `&timeUnit=${this.getUrlForm.timeUnit}`;
      }
      
      this.httpGet(url, (response) => {
        this.gettingUrl = false;
        
        if (response.status === 200) {
          this.urlResult = response.data;
          this.showMessage('获取URL成功', 'info');
        } else {
          this.showMessage(`获取URL失败: ${response.message}`, 'error');
        }
      });
    },
    
    // 确认删除对话框
    confirmDelete() {
      if (!this.deleteForm.bucketName || !this.deleteForm.objectName) {
        this.showMessage('请填写存储桶名称和对象名称', 'error');
        return;
      }
      
      this.deleteDialog = true;
    },
    
    // 删除文件
    handleDelete() {
      this.deleteDialog = false;
      this.deleting = true;
      
      // 由于fetch.js中没有实现DELETE方法，我们使用POST方法并在URL中添加_method=DELETE参数
      this.httpPost(`/oss/test/delete?bucketName=${this.deleteForm.bucketName}&objectName=${this.deleteForm.objectName}&_method=DELETE`, {}, (response) => {
        this.deleting = false;
        
        if (response.status === 200) {
          this.deleteResult = response.data;
          this.showMessage('文件删除成功', 'success');
        } else {
          this.showMessage(`删除失败: ${response.message}`, 'error');
        }
      });
    },
    
    // 填充获取URL表单
    fillGetUrlForm(bucketName, objectName) {
      this.getUrlForm.bucketName = bucketName;
      this.getUrlForm.objectName = objectName;
      // 滚动到获取URL区域
      this.$nextTick(() => {
        document.querySelectorAll('.v-card')[1].scrollIntoView({ behavior: 'smooth' });
      });
    },
    
    // 填充删除表单
    fillDeleteForm(bucketName, objectName) {
      this.deleteForm.bucketName = bucketName;
      this.deleteForm.objectName = objectName;
      // 滚动到删除区域
      this.$nextTick(() => {
        document.querySelectorAll('.v-card')[2].scrollIntoView({ behavior: 'smooth' });
      });
    },
    
    // 复制到剪贴板
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        this.showMessage('已复制到剪贴板', 'success');
      }).catch(err => {
        this.showMessage('复制失败: ' + err, 'error');
      });
    },
    
    // 显示消息
    showMessage(text, color = 'info') {
      this.snackbar.text = text;
      this.snackbar.color = color;
      this.snackbar.show = true;
    },
    
    // 格式化文件大小
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes';
      
      const k = 1024;
      const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    
    // 获取时间单位文本
    getTimeUnitText(unit) {
      const unitMap = {
        'SECONDS': '秒',
        'MINUTES': '分钟',
        'HOURS': '小时',
        'DAYS': '天'
      };
      
      return unitMap[unit] || unit;
    }
  }
}
</script>

<style scoped>
.v-card {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.v-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.text-truncate {
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>