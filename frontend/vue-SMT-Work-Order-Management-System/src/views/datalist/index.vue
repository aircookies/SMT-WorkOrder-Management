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
                            <el-button type="success" @click="onExport">导出</el-button>
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
                            <el-statistic :value="workOrderCompletedRate" precision="2">
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
                        <BaseChart :options="workOrderStatusChartOptions" height="350px" />
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card>
                        <BaseChart :options="workOrderTrendChartOptions" height="350px" />
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
import { getWorkOrderDetailApi, getstatisticsProductionQualityApi } from '@/api/datalist'
import { DocumentRemove } from '@element-plus/icons-vue'
import BaseChart from '@/components/BaseChart.vue'
import { pieChart, passRateLineChart } from '@/utils/chartConfig'
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

// 产线选项
const lineOptions = ref([])

// 产品选项
const productOptions = ref([])

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
    return workOrderCompletedCount.value / workOrderCount.value * 100
})

// 产品不良数
const badCount = computed(() => {
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

// 获取产线列表
const loadLineData = async () => {
    try {
        const res = await getLineListApi()
        if (res && res.data) {
            lineOptions.value = res.data
        }
    } catch (error) {
        console.error('获取产线数据失败:', error)
    }
}

// 获取产品列表
const loadProductData = async () => {
    try {
        // 获取所有产品，这里设置一个较大的页数以获取全部数据
        const res = await getProductListApi(1, 1000)
        if (res && res.data) {
            productOptions.value = res.data.records || []
        }
    } catch (error) {
        console.error('获取产品数据失败:', error)
    }
}

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

// 工单状态图表配置
const workOrderStatusChartOptions = computed(() => {
    return pieChart('工单状态分布', workOrderCountByStatus.value)
})

// 工单完成趋势图表配置
const workOrderTrendChartOptions = computed(() => {
    const trendData = qualityList.value.map(item => ({
        date: item.date,
        value: item.passRate
    }))
    return passRateLineChart(trendData)
})

// 查询按钮点击事件
const onQuery = () => {
    console.log('查询参数:', form.value)
    // 在这里实现查询逻辑
}

// 重置按钮点击事件
const onReset = () => {
    form.value = {
        dateRange: [],
        lineId: '',
        productId: ''
    }
}

// 导出按钮点击事件
const onExport = () => {
    console.log('导出数据')
    // 在这里实现导出逻辑
}

// 初始化数据
onMounted(async () => {
    await loadLineData()
    await loadProductData()
    await getWorkOrderDetail(form.value.dateRange[0], form.value.dateRange[1])
    getstatisticsProductionQuality(form.value.dateRange[0], form.value.dateRange[1])
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
    padding: 20px 0;
}
</style>