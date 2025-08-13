<template>
  <div class="chart-visualization">
    <!-- 页面标题 -->
    <div class="header-section">
      <h1 class="page-title">📈 可视化分析中心</h1>
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

    <!-- 数据摘要卡片 -->
    <div class="summary-section">
      <div class="summary-card">
        <div class="summary-item">
          <span class="summary-label">分析期间:</span>
          <span class="summary-value">{{ analysisDateRange }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">数据点数:</span>
          <span class="summary-value">{{ analysisResults?.dates?.length || 0 }}个交易日</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">总交易次数:</span>
          <span class="summary-value">{{ analysisResults?.tradeCount || 0 }}笔</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">年化收益率:</span>
          <span class="summary-value highlight">{{ analysisResults?.annualReturn || '0.00' }}%</span>
        </div>
      </div>
    </div>

    <!-- 大尺寸图表网格 -->
    <div class="charts-grid">
      <!-- 累计收益曲线 - 大图 -->
      <div class="chart-container large">
        <div class="chart-header">
          <h2 class="chart-title">💰 累计收益曲线</h2>
          <div class="chart-controls">
            <button @click="toggleChartType('profit', 'line')" 
                    :class="{active: chartTypes.profit === 'line'}" 
                    class="control-btn">线性</button>
            <button @click="toggleChartType('profit', 'bar')" 
                    :class="{active: chartTypes.profit === 'bar'}" 
                    class="control-btn">柱状</button>
            <button @click="openFullscreenChart('profit')" class="control-btn">🔍</button>
          </div>
        </div>
        <div class="chart-wrapper large-chart">
          <canvas ref="profitChart" class="chart-canvas"></canvas>
        </div>
        <div class="chart-insights">
          <div class="insight-item">
            <span class="insight-label">最大收益:</span>
            <span class="insight-value positive">{{ maxProfit }}元</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">最大亏损:</span>
            <span class="insight-value negative">{{ maxLoss }}元</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">收益波动:</span>
            <span class="insight-value">{{ profitVolatility }}%</span>
          </div>
        </div>
      </div>

      <!-- 价格与网格线 - 大图 -->
      <div class="chart-container large">
        <div class="chart-header">
          <h2 class="chart-title">📊 价格与网格分析</h2>
          <div class="chart-controls">
            <button @click="toggleGridLines" 
                    :class="{active: showGridLines}" 
                    class="control-btn">网格线</button>
            <button @click="toggleTradePoints" 
                    :class="{active: showTradePoints}" 
                    class="control-btn">交易点</button>
            <button @click="openFullscreenChart('grid')" class="control-btn">🔍</button>
          </div>
        </div>
        <div class="chart-wrapper large-chart">
          <canvas ref="gridChart" class="chart-canvas"></canvas>
        </div>
        <div class="chart-insights">
          <div class="insight-item">
            <span class="insight-label">网格触发次数:</span>
            <span class="insight-value">{{ gridTriggerCount }}次</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">网格效率:</span>
            <span class="insight-value">{{ gridEfficiency }}%</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">价格覆盖率:</span>
            <span class="insight-value">{{ priceCoverage }}%</span>
          </div>
        </div>
      </div>

      <!-- 回撤分析 - 中图 -->
      <div class="chart-container medium">
        <div class="chart-header">
          <h2 class="chart-title">📉 回撤风险分析</h2>
          <div class="chart-controls">
            <button @click="openFullscreenChart('drawdown')" class="control-btn">🔍</button>
          </div>
        </div>
        <div class="chart-wrapper medium-chart">
          <canvas ref="drawdownChart" class="chart-canvas"></canvas>
        </div>
        <div class="chart-insights">
          <div class="insight-item">
            <span class="insight-label">最大回撤:</span>
            <span class="insight-value negative">{{ analysisResults?.maxDrawdown || '0.00' }}%</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">回撤天数:</span>
            <span class="insight-value">{{ drawdownDays }}天</span>
          </div>
        </div>
      </div>

      <!-- 资金分布 - 中图 -->
      <div class="chart-container medium">
        <div class="chart-header">
          <h2 class="chart-title">💼 资金配置分析</h2>
          <div class="chart-controls">
            <button @click="openFullscreenChart('allocation')" class="control-btn">🔍</button>
          </div>
        </div>
        <div class="chart-wrapper medium-chart">
          <canvas ref="allocationChart" class="chart-canvas"></canvas>
        </div>
        <div class="chart-insights">
          <div class="insight-item">
            <span class="insight-label">平均现金比例:</span>
            <span class="insight-value">{{ avgCashRatio }}%</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">平均持仓比例:</span>
            <span class="insight-value">{{ avgPositionRatio }}%</span>
          </div>
        </div>
      </div>

      <!-- 交易频率分析 - 新增 -->
      <div class="chart-container medium">
        <div class="chart-header">
          <h2 class="chart-title">⚡ 交易频率分析</h2>
          <div class="chart-controls">
            <button @click="openFullscreenChart('frequency')" class="control-btn">🔍</button>
          </div>
        </div>
        <div class="chart-wrapper medium-chart">
          <canvas ref="frequencyChart" class="chart-canvas"></canvas>
        </div>
        <div class="chart-insights">
          <div class="insight-item">
            <span class="insight-label">日均交易:</span>
            <span class="insight-value">{{ avgDailyTrades }}笔</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">买卖比例:</span>
            <span class="insight-value">{{ buyVsSellRatio }}</span>
          </div>
        </div>
      </div>

      <!-- 收益分布 - 新增 -->
      <div class="chart-container medium">
        <div class="chart-header">
          <h2 class="chart-title">📊 收益分布分析</h2>
          <div class="chart-controls">
            <button @click="openFullscreenChart('distribution')" class="control-btn">🔍</button>
          </div>
        </div>
        <div class="chart-wrapper medium-chart">
          <canvas ref="distributionChart" class="chart-canvas"></canvas>
        </div>
        <div class="chart-insights">
          <div class="insight-item">
            <span class="insight-label">胜率:</span>
            <span class="insight-value positive">{{ analysisResults?.winRate || '0.00' }}%</span>
          </div>
          <div class="insight-item">
            <span class="insight-label">盈亏比:</span>
            <span class="insight-value">{{ profitLossRatio }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 全屏模态框 -->
    <div v-if="fullscreenChart" class="fullscreen-modal" @click="closeFullscreen">
      <div class="fullscreen-content" @click.stop>
        <div class="fullscreen-header">
          <h3>{{ fullscreenTitle }}</h3>
          <button @click="closeFullscreen" class="close-button">✕</button>
        </div>
        <div class="fullscreen-chart-wrapper">
          <canvas ref="fullscreenChartCanvas" class="fullscreen-chart-canvas"></canvas>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

export default {
  name: 'ChartVisualization',
  props: {
    analysisResults: {
      type: Object,
      required: false,
      default: null
    }
  },
  data() {
    return {
      charts: {
        profit: null,
        grid: null,
        drawdown: null,
        allocation: null,
        frequency: null,
        distribution: null,
        fullscreen: null
      },
      chartTypes: {
        profit: 'line',
        grid: 'line'
      },
      showGridLines: true,
      showTradePoints: true,
      fullscreenChart: null,
      fullscreenTitle: ''
    }
  },
  computed: {
    // 从props或sessionStorage获取分析结果
    currentAnalysisResults() {
      if (this.analysisResults) {
        return this.analysisResults
      }
      
      // 从sessionStorage获取数据
      const storedData = sessionStorage.getItem('tradingAnalysisResults')
      if (storedData) {
        try {
          return JSON.parse(storedData)
        } catch (error) {
          console.error('解析存储的分析结果失败:', error)
          return null
        }
      }
      
      return null
    },
    
    analysisDateRange() {
      if (!this.currentAnalysisResults?.dates?.length) return '无数据'
      const dates = this.currentAnalysisResults.dates
      return `${dates[0]} ~ ${dates[dates.length - 1]}`
    },
    maxProfit() {
      if (!this.currentAnalysisResults?.profitHistory?.length) return '0.00'
      const profits = this.currentAnalysisResults.profitHistory.map(p => p.profit)
      return Math.max(...profits).toFixed(2)
    },
    maxLoss() {
      if (!this.currentAnalysisResults?.profitHistory?.length) return '0.00'
      const profits = this.currentAnalysisResults.profitHistory.map(p => p.profit)
      return Math.min(...profits).toFixed(2)
    },
    profitVolatility() {
      if (!this.currentAnalysisResults?.profitHistory?.length) return '0.00'
      const profits = this.currentAnalysisResults.profitHistory.map(p => p.profit)
      const avg = profits.reduce((a, b) => a + b, 0) / profits.length
      const variance = profits.reduce((a, b) => a + Math.pow(b - avg, 2), 0) / profits.length
      return (Math.sqrt(variance) / avg * 100).toFixed(2)
    },
    gridTriggerCount() {
      if (!this.currentAnalysisResults?.tradeHistory?.length) return 0
      return this.currentAnalysisResults.tradeHistory.filter(t => t.reason?.includes('网格')).length
    },
    gridEfficiency() {
      const total = this.currentAnalysisResults?.tradeCount || 0
      const gridTrades = this.gridTriggerCount
      return total > 0 ? (gridTrades / total * 100).toFixed(1) : '0.0'
    },
    priceCoverage() {
      if (!this.currentAnalysisResults?.prices?.length || !this.currentAnalysisResults?.gridLines?.length) return '0.0'
      const priceRange = Math.max(...this.currentAnalysisResults.prices) - Math.min(...this.currentAnalysisResults.prices)
      const gridRange = Math.max(...this.currentAnalysisResults.gridLines) - Math.min(...this.currentAnalysisResults.gridLines)
      return (Math.min(gridRange / priceRange, 1) * 100).toFixed(1)
    },
    drawdownDays() {
      if (!this.currentAnalysisResults?.drawdownHistory?.length) return 0
      return this.currentAnalysisResults.drawdownHistory.filter(d => d.drawdown > 1).length
    },
    avgCashRatio() {
      if (!this.currentAnalysisResults?.allocationHistory?.length) return '0.0'
      const ratios = this.currentAnalysisResults.allocationHistory.map(a => 
        a.capital / (a.capital + a.position) * 100
      )
      return (ratios.reduce((a, b) => a + b, 0) / ratios.length).toFixed(1)
    },
    avgPositionRatio() {
      return (100 - parseFloat(this.avgCashRatio)).toFixed(1)
    },
    avgDailyTrades() {
      const total = this.currentAnalysisResults?.tradeCount || 0
      const days = this.currentAnalysisResults?.dates?.length || 1
      return (total / days).toFixed(2)
    },
    buyVsSellRatio() {
      if (!this.currentAnalysisResults?.tradeHistory?.length) return '1:1'
      const buys = this.currentAnalysisResults.tradeHistory.filter(t => t.type === 'buy').length
      const sells = this.currentAnalysisResults.tradeHistory.filter(t => t.type === 'sell').length
      return sells > 0 ? `${(buys/sells).toFixed(1)}:1` : `${buys}:0`
    },
    profitLossRatio() {
      if (!this.currentAnalysisResults?.tradeHistory?.length) return '1:1'
      const profits = this.currentAnalysisResults.tradeHistory.filter(t => (t.type === 'sell' && t.amount > 0))
      const losses = this.currentAnalysisResults.tradeHistory.filter(t => (t.type === 'sell' && t.amount < 0))
      return losses.length > 0 ? `${(profits.length/losses.length).toFixed(1)}:1` : `${profits.length}:0`
    }
  },
  mounted() {
    this.$nextTick(() => {
      setTimeout(() => {
        this.renderAllCharts()
      }, 100)
    })
  },
  beforeUnmount() {
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.destroy()
    })
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

    renderAllCharts() {
      try {
        this.renderProfitChart()
        this.renderGridChart()
        this.renderDrawdownChart()
        this.renderAllocationChart()
        this.renderFrequencyChart()
        this.renderDistributionChart()
      } catch (error) {
        console.error('图表渲染错误:', error)
      }
    },

    renderProfitChart() {
      if (!this.$refs.profitChart) return
      const ctx = this.$refs.profitChart.getContext('2d')
      if (!ctx) return
      
      if (this.charts.profit) this.charts.profit.destroy()
      
      this.charts.profit = new Chart(ctx, {
        type: this.chartTypes.profit,
        data: {
          labels: this.currentAnalysisResults.profitHistory?.map(p => p.date.slice(5)) || [],
          datasets: [{
            label: '累计收益 (元)',
            data: this.currentAnalysisResults.profitHistory?.map(p => p.profit) || [],
            borderColor: '#4CAF50',
            backgroundColor: 'rgba(76, 175, 80, 0.1)',
            fill: true,
            tension: 0.4,
            pointRadius: 2,
            pointHoverRadius: 6,
            borderWidth: 3
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { 
              display: true,
              position: 'top',
              labels: { font: { size: 14 } }
            },
            tooltip: {
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            },
            x: {
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            }
          }
        }
      })
    },

    renderGridChart() {
      if (!this.$refs.gridChart) return
      const ctx = this.$refs.gridChart.getContext('2d')
      if (!ctx) return
      
      if (this.charts.grid) this.charts.grid.destroy()
      
      const datasets = [{
        label: '股价',
        data: this.currentAnalysisResults.prices || [],
        borderColor: '#2196F3',
        backgroundColor: 'transparent',
        borderWidth: 2,
        pointRadius: 1,
        pointHoverRadius: 4
      }]
      
      // 添加网格线
      if (this.showGridLines && this.currentAnalysisResults.gridLines) {
        const step = Math.max(1, Math.floor(this.currentAnalysisResults.gridLines.length / 8))
        this.currentAnalysisResults.gridLines.forEach((line, index) => {
          if (index % step === 0) {
            datasets.push({
              label: `网格线 ${line.toFixed(2)}`,
              data: new Array(this.currentAnalysisResults.prices.length).fill(line),
              borderColor: '#FF9800',
              backgroundColor: 'transparent',
              borderWidth: 1,
              borderDash: [5, 5],
              pointRadius: 0
            })
          }
        })
      }
      
      this.charts.grid = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.currentAnalysisResults.dates?.map(d => d.slice(5)) || [],
          datasets: datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { 
              display: true,
              position: 'top',
              labels: { 
                font: { size: 12 },
                filter: (item) => !item.text.includes('网格线') || item.datasetIndex <= 5
              }
            }
          },
          scales: {
            y: {
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            },
            x: {
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            }
          }
        }
      })
    },

    renderDrawdownChart() {
      if (!this.$refs.drawdownChart) return
      const ctx = this.$refs.drawdownChart.getContext('2d')
      if (!ctx) return
      
      if (this.charts.drawdown) this.charts.drawdown.destroy()
      
      this.charts.drawdown = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.currentAnalysisResults.drawdownHistory?.map(d => d.date.slice(5)) || [],
          datasets: [{
            label: '回撤 (%)',
            data: this.currentAnalysisResults.drawdownHistory?.map(d => -d.drawdown) || [],
            borderColor: '#F44336',
            backgroundColor: 'rgba(244, 67, 54, 0.1)',
            fill: true,
            tension: 0.4,
            pointRadius: 1,
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false }
          },
          scales: {
            y: {
              max: 0,
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            },
            x: {
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            }
          }
        }
      })
    },

    renderAllocationChart() {
      if (!this.$refs.allocationChart) return
      const ctx = this.$refs.allocationChart.getContext('2d')
      if (!ctx) return
      
      if (this.charts.allocation) this.charts.allocation.destroy()
      
      this.charts.allocation = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.currentAnalysisResults.allocationHistory?.map(a => a.date.slice(5)) || [],
          datasets: [
            {
              label: '现金',
              data: this.currentAnalysisResults.allocationHistory?.map(a => a.capital) || [],
              borderColor: '#4CAF50',
              backgroundColor: 'rgba(76, 175, 80, 0.3)',
              fill: 'origin'
            },
            {
              label: '持仓市值',
              data: this.currentAnalysisResults.allocationHistory?.map(a => a.position) || [],
              borderColor: '#2196F3',
              backgroundColor: 'rgba(33, 150, 243, 0.3)',
              fill: '-1'
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { 
              display: true,
              position: 'top'
            }
          },
          scales: {
            y: {
              stacked: true,
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            },
            x: {
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            }
          }
        }
      })
    },

    renderFrequencyChart() {
      if (!this.$refs.frequencyChart) return
      const ctx = this.$refs.frequencyChart.getContext('2d')
      if (!ctx) return
      
      if (this.charts.frequency) this.charts.frequency.destroy()
      
      // 计算每日交易频率
      const dailyTrades = {}
      this.currentAnalysisResults.tradeHistory?.forEach(trade => {
        const date = trade.date.slice(5)
        dailyTrades[date] = (dailyTrades[date] || 0) + 1
      })
      
      this.charts.frequency = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: Object.keys(dailyTrades),
          datasets: [{
            label: '日交易次数',
            data: Object.values(dailyTrades),
            backgroundColor: 'rgba(156, 39, 176, 0.6)',
            borderColor: '#9C27B0',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: { color: '#f0f0f0' },
              ticks: { font: { size: 12 } }
            },
            x: {
              grid: { color: '#f0f0f0' },
              ticks: { 
                font: { size: 10 },
                maxRotation: 45
              }
            }
          }
        }
      })
    },

    renderDistributionChart() {
      if (!this.$refs.distributionChart) return
      const ctx = this.$refs.distributionChart.getContext('2d')
      if (!ctx) return
      
      if (this.charts.distribution) this.charts.distribution.destroy()
      
      // 计算收益分布
      const profitBuckets = {}
      this.currentAnalysisResults.profitHistory?.forEach(p => {
        const bucket = Math.floor(p.profitRatio / 5) * 5 // 5%区间
        profitBuckets[bucket] = (profitBuckets[bucket] || 0) + 1
      })
      
      this.charts.distribution = new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: Object.keys(profitBuckets).map(k => `${k}%~${parseInt(k)+5}%`),
          datasets: [{
            data: Object.values(profitBuckets),
            backgroundColor: [
              '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', 
              '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
            ]
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { 
              position: 'bottom',
              labels: { font: { size: 10 } }
            }
          }
        }
      })
    },

    toggleChartType(chart, type) {
      this.chartTypes[chart] = type
      if (chart === 'profit') this.renderProfitChart()
    },

    toggleGridLines() {
      this.showGridLines = !this.showGridLines
      this.renderGridChart()
    },

    toggleTradePoints() {
      this.showTradePoints = !this.showTradePoints
      this.renderGridChart()
    },

    openFullscreenChart(chartType) {
      this.fullscreenChart = chartType
      this.fullscreenTitle = this.getChartTitle(chartType)
      this.$nextTick(() => {
        this.renderFullscreenChart(chartType)
      })
    },

    closeFullscreen() {
      if (this.charts.fullscreen) {
        this.charts.fullscreen.destroy()
        this.charts.fullscreen = null
      }
      this.fullscreenChart = null
    },

    getChartTitle(chartType) {
      const titles = {
        profit: '累计收益曲线',
        grid: '价格与网格分析',
        drawdown: '回撤风险分析',
        allocation: '资金配置分析',
        frequency: '交易频率分析',
        distribution: '收益分布分析'
      }
      return titles[chartType] || '图表分析'
    },

    renderFullscreenChart(chartType) {
      // 复制对应的图表配置到全屏画布
      const canvas = this.$refs.fullscreenChartCanvas
      if (!canvas) return
      
      const ctx = canvas.getContext('2d')
      if (!ctx) return
      
      if (this.charts.fullscreen) {
        this.charts.fullscreen.destroy()
      }
      
      // 根据图表类型复制配置
      const sourceChart = this.charts[chartType]
      if (sourceChart) {
        this.charts.fullscreen = new Chart(ctx, {
          type: sourceChart.config.type,
          data: JSON.parse(JSON.stringify(sourceChart.data)),
          options: {
            ...sourceChart.options,
            responsive: true,
            maintainAspectRatio: false
          }
        })
      }
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

/* 数据摘要 */
.summary-section {
  margin-bottom: 30px;
}

.summary-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--border-radius);
  padding: 20px;
  box-shadow: var(--card-shadow);
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
  backdrop-filter: blur(10px);
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.summary-label {
  color: var(--light-text);
  font-size: 0.9em;
  margin-bottom: 5px;
}

.summary-value {
  font-size: 1.2em;
  font-weight: 600;
  color: var(--text);
}

.summary-value.highlight {
  color: var(--success-color);
  font-size: 1.4em;
}

/* 图表网格 */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
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

.chart-container.large {
  grid-column: span 2;
}

.chart-container.medium {
  grid-column: span 1;
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

.chart-controls {
  display: flex;
  gap: 8px;
}

.control-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 15px;
  font-size: 0.8em;
  cursor: pointer;
  transition: var(--transition);
}

.control-btn:hover {
  background: var(--accent);
  color: white;
  border-color: var(--accent);
}

.control-btn.active {
  background: var(--accent);
  color: white;
  border-color: var(--accent);
}

/* 图表包装器 */
.chart-wrapper {
  position: relative;
  margin-bottom: 15px;
}

.large-chart {
  height: 400px;
}

.medium-chart {
  height: 300px;
}

.chart-canvas {
  width: 100% !important;
  height: 100% !important;
}

/* 图表洞察 */
.chart-insights {
  display: flex;
  justify-content: space-around;
  padding: 15px;
  background: rgba(248, 245, 242, 0.5);
  border-radius: 8px;
  margin-top: 10px;
}

.insight-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.insight-label {
  color: var(--light-text);
  font-size: 0.85em;
  margin-bottom: 5px;
}

.insight-value {
  font-weight: 600;
  font-size: 1.1em;
}

.insight-value.positive {
  color: var(--success-color);
}

.insight-value.negative {
  color: var(--danger-color);
}

/* 全屏模态框 */
.fullscreen-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(5px);
}

.fullscreen-content {
  background: white;
  border-radius: var(--border-radius);
  width: 95%;
  height: 90%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.fullscreen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.fullscreen-header h3 {
  margin: 0;
  color: var(--primary);
  font-size: 1.5em;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5em;
  cursor: pointer;
  color: #999;
  padding: 5px 10px;
  border-radius: 50%;
  transition: var(--transition);
}

.close-button:hover {
  background: #f5f5f5;
  color: #333;
}

.fullscreen-chart-wrapper {
  flex: 1;
  padding: 20px;
  overflow: hidden;
}

.fullscreen-chart-canvas {
  width: 100% !important;
  height: 100% !important;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-container.large {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .chart-visualization {
    padding: 15px;
  }
  
  .page-title {
    font-size: 2em;
  }
  
  .summary-card {
    flex-direction: column;
    text-align: center;
  }
  
  .chart-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .chart-insights {
    flex-direction: column;
    gap: 10px;
  }
  
  .fullscreen-content {
    width: 98%;
    height: 95%;
  }
}
</style>