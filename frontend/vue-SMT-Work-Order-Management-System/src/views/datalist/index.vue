index.vue
<template>
    <div class="container">
        <h1>数据报表</h1>
        <div class="toolbar">
            <el-form :model="form" class="search-form">
                <el-row :gutter="20">
                    <el-col :span="6">
                        <el-form-item label="统计周期">
                            <el-date-picker v-model="form.dateRange" type="daterange" range-separator="至"
                                start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
                                format="YYYY-MM-DD" style="width: 100%" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="操作">
                            <el-button type="primary" @click="onQuery">查询</el-button>
                            <el-button @click="onReset">重置</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!-- 展示数值数据 -->
        <div class="numbercards">
            <el-row :gutter="20">
                <el-col :span="6">
                    <el-card>
                        <span>
                            <el-icon :size="64">
                                <DocumentRemove />
                            </el-icon>
                        </span>
                        <span class="numbercards-text">
                            <p>工单总数</p>
                            <h1><el-statistic :value="workOrderCount" /></h1>
                        </span>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <span>
                            <el-icon :size="64">
                                <DocumentRemove />
                            </el-icon>
                        </span>
                        <span class="numbercards-text">
                            <p>完成工单数</p>
                            <el-statistic :value="workOrderCompletedCount" />
                        </span>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <span>
                            <el-icon :size="64">
                                <DocumentRemove />
                            </el-icon>
                        </span>
                        <span class="numbercards-text">
                            <p>完成率</p>
                            <el-statistic :value="workOrderCompletedRate" :precision="2">
                                <template #suffix>%</template>
                            </el-statistic>
                        </span>
                    </el-card>
                </el-col>
                <el-col :span="6">
                    <el-card>
                        <span>
                            <el-icon :size="64">
                                <DocumentRemove />
                            </el-icon>
                        </span>
                        <span class="numbercards-text">
                            <p>不良品数</p>
                            <h1><el-statistic :value="badCount" /></h1>
                        </span>
                    </el-card>
                </el-col>
            </el-row>
        </div>
        <!-- 展示图表数据 -->
        <div class="charts">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-card>
                        <!-- // 工单状态分布饼图 -->
                        <BaseChart ref="pipChartRef" :options="workOrderStatusChartOptions" />
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card>
                        <!-- // 工单趋势折线图 -->
                        <BaseChart :options="qualityRateTrendChartOptions" />
                    </el-card>
                </el-col>
            </el-row>
        </div>
        <div class="charts">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-card>
                        <!-- // 产线产量对比柱状图 -->
                        <BaseChart :options="lineProductionChartOptions" />
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card>
                        <!-- // 产品产量TOP5条形图 -->
                        <BaseChart :options="productTop5ChartOptions" height="350px" />
                    </el-card>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getLineListApi } from '@/api/line'
import { getProductListApi } from '@/api/product'
import { getWorkOrderDetailApi, getstatisticsProductionQualityApi, getStatisticsLineProductionApi, getStatisticsProductProductionApi } from '@/api/datalist'
import { DocumentRemove } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import BaseChart from '@/components/BaseChart.vue'
import { pieChart, passRateLineChart, lineProductionBarChart, productTop5BarChart } from '@/utils/chartConfig'
import { getDefaultDateRange } from '@/utils/date'

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
const qualityList = ref([])

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
    if (qualityList.value.length === 0) return 0

    let count = 0
    qualityList.value.forEach(quality => {
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

// 获取指定时间内工单详细信息
const getWorkOrderDetail = async (startTime, endTime) => {
    try {
        const res = await getWorkOrderDetailApi(startTime, endTime)
        if (res.code === 200) {
            workOrderList.value = res.data
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
            qualityList.value = res.data
        }
    } catch (error) {
        console.error('获取质量数据失败:', error)
    }
}

// 获取指定时间内的产线产量统计数据
const getstatisticsLineProduction = async (startTime, endTime) => {
    try {
        const res = await getStatisticsLineProductionApi(startTime, endTime)
        if (res.code === 200) {
            lineProductionQualityData.value = res.data
        }
    } catch (error) {
        console.error('获取产线产量数据失败:', error)
    }
}

// 获取指定时间内产品产量统计数据
const getstatisticsProductProduction = async (startTime, endTime) => {
    try {
        const res = await getStatisticsProductProductionApi(startTime, endTime)
        if (res.code === 200) {
            productProductionList.value = res.data
        }
    } catch (error) {
        console.error('获取产品产量数据失败:', error)
    }
}

// 工单状态图表配置
const workOrderStatusChartOptions = computed(() => {
    return pieChart('工单状态分布', workOrderCountByStatus.value)
})

// 良品率趋势图表配置
const qualityRateTrendChartOptions = computed(() => {
    const trendData = qualityList.value.map(item => ({
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
    await Promise.all([
        getWorkOrderDetail(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsProductionQuality(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsLineProduction(form.value.dateRange[0], form.value.dateRange[1]),
        getstatisticsProductProduction(form.value.dateRange[0], form.value.dateRange[1])
    ])
})
</script>

<style scoped>
.container {
    padding: 10px 20px;
    width: 100%;
}

.toolbar {
    padding: 20px 0;
    width: 100%;
}

.search-form {
    width: 100%;
}

.search-form :deep(.el-form-item) {
    margin-bottom: 0;
    width: 100%;
    overflow: hidden;
}

.search-form :deep(.el-form-item__label) {
    float: none;
    display: block;
    text-align: left;
    margin-bottom: 4px;
}

.search-form :deep(.el-form-item__content) {
    margin-left: 0 !important;
}

.box-card {
    margin: 10px 0;
}

.sub-card {
    margin-bottom: 10px;
}

.numbercards-text {
    display: inline-block;
    padding: 0 20px;
    line-height: 35px;
}

.charts {
    padding-top: 20px;
}
</style>