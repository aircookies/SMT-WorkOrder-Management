<template>
    <!-- 主页 -->
    <div class="container" v-loading="loading">
        <!-- 欢迎信息 -->
        <div class="welcome">
            <el-card shadow="hover" class="welcome-card">
                <div class="welcome-content">
                    <div class="welcome-text">
                        <h2>
                            <el-icon class="welcome-icon">
                                <HomeFilled />
                            </el-icon>
                            欢迎回来，{{ currentUserName }}
                        </h2>
                        <p>{{ currentTime }}</p>
                    </div>
                </div>
            </el-card>
        </div>

        <!-- 数据展示板块(本月工单数，进行中，今日完成，良品率) -->
        <el-row :gutter="20" class="data-board">
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <el-card shadow="hover" class="stat-card workorder-stat">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <el-icon :size="48">
                                <Document />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <p class="stat-title">本月工单数</p>
                            <el-statistic :value="workOrderCount" class="stat-value" />
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <el-card shadow="hover" class="stat-card progress-stat">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <el-icon :size="48">
                                <Loading />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <p class="stat-title">进行中</p>
                            <el-statistic :value="workOrderDoingCount" class="stat-value" />
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <el-card shadow="hover" class="stat-card completed-stat">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <el-icon :size="48">
                                <CircleCheck />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <p class="stat-title">已完成</p>
                            <el-statistic :value="workOrderCompletedCount" class="stat-value" />
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <el-card shadow="hover" class="stat-card quality-stat">
                    <div class="stat-content">
                        <div class="stat-icon">
                            <el-icon :size="48">
                                <TrendCharts />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <p class="stat-title">平均良品率</p>
                            <el-statistic :value="averagePassRate" :precision="2" class="stat-value">
                                <template #suffix>%</template>
                            </el-statistic>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 最近工单列表，不良品趋势(小图表) -->
        <el-row :gutter="20" class="content-row">
            <el-col :xs="24" :sm="24" :md="10" :lg="10" :xl="10">
                <el-card shadow="hover" class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>
                                <el-icon>
                                    <TrendCharts />
                                </el-icon>
                                近30天良品率趋势
                            </span>
                        </div>
                    </template>
                    <BaseChart :options="qualityRateTrendChartOptions" height="383px" />
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="24" :md="14" :lg="14" :xl="14">
                <el-card shadow="hover" class="recent-card">
                    <template #header>
                        <div class="card-header">
                            <span>
                                <el-icon>
                                    <List />
                                </el-icon>
                                最近工单
                            </span>
                            <el-button type="primary" text @click="goToWorkOrder">
                                查看全部
                                <el-icon>
                                    <ArrowRight />
                                </el-icon>
                            </el-button>
                        </div>
                    </template>
                    <el-table :data="recentWorkOrders" style="width: 100%" stripe border class="custom-table">
                        <el-table-column prop="id" label="工单号" min-width="100" align="center">
                            <template #default="{ row }">
                                <el-tag type="primary" effect="plain">{{ row.id }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="productName" label="产品" show-overflow-tooltip align="center" />
                        <el-table-column prop="lineName" label="产线" show-overflow-tooltip align="center" />
                        <el-table-column prop="status" label="状态" min-width="100" align="center">
                            <template #default="{ row }">
                                <el-tag v-if="row.status === 0" type="warning" effect="dark" size="small">待生产</el-tag>
                                <el-tag v-if="row.status === 1" type="primary" effect="dark" size="small">生产中</el-tag>
                                <el-tag v-if="row.status === 2" type="success" effect="dark" size="small">已完成</el-tag>
                                <el-tag v-if="row.status === 3" type="info" effect="dark" size="small">已关闭</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="updateTime" label="更新时间" min-width="160" align="center">
                            <template #default="{ row }">
                                <el-icon>
                                    <Clock />
                                </el-icon>
                                {{ row.updateTime }}
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>

        <!-- 快捷入口卡片(新建工单，报工录入) -->
        <el-row :gutter="20" class="quick-entry">
            <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                <el-card shadow="hover" class="action-card create-workorder" @click="goToCreateWorkOrder">
                    <div class="action-content">
                        <div class="action-icon">
                            <el-icon :size="40">
                                <Plus />
                            </el-icon>
                        </div>
                        <p>新建工单</p>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                <el-card shadow="hover" class="action-card report-entry" @click="goToReport">
                    <div class="action-content">
                        <div class="action-icon">
                            <el-icon :size="40">
                                <EditPen />
                            </el-icon>
                        </div>
                        <p>报工录入</p>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                <el-card shadow="hover" class="action-card data-report" @click="goToDataList">
                    <div class="action-content">
                        <div class="action-icon">
                            <el-icon :size="40">
                                <DataAnalysis />
                            </el-icon>
                        </div>
                        <p>数据报表</p>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                <el-card shadow="hover" class="action-card product-manage" @click="goToProduct">
                    <div class="action-content">
                        <div class="action-icon">
                            <el-icon :size="40">
                                <Box />
                            </el-icon>
                        </div>
                        <p>产品管理</p>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                <el-card shadow="hover" class="action-card line-manage" @click="goToLine">
                    <div class="action-content">
                        <div class="action-icon">
                            <el-icon :size="40">
                                <Van />
                            </el-icon>
                        </div>
                        <p>产线管理</p>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                <el-card shadow="hover" class="action-card user-manage" @click="goToUserManagement">
                    <div class="action-content">
                        <div class="action-icon">
                            <el-icon :size="40">
                                <User />
                            </el-icon>
                        </div>
                        <p>用户管理</p>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import {computed, onBeforeUnmount, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {
  ArrowRight,
  Box,
  CircleCheck,
  Clock,
  DataAnalysis,
  Document,
  EditPen,
  HomeFilled,
  List,
  Loading,
  Plus,
  TrendCharts,
  User,
  Van
} from '@element-plus/icons-vue'
import BaseChart from '@/components/BaseChart.vue'
import {getstatisticsProductionQualityApi, getWorkOrderDetailApi} from '@/api/datalist'
import {getWorkOrderListApi} from '@/api/workorder'
import {passRateLineChart} from '@/utils/chartConfig'
import {getDefaultDateRange} from '@/utils/date'

defineOptions({
    name: 'HomePage'
})

const router = useRouter()

// 控制加载动画
const loading = ref(false)

// 当前时间
const currentTime = ref('')

// 工单详细信息
const workOrderData = ref([])

// 工单列表
const workOrderList = ref([])

// 质量信息
const qualityData = ref([])

// 工单总数
const workOrderCount = computed(() => {
    return workOrderData.value.length
})

// 工单完成数
const workOrderCompletedCount = computed(() => {
    return workOrderData.value.filter(workOrder => workOrder.status === 2).length
})

// 进行中工单数
const workOrderDoingCount = computed(() => {
    return workOrderData.value.filter(workOrder => workOrder.status === 1).length
})

// 定时器引用
let timeInterval = null

// 获取当前时间
const currentDate = (() => {
    return getDefaultDateRange()
    // 为了便于调试 先使用固定日期
    // return ['2025-04-01', '2026-01-01']
})()

// 获取本地存储的用户名
const currentUserName = localStorage.getItem('name') ? localStorage.getItem('name') : '访客'

/**
 * 更新当前时间
 */
const updateTime = () => {
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
    const weekDay = weekDays[now.getDay()]

    currentTime.value = `${year}年${month}月${day}日 ${hours}:${minutes}:${seconds} ${weekDay}`
}

// 获取工单列表
const getWorkOrderList = async (pageNum, pageSize) => {
    const result = await getWorkOrderListApi(pageNum, pageSize)
    if (result.code === 200) {
        workOrderList.value = result.data.list
    }
}

// 获取指定时间内工单详细信息
const getWorkOrderDetail = async (startTime, endTime) => {
    const res = await getWorkOrderDetailApi(startTime, endTime)
    if (res.code === 200) {
        workOrderData.value = res.data
    }
}

// 获取指定时间内的生产质量数据
const getstatisticsProductionQuality = async (startTime, endTime) => {
    const res = await getstatisticsProductionQualityApi(startTime, endTime)
    if (res.code === 200) {
        qualityData.value = res.data
    }
}

/**
 * 计算平均良品率
 */
const averagePassRate = computed(() => {
    if (qualityData.value.length === 0) return 0

    const totalRate = qualityData.value.reduce((sum, item) => sum + (item.passRate || 0), 0)
    return totalRate / qualityData.value.length
})

/**
 * 获取最近的工单列表（按更新时间降序排列，取前7条）
 */
const recentWorkOrders = computed(() => {
    return [...workOrderList.value]
        .sort((a, b) => new Date(b.updateTime) - new Date(a.updateTime))
        .slice(0, 7)
})

// 良品率趋势图表配置
const qualityRateTrendChartOptions = computed(() => {
    const trendData = qualityData.value.map(item => ({
        date: item.date,
        value: item.passRate
    }))
    return passRateLineChart(trendData)
})

/**
 * 跳转到工单管理页面
 */
const goToWorkOrder = () => {
    router.push('/workorder')
}

/**
 * 跳转到新建工单页面
 */
const goToCreateWorkOrder = () => {
    router.push('/workorder')
}

/**
 * 跳转到报工录入页面
 */
const goToReport = () => {
    router.push('/report')
}

/**
 * 跳转到数据报表页面
 */
const goToDataList = () => {
    router.push('/data-list')
}

/**
 * 跳转到产品管理页面
 */
const goToProduct = () => {
    router.push('/product')
}

/**
 * 跳转到产线管理页面
 */
const goToLine = () => {
    router.push('/line')
}

/**
 * 跳转到用户管理页面
 */
const goToUserManagement = () => {
    router.push('/system/user-management')
}

/**
 * 组件挂载时初始化数据
 */
onMounted(async () => {
    loading.value = true

    // 启动时间更新
    updateTime()
    timeInterval = setInterval(updateTime, 1000)

    // 并行获取数据
    await Promise.allSettled([
        getstatisticsProductionQuality(currentDate[0], currentDate[1]),
        getWorkOrderDetail(currentDate[0], currentDate[1]),
        getWorkOrderList(1, 6)
    ])

    loading.value = false
})

/**
 * 组件卸载前清理定时器
 */
onBeforeUnmount(() => {
    if (timeInterval) {
        clearInterval(timeInterval)
    }
})
</script>

<style scoped>
.container {
    padding: 20px;
    width: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
    min-height: calc(100vh - 60px);
}

/* 欢迎信息样式 */
.welcome {
    margin-bottom: 24px;
}

.welcome-card {
    background: linear-gradient(-90deg, #d9b3e2 0.000%, #d2a4e3 8.333%, #ca94e2 16.667%, #c183e1 25.000%, #b673de 33.333%, #aa63da 41.667%, #9d54d5 50.000%, #9047ce 58.333%, #833cc7 66.667%, #7533bf 75.000%, #692eb7 83.333%, #5d2bae 91.667%, #522ca4 100.000%);
    color: white;
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
}

.welcome-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 5px;
}

.welcome-text h2 {
    margin: 0 0 10px 0;
    font-size: 28px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 12px;
}

.welcome-icon {
    font-size: 32px;
}

.welcome-text p {
    margin: 0;
    font-size: 14px;
    opacity: 0.9;
}

/* 统计卡片样式 */
.data-board {
    margin-bottom: 24px;
}

.stat-card {
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
    cursor: pointer;
    overflow: hidden;
}

.stat-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.stat-content {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 10px 0;
}

.stat-icon {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    flex-shrink: 0;
}

.workorder-stat .stat-icon {
    background: #9966ea;
}

.progress-stat .stat-icon {
    background: #ffae78;
}

.completed-stat .stat-icon {
    background: #4facfe;
}

.quality-stat .stat-icon {
    background: #43e97b;
}

.stat-info {
    flex: 1;
}

.stat-title {
    margin: 0 0 8px 0;
    font-size: 14px;
    color: #909399;
    font-weight: 500;
}

.stat-value {
    font-size: 32px;
    font-weight: 700;
    color: #303133;
}

.stat-value :deep(.el-statistic__content) {
    font-size: 32px;
    font-weight: 700;
}

/* 内容区域 */
.content-row {
    margin-bottom: 24px;
}

.recent-card,
.chart-card {
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
}

.recent-card:hover,
.chart-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 16px;
    color: #303133;
}

.card-header .el-icon {
    font-size: 20px;
    color: #667eea;
    margin-right: 8px;
}

/* 表格样式 */
.custom-table {
    border-radius: 4px;
    overflow: hidden;
}

.custom-table :deep(.el-table__header th) {
    background: #8b7ed4;
    color: #ffffff;
    font-weight: 600;
    font-size: 14px;
    padding: 16px 0;
}

.custom-table :deep(.el-table__row) {
    transition: all 0.2s ease;
}

.custom-table :deep(.el-table__row:hover) {
    background-color: #f5f7fa !important;
    transform: scale(1.005);
}

.custom-table :deep(.el-table__cell) {
    padding: 14px 0;
}

/* 快捷入口样式 */
.quick-entry {
    margin-bottom: 20px;
}

.action-card {
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 4px;
    border: none;
    overflow: hidden;
}

.action-card:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.action-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 12px;
    height: 120px;
    padding: 20px;
}

.action-icon {
    width: 70px;
    height: 70px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    transition: all 0.3s ease;
}

.action-card:hover .action-icon {
    transform: scale(1.1);
}

.create-workorder .action-icon {
    background: #667eea;
}

.report-entry .action-icon {
    background: #11998e;
}

.data-report .action-icon {
    background: #fa709a;
}

.product-manage .action-icon {
    background: #b26eb9;
}

.line-manage .action-icon {
    background: #4facfe;
}

.user-manage .action-icon {
    background: #764ba2;
}

.action-content p {
    margin: 0;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .container {
        padding: 12px;
    }

    .welcome-content {
        flex-direction: column;
        text-align: center;
        gap: 16px;
        padding: 20px;
    }

    .welcome-text h2 {
        font-size: 22px;
        justify-content: center;
    }

    .stat-icon {
        width: 60px;
        height: 60px;
    }

    .stat-icon .el-icon {
        font-size: 32px !important;
    }

    .stat-value {
        font-size: 24px;
    }

    .stat-value :deep(.el-statistic__content) {
        font-size: 24px;
    }

    .action-icon {
        width: 60px;
        height: 60px;
    }

    .action-icon .el-icon {
        font-size: 32px !important;
    }
}

/* 动画效果 */
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.welcome {
    animation: fadeInUp 0.6s ease-out;
}

.data-board {
    animation: fadeInUp 0.6s ease-out 0.1s both;
}

.content-row {
    animation: fadeInUp 0.6s ease-out 0.2s both;
}

.quick-entry {
    animation: fadeInUp 0.6s ease-out 0.3s both;
}
</style>
