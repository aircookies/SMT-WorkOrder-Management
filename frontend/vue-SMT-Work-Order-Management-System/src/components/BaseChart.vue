<template>
    <div ref="chartRef" :style="{ width: width, height: height }"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

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

const initChart = async () => {
    if (!chartRef.value) return
    
    await nextTick()
    
    if (chartInstance) {
        chartInstance.dispose()
    }
    
    chartInstance = echarts.init(chartRef.value)
    chartInstance.setOption(props.options, true)
}

const resizeHandler = () => {
    if (chartInstance) {
        chartInstance.resize()
    }
}

watch(() => props.options, (newOptions) => {
    if (chartInstance && newOptions) {
        chartInstance.setOption(newOptions, true)
    }
}, { deep: true })

onMounted(() => {
    initChart()
    
    if (props.autoResize) {
        window.addEventListener('resize', resizeHandler)
    }
})

onBeforeUnmount(() => {
    if (chartInstance) {
        chartInstance.dispose()
        chartInstance = null
    }
    
    if (props.autoResize) {
        window.removeEventListener('resize', resizeHandler)
    }
})

defineExpose({
    getInstance: () => chartInstance,
    resize: resizeHandler
})
</script>

<style scoped>
</style>