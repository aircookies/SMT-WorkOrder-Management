<template>
  <view class="login-page">
    <!-- 顶部装饰区域 -->
    <view class="login-header">
      <view class="login-header-bg"></view>
      <view class="login-header-content">
        <image class="login-logo" src="/static/logo.png" mode="aspectFit" />
        <text class="login-title">SMT工单管理系统</text>
        <text class="login-subtitle">车间报工端</text>
      </view>
    </view>

    <!-- 登录表单 -->
    <view class="login-form-wrapper">
      <view class="login-form">
        <view class="form-item">
          <view class="form-item-icon">
            <text class="iconfont">&#x1F464;</text>
          </view>
          <input
            class="form-input"
            type="text"
            v-model="form.username"
            placeholder="请输入用户名"
            placeholder-class="placeholder"
            :maxlength="30"
            @confirm="handleLogin"
          />
        </view>

        <view class="form-item">
          <view class="form-item-icon">
            <text class="iconfont">&#x1F512;</text>
          </view>
          <input
            class="form-input"
            type="password"
            v-model="form.password"
            placeholder="请输入密码"
            placeholder-class="placeholder"
            :maxlength="30"
            @confirm="handleLogin"
          />
        </view>

        <button
          class="login-btn"
          :class="{ 'login-btn-disabled': !canLogin }"
          :disabled="!canLogin || loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { login } from '../../api/login'
import { setToken } from '../../utils/auth'
import store from '../../store/index'

const form = ref({
  username: '',
  password: ''
})

const loading = ref(false)

const canLogin = computed(() => {
  return form.value.username.trim().length > 0 && form.value.password.length > 0
})

const handleLogin = async () => {
  if (!canLogin.value || loading.value) return

  loading.value = true
  try {
    const res = await login(form.value.username.trim(), form.value.password)
    const data = res.data

    // 保存 Token 到本地存储
    setToken(data.token)

    // 保存用户信息到全局状态和本地存储
    store.setUser({
      userId: data.userId,
      username: data.username,
      name: data.name,
      roleId: data.roleId,
      roleName: data.roleName
    })

    uni.showToast({ title: '登录成功', icon: 'success' })

    // 跳转到首页（tabBar 页面用 switchTab）
    setTimeout(() => {
      uni.switchTab({ url: '/pages/home/index' })
    }, 500)

  } catch (err) {
    console.error('登录失败:', err)
    // 错误提示已在 request.js 中统一处理
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background-color: $bg-page;
  display: flex;
  flex-direction: column;
}

/* 顶部装饰区域 */
.login-header {
  position: relative;
  height: 480rpx;
  overflow: hidden;
}

.login-header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 520rpx;
  background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
  border-radius: 0 0 60rpx 60rpx;
}

.login-header-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 160rpx;
}

.login-logo {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 24rpx;
}

.login-title {
  font-size: $font-xxl;
  font-weight: 600;
  color: #FFFFFF;
  letter-spacing: 2rpx;
}

.login-subtitle {
  font-size: $font-md;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 12rpx;
}

/* 表单区域 */
.login-form-wrapper {
  flex: 1;
  padding: 0 60rpx;
  margin-top: -60rpx;
  position: relative;
  z-index: 2;
}

.login-form {
  background: $bg-card;
  border-radius: $radius-lg;
  padding: 60rpx 40rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.08);
}

.form-item {
  display: flex;
  align-items: center;
  border-bottom: 2rpx solid $border-light;
  padding: 28rpx 0;
  margin-bottom: 20rpx;
}

.form-item-icon {
  width: 60rpx;
  text-align: center;
  font-size: 36rpx;
}

.form-input {
  flex: 1;
  font-size: $font-md;
  color: $text-primary;
  height: 60rpx;
}

.placeholder {
  color: $text-placeholder;
  font-size: $font-md;
}

.login-btn {
  margin-top: 60rpx;
  height: 88rpx;
  line-height: 88rpx;
  background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
  color: #FFFFFF;
  font-size: $font-lg;
  font-weight: 500;
  border-radius: $radius-round;
  border: none;
  letter-spacing: 4rpx;
}

.login-btn-disabled {
  opacity: 0.5;
}

.login-btn::after {
  border: none;
}
</style>
