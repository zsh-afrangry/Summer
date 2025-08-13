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
        <div class="chart-header profit-chart-header">
          <div class="chart-title-section">
            <h2 class="chart-title">💰 累计收益曲线</h2>
            <div class="chart-controls">
              <button @click="toggleChartType('profit', 'line')" 
                      :class="{active: chartTypes.profit === 'line'}" 
                      class="control-btn">线性</button>
              <button @click="toggleChartType('profit', 'bar')" 
                      :class="{active: chartTypes.profit === 'bar'}" 
                      class="control-btn">柱状</button>
            </div>
          </div>
          <div class="chart-insights-header">
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
        <div class="chart-wrapper large-chart">
          <canvas ref="profitChart" class="chart-canvas"></canvas>
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
            <!-- 回撤分析图表控制 -->
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
            <!-- 资金配置分析图表控制 -->
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
            <!-- 交易频率分析图表控制 -->
          </div>
        </div>
        <div class="chart-wrapper medium-chart">
          <canvas ref="frequencyChart" class="chart-canvas"></canvas>
        </div>
        <div class="chart-insights">
          <div class="insight-item">
            <span class="insight-label">月均交易:</span>
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
            <!-- 收益分布分析图表控制 -->
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

    <!-- 全屏模态框已删除 -->
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
        distribution: null
      },
      chartTypes: {
        profit: 'line',
        grid: 'line'
      },
      showGridLines: true,
      showTradePoints: true
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
      const months = Math.ceil(days / 30) // 大概估算月数
      return (total / months).toFixed(1)
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
      
      const profitData = this.currentAnalysisResults.profitHistory || []
      
      this.charts.profit = new Chart(ctx, {
        type: this.chartTypes.profit,
        data: {
          labels: profitData.map((p, index, arr) => {
            // 优化的日期标签显示策略
            const totalPoints = arr.length
            
            // 计算理想的标签间隔，目标是显示8-12个标签
            const targetLabels = 10
            const interval = Math.max(1, Math.floor(totalPoints / targetLabels))
            
            // 显示条件：按间隔显示，或者是第一个、最后一个
            const showLabel = index === 0 || 
                            index === totalPoints - 1 || 
                            index % interval === 0
            
            return showLabel ? p.date.slice(0, 7) : ''
          }),
          originalDates: profitData.map(p => p.date), // 保存原始日期用于tooltip
          datasets: [{
            label: '累计收益 (元)',
            data: this.currentAnalysisResults.profitHistory?.map(p => p.profit) || [],
            borderColor: '#4CAF50',
            backgroundColor: 'rgba(76, 175, 80, 0.1)',
            fill: true,
            tension: 0.4,
            pointRadius: 0,          // 隐藏默认点
            pointHoverRadius: 5,     // 悬停时显示更大的点
            borderWidth: 2           // 减细线条
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'nearest',         // 最近点模式，更容易触发
            intersect: false,        // 不需要精确交叉
            axis: 'x'               // 主要根据X轴位置
          },
          plugins: {
            legend: { 
              display: true,
              position: 'top',
              labels: { font: { size: 14 } }
            },
            tooltip: {
              enabled: true,
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 },
              cornerRadius: 6,
              displayColors: true,
              callbacks: {
                title: function(context) {
                  // 显示原始日期而不是年-月
                  const index = context[0].dataIndex
                  const originalDate = context[0].chart.data.originalDates?.[index] || context[0].label
                  return originalDate
                },
                label: function(context) {
                  return `${context.dataset.label}: ${Number(context.parsed.y).toLocaleString()}元`
                }
              }
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
              ticks: { 
                font: { size: 12 },
                maxRotation: 45,     // 标签最大旋转角度
                minRotation: 0,      // 标签最小旋转角度
                maxTicksLimit: 8     // 最大显示标签数量
              }
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
        pointRadius: 0,         // 隐藏默认点
        pointHoverRadius: 3     // 悬停时显示小点
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
          labels: this.currentAnalysisResults.dates?.map((d, index, arr) => {
            const currentMonth = d.slice(0, 7)
            const prevMonth = index > 0 ? arr[index - 1].slice(0, 7) : ''
            return currentMonth !== prevMonth ? currentMonth : ''
          }) || [],
          datasets: datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'nearest',
            intersect: false,
            axis: 'x'
          },
          plugins: {
            legend: { 
              display: true,
              position: 'top',
              labels: { 
                font: { size: 12 },
                filter: (item) => !item.text.includes('网格线') || item.datasetIndex <= 5
              }
            },
            tooltip: {
              enabled: true,
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 },
              cornerRadius: 6,
              displayColors: true,
              callbacks: {
                label: function(context) {
                  return `${context.dataset.label}: ${Number(context.parsed.y).toFixed(2)}元`
                }
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
          labels: this.currentAnalysisResults.drawdownHistory?.map((d, index, arr) => {
            const currentMonth = d.date.slice(0, 7)
            const prevMonth = index > 0 ? arr[index - 1].date.slice(0, 7) : ''
            return currentMonth !== prevMonth ? currentMonth : ''
          }) || [],
          datasets: [{
            label: '回撤 (%)',
            data: this.currentAnalysisResults.drawdownHistory?.map(d => -d.drawdown) || [],
            borderColor: '#F44336',
            backgroundColor: 'rgba(244, 67, 54, 0.1)',
            fill: true,
            tension: 0.4,
            pointRadius: 0,          // 隐藏默认点
            pointHoverRadius: 3,     // 悬停时显示小点
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'nearest',
            intersect: false,
            axis: 'x'
          },
          plugins: {
            legend: { display: false },
            tooltip: {
              enabled: true,
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 },
              cornerRadius: 6,
              displayColors: true,
              callbacks: {
                label: function(context) {
                  return `${context.dataset.label}: ${Number(context.parsed.y).toFixed(2)}%`
                }
              }
            }
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
          labels: this.currentAnalysisResults.allocationHistory?.map((a, index, arr) => {
            const currentMonth = a.date.slice(0, 7)
            const prevMonth = index > 0 ? arr[index - 1].date.slice(0, 7) : ''
            return currentMonth !== prevMonth ? currentMonth : ''
          }) || [],
          datasets: [
            {
              label: '现金',
              data: this.currentAnalysisResults.allocationHistory?.map(a => a.capital) || [],
              borderColor: '#4CAF50',
              backgroundColor: 'rgba(76, 175, 80, 0.3)',
              fill: 'origin',
              pointRadius: 0,          // 隐藏默认点
              pointHoverRadius: 3,     // 悬停时显示小点
              borderWidth: 2
            },
            {
              label: '持仓市值',
              data: this.currentAnalysisResults.allocationHistory?.map(a => a.position) || [],
              borderColor: '#2196F3',
              backgroundColor: 'rgba(33, 150, 243, 0.3)',
              fill: '-1',
              pointRadius: 0,          // 隐藏默认点
              pointHoverRadius: 3,     // 悬停时显示小点
              borderWidth: 2
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'nearest',
            intersect: false,
            axis: 'x'
          },
          plugins: {
            legend: { 
              display: true,
              position: 'top'
            },
            tooltip: {
              enabled: true,
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 },
              cornerRadius: 6,
              displayColors: true,
              callbacks: {
                label: function(context) {
                  return `${context.dataset.label}: ${Number(context.parsed.y).toLocaleString()}元`
                }
              }
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
      
      // 计算每月交易频率
      const monthlyTrades = {}
      this.currentAnalysisResults.tradeHistory?.forEach(trade => {
        const month = trade.date.slice(0, 7) // 改为年-月格式
        monthlyTrades[month] = (monthlyTrades[month] || 0) + 1
      })
      
      this.charts.frequency = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: Object.keys(monthlyTrades),
          datasets: [{
            label: '月交易次数',
            data: Object.values(monthlyTrades),
            backgroundColor: 'rgba(156, 39, 176, 0.6)',
            borderColor: '#9C27B0',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            mode: 'nearest',
            intersect: false,
            axis: 'x'
          },
          plugins: {
            legend: { display: false },
            tooltip: {
              enabled: true,
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 },
              cornerRadius: 6,
              displayColors: true,
              callbacks: {
                label: function(context) {
                  return `${context.dataset.label}: ${context.parsed.y}笔`
                }
              }
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
          interaction: {
            mode: 'point'
          },
          plugins: {
            legend: { 
              position: 'bottom',
              labels: { font: { size: 10 } }
            },
            tooltip: {
              enabled: true,
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 },
              cornerRadius: 6,
              displayColors: true,
              callbacks: {
                label: function(context) {
                  const total = context.dataset.data.reduce((a, b) => a + b, 0)
                  const percentage = ((context.parsed / total) * 100).toFixed(1)
                  return `${context.label}: ${context.parsed}次 (${percentage}%)`
                }
              }
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

    // 全屏相关方法已删除
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

/* 累计收益图表的特殊布局 */
.profit-chart-header {
  flex-direction: column;
  gap: 15px;
  align-items: stretch;
}

.chart-title-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-insights-header {
  display: flex;
  justify-content: space-around;
  padding: 10px 15px;
  background: rgba(248, 245, 242, 0.5);
  border-radius: 8px;
  border: 1px solid rgba(212, 184, 160, 0.2);
}

.chart-insights-header .insight-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
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

/* 全屏相关样式已删除 */

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
  

}
</style>