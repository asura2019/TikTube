<template>
  <v-container fluid>
    <!-- 系统信息卡片 -->
    <v-row>
      <v-col cols="12">
        <v-card class="mx-auto w-100 mb-4" elevation="2" rounded="lg">
          <v-toolbar color="blue">
            <v-toolbar-title class="text-h5 font-weight-medium">
              <v-icon color="primary" class="mr-2">mdi-server</v-icon>
              系统运行信息
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-chip color="success" variant="flat" v-if="systemInfo" class="mr-4">
              最后更新: {{ formatTime(new Date()) }}
            </v-chip>
          </v-toolbar>

          <v-card-text v-if="systemInfo">
            <v-row>
              <!-- CPU使用率 -->
              <v-col cols="12" md="6" lg="3">
                <v-card variant="outlined" class="pa-3">
                  <div class="d-flex align-center mb-2">
                    <v-icon color="red" class="mr-2">mdi-cpu-64-bit</v-icon>
                    <span class="text-subtitle-1 font-weight-medium">CPU使用率</span>
                  </div>
                  <v-progress-circular
                    :model-value="cpuUsage"
                    :size="100"
                    :width="10"
                    :color="getCpuColor(cpuUsage)"
                  >
                    {{ cpuUsage }}%
                  </v-progress-circular>
                  <div class="mt-2 text-caption">
                    处理器数量: {{ systemInfo.cpuCount || '未知' }}
                  </div>
                </v-card>
              </v-col>

              <!-- 内存使用率 -->
              <v-col cols="12" md="6" lg="3">
                <v-card variant="outlined" class="pa-3">
                  <div class="d-flex align-center mb-2">
                    <v-icon color="blue" class="mr-2">mdi-memory</v-icon>
                    <span class="text-subtitle-1 font-weight-medium">内存使用率</span>
                  </div>
                  <v-progress-circular
                    :model-value="memoryUsage"
                    :size="100"
                    :width="10"
                    :color="getMemoryColor(memoryUsage)"
                  >
                    {{ memoryUsage }}%
                  </v-progress-circular>
                  <div class="mt-2 text-caption">
                    总内存: {{ formatMemory(systemInfo.systemTotalMemory) }}
                  </div>
                </v-card>
              </v-col>

              <!-- 磁盘使用率 -->
              <v-col cols="12" md="6" lg="3">
                <v-card variant="outlined" class="pa-3">
                  <div class="d-flex align-center mb-2">
                    <v-icon color="green" class="mr-2">mdi-harddisk</v-icon>
                    <span class="text-subtitle-1 font-weight-medium">磁盘使用率</span>
                  </div>
                  <v-progress-circular
                    :model-value="diskUsage"
                    :size="100"
                    :width="10"
                    :color="getDiskColor(diskUsage)"
                  >
                    {{ diskUsage }}%
                  </v-progress-circular>
                  <div class="mt-2 text-caption">
                    总空间: {{ formatMemory(systemInfo.diskTotalSpace) }} | 可用:
                    {{ formatMemory(systemInfo.diskAvailableSpace) }}
                  </div>
                </v-card>
              </v-col>

              <!-- JVM信息 -->
              <v-col cols="12" md="6" lg="3">
                <v-card variant="outlined" class="pa-3">
                  <div class="d-flex align-center mb-2">
                    <v-icon color="purple" class="mr-2">mdi-coffee</v-icon>
                    <span class="text-subtitle-1 font-weight-medium">JVM内存</span>
                  </div>
                  <v-progress-circular
                    :model-value="jvmUsage"
                    :size="100"
                    :width="10"
                    :color="getJvmColor(jvmUsage)"
                  >
                    {{ jvmUsage }}%
                  </v-progress-circular>
                  <div class="mt-2 text-caption">
                    JVM版本: {{ systemInfo.javaVersion || '未知' }}
                  </div>
                </v-card>
              </v-col>
            </v-row>

            <!-- 系统详细信息 -->
            <v-row class="mt-4">
              <v-col cols="12">
                <v-card variant="outlined">
                  <v-card-title class="text-subtitle-1">
                    <v-icon color="info" class="mr-2">mdi-information-outline</v-icon>
                    系统详细信息
                  </v-card-title>
                  <v-divider></v-divider>
                  <v-list density="compact">
                    <v-list-item>
                      <template v-slot:prepend>
                        <v-icon color="grey" class="mr-2">mdi-desktop-tower</v-icon>
                      </template>
                      <v-list-item-title>操作系统</v-list-item-title>
                      <v-list-item-subtitle
                        >{{ systemInfo.osName }} {{ systemInfo.osVersion }}</v-list-item-subtitle
                      >
                    </v-list-item>
                    <v-list-item>
                      <template v-slot:prepend>
                        <v-icon color="grey" class="mr-2">mdi-desktop-tower</v-icon>
                      </template>
                      <v-list-item-title>{{ webInfo.name }} 服务器版本</v-list-item-title>
                      <v-list-item-subtitle
                        >{{ systemInfo.webServerVersion }}
                      </v-list-item-subtitle>
                    </v-list-item>
                    <v-list-item>
                      <template v-slot:prepend>
                        <v-icon color="grey" class="mr-2">mdi-clock-outline</v-icon>
                      </template>
                      <v-list-item-title>系统运行时间</v-list-item-title>
                      <v-list-item-subtitle>{{
                        formatUptime(systemInfo.runTime)
                      }}</v-list-item-subtitle>
                    </v-list-item>

                    <v-list-item>
                      <template v-slot:prepend>
                        <v-icon color="grey" class="mr-2">mdi-trash-can-outline</v-icon>
                      </template>
                      <v-list-item-title>垃圾回收信息</v-list-item-title>
                      <v-list-item-subtitle
                        >次数: {{ systemInfo.gcCount || 0 }}次 | 总耗时:
                        {{ formatTime(systemInfo.gcTime) }} | 最大耗时:
                        {{
                          systemInfo.gcTopTime ? systemInfo.gcTopTime.toFixed(2) : 0
                        }}ms</v-list-item-subtitle
                      >
                    </v-list-item>

                    <v-list-item>
                      <template v-slot:prepend>
                        <v-icon color="grey" class="mr-2">mdi-memory</v-icon>
                      </template>
                      <v-list-item-title>JVM内存详情</v-list-item-title>
                      <v-list-item-subtitle
                        >已分配: {{ formatMemory(systemInfo.totalMemory) }} | 空闲:
                        {{ formatMemory(systemInfo.freeMemory) }} | 最大可用:
                        {{ formatMemory(systemInfo.maxMemory) }}</v-list-item-subtitle
                      >
                    </v-list-item>

                    <v-list-item>
                      <template v-slot:prepend>
                        <v-icon color="grey" class="mr-2">mdi-cpu-64-bit</v-icon>
                      </template>
                      <v-list-item-title>系统内存详情</v-list-item-title>
                      <v-list-item-subtitle
                        >总内存: {{ formatMemory(systemInfo.systemTotalMemory) }} | 空闲内存:
                        {{ formatMemory(systemInfo.systemFreeMemory) }}</v-list-item-subtitle
                      >
                    </v-list-item>
                  </v-list>
                </v-card>
              </v-col>
            </v-row>

            <!-- 历史数据图表 -->
            <v-row class="mt-4">
              <v-col cols="12">
                <v-card variant="outlined">
                  <v-card-title class="text-subtitle-1">
                    <v-icon color="primary" class="mr-2">mdi-chart-line</v-icon>
                    资源使用趋势
                  </v-card-title>
                  <v-card-text>
                    <div ref="chartContainer" style="width: 100%; height: 400px"></div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>

          <v-card-text v-else>
            <v-skeleton-loader type="article" />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { useWebInfoStore } from '@/stores/webInfoStore'
export default {
  data() {
    return {
      systemInfo: null,
      pollingInterval: null,
      chartInstance: null,
      chartData: {
        labels: [],
        cpuData: [],
        memoryData: [],
        diskData: [],
        jvmData: [],
      },
      snackbar: {
        show: false,
        text: '',
        color: 'success',
      },
      webInfo: useWebInfoStore().webInfo,
    }
  },
  computed: {
    // 计算CPU使用率
    cpuUsage() {
      if (!this.systemInfo || this.systemInfo.systemCpuLoad === undefined) return 0
      return Math.round(this.systemInfo.systemCpuLoad * 100)
    },

    // 计算内存使用率
    memoryUsage() {
      if (!this.systemInfo || !this.systemInfo.systemTotalMemory) return 0
      const usedSystemMemory = this.systemInfo.systemTotalMemory - this.systemInfo.systemFreeMemory
      return Math.round((usedSystemMemory / this.systemInfo.systemTotalMemory) * 100)
    },

    // 计算磁盘使用率 - 使用后端提供的实际值
    diskUsage() {
      if (
        !this.systemInfo ||
        !this.systemInfo.diskTotalSpace ||
        !this.systemInfo.diskAvailableSpace
      )
        return 0

      const usedDiskSpace = this.systemInfo.diskTotalSpace - this.systemInfo.diskAvailableSpace
      return Math.round((usedDiskSpace / this.systemInfo.diskTotalSpace) * 100)
    },

    // 计算JVM使用率
    jvmUsage() {
      if (!this.systemInfo || !this.systemInfo.maxMemory) return 0
      return Math.round((this.systemInfo.usedMemory / this.systemInfo.maxMemory) * 100)
    },
  },
  mounted() {
    this.fetchSystemInfo()
    // 设置轮询，每5秒获取一次系统信息
    this.pollingInterval = setInterval(() => {
      this.fetchSystemInfo()
    }, 5000)
  },
  beforeUnmount() {
    // 组件销毁前清除轮询
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval)
    }
    // 销毁图表实例
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  methods: {
    // 获取系统信息
    fetchSystemInfo() {
      this.httpGet('/admin/system/info', (json) => {
        if (json.status === 200) {
          // 保存原始数据
          this.systemInfo = json.data

          // 更新图表数据
          this.updateChartData()
        }
      })
    },

    // 更新图表数据
    updateChartData() {
      const now = new Date()
      const timeLabel = `${now.getHours().toString().padStart(2, '0')}:${now
        .getMinutes()
        .toString()
        .padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`

      // 限制数据点数量，保持最新的10个数据点
      if (this.chartData.labels.length >= 10) {
        this.chartData.labels.shift()
        this.chartData.cpuData.shift()
        this.chartData.memoryData.shift()
        this.chartData.diskData.shift()
        this.chartData.jvmData.shift()
      }

      // 添加新数据点
      this.chartData.labels.push(timeLabel)
      this.chartData.cpuData.push(this.cpuUsage)
      this.chartData.memoryData.push(this.memoryUsage)
      this.chartData.diskData.push(this.diskUsage)
      this.chartData.jvmData.push(this.jvmUsage)

      // 更新或创建图表
      this.renderChart()
    },

    // 渲染图表 - 使用ECharts
    renderChart() {
      if (!this.$refs.chartContainer) return

      // 如果图表实例不存在，则创建新图表
      if (!this.chartInstance) {
        // 动态导入ECharts
        import('echarts')
          .then((echarts) => {
            // 初始化ECharts实例
            this.chartInstance = echarts.init(this.$refs.chartContainer)
            this.updateEChart()

            // 添加窗口大小变化时自动调整图表大小
            window.addEventListener('resize', () => {
              if (this.chartInstance) {
                this.chartInstance.resize()
              }
            })
          })
          .catch((error) => {
            console.error('加载ECharts失败:', error)
          })
      } else {
        // 更新现有图表
        this.updateEChart()
      }
    },

    // 更新ECharts图表
    updateEChart() {
      if (!this.chartInstance) return

      // 设置图表选项
      const option = {
        title: {
          text: '系统资源使用趋势',
          textStyle: {
            fontSize: 14,
          },
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985',
            },
          },
        },
        legend: {
          data: ['CPU使用率', '内存使用率', '磁盘使用率', 'JVM使用率'],
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.chartData.labels,
          axisLabel: {
            rotate: 30,
          },
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: 100,
          axisLabel: {
            formatter: '{value}%',
          },
        },
        series: [
          {
            name: 'CPU使用率',
            type: 'line',
            // 移除 stack 属性
            areaStyle: {
              opacity: 0.2,
            },
            emphasis: {
              focus: 'series',
            },
            data: this.chartData.cpuData,
            itemStyle: {
              color: '#FF6384',
            },
            lineStyle: {
              width: 2,
            },
            smooth: true,
          },
          {
            name: '内存使用率',
            type: 'line',
            // 移除 stack 属性
            areaStyle: {
              opacity: 0.2,
            },
            emphasis: {
              focus: 'series',
            },
            data: this.chartData.memoryData,
            itemStyle: {
              color: '#36A2EB',
            },
            lineStyle: {
              width: 2,
            },
            smooth: true,
          },
          {
            name: '磁盘使用率',
            type: 'line',
            // 移除 stack 属性
            areaStyle: {
              opacity: 0.2,
            },
            emphasis: {
              focus: 'series',
            },
            data: this.chartData.diskData,
            itemStyle: {
              color: '#4BC0C0',
            },
            lineStyle: {
              width: 2,
            },
            smooth: true,
          },
          {
            name: 'JVM使用率',
            type: 'line',
            // 移除 stack 属性
            areaStyle: {
              opacity: 0.2,
            },
            emphasis: {
              focus: 'series',
            },
            data: this.chartData.jvmData,
            itemStyle: {
              color: '#9966FF',
            },
            lineStyle: {
              width: 2,
            },
            smooth: true,
          },
        ],
      }

      // 应用配置项
      this.chartInstance.setOption(option)
    },

    // 格式化内存大小
    formatMemory(bytes) {
      if (!bytes || bytes === 0) return '0 B'

      const units = ['B', 'KB', 'MB', 'GB', 'TB']
      let i = 0
      let value = bytes

      while (value >= 1024 && i < units.length - 1) {
        value /= 1024
        i++
      }

      return `${value.toFixed(2)} ${units[i]}`
    },

    // 格式化时间
    formatTime(date) {
      // 如果是Date对象
      if (date instanceof Date) {
        return `${date.getHours().toString().padStart(2, '0')}:${date
          .getMinutes()
          .toString()
          .padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`
      }

      // 如果是毫秒数
      if (typeof date === 'number') {
        const seconds = Math.floor(date / 1000)
        const minutes = Math.floor(seconds / 60)
        const hours = Math.floor(minutes / 60)

        return `${hours.toString().padStart(2, '0')}:${(minutes % 60)
          .toString()
          .padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}`
      }

      return '00:00:00'
    },

    // 格式化运行时间
    formatUptime(milliseconds) {
      if (!milliseconds) return '0秒'

      const totalSeconds = Math.floor(milliseconds / 1000)
      const days = Math.floor(totalSeconds / 86400)
      const hours = Math.floor((totalSeconds % 86400) / 3600)
      const minutes = Math.floor((totalSeconds % 3600) / 60)
      const remainingSeconds = totalSeconds % 60

      let result = ''
      if (days > 0) result += `${days}天 `
      if (hours > 0 || days > 0) result += `${hours}小时 `
      if (minutes > 0 || hours > 0 || days > 0) result += `${minutes}分钟 `
      result += `${remainingSeconds}秒`

      return result
    },

    // 获取CPU使用率颜色
    getCpuColor(usage) {
      if (usage < 50) return 'green'
      if (usage < 80) return 'orange'
      return 'red'
    },

    // 获取内存使用率颜色
    getMemoryColor(usage) {
      if (usage < 60) return 'blue'
      if (usage < 85) return 'orange'
      return 'red'
    },

    // 获取磁盘使用率颜色
    getDiskColor(usage) {
      if (usage < 70) return 'green'
      if (usage < 90) return 'orange'
      return 'red'
    },

    // 获取JVM使用率颜色
    getJvmColor(usage) {
      if (usage < 60) return 'purple'
      if (usage < 85) return 'orange'
      return 'red'
    },
  },
}
</script>
  
<style>
.v-progress-circular {
  margin: 0 auto;
  display: block;
}
</style>