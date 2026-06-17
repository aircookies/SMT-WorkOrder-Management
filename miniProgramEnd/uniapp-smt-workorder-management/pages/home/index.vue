<template>
  <view class="home-page">
    <!-- 顶部状态筛选 Tab -->
    <view class="filter-bar">
      <scroll-view scroll-x class="filter-scroll">
        <view class="filter-tabs">
          <view
            v-for="tab in tabs"
            :key="tab.value"
            class="filter-tab"
            :class="{ 'filter-tab-active': currentTab === tab.value }"
            @click="switchTab(tab.value)"
          >
            <text>{{ tab.label }}</text>
            <view v-if="currentTab === tab.value" class="filter-tab-indicator"></view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 工单列表 -->
    <scroll-view
      scroll-y
      class="order-list"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <!-- 空状态 -->
      <view v-if="!loading && orderList.length === 0" class="empty-state">
        <text class="empty-icon">&#x1F4CB;</text>
        <text class="empty-text">暂无工单数据</text>
      </view>

      <!-- 工单卡片列表 -->
      <view
        v-for="order in orderList"
        :key="order.id"
        class="order-card"
        @click="goDetail(order.id)"
      >
        <view class="order-card-header">
          <text class="order-id">工单 #{{ order.id }}</text>
          <view class="status-tag" :class="getStatusClass(order.status)">
            <text>{{ getStatusName(order.status) }}</text>
          </view>
        </view>

        <view class="order-card-body">
          <view class="order-info-row">
            <text class="info-label">产品</text>
            <text class="info-value">{{ order.productName || '-' }}</text>
          </view>
          <view class="order-info-row">
            <text class="info-label">产线</text>
            <text class="info-value">{{ order.lineName || '-' }}</text>
          </view>
          <view class="order-info-row">
            <text class="info-label">数量</text>
            <text class="info-value info-value-highlight">{{ order.quantity }}</text>
          </view>
          <view class="order-info-row">
            <text class="info-label">计划时间</text>
            <text class="info-value">{{ order.planningTime || '-' }}</text>
          </view>
        </view>

        <view class="order-card-footer">
          <view class="priority-tag" :class="getPriorityClass(order.priority)">
            <text>{{ getPriorityName(order.priority) }}</text>
          </view>
          <text class="order-time">{{ order.createTime }}</text>
        </view>
      </view>

      <!-- 加载状态 -->
      <view v-if="loading" class="loading-state">
        <text class="loading-text">加载中...</text>
      </view>
      <view v-if="!loading && finished && orderList.length > 0" class="loading-state">
        <text class="loading-text">没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { queryWorkOrders } from '../../api/workorder'
import store from '../../store/index'

// 筛选 Tab 配置
const tabs = [
  { label: '全部', value: -1 },
  { label: '待生产', value: 0 },
  { label: '生产中', value: 1 },
  { label: '已完工', value: 2 },
  { label: '已关闭', value: 3 }
]

const currentTab = ref(-1)
const orderList = ref([])
const loading = ref(false)
const refreshing = ref(false)
const finished = ref(false)
const pageNum = ref(1)
const pageSize = 10

// ========== 数据加载 ==========

const fetchOrders = async (isRefresh = false) => {
  if (loading.value) return

  if (isRefresh) {
    pageNum.value = 1
    finished.value = false
  }

  if (finished.value) return

  loading.value = true
  try {
    const conditions = {
      pageNum: pageNum.value,
      pageSize
    }
    // 按状态筛选
    if (currentTab.value !== -1) {
      conditions.status = currentTab.value
    }

    const res = await queryWorkOrders(conditions)
    const pageData = res.data

    if (isRefresh) {
      orderList.value = pageData.list || []
    } else {
      orderList.value = [...orderList.value, ...(pageData.list || [])]
    }

    // 判断是否还有更多数据
    if (orderList.value.length >= pageData.total) {
      finished.value = true
    }
  } catch (err) {
    console.error('获取工单列表失败:', err)
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// ========== 事件处理 ==========

const switchTab = (value) => {
  if (currentTab.value === value) return
  currentTab.value = value
  orderList.value = []
  fetchOrders(true)
}

const onRefresh = () => {
  refreshing.value = true
  fetchOrders(true)
}

const onLoadMore = () => {
  if (finished.value || loading.value) return
  pageNum.value++
  fetchOrders()
}

const goDetail = (id) => {
  uni.navigateTo({ url: `/pages/detail/index?id=${id}` })
}

// ========== 工具函数 ==========

const getStatusName = (status) => {
  return store.state.statusMap[status] || '未知'
}

const getStatusClass = (status) => {
  const map = {
    0: 'status-pending',
    1: 'status-producing',
    2: 'status-completed',
    3: 'status-closed'
  }
  return map[status] || 'status-default'
}

const getPriorityName = (priority) => {
  return store.state.priorityMap[priority] || '-'
}

const getPriorityClass = (priority) => {
  const map = {
    0: 'priority-low',
    1: 'priority-medium',
    2: 'priority-high',
    3: 'priority-urgent'
  }
  return map[priority] || 'priority-low'
}

// ========== 生命周期 ==========

onMounted(() => {
  fetchOrders(true)
})
</script>

<style lang="scss" scoped>
.home-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: $bg-page;
}

/* 筛选栏 */
.filter-bar {
  background-color: $bg-card;
  padding: 16rpx 0;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 10;
}

.filter-scroll {
  white-space: nowrap;
}

.filter-tabs {
  display: flex;
  padding: 0 20rpx;
}

.filter-tab {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  padding: 12rpx 28rpx;
  font-size: $font-md;
  color: $text-regular;
  position: relative;
  flex-shrink: 0;
}

.filter-tab-active {
  color: $primary-color;
  font-weight: 600;
}

.filter-tab-indicator {
  width: 40rpx;
  height: 6rpx;
  background-color: $primary-color;
  border-radius: 3rpx;
  margin-top: 8rpx;
}

/* 工单列表 */
.order-list {
  flex: 1;
  padding: 16rpx 24rpx;
}

.order-card {
  background-color: $bg-card;
  border-radius: $radius-lg;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.order-id {
  font-size: $font-lg;
  font-weight: 600;
  color: $text-primary;
}

/* 状态标签 */
.status-tag {
  padding: 4rpx 16rpx;
  border-radius: $radius-round;
  font-size: $font-xs;
}

.status-pending {
  background-color: #FFF7E6;
  color: #D48806;
}

.status-producing {
  background-color: #E6F7FF;
  color: #096DD9;
}

.status-completed {
  background-color: #F6FFED;
  color: #389E0D;
}

.status-closed {
  background-color: #F5F5F5;
  color: #8C8C8C;
}

/* 工单信息 */
.order-card-body {
  padding: 8rpx 0;
}

.order-info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.info-label {
  font-size: $font-sm;
  color: $text-secondary;
  width: 120rpx;
  flex-shrink: 0;
}

.info-value {
  font-size: $font-sm;
  color: $text-primary;
  flex: 1;
}

.info-value-highlight {
  color: $primary-color;
  font-weight: 600;
}

/* 卡片底部 */
.order-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid $border-light;
}

.priority-tag {
  padding: 4rpx 16rpx;
  border-radius: $radius-sm;
  font-size: $font-xs;
}

.priority-low {
  background-color: #F5F5F5;
  color: #8C8C8C;
}

.priority-medium {
  background-color: #E6F7FF;
  color: #096DD9;
}

.priority-high {
  background-color: #FFF7E6;
  color: #D48806;
}

.priority-urgent {
  background-color: #FFF1F0;
  color: #CF1322;
}

.order-time {
  font-size: $font-xs;
  color: $text-secondary;
}

/* 空状态 / 加载状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: $font-md;
  color: $text-secondary;
}

.loading-state {
  text-align: center;
  padding: 32rpx 0;
}

.loading-text {
  font-size: $font-sm;
  color: $text-secondary;
}
</style>
