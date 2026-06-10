<template>
    <div ref="chartRef" :style="{ width: width, height: height }"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

/**
 * ECharts 图表基础组件
 * @param {Object} options - ECharts 配置项对象，包含图表的所有配置信息
 * @param {string} width - 图表容器宽度，支持 CSS 单位，默认为 '100%'
 * @param {string} height - 图表容器高度，支持 CSS 单位，默认为 '400px'
 * @param {boolean} autoResize - 是否自动响应窗口大小变化进行调整，默认为 true
 */
const props = defineProps({
    options: {
        type: Object,
        required: true
    },
    width: {
        type: String,
        default: '100%'
    },
    height: {
        type: String,
        default: '400px'
    },
    autoResize: {
        type: Boolean,
        default: true
    }
})

const chartRef = ref(null)
let chartInstance = null

/**
 * 初始化或重新初始化 ECharts 实例
 * 销毁旧实例并创建新实例，应用当前配置项
 */
const initChart = async () => {
    if (!chartRef.value) return

    await nextTick()

    if (chartInstance) {
        chartInstance.dispose()
    }

    chartInstance = echarts.init(chartRef.value)
    chartInstance.setOption(props.options, true)
}

/**
 * 处理窗口大小变化事件，调整图表尺寸以适应容器
 */
const resizeHandler = () => {
    if (chartInstance) {
        chartInstance.resize()
    }
}

// 监听配置项变化，动态更新图表
watch(() => props.options, (newOptions) => {
    if (!chartInstance) return

    nextTick(() => {
        chartInstance.setOption(newOptions, true)
    })
}, { deep: true })

// 手动刷新图表
// const flushChart = () => {
//     if (!chartInstance) return

//     nextTick(() => {
//         chartInstance.setOption(props.options, true)
//     })
// }

// 组件挂载时初始化图表并注册窗口大小变化监听器
onMounted(() => {
    initChart()

    if (props.autoResize) {
        window.addEventListener('resize', resizeHandler)
    }
})

// 组件卸载前清理资源，销毁图表实例并移除事件监听器
onBeforeUnmount(() => {
    if (chartInstance) {
        chartInstance.dispose()
        chartInstance = null
    }

    if (props.autoResize) {
        window.removeEventListener('resize', resizeHandler)
    }
})

/**
 * 暴露给父组件的方法
 * @returns {Object} getInstance - 获取 ECharts 实例的方法，返回当前的 chartInstance 对象
 * @returns {Function} resize - 手动触发图表尺寸调整的方法
 */
defineExpose({
    getInstance: () => chartInstance,
    resize: resizeHandler
})
</script>

<style scoped></style>