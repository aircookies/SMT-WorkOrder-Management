<template>
  <view class="mine-page">
    <!-- 用户头像区域 -->
    <view class="user-header">
      <view class="avatar-wrapper">
        <view class="avatar">
          <text class="avatar-text">{{ avatarText }}</text>
        </view>
      </view>
      <text class="user-name">{{ user?.name || '未登录' }}</text>
      <view class="role-badge">
        <text>{{ user?.roleName || '-' }}</text>
      </view>
    </view>

    <!-- 信息卡片 -->
    <view class="info-section">
      <view class="section-card">
        <view class="section-title">个人信息</view>

        <view class="info-row">
          <text class="info-icon">&#x1F464;</text>
          <text class="info-label">用户名</text>
          <text class="info-value">{{ user?.username || '-' }}</text>
        </view>

        <view class="info-row">
          <text class="info-icon">&#x1F4CB;</text>
          <text class="info-label">姓名</text>
          <text class="info-value">{{ user?.name || '-' }}</text>
        </view>

        <view class="info-row">
          <text class="info-icon">&#x1F511;</text>
          <text class="info-label">角色</text>
          <text class="info-value">{{ user?.roleName || '-' }}</text>
        </view>

        <view class="info-row">
          <text class="info-icon">&#x1F4C5;</text>
          <text class="info-label">权限说明</text>
          <text class="info-value info-value-desc">{{ permissionDesc }}</text>
        </view>
      </view>
    </view>

    <!-- 关于系统 -->
    <view class="info-section">
      <view class="section-card">
        <view class="section-title">关于系统</view>

        <view class="info-row">
          <text class="info-icon">&#x2139;</text>
          <text class="info-label">系统版本</text>
          <text class="info-value">1.0.0</text>
        </view>

        <view class="info-row">
          <text class="info-icon">&#x1F4F1;</text>
          <text class="info-label">客户端</text>
          <text class="info-value">微信小程序</text>
        </view>
      </view>
    </view>

    <!-- 退出登录按钮 -->
    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">
        退出登录
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { logout } from '../../api/login'
import { clearAuth } from '../../utils/auth'
import store from '../../store/index'

// 当前用户信息
const user = computed(() => store.state.user)

// 头像文字（取名字最后一个字）
const avatarText = computed(() => {
  const name = user.value?.name
  if (!name) return '?'
  return name.charAt(name.length - 1)
})

// 权限说明
const permissionDesc = computed(() => {
  const roleId = String(user.value?.roleId || '')
  const map = {
    '1': '可管理所有模块，包括用户、部门、产线、产品、工单',
    '2': '可管理工单和进行工序报工',
    '3': '可进行工序报工操作',
    '4': '仅可查看所有数据，无编辑权限'
  }
  return map[roleId] || '-'
})

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    confirmColor: '#4A90D9',
    success: async (res) => {
      if (res.confirm) {
        try {
          // 通知服务端 Token 失效
          await logout()
        } catch (err) {
          // 即使服务端退出失败，也清除本地登录信息
          console.warn('服务端退出失败，仍清除本地信息:', err)
        }

        // 清除本地登录信息
        clearAuth()
        store.clearUser()

        uni.showToast({ title: '已退出登录', icon: 'none' })

        setTimeout(() => {
          uni.reLaunch({ url: '/pages/login/index' })
        }, 500)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background-color: $bg-page;
}

/* 用户头部 */
.user-header {
  background: linear-gradient(135deg, $primary-color 0%, $primary-light 100%);
  padding: 80rpx 0 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  margin-bottom: 20rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid rgba(255, 255, 255, 0.6);
}

.avatar-text {
  font-size: 48rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.user-name {
  font-size: $font-xl;
  font-weight: 600;
  color: #FFFFFF;
  margin-bottom: 12rpx;
}

.role-badge {
  background-color: rgba(255, 255, 255, 0.2);
  padding: 6rpx 24rpx;
  border-radius: $radius-round;
  font-size: $font-xs;
  color: rgba(255, 255, 255, 0.9);
}

/* 信息分区 */
.info-section {
  padding: 20rpx 24rpx 0;
}

.section-card {
  background-color: $bg-card;
  border-radius: $radius-lg;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: $font-lg;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 20rpx;
  padding-left: 16rpx;
  border-left: 6rpx solid $primary-color;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid $border-light;

  &:last-child {
    border-bottom: none;
  }
}

.info-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
  width: 40rpx;
  text-align: center;
}

.info-label {
  font-size: $font-md;
  color: $text-regular;
  width: 160rpx;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  font-size: $font-md;
  color: $text-primary;
  text-align: right;
}

.info-value-desc {
  font-size: $font-sm;
  color: $text-secondary;
  text-align: right;
  line-height: 1.5;
}

/* 退出登录 */
.logout-section {
  padding: 60rpx 48rpx;
}

.logout-btn {
  height: 88rpx;
  line-height: 88rpx;
  background-color: $bg-card;
  color: $danger-color;
  font-size: $font-lg;
  border-radius: $radius-round;
  border: 2rpx solid $danger-color;
  letter-spacing: 2rpx;
}

.logout-btn::after {
  border: none;
}
</style>
