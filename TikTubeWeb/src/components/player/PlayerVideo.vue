<!-- 视频播放组件 -->
<template>
  <div id="artRef" ref="artRef"></div>
</template>

<script>
import Artplayer from 'artplayer'
import artplayerPluginDanmuku from 'artplayer-plugin-danmuku'
import artplayerPluginAds from 'artplayer-plugin-ads'
import { useUserStore } from '@/stores/userStore'

export default {
  data() {
    return {
      instance: null,
      snackbar: false,
      // 播放记录事件
      playLogTask: null,
      seek: 0,
    }
  },
  props: {
    video: {
      type: Object,
      default: () => {},
    },
    picurl: {
      type: String,
      default: '',
    },
    article: {
      type: Number,
      default: 0,
    },
    openAds: {
      type: Boolean,
      default: false,
    },
    adsInfo: {
      type: Object,
      default: () => {},
    },
  },
  methods: {
    // 调整播放器高度
    adjustPlayerHeight() {
      const viewportHeight = window.innerHeight
      const playerHeight = viewportHeight * 0.8 // 80%的视口高度
      this.$refs.artRef.style.height = `${playerHeight}px`
    },
  },
  mounted() {
    //this.adjustPlayerHeight();
    this.seek = parseInt(this.$route.query.seek)
    if (isNaN(this.seek)) {
      this.seek = 0
    }
    const videoId = this.video.id
    const API = this.SERVER_API_URL
    const userInfo = useUserStore()
    const adsInfo = this.adsInfo
    if (this.openAds) {
      //
      this.instance = new Artplayer({
        // 容器
        container: this.$refs.artRef,
        // 视频地址
        url: this.video.fileUrl + '?key=' + encodeURIComponent(this.video.key),
        // 视频封面
        poster: this.picurl,
        // 自动启用迷你窗口
        // autoMini: true,
        // 显示视频反转按钮
        flip: true,
        // 显示视频播放速度
        playbackRate: true,
        // 显示视频长宽比
        aspectRatio: true,
        // 显示视频截图功能
        screenshot: true,
        setting: true,
        hotkey: true,
        // 画中画
        pip: true,
        fullscreen: true,
        fullscreenWeb: true,
        // 移动端长按快进
        fastForward: true,
        // 移动端自动旋转播放器
        autoOrientation: true,
        // 自定义右键菜单
        contextmenu: [
          {
            html: '不挂高数出品',
            click: function (...args) {
              window.open('https://www.buguagaoshu.com/')
            },
          },
        ],
        plugins: [
          artplayerPluginDanmuku({
            danmuku: function () {
              return new Promise((resolve) => {
                let dList = []
                fetch(`${API}/danmaku/v1?id=${videoId}`, {
                  headers: {
                    'Content-Type': 'application/json; charset=UTF-8',
                  },
                  method: 'GET',
                  credentials: 'include',
                })
                  .then((response) => response.json())
                  .then((json) => {
                    if (json.status === 200) {
                      for (let d of json.data) {
                        let a = {
                          text: d.text,
                          time: d.time,
                          mode: d.type,
                          color: d.color,
                        }
                        dList.push(a)
                      }
                    }
                    resolve(dList)
                  })
                  .catch((error) => {
                    console.error('HTTP Post Error:', error)
                    resolve([])
                  })
              })
            },
            // 这是用户在输入框输入弹幕文本，然后点击发送按钮后触发的函数
            // 你可以对弹幕做合法校验，或者做存库处理
            // 当返回true后才表示把弹幕加入到弹幕队列
            async beforeEmit(danmu) {
              if (userInfo.userData == null) {
                alert('请先登录')
                console.log('请先登录')
                return false
              }
              // 构造提交后端的数据
              const danmuData = {
                text: danmu.text,
                time: danmu.time,
                type: danmu.mode,
                color: danmu.color,
                id: videoId,
              }
              fetch(`${API}/danmaku/v1`, {
                headers: {
                  'Content-Type': 'application/json; charset=UTF-8',
                  //'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
                },
                method: 'POST',
                credentials: 'include',
                body: JSON.stringify(danmuData),
              })
                .then((response) => response.json())
                .then((json) => {
                  if (json.code == 0) {
                    return true
                  } else if (json.code == -1) {
                    console.log('弹幕发送成功，待管理员审核通过后其他观众即可看见你的弹幕！')
                    return true
                  } else {
                    return false
                  }
                })
                .catch((error) => {
                  console.error('HTTP Post Error:', error)
                  return null
                })
              return true
            },
          }),
          // 如果 openAds 为 true 则开启广告
          artplayerPluginAds({
            // html广告，假如是视频广告则忽略该值
            html: `<img src="${adsInfo.imageUrl}">`,

            // 视频广告的地址
            video: adsInfo.videoUrl,

            // 广告跳转网址，为空则不跳转
            url: adsInfo.url,

            // 必须观看的时长，期间不能被跳过，单位为秒
            // 当该值大于或等于totalDuration时，不能提前关闭广告
            // 当该值等于或小于0时，则随时都可以关闭广告
            playDuration: 5,

            // 广告总时长，单位为秒
            totalDuration: 10,
            // 多语言支持
            i18n: {
              close: '关闭广告',
              countdown: '%s秒',
              detail: '查看详情',
              canBeClosed: '%s秒后可关闭广告',
            },
          }),
        ],
      })

      this.instance.on('artplayerPluginAds:click', (ads) => {
        this.httpPost(`/web/notice/click/${adsInfo.id}`, {}, (json) => {
          // console.log(json)
        })
      })

      this.instance.on('artplayerPluginAds:skip', (ads) => {
        console.log('跳过广告')
        if (this.$refs.artRef == null) {
          setTimeout(() => {
            console.log('暂停播放销毁视频！')
            this.instance.pause()
            this.instance.destroy(false)
          }, 10)
        }
      })
    } else {
      this.instance = new Artplayer({
        // 容器
        container: this.$refs.artRef,
        // 视频地址
        url: this.video.fileUrl + '?key=' + encodeURIComponent(this.video.key),
        // 视频封面
        poster: this.picurl,
        // 自动启用迷你窗口
        // autoMini: true,
        // 显示视频反转按钮
        flip: true,
        // 显示视频播放速度
        playbackRate: true,
        // 显示视频长宽比
        aspectRatio: true,
        // 显示视频截图功能
        screenshot: true,
        setting: true,
        hotkey: true,
        // 画中画
        pip: true,
        fullscreen: true,
        fullscreenWeb: true,
        // 移动端长按快进
        fastForward: true,
        // 移动端自动旋转播放器
        autoOrientation: true,
        // 自定义右键菜单
        contextmenu: [
          {
            html: '不挂高数出品',
            click: function (...args) {
              window.open('https://www.buguagaoshu.com/')
            },
          },
        ],
        plugins: [
          artplayerPluginDanmuku({
            danmuku: function () {
              return new Promise((resolve) => {
                let dList = []
                fetch(`${API}/danmaku/v1?id=${videoId}`, {
                  headers: {
                    'Content-Type': 'application/json; charset=UTF-8',
                  },
                  method: 'GET',
                  credentials: 'include',
                })
                  .then((response) => response.json())
                  .then((json) => {
                    if (json.status === 200) {
                      for (let d of json.data) {
                        let a = {
                          text: d.text,
                          time: d.time,
                          mode: d.type,
                          color: d.color,
                        }
                        dList.push(a)
                      }
                    }
                    resolve(dList)
                  })
                  .catch((error) => {
                    console.error('HTTP Post Error:', error)
                    resolve([])
                  })
              })
            },
            // 这是用户在输入框输入弹幕文本，然后点击发送按钮后触发的函数
            // 你可以对弹幕做合法校验，或者做存库处理
            // 当返回true后才表示把弹幕加入到弹幕队列
            async beforeEmit(danmu) {
              if (userInfo.userData == null) {
                alert('请先登录')
                console.log('请先登录')
                return false
              }
              // 构造提交后端的数据
              const danmuData = {
                text: danmu.text,
                time: danmu.time,
                type: danmu.mode,
                color: danmu.color,
                id: videoId,
              }
              fetch(`${API}/danmaku/v1`, {
                headers: {
                  'Content-Type': 'application/json; charset=UTF-8',
                  //'X-XSRF-TOKEN': this.$cookies.get('XSRF-TOKEN')
                },
                method: 'POST',
                credentials: 'include',
                body: JSON.stringify(danmuData),
              })
                .then((response) => response.json())
                .then((json) => {
                  if (json.code == 0) {
                    return true
                  } else if (json.code == -1) {
                    console.log('弹幕发送成功，待管理员审核通过后其他观众即可看见你的弹幕！')
                    return true
                  } else {
                    return false
                  }
                })
                .catch((error) => {
                  console.error('HTTP Post Error:', error)
                  return null
                })
              return true
            },
          }),
          // 如果 openAds 为 true 则开启广告
        ],
      })
    }

    this.instance.on('ready', () => {
      console.log('播放器准备完成')
      this.instance.seek = this.seek
    })

    this.instance.on('play', () => {
      console.info('play')
      // 记录播放量
      this.httpPost(`/article/playrecording/view/${this.video.articleId}`, {}, (json) => {})
      const userId = userInfo.userData.id
      // 构造播放历史记录请求数据
      const data = {
        articleId: this.video.articleId,
        fileId: 1,
        videoTime: this.instance.currentTime,
        userId: userId,
        videoId: this.video.id,
      }
      if (userId == null) {
        clearInterval(this.playLogTask)
      } else {
        this.playLogTask = setInterval(() => {
          data.videoTime = this.instance.currentTime
          // console.log(data)
          this.httpPost('/user/playrecording/save', data, (json) => {
            // console.log(json)
          })
        }, 5000)
      }
    })

    this.instance.on('pause', () => {
      console.info('pause')
      clearInterval(this.playLogTask)
    })

    this.instance.on('video:ended', () =>{
      console.info('video:ended')
      clearInterval(this.playLogTask)
    })

    this.instance.on('destroy', () => {
      console.info('destroy')
      clearInterval(this.playLogTask)
    })

    this.$nextTick(() => {
      this.$emit('get-instance', this.instance)
    })
  },
  beforeUnmount() {
    if (this.instance && this.instance.destroy) {
      console.log('销毁播放器')
      this.instance.destroy()
    }
  },
}
</script>

<style>
#artRef {
  height: 80vh; /* 视口高度的80%作为最大高度 */
}
</style>