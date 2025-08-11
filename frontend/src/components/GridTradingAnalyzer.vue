<template>
  <div class="grid-trading-analyzer">
    <!-- 页面标题和标签选择区域 -->
    <div class="header-section">
      <h1 class="page-title">交易算法分析系统</h1>
      <div class="algorithm-tabs">
        <button 
          class="tab-button active" 
          @click="selectAlgorithm('grid')"
        >
          网格交易
        </button>
        <button 
          class="tab-button disabled" 
          disabled
        >
          ETF交易 (敬请期待)
        </button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧参数输入区域 -->
      <div class="input-section">
        <div class="section-card">
          <h3 class="section-title">数据上传</h3>
          <div class="upload-area">
            <input 
              type="file" 
              id="csvFile" 
              accept=".csv" 
              @change="handleFileUpload"
              class="file-input"
            >
            <label for="csvFile" class="file-upload-label">
              <div class="upload-icon">📁</div>
              <div class="upload-text">
                {{ fileName || '点击上传CSV文件' }}
              </div>
            </label>
          </div>
        </div>

        <div class="section-card">
          <h3 class="section-title">网格交易参数</h3>
          <div class="parameter-form">
            <div class="form-group">
              <label>初始资金 (元)</label>
              <input 
                v-model.number="parameters.initialCapital" 
                type="number" 
                min="1000"
                step="1000"
                class="form-input"
              >
            </div>
            
            <div class="form-group">
              <label>网格层数</label>
              <input 
                v-model.number="parameters.gridLevels" 
                type="number" 
                min="5" 
                max="50"
                class="form-input"
              >
            </div>
            
            <div class="form-group">
              <label>网格密度 (%)</label>
              <input 
                v-model.number="parameters.gridDensity" 
                type="number" 
                min="1" 
                max="10"
                step="0.5"
                class="form-input"
              >
            </div>
            
            <div class="form-group">
              <label>交易手续费率 (%)</label>
              <input 
                v-model.number="parameters.feeRate" 
                type="number" 
                min="0" 
                max="1"
                step="0.01"
                class="form-input"
              >
            </div>
            
            <button 
              @click="runGridTrading" 
              class="analyze-button"
              :disabled="!csvData || isAnalyzing"
            >
              {{ isAnalyzing ? '分析中...' : '开始分析' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧结果展示区域 -->
      <div class="results-section">
        <div v-if="!analysisResults" class="empty-state">
          <div class="empty-icon">📊</div>
          <div class="empty-text">上传数据并设置参数后，点击"开始分析"查看结果</div>
        </div>
        
        <div v-else class="results-container">
          <!-- 关键指标卡片 -->
          <div class="metrics-grid">
            <div class="metric-card highlight">
              <div class="metric-label">年化收益率</div>
              <div class="metric-value">{{ analysisResults.annualReturn }}%</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">总收益</div>
              <div class="metric-value">{{ analysisResults.totalProfit }} 元</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">最大回撤</div>
              <div class="metric-value">{{ analysisResults.maxDrawdown }}%</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">交易次数</div>
              <div class="metric-value">{{ analysisResults.tradeCount }} 次</div>
            </div>
          </div>

          <!-- 图表展示区域 -->
          <div class="charts-container">
            <div class="chart-card">
              <h4 class="chart-title">
                累计收益曲线
                <button @click="showChartDetail('profit')" class="detail-button">查看详情</button>
              </h4>
              <canvas ref="profitChart" class="chart-canvas"></canvas>
            </div>
            
            <div class="chart-card">
              <h4 class="chart-title">
                价格与网格线
                <button @click="showChartDetail('grid')" class="detail-button">查看详情</button>
              </h4>
              <canvas ref="gridChart" class="chart-canvas"></canvas>
            </div>
            
            <div class="chart-card">
              <h4 class="chart-title">
                回撤分析
                <button @click="showChartDetail('drawdown')" class="detail-button">查看详情</button>
              </h4>
              <canvas ref="drawdownChart" class="chart-canvas"></canvas>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ detailTitle }}</h3>
          <button @click="closeDetailModal" class="close-button">×</button>
        </div>
        <div class="modal-body">
          <canvas ref="detailChart" class="detail-chart-canvas"></canvas>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

export default {
  name: 'GridTradingAnalyzer',
  data() {
    return {
      fileName: '',
      csvData: null,
      isAnalyzing: false,
      analysisResults: null,
      showDetailModal: false,
      detailTitle: '',
      currentDetailType: '',
      parameters: {
        initialCapital: 100000,
        gridLevels: 20,
        gridDensity: 2.0,
        feeRate: 0.1
      },
      charts: {
        profit: null,
        grid: null,
        drawdown: null,
        detail: null
      }
    }
  },
  methods: {
    selectAlgorithm(type) {
      console.log('选择算法:', type)
    },
    
    handleFileUpload(event) {
      const file = event.target.files[0]
      if (file) {
        this.fileName = file.name
        const reader = new FileReader()
        reader.onload = (e) => {
          this.parseCSV(e.target.result)
        }
        reader.readAsText(file, 'utf-8')
      }
    },
    
    parseCSV(content) {
      try {
        const lines = content.split('\n').filter(line => line.trim())
        const headers = lines[0].split(',').map(h => h.replace(/"/g, '').trim())
        
        const data = []
        for (let i = 1; i < lines.length; i++) {
          const values = lines[i].split(',').map(v => v.replace(/"/g, '').trim())
          if (values.length === headers.length) {
            const row = {}
            headers.forEach((header, index) => {
              row[header] = values[index]
            })
            data.push(row)
          }
        }
        
        this.csvData = data
        console.log('CSV数据解析完成:', data.length, '条记录')
      } catch (error) {
        console.error('CSV解析错误:', error)
        alert('CSV文件解析失败，请检查文件格式')
      }
    },
    
    async runGridTrading() {
      if (!this.csvData) {
        alert('请先上传CSV文件')
        return
      }
      
      this.isAnalyzing = true
      
      try {
        // 模拟分析过程
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // 执行网格交易算法
        const results = this.calculateGridTrading()
        this.analysisResults = results
        
        // 渲染图表
        this.$nextTick(() => {
          this.renderCharts()
        })
        
      } catch (error) {
        console.error('分析错误:', error)
        alert('分析过程中出现错误')
      } finally {
        this.isAnalyzing = false
      }
    },
    
    calculateGridTrading() {
      const { initialCapital, gridLevels, gridDensity, feeRate } = this.parameters
      
      // 获取价格数据
      const prices = this.csvData.map(row => parseFloat(row['收盘']) || 0).reverse()
      const dates = this.csvData.map(row => row['日期']).reverse()
      
      // 计算价格范围
      const maxPrice = Math.max(...prices)
      const minPrice = Math.min(...prices)
      const priceRange = maxPrice - minPrice
      
      // 生成网格线
      const gridStep = (priceRange * gridDensity / 100) / gridLevels
      const gridLines = []
      for (let i = 0; i <= gridLevels; i++) {
        gridLines.push(minPrice + i * gridStep)
      }
      
      // 模拟交易
      let capital = initialCapital
      let position = 0
      let totalProfit = 0
      let tradeCount = 0
      const profitHistory = []
      const drawdownHistory = []
      let maxCapital = initialCapital
      
      for (let i = 1; i < prices.length; i++) {
        const currentPrice = prices[i]
        const prevPrice = prices[i - 1]
        
        // 检查是否触发网格交易
        for (let j = 0; j < gridLines.length - 1; j++) {
          const lowerGrid = gridLines[j]
          const upperGrid = gridLines[j + 1]
          
          // 价格下穿买入
          if (prevPrice > lowerGrid && currentPrice <= lowerGrid && capital > 0) {
            const buyAmount = capital * 0.1 // 每次买入10%资金
            const shares = buyAmount / currentPrice
            const fee = buyAmount * feeRate / 100
            
            capital -= (buyAmount + fee)
            position += shares
            tradeCount++
          }
          
          // 价格上穿卖出
          if (prevPrice < upperGrid && currentPrice >= upperGrid && position > 0) {
            const sellShares = position * 0.2 // 每次卖出20%持仓
            const sellAmount = sellShares * currentPrice
            const fee = sellAmount * feeRate / 100
            
            capital += (sellAmount - fee)
            position -= sellShares
            tradeCount++
          }
        }
        
        // 计算当前总资产
        const currentValue = capital + position * currentPrice
        const profit = currentValue - initialCapital
        
        profitHistory.push({
          date: dates[i],
          profit: profit,
          totalValue: currentValue
        })
        
        // 计算回撤
        if (currentValue > maxCapital) {
          maxCapital = currentValue
        }
        const drawdown = (maxCapital - currentValue) / maxCapital * 100
        drawdownHistory.push({
          date: dates[i],
          drawdown: drawdown
        })
        
        totalProfit = profit
      }
      
      // 计算年化收益率
      const days = prices.length
      const years = days / 365
      const annualReturn = ((totalProfit + initialCapital) / initialCapital - 1) / years * 100
      
      // 计算最大回撤
      const maxDrawdown = Math.max(...drawdownHistory.map(d => d.drawdown))
      
      return {
        annualReturn: annualReturn.toFixed(2),
        totalProfit: totalProfit.toFixed(2),
        maxDrawdown: maxDrawdown.toFixed(2),
        tradeCount: tradeCount,
        profitHistory: profitHistory,
        drawdownHistory: drawdownHistory,
        gridLines: gridLines,
        prices: prices,
        dates: dates
      }
    },
    
    renderCharts() {
      this.renderProfitChart()
      this.renderGridChart()
      this.renderDrawdownChart()
    },
    
    renderProfitChart() {
      const ctx = this.$refs.profitChart.getContext('2d')
      
      if (this.charts.profit) {
        this.charts.profit.destroy()
      }
      
      this.charts.profit = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.analysisResults.profitHistory.map(p => p.date.slice(5)),
          datasets: [{
            label: '累计收益',
            data: this.analysisResults.profitHistory.map(p => p.profit),
            borderColor: '#4CAF50',
            backgroundColor: 'rgba(76, 175, 80, 0.1)',
            fill: true,
            tension: 0.4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: '#f0f0f0'
              }
            },
            x: {
              grid: {
                color: '#f0f0f0'
              }
            }
          }
        }
      })
    },
    
    renderGridChart() {
      const ctx = this.$refs.gridChart.getContext('2d')
      
      if (this.charts.grid) {
        this.charts.grid.destroy()
      }
      
      const datasets = [{
        label: '股价',
        data: this.analysisResults.prices,
        borderColor: '#2196F3',
        backgroundColor: 'transparent',
        borderWidth: 2,
        pointRadius: 0
      }]
      
      // 添加网格线
      this.analysisResults.gridLines.forEach((line, index) => {
        datasets.push({
          label: `网格线 ${index + 1}`,
          data: new Array(this.analysisResults.prices.length).fill(line),
          borderColor: '#FF9800',
          backgroundColor: 'transparent',
          borderWidth: 1,
          borderDash: [5, 5],
          pointRadius: 0
        })
      })
      
      this.charts.grid = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.analysisResults.dates.map(d => d.slice(5)),
          datasets: datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              grid: {
                color: '#f0f0f0'
              }
            },
            x: {
              grid: {
                color: '#f0f0f0'
              }
            }
          }
        }
      })
    },
    
    renderDrawdownChart() {
      const ctx = this.$refs.drawdownChart.getContext('2d')
      
      if (this.charts.drawdown) {
        this.charts.drawdown.destroy()
      }
      
      this.charts.drawdown = new Chart(ctx, {
        type: 'area',
        data: {
          labels: this.analysisResults.drawdownHistory.map(d => d.date.slice(5)),
          datasets: [{
            label: '回撤',
            data: this.analysisResults.drawdownHistory.map(d => -d.drawdown),
            borderColor: '#F44336',
            backgroundColor: 'rgba(244, 67, 54, 0.1)',
            fill: true,
            tension: 0.4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              max: 0,
              grid: {
                color: '#f0f0f0'
              }
            },
            x: {
              grid: {
                color: '#f0f0f0'
              }
            }
          }
        }
      })
    },
    
    showChartDetail(type) {
      this.currentDetailType = type
      this.showDetailModal = true
      
      const titles = {
        profit: '累计收益曲线详情',
        grid: '价格与网格线详情',
        drawdown: '回撤分析详情'
      }
      this.detailTitle = titles[type]
      
      this.$nextTick(() => {
        this.renderDetailChart(type)
      })
    },
    
    renderDetailChart(type) {
      const ctx = this.$refs.detailChart.getContext('2d')
      
      if (this.charts.detail) {
        this.charts.detail.destroy()
      }
      
      let chartConfig = {}
      
      switch (type) {
        case 'profit':
          chartConfig = {
            type: 'line',
            data: {
              labels: this.analysisResults.profitHistory.map(p => p.date),
              datasets: [{
                label: '累计收益 (元)',
                data: this.analysisResults.profitHistory.map(p => p.profit),
                borderColor: '#4CAF50',
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
                fill: true,
                tension: 0.4
              }, {
                label: '总资产 (元)',
                data: this.analysisResults.profitHistory.map(p => p.totalValue),
                borderColor: '#2196F3',
                backgroundColor: 'transparent',
                borderWidth: 2
              }]
            }
          }
          break
        case 'grid':
          chartConfig = {
            type: 'line',
            data: {
              labels: this.analysisResults.dates,
              datasets: [{
                label: '股价',
                data: this.analysisResults.prices,
                borderColor: '#2196F3',
                backgroundColor: 'transparent',
                borderWidth: 2,
                pointRadius: 1
              }]
            }
          }
          // 添加网格线
          this.analysisResults.gridLines.forEach((line, index) => {
            chartConfig.data.datasets.push({
              label: `网格线 ${(line).toFixed(2)}`,
              data: new Array(this.analysisResults.prices.length).fill(line),
              borderColor: index % 2 === 0 ? '#FF9800' : '#E91E63',
              backgroundColor: 'transparent',
              borderWidth: 1,
              borderDash: [3, 3],
              pointRadius: 0
            })
          })
          break
        case 'drawdown':
          chartConfig = {
            type: 'line',
            data: {
              labels: this.analysisResults.drawdownHistory.map(d => d.date),
              datasets: [{
                label: '回撤 (%)',
                data: this.analysisResults.drawdownHistory.map(d => -d.drawdown),
                borderColor: '#F44336',
                backgroundColor: 'rgba(244, 67, 54, 0.1)',
                fill: true,
                tension: 0.4
              }]
            }
          }
          break
      }
      
      chartConfig.options = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top'
          }
        },
        scales: {
          y: {
            grid: {
              color: '#f0f0f0'
            }
          },
          x: {
            grid: {
              color: '#f0f0f0'
            }
          }
        }
      }
      
      this.charts.detail = new Chart(ctx, chartConfig)
    },
    
    closeDetailModal() {
      this.showDetailModal = false
      if (this.charts.detail) {
        this.charts.detail.destroy()
        this.charts.detail = null
      }
    }
  },
  
  beforeUnmount() {
    // 清理图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.destroy()
      }
    })
  }
}
</script>

<style scoped>
.grid-trading-analyzer {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header-section {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  color: white;
  font-size: 2.5em;
  margin-bottom: 20px;
  font-weight: 300;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.algorithm-tabs {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.tab-button {
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  background: white;
  color: #667eea;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.tab-button.active {
  background: #4CAF50;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.tab-button.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.main-content {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.input-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
}

.section-title {
  color: #333;
  margin-bottom: 20px;
  font-size: 1.3em;
  font-weight: 600;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #4CAF50;
  background: rgba(76, 175, 80, 0.05);
}

.file-input {
  display: none;
}

.file-upload-label {
  cursor: pointer;
  display: block;
}

.upload-icon {
  font-size: 2em;
  margin-bottom: 10px;
}

.upload-text {
  color: #666;
  font-weight: 500;
}

.parameter-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #555;
  font-weight: 600;
  font-size: 0.9em;
}

.form-input {
  padding: 12px 15px;
  border: 2px solid #eee;
  border-radius: 8px;
  font-size: 1em;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.analyze-button {
  padding: 15px 25px;
  background: linear-gradient(45deg, #4CAF50, #45a049);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1.1em;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.analyze-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.analyze-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.results-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 4em;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 1.1em;
  line-height: 1.6;
}

.results-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.metric-card {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  transition: transform 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-3px);
}

.metric-card.highlight {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: white;
}

.metric-label {
  font-size: 0.9em;
  opacity: 0.8;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 1.8em;
  font-weight: 700;
}

.charts-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.chart-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.chart-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  color: #333;
  font-size: 1.1em;
  font-weight: 600;
}

.detail-button {
  background: #2196F3;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.8em;
  cursor: pointer;
  transition: background 0.3s ease;
}

.detail-button:hover {
  background: #1976D2;
}

.chart-canvas {
  width: 100% !important;
  height: 250px !important;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 1000px;
  max-height: 80%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.3em;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5em;
  cursor: pointer;
  color: #999;
  padding: 5px;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-button:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 25px;
  flex: 1;
}

.detail-chart-canvas {
  width: 100% !important;
  height: 400px !important;
}

@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .input-section {
    grid-row: 2;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2em;
  }
  
  .algorithm-tabs {
    flex-direction: column;
    align-items: center;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
}
</style>