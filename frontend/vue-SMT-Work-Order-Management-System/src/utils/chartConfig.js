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
