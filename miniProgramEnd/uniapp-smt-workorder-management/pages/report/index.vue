<template>
  <view class="report-page">
    <view class="report-form">
      <!-- 关联工单 -->
      <view class="form-section">
        <text class="section-title">关联工单</text>
        <view class="form-item">
          <text class="form-label">工单编号</text>
          <text class="form-value">工单 #{{ orderId }}</text>
        </view>
        <view class="form-item">
          <text class="form-label">计划数量</text>
          <text class="form-value">{{ orderQuantity }}</text>
        </view>
      </view>

      <!-- 工序信息 -->
      <view class="form-section">
        <text class="section-title">工序信息</text>

        <view class="form-item">
          <text class="form-label">工序</text>
          <picker :range="processOptions" range-key="label" :value="processIndex" @change="onProcessChange">
            <view class="picker-display" :class="{ 'picker-placeholder': !form.processSeq }">
              {{ form.processSeq ? processOptions[processIndex].label : '请选择工序' }}
              <text class="picker-arrow">&#x25BC;</text>
            </view>
          </picker>
        </view>

        <view class="form-item">
          <text class="form-label">合格数量</text>
          <input
            class="form-input"
            type="number"
            v-model="form.qualifiedQuantity"
            placeholder="请输入合格数量"
            placeholder-class="placeholder"
          />
        </view>

        <view class="form-item">
          <text class="form-label">不良数量</text>
          <input
            class="form-input"
            type="number"
            v-model="form.badQuantity"
            placeholder="请输入不良数量"
            placeholder-class="placeholder"
          />
        </view>
      </view>

      <!-- 时间信息 -->
      <view class="form-section">
        <text class="section-title">时间信息</text>

        <view class="form-item">
          <text class="form-label">开始时间</text>
          <picker mode="date" :value="form.startDate" @change="onStartDateChange">
            <view class="picker-display" :class="{ 'picker-placeholder': !form.startDate }">
              {{ form.startDate || '选择开始日期' }}
              <text class="picker-arrow">&#x25BC;</text>
            </view>
          </picker>
        </view>

        <view class="form-item">
          <text class="form-label">结束时间</text>
          <picker mode="date" :value="form.endDate" @change="onEndDateChange">
            <view class="picker-display" :class="{ 'picker-placeholder': !form.endDate }">
              {{ form.endDate || '选择结束日期' }}
              <text class="picker-arrow">&#x25BC;</text>
            </view>
          </picker>
        </view>
      </view>

      <!-- 备注 -->
      <view class="form-section">
        <text class="section-title">备注</text>
        <textarea
          class="form-textarea"
          v-model="form.remarks"
          placeholder="选填，记录工序过程中的异常情况"
          placeholder-class="placeholder"
          :maxlength="200"
          auto-height
        />
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="bottom-action">
      <button
        class="submit-btn"
        :class="{ 'submit-btn-disabled': !canSubmit }"
        :disabled="!canSubmit || submitting"
        @click="handleSubmit"
      >
        {{ submitting ? '提交中...' : '提交报工' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { addProcess } from '../../api/process'
import store from '../../store/index'

const orderId = ref('')
const orderQuantity = ref(0)
const reportedSum = ref(0)
const submitting = ref(false)

// 工序选项
const processOptions = [
  { label: '印刷', value: 1 },
  { label: '贴片', value: 2 },
  { label: '回流焊', value: 3 }
]

// 表单数据
const form = ref({
  processSeq: null,
  qualifiedQuantity: '',
  badQuantity: '',
  startDate: '',
  endDate: '',
  remarks: ''
})

const processIndex = computed(() => {
  if (!form.value.processSeq) return 0
  return processOptions.findIndex(o => o.value === form.value.processSeq)
})

// 表单校验
const canSubmit = computed(() => {
  return (
    form.value.processSeq !== null &&
    form.value.qualifiedQuantity !== '' &&
    form.value.badQuantity !== ''
  )
})

// 输入净化：去除小数点、负号和非数字字符，只保留非负整数
const sanitizeQuantity = (val) => {
  if (val === '' || val == null) return ''
  const cleaned = String(val).replace(/[^0-9]/g, '')
  // 去掉前导零（但保留单独的 "0"）
  return cleaned.replace(/^0+(?=\d)/, '')
}

watch(() => form.value.qualifiedQuantity, (val) => {
  const sanitized = sanitizeQuantity(val)
  if (sanitized !== val) form.value.qualifiedQuantity = sanitized
})

watch(() => form.value.badQuantity, (val) => {
  const sanitized = sanitizeQuantity(val)
  if (sanitized !== val) form.value.badQuantity = sanitized
})

// ========== 事件处理 ==========

const onProcessChange = (e) => {
  const index = e.detail.value
  form.value.processSeq = processOptions[index].value
}

const onStartDateChange = (e) => {
  form.value.startDate = e.detail.value
}

const onEndDateChange = (e) => {
  form.value.endDate = e.detail.value
}

const handleSubmit = async () => {
  if (!canSubmit.value || submitting.value) return

  // 数量校验：先做最终净化（安全兜底），再校验
  const qualified = Number(sanitizeQuantity(form.value.qualifiedQuantity))
  const bad = Number(sanitizeQuantity(form.value.badQuantity))

  if (qualified + bad === 0) {
    uni.showToast({ title: '合格数量和不良数量不能同时为0', icon: 'none' })
    return
  }

  if (qualified + bad > orderQuantity.value) {
    uni.showToast({ title: `报工数量不能超过工单计划数量 ${orderQuantity.value}`, icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const reportData = {
      orderId: Number(orderId.value),
      processSeq: form.value.processSeq,
      qualifiedQuantity: qualified,
      badQuantity: bad,
      operatorId: store.state.user?.userId,
      remarks: form.value.remarks
    }

    // 拼接时间字符串，使用 ISO-8601 格式（T 分隔日期和时间）以匹配后端 LocalDateTime
    if (form.value.startDate) {
      reportData.startTime = `${form.value.startDate}T00:00:00`
    }
    if (form.value.endDate) {
      reportData.finishTime = `${form.value.endDate}T00:00:00`
    }

    await addProcess(reportData)

    uni.showToast({ title: '报工成功', icon: 'success' })

    setTimeout(() => {
      uni.navigateBack()
    }, 1500)

  } catch (err) {
    console.error('报工失败:', err)
  } finally {
    submitting.value = false
  }
}

// ========== 生命周期 ==========

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options

  orderId.value = options.orderId
  orderQuantity.value = Number(options.orderQuantity) || 0
  reportedSum.value = Number(options.reportedSum) || 0

  if (!orderId.value) {
    uni.showToast({ title: '工单ID缺失', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1500)
  }
})
</script>

<style lang="scss" scoped>
.report-page {
  min-height: 100vh;
  background-color: $bg-page;
  padding-bottom: 140rpx;
}

.report-form {
  padding: 20rpx 24rpx;
}

/* 表单分区 */
.form-section {
  background-color: $bg-card;
  border-radius: $radius-lg;
  padding: 28rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: $font-lg;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 24rpx;
  padding-left: 16rpx;
  border-left: 6rpx solid $primary-color;
  display: block;
}

/* 表单项 */
.form-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid $border-light;

  &:last-child {
    border-bottom: none;
  }
}

.form-label {
  font-size: $font-md;
  color: $text-regular;
  width: 160rpx;
  flex-shrink: 0;
}

.form-value {
  font-size: $font-md;
  color: $text-primary;
  font-weight: 500;
}

.form-value-warning {
  color: $danger-color;
}

.form-input {
  flex: 1;
  font-size: $font-md;
  color: $text-primary;
  text-align: right;
}

/* Picker 样式 */
.picker-display {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: $font-md;
  color: $text-primary;
}

.picker-placeholder {
  color: $text-placeholder;
}

.picker-arrow {
  font-size: $font-xs;
  color: $text-secondary;
  margin-left: 8rpx;
}

/* 文本域 */
.form-textarea {
  width: 100%;
  min-height: 120rpx;
  font-size: $font-md;
  color: $text-primary;
  line-height: 1.6;
  padding: 16rpx 0;
}

.placeholder {
  color: $text-placeholder;
  font-size: $font-md;
}

/* 底部提交按钮 */
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

.submit-btn {
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

.submit-btn-disabled {
  opacity: 0.5;
}

.submit-btn::after {
  border: none;
}
</style>
