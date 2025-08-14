<template>
  <div class="chart-visualization">
    <!-- 页面标题 -->
    <div class="header-section">
      <TopActionsBar title="📈 可视化分析中心" />
      <p class="page-subtitle">专业的图表分析与数据洞察</p>
      <div class="header-actions">
        <button @click="goBack" class="back-button">
          ← 返回分析页面
        </button>
        <button @click="exportCharts" class="export-button">
          📊 导出图表
        </button>
      </div>
    </div>

    <!-- 数据摘要已删除 -->

    <!-- 单图表容器 -->
    <div class="charts-grid">
      <!-- 累计收益曲线 -->
      <div class="chart-container">
        <div class="chart-header">
          <h2 class="chart-title">💰 累计收益曲线</h2>
        </div>
        <div class="chart-wrapper">
          <div ref="profitChart" class="chart-canvas"></div>
        </div>
      </div>

      <!-- 价格与网格线 -->
      <div class="chart-container">
        <div class="chart-header">
          <h2 class="chart-title">📉 价格与网格线</h2>
        </div>
        <div class="chart-wrapper">
          <div ref="priceChart" class="chart-canvas"></div>
        </div>
      </div>

      <!-- 回撤曲线（Underwater） -->
      <div class="chart-container">
        <div class="chart-header">
          <h2 class="chart-title">🌊 回撤曲线</h2>
        </div>
        <div class="chart-wrapper">
          <div ref="drawdownChart" class="chart-canvas"></div>
        </div>
      </div>

      <!-- 资金与仓位分布（堆叠面积） -->
      <div class="chart-container">
        <div class="chart-header">
          <h2 class="chart-title">💼 资金与仓位分布</h2>
        </div>
        <div class="chart-wrapper">
          <div ref="allocationChart" class="chart-canvas"></div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import * as echarts from 'echarts'
import TopActionsBar from '../common/TopActionsBar.vue'

export default {
  name: 'ChartVisualization',
  components: { TopActionsBar },
  props: {
    analysisResults: {
      type: Object,
      required: false,
      default: null
    }
  },
    data() {
    return {
      // ECharts实例
      profitChart: null,
      priceChart: null,
      drawdownChart: null,
      allocationChart: null,
      resizeHandler: null
    }
  },
  computed: {
    // 简化数据获取逻辑
    analysisData() {
      return this.analysisResults || 
             JSON.parse(sessionStorage.getItem('tradingAnalysisResults') || 'null')
    },
    
    // 预处理的计算属性将在这里重新实现
  },
  mounted() {
    this.$nextTick(() => {
      // 没有分析数据则返回分析页
      const hasData = !!(this.analysisResults || JSON.parse(sessionStorage.getItem('tradingAnalysisResults') || 'null'))
      if (!hasData) {
        alert('请先完成回测分析')
        this.$router.push('/trading-page')
        return
      }

      this.initProfitChart()
      this.initPriceGridChart()
      this.initDrawdownChart()
      this.initAllocationChart()

      // 统一窗口大小变化处理
      this.resizeHandler = () => {
        if (this.profitChart) this.profitChart.resize()
        if (this.priceChart) this.priceChart.resize()
        if (this.drawdownChart) this.drawdownChart.resize()
        if (this.allocationChart) this.allocationChart.resize()
      }
      window.addEventListener('resize', this.resizeHandler)
    })
  },
  beforeUnmount() {
    if (this.profitChart) {
      this.profitChart.dispose()
    }
    if (this.priceChart) {
      this.priceChart.dispose()
    }
    if (this.resizeHandler) {
      window.removeEventListener('resize', this.resizeHandler)
      this.resizeHandler = null
    }
  },
  methods: {
    goBack() {
      // 直接跳转到交易分析页面
      this.$router.push('/trading-page')
    },
    
    exportCharts() {
      // TODO: 实现图表导出功能
      alert('图表导出功能开发中...')
    },

    openDoc() {
      this.$router.push('/op-guide')
    },

    initProfitChart() {
      if (!this.$refs.profitChart || !this.analysisData) return
      
      // 初始化ECharts实例
      this.profitChart = echarts.init(this.$refs.profitChart)
      
      // 处理数据和时间轴
      const profitData = this.analysisData.profitHistory || []
      const dates = this.analysisData.dates || []
      
      // X轴标签显示策略：每年显示 3 个月(1/5/9)，并仅在该月的首次交易日显示一次
      const labelMonths = new Set([1, 5, 9])
      const shouldShowLabel = dates.map((date, index) => {
        const parts = (date || '').split('-')
        const year = parts[0] || ''
        const month = parseInt(parts[1], 10)
        if (!labelMonths.has(month)) return false
        if (index === 0) return true
        const prevParts = (dates[index - 1] || '').split('-')
        const prevYear = prevParts[0] || ''
        const prevMonth = parseInt(prevParts[1], 10)
        // 同年同月只显示第一次出现
        return !(prevMonth === month && prevYear === year)
      })
      
      // 判断是否显示数据点
      const dataLength = profitData.length
      let showSymbol = false
      let symbolSize = 0
      
      if (dataLength <= 100) {
        showSymbol = true
        symbolSize = 4
      } else if (dataLength <= 500) {
        showSymbol = true
        symbolSize = 2
      } else {
        showSymbol = false
        symbolSize = 0
      }
      
      // 构造基准（买入并持有）收益序列：以初始资产为基准，收益=初始资产*(价格/首日价格-1)
      const pricesForBenchmark = this.analysisData.prices || []
      const initialAsset = (this.analysisData.profitHistory && this.analysisData.profitHistory[0]?.totalValue) || 0
      const firstPrice = pricesForBenchmark.length > 0 ? pricesForBenchmark[0] : null
      const benchmarkProfit = (firstPrice && initialAsset)
        ? pricesForBenchmark.map(p => initialAsset * (p / firstPrice - 1))
        : []

      // 图表配置
      const option = {
        legend: {
          data: ['累计收益'].concat(benchmarkProfit.length > 0 ? ['基准(买入并持有)'] : []),
          right: 10,
          top: 8,
          textStyle: { color: '#666' }
        },
        // 删除标题，因为左上角已有
        tooltip: {
          trigger: 'axis', // 使用axis触发器
          backgroundColor: 'rgba(0,0,0,0.8)',
          borderColor: '#4CAF50',
          borderWidth: 1,
          textStyle: {
            color: '#fff'
          },
          axisPointer: {
            type: 'cross',
            snap: true, // 关键：自动吸附到最近的数据点
            crossStyle: {
              color: '#4CAF50'
            }
          },
          formatter: function(params) {
            if (!params || params.length === 0) return ''
            const dataIndex = params[0].dataIndex
            const originalDate = dates[dataIndex]
            const profitItem = params.find(p => p.seriesName === '累计收益')
            const benchmarkItem = params.find(p => p.seriesName === '基准(买入并持有)')
            const profitVal = profitItem ? Number(profitItem.value) : 0
            const totalVal = Number(initialAsset) + profitVal
            let lines = [`日期: ${originalDate}`, `累计收益: ${profitVal.toLocaleString()}元`, `总资产: ${totalVal.toLocaleString()}元`]
            if (benchmarkItem) {
              const ben = Number(benchmarkItem.value)
              lines.splice(1, 0, `基准收益: ${ben.toLocaleString()}元`)
            }
            return lines.join('<br/>')
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates, // 使用完整日期，让 interval 函数基于原始日期判断
          axisLabel: {
            color: '#666',
            // 仅在我们指定的位置显示标签，保证每年固定 1/4/7/12 月各显示一次（若区间内存在）
            interval: function(index) {
              return shouldShowLabel[index]
            },
            formatter: function(value) {
              if (!value) return ''
              const parts = String(value).split('-')
              if (parts.length < 2) return value
              const yy = (parts[0] || '').slice(2)
              const mm = String(parseInt(parts[1], 10)).padStart(2, '0')
              return `${yy}-${mm}`
            },
            rotate: 0,
            margin: 8
          },
          axisLine: {
            lineStyle: {
              color: '#ddd'
            }
          },
          axisTick: {
            show: false
          },
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          name: '收益(元)',
          nameTextStyle: {
            color: '#666',
            padding: [0, 0, 0, 20]
          },
          axisLabel: {
            color: '#666',
            formatter: function(value) {
              return Number(value).toLocaleString()
            }
          },
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          }
        },
        series: [
          {
            name: '累计收益',
            type: 'line',
            data: profitData.map(p => p.profit),
            smooth: true,
            symbol: showSymbol ? 'circle' : 'none',
            symbolSize: symbolSize,
            lineStyle: { color: '#4CAF50', width: 2 },
            itemStyle: { color: '#4CAF50' },
            areaStyle: {
              color: {
                type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(76, 175, 80, 0.3)' },
                  { offset: 1, color: 'rgba(76, 175, 80, 0.1)' }
                ]
              }
            }
          },
          benchmarkProfit.length > 0 ? {
            name: '基准(买入并持有)',
            type: 'line',
            data: benchmarkProfit,
            smooth: true,
            symbol: 'none',
            lineStyle: { color: '#9E9E9E', width: 2, type: 'dashed' }
          } : null
        ].filter(Boolean)
      }
      this.profitChart.setOption(option)
    }

    ,initDrawdownChart() {
      if (!this.$refs.drawdownChart || !this.analysisData) return
      const chart = echarts.init(this.$refs.drawdownChart)
      this.drawdownChart = chart

      const drawdown = (this.analysisData.drawdownHistory || []).map(d => d.drawdown)
      const dates = this.analysisData.dates || []

      const labelMonths = new Set([1, 5, 9])
      const shouldShowLabel = dates.map((date, index) => {
        const parts = (date || '').split('-')
        const year = parts[0] || ''
        const month = parseInt(parts[1], 10)
        if (!labelMonths.has(month)) return false
        if (index === 0) return true
        const prevParts = (dates[index - 1] || '').split('-')
        const prevYear = prevParts[0] || ''
        const prevMonth = parseInt(prevParts[1], 10)
        return !(prevMonth === month && prevYear === year)
      })

      chart.setOption({
        legend: {
          data: ['回撤'],
          right: 10,
          top: 8,
          textStyle: { color: '#666' }
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0,0,0,0.8)',
          borderColor: '#FF9800',
          borderWidth: 1,
          textStyle: { color: '#fff' },
          axisPointer: { type: 'cross', snap: true, crossStyle: { color: '#FF9800' } },
          formatter: function(params) {
            if (params && params.length > 0) {
              const dataIndex = params[0].dataIndex
              const originalDate = dates[dataIndex]
              const dd = params[0].value
              return `日期: ${originalDate}<br/>回撤: ${Number(dd).toFixed(2)}%`
            }
            return ''
          }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            color: '#666',
            interval: function(index) { return shouldShowLabel[index] },
            formatter: function(value) {
              if (!value) return ''
              const parts = String(value).split('-')
              if (parts.length < 2) return value
              const yy = (parts[0] || '').slice(2)
              const mm = String(parseInt(parts[1], 10)).padStart(2, '0')
              return `${yy}-${mm}`
            },
            rotate: 0,
            margin: 8
          },
          axisLine: { lineStyle: { color: '#ddd' } },
          axisTick: { show: false },
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          name: '回撤(%)',
          nameTextStyle: { color: '#666', padding: [0, 0, 0, 20] },
          axisLabel: { color: '#666' },
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: '回撤',
            type: 'line',
            data: drawdown,
            smooth: true,
            symbol: 'none',
            lineStyle: { color: '#FF9800', width: 2 },
            areaStyle: {
              color: {
                type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(255, 152, 0, 0.25)' },
                  { offset: 1, color: 'rgba(255, 152, 0, 0.05)' }
                ]
              }
            }
          }
        ]
      })
    }

    ,initAllocationChart() {
      if (!this.$refs.allocationChart || !this.analysisData) return
      const chart = echarts.init(this.$refs.allocationChart)
      this.allocationChart = chart

      const allocation = this.analysisData.allocationHistory || []
      const dates = this.analysisData.dates || []
      const cash = allocation.map(a => a.capital)
      const position = allocation.map(a => a.position)

      const labelMonths = new Set([1, 5, 9])
      const shouldShowLabel = dates.map((date, index) => {
        const parts = (date || '').split('-')
        const year = parts[0] || ''
        const month = parseInt(parts[1], 10)
        if (!labelMonths.has(month)) return false
        if (index === 0) return true
        const prevParts = (dates[index - 1] || '').split('-')
        const prevYear = prevParts[0] || ''
        const prevMonth = parseInt(prevParts[1], 10)
        return !(prevMonth === month && prevYear === year)
      })

      chart.setOption({
        legend: {
          data: ['现金', '持仓'],
          right: 10,
          top: 8,
          textStyle: { color: '#666' }
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0,0,0,0.8)',
          borderColor: '#9C27B0',
          borderWidth: 1,
          textStyle: { color: '#fff' },
          axisPointer: { type: 'cross', snap: true, crossStyle: { color: '#9C27B0' } },
          formatter: function(params) {
            if (params && params.length > 0) {
              const dataIndex = params[0].dataIndex
              const originalDate = dates[dataIndex]
              const cashVal = cash[dataIndex]
              const posVal = position[dataIndex]
              const totalVal = (cashVal || 0) + (posVal || 0)
              return `日期: ${originalDate}<br/>现金: ${Number(cashVal).toFixed(2)}<br/>持仓: ${Number(posVal).toFixed(2)}<br/>总资产: ${Number(totalVal).toFixed(2)}`
            }
            return ''
          }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            color: '#666',
            interval: function(index) { return shouldShowLabel[index] },
            formatter: function(value) {
              if (!value) return ''
              const parts = String(value).split('-')
              if (parts.length < 2) return value
              const yy = (parts[0] || '').slice(2)
              const mm = String(parseInt(parts[1], 10)).padStart(2, '0')
              return `${yy}-${mm}`
            },
            rotate: 0,
            margin: 8
          },
          axisLine: { lineStyle: { color: '#ddd' } },
          axisTick: { show: false },
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          name: '金额(元)',
          nameTextStyle: { color: '#666', padding: [0, 0, 0, 20] },
          axisLabel: { color: '#666' },
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: '现金',
            type: 'line',
            stack: 'total',
            areaStyle: {},
            symbol: 'none',
            lineStyle: { color: 'rgba(156, 39, 176, 0.8)' },
            itemStyle: { color: 'rgba(156, 39, 176, 0.6)' },
            data: cash
          },
          {
            name: '持仓',
            type: 'line',
            stack: 'total',
            areaStyle: {},
            symbol: 'none',
            lineStyle: { color: 'rgba(33, 150, 243, 0.8)' },
            itemStyle: { color: 'rgba(33, 150, 243, 0.6)' },
            data: position
          }
        ]
      })
    }

    ,initPriceGridChart() {
      if (!this.$refs.priceChart || !this.analysisData) return
      this.priceChart = echarts.init(this.$refs.priceChart)

      const prices = this.analysisData.prices || []
      const gridLines = this.analysisData.gridLines || []
      const dates = this.analysisData.dates || []

      // X轴标签策略与收益图一致：每年显示 1/5/9 的首次交易日
      const labelMonths = new Set([1, 5, 9])
      const shouldShowLabel = dates.map((date, index) => {
        const parts = (date || '').split('-')
        const year = parts[0] || ''
        const month = parseInt(parts[1], 10)
        if (!labelMonths.has(month)) return false
        if (index === 0) return true
        const prevParts = (dates[index - 1] || '').split('-')
        const prevYear = prevParts[0] || ''
        const prevMonth = parseInt(prevParts[1], 10)
        return !(prevMonth === month && prevYear === year)
      })

      const option = {
        legend: {
          data: ['价格'],
          right: 10,
          top: 8,
          textStyle: { color: '#666' }
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0,0,0,0.8)',
          borderColor: '#2196F3',
          borderWidth: 1,
          textStyle: { color: '#fff' },
          axisPointer: { type: 'cross', snap: true, crossStyle: { color: '#2196F3' } },
          formatter: function(params) {
            if (params && params.length > 0) {
              const dataIndex = params[0].dataIndex
              const originalDate = dates[dataIndex]
              const price = params[0].value
              return `日期: ${originalDate}<br/>收盘价: ${Number(price).toFixed(2)}元`
            }
            return ''
          }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            color: '#666',
            interval: function(index) { return shouldShowLabel[index] },
            formatter: function(value) {
              if (!value) return ''
              const parts = String(value).split('-')
              if (parts.length < 2) return value
              const yy = (parts[0] || '').slice(2)
              const mm = String(parseInt(parts[1], 10)).padStart(2, '0')
              return `${yy}-${mm}`
            },
            rotate: 0,
            margin: 8
          },
          axisLine: { lineStyle: { color: '#ddd' } },
          axisTick: { show: false },
          boundaryGap: false
        },
        yAxis: {
          type: 'value',
          name: '价格(元)',
          nameTextStyle: { color: '#666', padding: [0, 0, 0, 20] },
          axisLabel: { color: '#666' },
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { lineStyle: { color: '#f0f0f0' } }
        },
        series: [
          {
            name: '价格',
            type: 'line',
            data: prices,
            smooth: true,
            symbol: 'none',
            lineStyle: { color: '#2196F3', width: 2 },
            markLine: {
              silent: true,
              symbol: 'none',
              label: { show: false },
              lineStyle: { type: 'dashed', color: '#999', width: 1, opacity: 0.6 },
              data: (gridLines || []).map(v => ({ yAxis: v }))
            }
          }
        ]
      }

      this.priceChart.setOption(option)
    }










  }
}
</script>

<style scoped>
/* 使用与TradingPage一致的主题变量 */
:root {
  --primary: #1a1a1a;
  --secondary: #f8f5f2;
  --accent: #d4b8a0;
  --text: #333333;
  --light-text: #777777;
  --success-color: #4CAF50;
  --warning-color: #FF9800;
  --danger-color: #F44336;
  --card-shadow: 0 8px 32px rgba(0,0,0,0.1);
  --border-radius: 12px;
  --transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.chart-visualization {
  min-height: 100vh;
  background-color: var(--secondary);
  padding: 20px;
  font-family: 'Montserrat', sans-serif;
  color: var(--text);
}

/* 页面标题 */
.header-section {
  text-align: center;
  margin-bottom: 30px;
}

.top-actions {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 10px;
}

.nav-btn {
  justify-self: start;
  padding: 6px 12px;
  border-radius: 16px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}

.top-actions .page-title {
  margin: 0 auto;
}

.top-actions .nav-btn:last-child {
  justify-self: end;
}

.page-title {
  color: var(--primary);
  font-size: 2.5em;
  margin-bottom: 10px;
  font-weight: 300;
  font-family: 'Cormorant Garamond', serif;
}

.page-subtitle {
  color: var(--light-text);
  font-size: 1.2em;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.back-button, .export-button {
  padding: 10px 20px;
  border: none;
  border-radius: 25px;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.back-button {
  background: var(--accent);
  color: white;
}

.export-button {
  background: #4CAF50;
  color: white;
}

.back-button:hover, .export-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}



/* 图表网格 */
.charts-grid {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.chart-container {
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--border-radius);
  padding: 20px;
  box-shadow: var(--card-shadow);
  backdrop-filter: blur(10px);
  transition: var(--transition);
}

.chart-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0,0,0,0.15);
}

.chart-container {
  width: 100%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}



.chart-title {
  color: var(--primary);
  font-size: 1.3em;
  font-weight: 600;
  margin: 0;
}





.chart-wrapper {
  height: 500px;
  position: relative;
}

.chart-canvas {
  width: 100%;
  height: 100%;
}





/* 全屏相关样式已删除 */

/* 响应式设计 */


@media (max-width: 768px) {
  .chart-visualization {
    padding: 15px;
  }
  
  .page-title {
    font-size: 2em;
  }
  

  
  .chart-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  

  

}
</style>