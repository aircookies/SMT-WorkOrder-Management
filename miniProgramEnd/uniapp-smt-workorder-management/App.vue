<script>
import { isLoggedIn } from './utils/auth'
import store from './store/index'

export default {
  onLaunch() {
    console.log('App Launch')

    // 从本地存储恢复用户信息到全局状态
    store.loadUser()

    // 路由守卫：未登录时跳转登录页
    if (!isLoggedIn()) {
      uni.reLaunch({ url: '/pages/login/index' })
    }
  },

  onShow() {
    console.log('App Show')
  },

  onHide() {
    console.log('App Hide')
  }
}
</script>

<style>
/* 全局公共样式 */

page {
  background-color: #F5F6FA;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial,
    sans-serif;
  font-size: 28rpx;
  color: #333333;
  line-height: 1.5;
}

/* 清除默认样式 */
view, text, image, input, textarea, button, scroll-view {
  box-sizing: border-box;
}

button {
  margin: 0;
  padding: 0;
}

/* 安全区域底部占位 */
.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

/* 通用单行文本溢出省略 */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
