<template>
    <div class="container" v-loading="loading">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">
                <el-icon class="title-icon">
                    <DataAnalysis />
                </el-icon>
                数据报表
            </h1>
            <p class="page-subtitle">生产数据统计与可视化分析</p>
        </div>

        <!-- 查询工具栏 -->
        <el-card class="toolbar-card" shadow="hover">
            <el-form :model="form" class="search-form">
                <el-row :gutter="20" justify="space-between" align="middle">
                    <el-col :span="8">
                        <el-form-item label="统计周期">
                            <el-date-picker v-model="form.dateRange" type="daterange" range-separator="至"
                                start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
                                format="YYYY-MM-DD" style="width: 100%" prefix-icon="Calendar" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="16">
                        <el-form-item class="action-buttons">
                            <el-button type="primary" @click="onQuery" :icon="Search" size="large">查询</el-button>
                            <el-button @click="onReset" :icon="Refresh" size="large">重置</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </el-card>

        <!-- 关键指标卡片 -->
        <el-row :gutter="20" class="metrics-row">
            <el-col :xs="24" :sm="12" :md="6">
                <el-card class="metric-card workorder-total" shadow="hover">
                    <div class="metric-content">
                        <div class="metric-icon">
                            <el-icon :size="48">
                                <Tickets />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <p class="metric-label">工单总数</p>
                            <el-statistic :value="workOrderCount" class="metric-value" />
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
                <el-card class="metric-card workorder-completed" shadow="hover">
                    <div class="metric-content">
                        <div class="metric-icon">
                            <el-icon :size="48">
                                <CircleCheck />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <p class="metric-label">完成工单数</p>
                            <el-statistic :value="workOrderCompletedCount" class="metric-value" />
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
                <el-card class="metric-card completion-rate" shadow="hover">
                    <div class="metric-content">
                        <div class="metric-icon">
                            <el-icon :size="48">
                                <TrendCharts />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <p class="metric-label">完成率</p>
                            <el-statistic :value="workOrderCompletedRate" :precision="2" class="metric-value">
                                <template #suffix>%</template>
                            </el-statistic>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
                <el-card class="metric-card defect-count" shadow="hover">
                    <div class="metric-content">
                        <div class="metric-icon">
                            <el-icon :size="48">
                                <Warning />
                            </el-icon>
                        </div>
                        <div class="metric-info">
                            <p class="metric-label">不良品数</p>
                            <el-statistic :value="badCount" class="metric-value" />
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 图表区域 -->
        <el-row :gutter="20" class="charts-row">
            <el-col :xs="24" :md="12">
                <el-card class="chart-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon>
                                <DataLine />
                            </el-icon>
                            <span>良品率趋势</span>
                        </div>
                    </template>
                    <BaseChart :options="qualityRateTrendChartOptions" height="350px" />
                </el-card>
            </el-col>
            <el-col :xs="24" :md="12">
                <el-card class="chart-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon>
                                <PieChart />
                            </el-icon>
                            <span>工单状态分布</span>
                        </div>
                    </template>
                    <BaseChart ref="pipChartRef" :options="workOrderStatusChartOptions" height="350px" />
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row">
            <el-col :xs="24" :md="12">
                <el-card class="chart-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon>
                                <Histogram />
                            </el-icon>
                            <span>产线产量对比</span>
                        </div>
                    </template>
                    <BaseChart :options="lineProductionChartOptions" height="350px" />
                </el-card>
            </el-col>
            <el-col :xs="24" :md="12">
                <el-card class="chart-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon>
                                <Trophy />
                            </el-icon>
                            <span>产品产量 TOP5</span>
                        </div>
                    </template>
                    <BaseChart :options="productTop5ChartOptions" height="350px" />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getWorkOrderDetailApi, getstatisticsProductionQualityApi, getStatisticsLineProductionApi, getStatisticsProductProductionApi } from '@/api/datalist'
import { DataAnalysis, Search, Refresh, Tickets, CircleCheck, TrendCharts, Warning, PieChart, DataLine, Histogram, Trophy } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import BaseChart from '@/components/BaseChart.vue'
import { pieChart, passRateLineChart, lineProductionBarChart, productTop5BarChart } from '@/utils/chartConfig'

defineOptions({
    name: 'DataList'
})

// 表单数据
const form = ref({
    // 默认使用本月的数据
    dateRange: (() => {
        // return getDefaultDateRange()
        // 为了便于调试 先使用固定日期
        return ['2025-04-01', '2026-01-01']
    })(),
    lineId: '',
    productId: ''
})

// 工单信息
const workOrderList = ref([])

// 质量信息
const qualityData = ref([])

// 工单总数
const workOrderCount = computed(() => {
    return workOrderList.value.length
})

// 工单完成数
const workOrderCompletedCount = computed(() => {
    return workOrderList.value.filter(workOrder => workOrder.status === 2).length
})

// 工单完成率
const workOrderCompletedRate = computed(() => {
    if (workOrderCount.value === 0) return 0

    return workOrderCompletedCount.value / workOrderCount.value * 100
})

// 产品不良数
const badCount = computed(() => {
    if (qualityData.value.length === 0) return 0

    let count = 0
    qualityData.value.forEach(quality => {
        count += quality.badQuantity
    })
    return count
})

// 各状态工单数量
const workOrderCountByStatus = computed(() => {
    // 待生产工单数
    let waitCount = workOrderList.value.filter(workOrder => workOrder.status === 0).length
    // 进行中工单数
    let doingCount = workOrderList.value.filter(workOrder => workOrder.status === 1).length
    // 已完成工单数
    let doneCount = workOrderList.value.filter(workOrder => workOrder.status === 2).length
    // 已关闭工单数
    let closedCount = workOrderList.value.filter(workOrder => workOrder.status === 3).length
    // 封装结果并返回
    return [
        {
            name: '待生产',
            value: waitCount
        },
        {
            name: '进行中',
            value: doingCount
        },
        {
            name: '已完成',
            value: doneCount
        },
        {
            name: '已关闭',
            value: closedCount
        }
    ]
})

// 各产线产量统计数据
const lineProductionQualityData = ref([]);

// 各产品产量统计数据
const productProductionList = ref([]);

// 控制加载动画
const loading = ref(false);

// 获取指定时间内工单详细信息
const getWorkOrderDetail = async (startTime, endTime) => {
    const res = await getWorkOrderDetailApi(startTime, endTime)
    if (res.code === 200) {
        workOrderList.value = res.data
    }
}

// 获取指定时间内的生产质量数据
const getstatisticsProductionQuality = async (startTime, endTime) => {
    const res = await getstatisticsProductionQualityApi(startTime, endTime)
    if (res.code === 200) {
        qualityData.value = res.data
    }
}

// 获取指定时间内的产线产量统计数据
const getstatisticsLineProduction = async (startTime, endTime) => {
    const res = await getStatisticsLineProductionApi(startTime, endTime)
    if (res.code === 200) {
        lineProductionQualityData.value = res.data
    }
}

// 获取指定时间内产品产量统计数据
const getstatisticsProductProduction = async (startTime, endTime) => {
    const res = await getStatisticsProductProductionApi(startTime, endTime)
    if (res.code === 200) {
        productProductionList.value = res.data
    }
}

// 工单状态图表配置
const workOrderStatusChartOptions = computed(() => {
    return pieChart('工单状态分布', workOrderCountByStatus.value)
})

// 良品率趋势图表配置
const qualityRateTrendChartOptions = computed(() => {
    const trendData = qualityData.value.map(item => ({
        date: item.date,
        value: item.passRate
    }))
    return passRateLineChart(trendData)
})

// 产线产量对比图表配置
const lineProductionChartOptions = computed(() => {
    return lineProductionBarChart(lineProductionQualityData.value)
})

// 产品产量TOP5图表配置
const productTop5ChartOptions = computed(() => {
    return productTop5BarChart(productProductionList.value)
})

// 查询按钮点击事件
const onQuery = async () => {
    if (form.value.dateRange.length === 0) {
        ElMessage.error('请选择时间范围')
        return
    }
    // 并行发送异步请求
    await Promise.all([
        getWorkOrderDetail(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsProductionQuality(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsLineProduction(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsProductProduction(form.value.dateRange[0], form.value.dateRange[1])
    ])

}

// 重置按钮点击事件
const onReset = () => {
    form.value = {
        dateRange: [],
        lineId: '',
        productId: ''
    }
}

// 初始化数据
onMounted(async () => {
    loading.value = true
    // 并行发送异步请求
    await Promise.all([
        getWorkOrderDetail(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsProductionQuality(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsLineProduction(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsProductProduction(form.value.dateRange[0], form.value.dateRange[1])
    ])
    loading.value = false
})
</script>

<style scoped>
.container {
    padding: 20px;
    width: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
    min-height: 100vh;
}

/* 页面标题区域 */
.page-header {
    margin-bottom: 24px;
    padding: 20px 24px;
    background: linear-gradient(-90deg, #989ecb 0.000%, #8ca4c6 16.667%, #92a9be 33.333%, #a7acb4 50.000%, #c4afa8 66.667%, #dfaf9d 83.333%, #eeae93 100.000%);
    border-radius: 4px;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.page-title {
    color: #ffffff;
    font-size: 28px;
    font-weight: 600;
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    gap: 12px;
}

.title-icon {
    font-size: 32px;
}

.page-subtitle {
    color: rgba(255, 255, 255, 0.9);
    font-size: 14px;
    margin: 0;
}

/* 工具栏卡片 */
.toolbar-card {
    margin-bottom: 20px;
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
}

.toolbar-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.search-form {
    padding: 10px 0;
}

.search-form :deep(.el-form-item) {
    margin-bottom: 0;
}

.search-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
}

.action-buttons {
    text-align: right;
}

/* 指标卡片行 */
.metrics-row {
    margin-bottom: 20px;
}

/* 指标卡片 */
.metric-card {
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
    cursor: pointer;
    overflow: hidden;
}

.metric-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
}

.metric-content {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 10px 0;
}

.metric-icon {
    width: 80px;
    height: 80px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ffffff;
    flex-shrink: 0;
}

.workorder-total .metric-icon {
    background: #764ba2;
}

.workorder-completed .metric-icon {
    background: #11998e;
}

.completion-rate .metric-icon {
    background: #4facfe;
}

.defect-count .metric-icon {
    background: #fa709a;
}

.metric-info {
    flex: 1;
}

.metric-label {
    font-size: 14px;
    color: #909399;
    margin: 0 0 8px 0;
    font-weight: 500;
}

.metric-value {
    font-size: 32px;
    font-weight: 700;
    color: #303133;
}

.metric-value :deep(.el-statistic__content) {
    font-size: 32px;
    font-weight: 700;
    color: #303133;
}

/* 图表行 */
.charts-row {
    margin-bottom: 20px;
}

/* 图表卡片 */
.chart-card {
    margin-bottom: 20px;
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
}

.chart-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    font-size: 16px;
    color: #303133;
}

.card-header .el-icon {
    font-size: 20px;
    color: #667eea;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .container {
        padding: 12px;
    }

    .page-header {
        padding: 16px;
    }

    .page-title {
        font-size: 22px;
    }

    .metric-icon {
        width: 60px;
        height: 60px;
    }

    .metric-icon .el-icon {
        font-size: 32px !important;
    }

    .metric-value {
        font-size: 24px;
    }

    .metric-value :deep(.el-statistic__content) {
        font-size: 24px;
    }

    .action-buttons {
        text-align: left;
        margin-top: 10px;
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

.page-header {
    animation: fadeInUp 0.6s ease-out;
}

.toolbar-card {
    animation: fadeInUp 0.6s ease-out 0.1s both;
}

.metric-card {
    animation: fadeInUp 0.6s ease-out 0.2s both;
}

.chart-card {
    animation: fadeInUp 0.6s ease-out 0.3s both;
}
</style>