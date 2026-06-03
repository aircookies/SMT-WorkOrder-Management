<template>
    <!-- 主页 -->
    <div class="container" v-loading="loading">
        <!-- 欢迎信息 -->
        <div class="welcome">
            <el-card shadow="hover" class="welcome-card">
                <div class="welcome-content">
                    <div class="welcome-text">
                        <h2>欢迎使用 SMT 工单管理系统</h2>
                        <p>{{ currentTime }}</p>
                    </div>
                    <div class="welcome-icon">
                        <el-icon :size="60" color="#409EFF">
                            <House />
                        </el-icon>
                    </div>
                </div>
            </el-card>
        </div>

        <!-- 数据展示板块(本月工单数，进行中，今日完成，良品率) -->
        <div class="data-board">
            <el-row :gutter="20">
                <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                    <el-card shadow="hover" class="stat-card">
                        <div class="stat-content">
                            <div class="stat-icon workorder-icon">
                                <el-icon :size="40">
                                    <Document />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <p class="stat-title">本月工单数</p>
                                <el-statistic :value="workOrderCount" />
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                    <el-card shadow="hover" class="stat-card">
                        <div class="stat-content">
                            <div class="stat-icon progress-icon">
                                <el-icon :size="40">
                                    <Loading />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <p class="stat-title">进行中</p>
                                <el-statistic :value="workOrderDoingCount" />
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                    <el-card shadow="hover" class="stat-card">
                        <div class="stat-content">
                            <div class="stat-icon completed-icon">
                                <el-icon :size="40">
                                    <CircleCheck />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <p class="stat-title">完成</p>
                                <el-statistic :value="workOrderCompletedCount" />
                            </div>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                    <el-card shadow="hover" class="stat-card">
                        <div class="stat-content">
                            <div class="stat-icon quality-icon">
                                <el-icon :size="40">
                                    <TrendCharts />
                                </el-icon>
                            </div>
                            <div class="stat-info">
                                <p class="stat-title">平均良品率</p>
                                <el-statistic :value="averagePassRate" :precision="2">
                                    <template #suffix>%</template>
                                </el-statistic>
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!-- 最近工单列表，不良品趋势(小图表) -->
        <div class="recent-workorder-list">
            <el-row :gutter="20">
                <el-col :xs="24" :sm="24" :md="10" :lg="10" :xl="10">
                    <el-card shadow="hover" class="chart-card">
                        <template #header>
                            <div class="card-header">
                                <span>近30天良品率趋势</span>
                            </div>
                        </template>
                        <!--  良品率趋势折线图 -->
                        <BaseChart :options="qualityRateTrendChartOptions" />
                    </el-card>
                </el-col>
                <el-col :xs="24" :sm="24" :md="14" :lg="14" :xl="14">
                    <el-card shadow="hover" class="recent-card">
                        <template #header>
                            <div class="card-header">
                                <span>最近工单</span>
                                <el-button type="primary" text @click="goToWorkOrder">查看全部</el-button>
                            </div>
                        </template>
                        <el-table :data="recentWorkOrders" style="width: 100%" size="large" stripe>
                            <el-table-column prop="id" label="工单号" width="80" />
                            <el-table-column prop="productName" label="产品" show-overflow-tooltip />
                            <el-table-column prop="lineName" label="产线" show-overflow-tooltip />
                            <el-table-column prop="status" label="状态">
                                <template #default="{ row }">
                                    <el-tag v-if="row.status === 0" type="warning" size="small">待生产</el-tag>
                                    <el-tag v-if="row.status === 1" type="primary" size="small">生产中</el-tag>
                                    <el-tag v-if="row.status === 2" type="success" size="small">已完成</el-tag>
                                    <el-tag v-if="row.status === 3" type="info" size="small">已关闭</el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="updateTime" label="更新时间" />
                        </el-table>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!-- 快捷入口卡片(新建工单，报工录入) -->
        <div class="quick-entry">
            <el-row :gutter="20">
                <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                    <el-card shadow="hover" class="action-card" @click="goToCreateWorkOrder">
                        <div class="action-content">
                            <el-icon :size="40" color="#409EFF">
                                <Plus />
                            </el-icon>
                            <p>新建工单</p>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                    <el-card shadow="hover" class="action-card" @click="goToReport">
                        <div class="action-content">
                            <el-icon :size="40" color="#67C23A">
                                <EditPen />
                            </el-icon>
                            <p>报工录入</p>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                    <el-card shadow="hover" class="action-card" @click="goToDataList">
                        <div class="action-content">
                            <el-icon :size="40" color="#E6A23C">
                                <DataAnalysis />
                            </el-icon>
                            <p>数据报表</p>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                    <el-card shadow="hover" class="action-card" @click="goToProduct">
                        <div class="action-content">
                            <el-icon :size="40" color="#F56C6C">
                                <Box />
                            </el-icon>
                            <p>产品管理</p>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                    <el-card shadow="hover" class="action-card" @click="goToLine">
                        <div class="action-content">
                            <el-icon :size="40" color="#909399">
                                <Van />
                            </el-icon>
                            <p>产线管理</p>
                        </div>
                    </el-card>
                </el-col>
                <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4">
                    <el-card shadow="hover" class="action-card" @click="goToUserManagement">
                        <div class="action-content">
                            <el-icon :size="40" color="#409EFF">
                                <User />
                            </el-icon>
                            <p>用户管理</p>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
    House, Document, Loading, CircleCheck, TrendCharts,
    Plus, EditPen, DataAnalysis, Box, Van, User
} from '@element-plus/icons-vue'
import BaseChart from '@/components/BaseChart.vue'
import { getstatisticsProductionQualityApi, getWorkOrderDetailApi } from '@/api/datalist'
import { getWorkOrderListApi } from '@/api/workorder'
import { getDefaultDateRange } from '@/utils/date'
import { pieChart, passRateLineChart, lineProductionBarChart, productTop5BarChart } from '@/utils/chartConfig'


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
    // return getDefaultDateRange()
    // 为了便于调试 先使用固定日期
    return ['2025-04-01', '2026-01-01']
})()

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
    try {
        const result = await getWorkOrderListApi(pageNum, pageSize)
        if (result.code === 200) {
            workOrderList.value = result.data.list
        }
    } catch (error) {
        console.log('获取工单列表失败', error)
    }
}

// 获取指定时间内工单详细信息
const getWorkOrderDetail = async (startTime, endTime) => {
    try {
        const res = await getWorkOrderDetailApi(startTime, endTime)
        if (res.code === 200) {
            workOrderData.value = res.data
        }
    } catch (error) {
        console.error('获取工单详细信息失败:', error)
    }
}

// 获取指定时间内的生产质量数据
const getstatisticsProductionQuality = async (startTime, endTime) => {
    try {
        const res = await getstatisticsProductionQualityApi(startTime, endTime)
        if (res.code === 200) {
            qualityData.value = res.data
        }
    } catch (error) {
        console.error('获取质量数据失败:', error)
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

    try {
        // 并行获取数据
        await Promise.all([
            getstatisticsProductionQuality(currentDate[0], currentDate[1]),
            getWorkOrderDetail(currentDate[0], currentDate[1]),
            getWorkOrderList(1, 7)
        ])
    } catch (error) {
        console.error('初始化数据失败:', error)
        ElMessage.error('初始化数据失败')
    } finally {
        loading.value = false
    }
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
    background-color: #f5f7fa;
    min-height: calc(100vh - 60px);
}

/* 欢迎信息样式 */
.welcome {
    margin-bottom: 20px;
}

.welcome-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.welcome-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
}

.welcome-text h2 {
    margin: 0 0 10px 0;
    font-size: 24px;
    font-weight: 600;
}

.welcome-text p {
    margin: 0;
    font-size: 14px;
    opacity: 0.9;
}

.welcome-icon {
    opacity: 0.8;
}

/* 统计卡片样式 */
.data-board {
    margin-bottom: 20px;
}

.stat-card {
    height: 120px;
    transition: all 0.3s;
}

.stat-content {
    display: flex;
    align-items: center;
    gap: 20px;
}

.stat-icon {
    width: 70px;
    height: 70px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
}

.workorder-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.progress-icon {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.completed-icon {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.quality-icon {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
    flex: 1;
}

.stat-title {
    margin: 0 0 10px 0;
    font-size: 14px;
    color: #909399;
}

/* 最近工单和图表样式 */
.recent-workorder-list {
    margin-bottom: 20px;
}

.recent-card,
.chart-card {
    height: 100%;
    width: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
}

/* 快捷入口样式 */
.quick-entry {
    margin-bottom: 20px;
}

.action-card {
    cursor: pointer;
    transition: all 0.3s;
    height: 120px;
}

.action-card:hover {
    transform: translateY(-5px);
}

.action-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10px;
    height: 100%;
}

.action-content p {
    margin: 0;
    font-size: 14px;
    font-weight: 500;
    color: #606266;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .welcome-content {
        flex-direction: column;
        text-align: center;
        gap: 20px;
    }

    .stat-card {
        margin-bottom: 10px;
    }
}
</style>
