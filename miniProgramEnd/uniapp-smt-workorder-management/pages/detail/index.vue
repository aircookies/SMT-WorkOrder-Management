<template>
  <view class="detail-page">
    <!-- 加载状态 -->
    <view v-if="pageLoading" class="page-loading">
      <text class="loading-text">加载中...</text>
    </view>

    <template v-else-if="order">
      <!-- 工单状态头部 -->
      <view class="status-header" :class="getHeaderClass(order.status)">
        <view class="status-header-inner">
          <text class="status-name">{{ getStatusName(order.status) }}</text>
          <text class="order-id-text">工单 #{{ order.id }}</text>
        </view>
      </view>

      <!-- 基本信息卡片 -->
      <view class="info-card">
        <view class="card-title">基本信息</view>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">产品</text>
            <text class="info-value">{{ order.productName || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">产线</text>
            <text class="info-value">{{ order.lineName || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">计划数量</text>
            <text class="info-value info-value-bold">{{ order.quantity }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">优先级</text>
            <view class="priority-tag" :class="getPriorityClass(order.priority)">
              <text>{{ getPriorityName(order.priority) }}</text>
            </view>
          </view>
          <view class="info-item">
            <text class="info-label">计划时间</text>
            <text class="info-value">{{ order.planningTime || '-' }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">创建人</text>
            <text class="info-value">{{ order.creatorName || '-' }}</text>
          </view>
        </view>
        <view v-if="order.remarks" class="remarks-section">
          <text class="info-label">备注</text>
          <text class="remarks-text">{{ order.remarks }}</text>
        </view>
      </view>

      <!-- 工序报告列表 -->
      <view class="process-card">
        <view class="card-title">工序报告</view>

        <!-- 工序概览 -->
        <view class="process-overview">
          <view
            v-for="seq in [1, 2, 3]"
            :key="seq"
            class="process-step"
            :class="{ 'process-step-done': isProcessDone(seq) }"
          >
            <view class="step-circle" :class="{ 'step-circle-done': isProcessDone(seq) }">
              <text>{{ seq }}</text>
            </view>
            <text class="step-name">{{ getProcessName(seq) }}</text>
            <text class="step-status">{{ isProcessDone(seq) ? '已报工' : '未报工' }}</text>
          </view>
          <!-- 连接箭头 -->
          <view class="process-arrow" style="left: 33%;">&#x2192;</view>
          <view class="process-arrow" style="left: 66%;">&#x2192;</view>
        </view>

        <!-- 工序报告详情 -->
        <view v-if="processList.length === 0" class="empty-process">
          <text>暂无报工记录</text>
        </view>

        <view
          v-for="report in processList"
          :key="report.id"
          class="process-item"
        >
          <view class="process-item-header">
            <view class="process-seq-tag">
              <text>{{ getProcessName(report.processSeq) }}</text>
            </view>
            <text class="process-time">{{ report.createTime }}</text>
          </view>
          <view class="process-item-body">
            <view class="process-stat">
              <text class="stat-label">合格</text>
              <text class="stat-value stat-good">{{ report.qualifiedQuantity }}</text>
            </view>
            <view class="process-stat">
              <text class="stat-label">不良</text>
              <text class="stat-value stat-bad">{{ report.badQuantity }}</text>
            </view>
            <view class="process-stat">
              <text class="stat-label">良率</text>
              <text class="stat-value stat-rate">{{ calcPassRate(report) }}</text>
            </view>
          </view>
          <view v-if="report.remarks" class="process-remarks">
            <text>备注: {{ report.remarks }}</text>
          </view>
        </view>
      </view>

      <!-- 底部报工按钮 -->
      <view v-if="canReport" class="bottom-action">
        <button class="report-btn" @click="goReport">
          工序报工
        </button>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getWorkOrderById } from '../../api/workorder'
import { getProcessByOrderId } from '../../api/process'
import store from '../../store/index'

const orderId = ref('')
const order = ref(null)
const processList = ref([])
const pageLoading = ref(true)

// 是否可以报工：角色 1(管理员)、2(计划员)、3(操作员) 可以报工
const canReport = computed(() => {
  const user = store.state.user
  if (!user) return false
  const roleId = String(user.roleId)
  return ['1', '2', '3'].includes(roleId)
})

// 已报工的工序序号集合
const doneProcessSet = computed(() => {
  const set = new Set()
  processList.value.forEach(r => set.add(r.processSeq))
  return set
})

const isProcessDone = (seq) => doneProcessSet.value.has(seq)

// ========== 数据加载 ==========

const loadData = async () => {
  pageLoading.value = true
  try {
    // 使用 allSettled 确保两个请求独立处理，一个失败不影响另一个
    const [orderResult, processResult] = await Promise.allSettled([
      getWorkOrderById(orderId.value),
      getProcessByOrderId(orderId.value)
    ])

    // 工单详情
    if (orderResult.status === 'fulfilled') {
      order.value = orderResult.value.data
    } else {
      console.error('加载工单详情失败:', orderResult.reason)
      uni.showToast({ title: '加载工单信息失败', icon: 'none' })
    }

    // 工序报告列表（加载失败时视为空列表，不影响页面渲染）
    if (processResult.status === 'fulfilled') {
      const data = processResult.value.data
      // 兼容后端返回单个对象或数组两种格式
      if (Array.isArray(data)) {
        processList.value = data
      } else if (data && typeof data === 'object') {
        processList.value = [data]
      } else {
        processList.value = []
      }
    } else {
      // 工序报告加载失败（可能是该工单暂无报工记录），不阻断页面
      console.warn('工序报告加载失败（可能暂无报工记录）:', processResult.reason)
      processList.value = []
    }
  } catch (err) {
    console.error('加载工单详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    pageLoading.value = false
  }
}

// ========== 导航 ==========

const goReport = () => {
  const orderQuantity = order.value?.quantity || 0
  const reportedSum = processList.value.reduce(
    (sum, r) => sum + (r.qualifiedQuantity || 0) + (r.badQuantity || 0), 0
  )
  uni.navigateTo({
    url: `/pages/report/index?orderId=${orderId.value}&orderQuantity=${orderQuantity}&reportedSum=${reportedSum}`
  })
}

// ========== 工具函数 ==========

const getStatusName = (status) => store.state.statusMap[status] || '未知'

const getHeaderClass = (status) => {
  const map = {
    0: 'header-pending',
    1: 'header-producing',
    2: 'header-completed',
    3: 'header-closed'
  }
  return map[status] || 'header-default'
}

const getPriorityName = (priority) => store.state.priorityMap[priority] || '-'

const getPriorityClass = (priority) => {
  const map = {
    0: 'priority-low',
    1: 'priority-medium',
    2: 'priority-high',
    3: 'priority-urgent'
  }
  return map[priority] || 'priority-low'
}

const getProcessName = (seq) => store.state.processMap[seq] || `工序${seq}`

const calcPassRate = (report) => {
  const total = (report.qualifiedQuantity || 0) + (report.badQuantity || 0)
  if (total === 0) return '-'
  const rate = ((report.qualifiedQuantity / total) * 100).toFixed(1)
  return `${rate}%`
}

// ========== 生命周期 ==========

onMounted(() => {
  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  orderId.value = currentPage.options.id || currentPage.options.orderId

  if (orderId.value) {
    loadData()
  } else {
    uni.showToast({ title: '工单ID缺失', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1500)
  }
})

// 页面每次显示时刷新数据（从报工页返回时自动更新工序报告列表）
let isFirstShow = true
onShow(() => {
  if (isFirstShow) {
    isFirstShow = false
    return
  }
  if (orderId.value) {
    loadData()
  }
})
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background-color: $bg-page;
  padding-bottom: 140rpx;
}

.page-loading {
  display: flex;
  justify-content: center;
  padding: 120rpx 0;
}

.loading-text {
  font-size: $font-md;
  color: $text-secondary;
}

/* 状态头部 */
.status-header {
  padding: 40rpx 32rpx;
}

.status-header-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-pending {
  background: linear-gradient(135deg, #FFA940 0%, #FF7A45 100%);
}

.header-producing {
  background: linear-gradient(135deg, #40A9FF 0%, #36CFC9 100%);
}

.header-completed {
  background: linear-gradient(135deg, #73D13D 0%, #36CFC9 100%);
}

.header-closed {
  background: linear-gradient(135deg, #8C8C8C 0%, #595959 100%);
}

.status-name {
  font-size: $font-xl;
  font-weight: 600;
  color: #FFFFFF;
}

.order-id-text {
  font-size: $font-md;
  color: rgba(255, 255, 255, 0.85);
}

/* 信息卡片 */
.info-card,
.process-card {
  background-color: $bg-card;
  margin: 20rpx 24rpx;
  border-radius: $radius-lg;
  padding: 28rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: $font-lg;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 24rpx;
  padding-left: 16rpx;
  border-left: 6rpx solid $primary-color;
}

.info-grid {
  display: flex;
  flex-wrap: wrap;
}

.info-item {
  width: 50%;
  padding: 12rpx 0;
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: $font-xs;
  color: $text-secondary;
  margin-bottom: 8rpx;
}

.info-value {
  font-size: $font-md;
  color: $text-primary;
}

.info-value-bold {
  font-weight: 600;
  color: $primary-color;
  font-size: $font-lg;
}

.remarks-section {
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid $border-light;
}

.remarks-text {
  font-size: $font-sm;
  color: $text-regular;
  margin-top: 8rpx;
  line-height: 1.6;
}

/* 优先级标签 */
.priority-tag {
  display: inline-block;
  padding: 2rpx 12rpx;
  border-radius: $radius-sm;
  font-size: $font-xs;
}

.priority-low { background-color: #F5F5F5; color: #8C8C8C; }
.priority-medium { background-color: #E6F7FF; color: #096DD9; }
.priority-high { background-color: #FFF7E6; color: #D48806; }
.priority-urgent { background-color: #FFF1F0; color: #CF1322; }

/* 工序概览 */
.process-overview {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding: 24rpx 0;
  position: relative;
}

.process-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1;
}

.step-circle {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background-color: $bg-grey;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-md;
  color: $text-secondary;
  margin-bottom: 12rpx;
  border: 4rpx solid $border-color;
}

.step-circle-done {
  background-color: $primary-color;
  color: #FFFFFF;
  border-color: $primary-color;
}

.step-name {
  font-size: $font-sm;
  color: $text-primary;
  margin-bottom: 4rpx;
}

.step-status {
  font-size: $font-xs;
  color: $text-secondary;
}

.process-step-done .step-status {
  color: $primary-color;
}

.process-arrow {
  position: absolute;
  top: 44rpx;
  font-size: $font-xl;
  color: $border-color;
  transform: translateX(-50%);
}

/* 工序报告条目 */
.empty-process {
  text-align: center;
  padding: 40rpx 0;
  color: $text-secondary;
  font-size: $font-sm;
}

.process-item {
  border: 1rpx solid $border-light;
  border-radius: $radius-md;
  padding: 20rpx;
  margin-top: 20rpx;
}

.process-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.process-seq-tag {
  background-color: $primary-color;
  color: #FFFFFF;
  padding: 4rpx 16rpx;
  border-radius: $radius-round;
  font-size: $font-xs;
}

.process-time {
  font-size: $font-xs;
  color: $text-secondary;
}

.process-item-body {
  display: flex;
  justify-content: space-around;
}

.process-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-size: $font-xs;
  color: $text-secondary;
  margin-bottom: 8rpx;
}

.stat-value {
  font-size: $font-lg;
  font-weight: 600;
}

.stat-good { color: $success-color; }
.stat-bad { color: $danger-color; }
.stat-rate { color: $primary-color; }

.process-remarks {
  margin-top: 12rpx;
  padding-top: 12rpx;
  border-top: 1rpx solid $border-light;
  font-size: $font-xs;
  color: $text-secondary;
}

/* 底部操作栏 */
.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: $bg-card;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.report-btn {
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

.report-btn::after {
  border: none;
}
</style>
