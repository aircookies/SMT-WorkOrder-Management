export const passRateLineChart = (data = []) => {
    // data 格式: [{ date: '2025-04-01', value: 10 }, ...]
    const dates = data.map(item => item.date)
    const values = data.map(item => item.value)

    return {
        title: {
            text: '良品率趋势',
            left: 'center',
            textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
            }
        },
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                const data = params[0]
                return `${data.name}<br/>${data.seriesName}: ${data.value}%`
            }
        },
        legend: {
            data: ['良品率'],
            top: 40
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: dates
        },
        yAxis: {
            type: 'value',
            name: '良品率(%)',
            min: 95,
            max: 100,
            axisLabel: {
                formatter: '{value}%'
            }
        },
        series: [
            {
                name: '良品率',
                type: 'line',
                smooth: true,
                data: values,
                itemStyle: {
                    color: '#409EFF'
                },
                areaStyle: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [
                            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                        ]
                    }
                },
                markLine: {
                    data: [
                        {
                            type: 'average',
                            name: '平均值'
                        }
                    ]
                }
            }
        ]
    }
}

export const barChart = (data = {}) => {
    const { xAxisData = [], seriesData = [], title = '', legend = [] } = data
    
    return {
        title: {
            text: title,
            left: 'center',
            textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: legend,
            top: 30
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: xAxisData
        },
        yAxis: {
            type: 'value'
        },
        series: seriesData.map(item => ({
            name: item.name,
            type: 'bar',
            data: item.data,
            itemStyle: item.itemStyle || {}
        }))
    }
}

export const pieChart = (title = '', data = []) => {
    const seriesData = data.map(item => ({
        name: item.name,
        value: item.value,
    }))
    
    return {
        title: {
            text: title,
            left: 'center',
            textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
            }
        },
        tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)'
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            top: 'middle'
        },
        series: [
            {
                name: title,
                type: 'pie',
                radius: '50%',
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 5,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: true,
                    formatter: '{b}: {c} ({d}%)'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 16,
                        fontWeight: 'bold'
                    }
                },
                data: seriesData
            }
        ]
    }
}

export const lineProductionBarChart = (data = []) => {
    const lineNames = data.map(item => item.lineName)
    const planQuantities = data.map(item => item.totalPlanQuantity)
    const completedQuantities = data.map(item => item.completedQuantity)

    return {
        title: {
            text: '产线产量对比',
            left: 'center',
            textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            formatter: function (params) {
                let result = params[0].name + '<br/>'
                params.forEach(param => {
                    result += `${param.marker}${param.seriesName}: ${param.value}<br/>`
                })
                return result
            }
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        legend: {
            data: ['计划数量', '完成数量'],
            top: 40
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: lineNames,
            axisLabel: {
                interval: 0,
                rotate: 30
            }
        },
        yAxis: {
            type: 'value',
            name: '数量'
        },
        series: [
            {
                name: '计划数量',
                type: 'bar',
                data: planQuantities,
                itemStyle: {
                    color: '#409EFF'
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            },
            {
                name: '完成数量',
                type: 'bar',
                data: completedQuantities,
                itemStyle: {
                    color: '#67C23A'
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
}

export const productTop5BarChart = (data = []) => {
    const sortedData = [...data]
        .sort((a, b) => b.completedQuantity - a.completedQuantity)
        .slice(0, 5)

    const productNames = sortedData.map(item => item.productName).reverse()
    const completedQuantities = sortedData.map(item => item.completedQuantity).reverse()
    const planQuantities = sortedData.map(item => item.totalPlanQuantity).reverse()

    return {
        title: {
            text: '产品产量TOP5',
            left: 'center',
            textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
            }
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            formatter: function (params) {
                let result = params[0].name + '<br/>'
                params.forEach(param => {
                    result += `${param.marker}${param.seriesName}: ${param.value}<br/>`
                })
                if (params.length >= 2) {
                    const completionRate = ((params[0].value / params[1].value) * 100).toFixed(2)
                    result += `完成率: ${completionRate}%`
                }
                return result
            }
        },
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        legend: {
            data: ['完成数量', '计划数量'],
            top: 40
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'value',
            name: '数量'
        },
        yAxis: {
            type: 'category',
            data: productNames,
            axisLabel: {
                interval: 0,
                formatter: function (value) {
                    return value.length > 8 ? value.substring(0, 8) + '...' : value
                }
            }
        },
        series: [
            {
                name: '完成数量',
                type: 'bar',
                data: completedQuantities,
                itemStyle: {
                    color: '#67C23A'
                },
                label: {
                    show: true,
                    position: 'right',
                    formatter: '{c}'
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            },
            {
                name: '计划数量',
                type: 'bar',
                data: planQuantities,
                itemStyle: {
                    color: '#409EFF'
                },
                label: {
                    show: true,
                    position: 'right',
                    formatter: '{c}'
                },
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
}
