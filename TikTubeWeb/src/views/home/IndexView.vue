<template>
  <v-container fluid>

    <v-banner class="my-4" color="warning" icon="$warning" lines="one" v-if="systemNotice != null">
      <v-banner-text>
        {{ systemNotice.content }}
      </v-banner-text>
    </v-banner>
    <!-- 分区 -->
    <v-row>
      <v-col>
        <v-menu v-for="item in categoryList" :key="item.id" open-on-hover offset-y>
          <template v-slot:activator="{ props }">
            <v-btn variant="text" color="primary" v-bind="props" @click="setCategory(item)">
              {{ item.name }}
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              v-for="c in item.children"
              :key="c.id"
              :title="c.name"
              @click="setCategory(c)"
            >
            </v-list-item>
          </v-list>
        </v-menu>
      </v-col>
    </v-row>
    <v-row>
      <v-divider />
    </v-row>
    <v-col />
    <!-- 视频卡片 -->
    <v-row>
      <v-col cols="12" sm="6" md="4" lg="3" xxl="2" v-for="item in videoList" :key="item.id">
        <VideoCared :video="item" />
      </v-col>
    </v-row>
    <v-container>
      <v-row justify="center">
        <v-pagination
          rounded="circle"
          :total-visible="7"
          v-model="page"
          :length="length"
          color="red"
          @update:model-value="pageChange"
        />
      </v-row>
    </v-container>
    <v-col> &nbsp; </v-col>
  </v-container>
</template>

<script>
import VideoCared from '@/components/card/VideoCard.vue'

export default {
  components: {
    VideoCared,
  },
  data() {
    return {
      videoList: [],
      page: 1,
      size: 24,
      length: 0,
      categoryList: [],
      systemNotice: {},
    }
  },
  created() {
    if (this.$route.query.page === undefined) {
      this.page = 1
    } else {
      this.page = parseInt(this.$route.query.page)
    }
    this.getCategory()
    this.getVideoList()
    this.getSystemNotice()
  },
  methods: {
    getCategory() {
      this.httpGet(`/category/tree`, (json) => {
        this.categoryList = json.data
      })
    },
    getSystemNotice() {
      this.httpGet(`/web/notice`, (json) => {
        if (json.data.show === 'true') {
          this.systemNotice = json.data
        } else {
          this.systemNotice = null
        }
      })
    },
    setCategory(value) {
      this.$router.push(`/v/${value.id}`)
    },
    getVideoList() {
      this.httpGet(`/article/home/list?page=${this.page}&limit=${this.size}`, (json) => {
        this.videoList = json.data.list
        this.page = json.data.currPage
        this.length = json.data.totalPage
      })
    },
    pageChange(value) {
      this.page = value
      this.$router.push({ query: { page: this.page } })
      this.getVideoList()
      window.scrollTo({
        top: 0,
        behavior: 'smooth',
      })
    },
  },
}
</script>

<style scoped>
a {
  text-decoration: none;
}
</style>
