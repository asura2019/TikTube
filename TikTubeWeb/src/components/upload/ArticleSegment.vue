<template>
  <v-card class="mb-6" variant="outlined">
    <v-card-title class="d-flex justify-space-between align-center">
      <div class="d-flex align-center">
        <v-icon icon="mdi-file-document-outline" class="mr-2" color="primary"></v-icon>
        <span>段落 {{ index + 1 }}</span>
      </div>
      <v-btn
        v-if="segments.length > 1"
        icon="mdi-delete"
        variant="text"
        color="error"
        density="comfortable"
        @click="$emit('remove-segment', index)"
      ></v-btn>
    </v-card-title>
    
    <v-divider></v-divider>
    
    <v-card-text>
      <!-- 文章类型选择 -->
      <v-row class="mb-4">
        <v-col cols="12" md="6">
          <v-select
            v-model="segment.type"
            :items="articleTypeOptions"
            label="段落类型"
            variant="outlined"
            density="comfortable"
            prepend-inner-icon="mdi-format-list-text"
            @update:model-value="handleTypeChange"
          ></v-select>
        </v-col>
        
        <!-- 加密文章密码输入框 -->
        <v-col cols="12" md="6" v-if="segment.type === 2">
          <v-text-field
            v-model="segment.password"
            label="访问密码"
            variant="outlined"
            density="comfortable"
            prepend-inner-icon="mdi-lock"
            placeholder="请设置段落访问密码"
            :rules="[(v) => segment.type !== 2 || !!v || '密码不能为空']"
            type="password"
          ></v-text-field>
        </v-col>
      </v-row>
      
      <!-- Markdown编辑器 -->
      <ArticleTextEdit
        :ref="`markdownEditor${index}`"
        :idname="`articleEditor${index}`"
        :height="400"
        :uploadurl="`/api/upload/article`"
        :markdown="segment.content"
        :placeholder="`在这里编写段落${index + 1}的内容...`"
        @vditor-input="updateContent"
      />
    </v-card-text>
  </v-card>
</template>

<script>
import ArticleTextEdit from '@/components/vditor/ArticleTextEdit.vue'

export default {
  name: 'ArticleSegment',
  components: {
    ArticleTextEdit
  },
  props: {
    segment: {
      type: Object,
      required: true
    },
    index: {
      type: Number,
      required: true
    },
    segments: {
      type: Array,
      required: true
    },
    articleTypeOptions: {
      type: Array,
      required: true
    }
  },
  created() {
  },
  methods: {
    updateContent(content) {
      this.segment.content = content
      this.$emit('update-content', this.index, content)
    },
    handleTypeChange(value) {
      // 如果不是加密文章，清空密码
      if (value !== 2) {
        this.segment.password = ''
      }
      this.$emit('update-type', this.index, value)
    },
    setTextValue(value) {
      this.$refs[`markdownEditor${this.index}`].setTextValue(value)
    }
  }
}
</script>