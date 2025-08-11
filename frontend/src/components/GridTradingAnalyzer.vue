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

    <!-- 顶部配置区域 -->
    <div class="top-config-section">
      <div class="config-cards-row">
        <!-- 基础配置 -->
        <div class="config-card">
          <h4>基础配置</h4>
          <div class="config-row">
            <div class="config-item">
              <label>初始资金 (万元)</label>
              <input v-model.number="parameters.initialCapital" type="number" min="1" step="1" class="mini-input">
              </div>
            <div class="config-item">
              <label>底仓比例 (%)</label>
              <input v-model.number="parameters.basePositionRatio" type="number" min="0" max="50" step="5" class="mini-input">
            </div>
            <div class="config-item">
              <label>网格层数</label>
              <input v-model.number="parameters.gridLevels" type="number" min="5" max="50" class="mini-input">
            </div>
          </div>
        </div>

        <!-- 数据来源配置 -->
        <div class="config-card">
          <h4>数据来源</h4>
          <div class="config-row">
            <div class="config-item">
              <label>数据来源</label>
              <select v-model="parameters.dataSource" class="mini-input">
                <option value="upload">文件上传</option>
                <option value="project">项目文件</option>
              </select>
            </div>
            <div v-if="parameters.dataSource === 'upload'" class="config-item">
              <label>选择文件</label>
              <input type="file" accept=".csv" @change="handleFileUpload" class="mini-input file-input-mini">
            </div>
            <div v-if="parameters.dataSource === 'project'" class="config-item">
              <label>项目文件</label>
              <select v-model="parameters.projectFile" class="mini-input">
                <option value="">请选择文件</option>
                <option value="600585历史数据.csv">600585历史数据.csv</option>
                <option value="sample.csv">示例数据</option>
              </select>
            </div>
            <div class="config-item">
              <label>文件状态</label>
              <span class="file-status" :class="getFileStatusClass()">{{ getFileStatus() }}</span>
            </div>
          </div>
            </div>
            
        <!-- 建仓时间配置 -->
        <div class="config-card">
          <h4>建仓时间</h4>
          <div class="config-row">
            <div class="config-item">
              <label>建仓模式</label>
              <select v-model="parameters.basePositionMode" class="mini-input">
                <option value="days">数据开始后第N天</option>
                <option value="date">指定具体日期</option>
              </select>
            </div>
            <div v-if="parameters.basePositionMode === 'days'" class="config-item">
              <label>第几天建仓</label>
              <input v-model.number="parameters.basePositionDays" type="number" min="1" max="30" class="mini-input">
            </div>
            <div v-if="parameters.basePositionMode === 'date'" class="config-item">
              <label>建仓日期</label>
              <input v-model="parameters.basePositionDate" type="date" class="mini-input" :min="getMinDate()" :max="getMaxDate()">
            </div>
            <div class="config-item">
              <label>当前建仓日期</label>
              <span class="date-display">{{ getCurrentBasePositionDate() }}</span>
            </div>
          </div>
            </div>
            
        <!-- 交易配置 -->
        <div class="config-card">
          <h4>交易配置</h4>
          <div class="config-row">
            <div class="config-item">
              <label>单次交易 (%)</label>
              <input v-model.number="parameters.singleTradeRatio" type="number" min="1" max="20" class="mini-input">
            </div>
            <div class="config-item">
              <label>手续费 (%)</label>
              <input v-model.number="parameters.feeRate" type="number" min="0" max="1" step="0.01" class="mini-input">
            </div>
          </div>
        </div>
            
        <!-- 网格配置 -->
        <div class="config-card">
          <h4>网格配置</h4>
          <div class="config-row">
            <div class="config-item">
              <label>网格间距模式</label>
              <select v-model="parameters.gridSpacingMode" class="mini-input">
                <option value="percentage">等比间距 (%)</option>
                <option value="fixed">等差间距 (固定金额)</option>
              </select>
            </div>
            <div class="config-item">
              <label>{{ parameters.gridSpacingMode === 'percentage' ? '网格间距 (%)' : '网格间距 (元)' }}</label>
              <input 
                v-model.number="parameters.gridSpacing" 
                type="number" 
                :min="parameters.gridSpacingMode === 'percentage' ? 1 : 0.1" 
                :max="parameters.gridSpacingMode === 'percentage' ? 20 : 50" 
                :step="parameters.gridSpacingMode === 'percentage' ? 0.5 : 0.1" 
                class="mini-input">
            </div>
            <div class="config-item">
              <label>网格范围 (%)</label>
              <input v-model.number="parameters.gridRange" type="number" min="10" max="80" step="5" class="mini-input">
              <small class="param-hint">以建仓价格为中心的上下波动范围</small>
            </div>
          </div>
            </div>
            
        <!-- 风险控制 -->
        <div class="config-card">
          <h4>风险控制</h4>
          <div class="config-row">
            <div class="config-item">
              <label>止损比例 (%)</label>
              <input v-model.number="parameters.stopLossRatio" type="number" min="0" max="30" class="mini-input">
            </div>
            <div class="config-item">
              <label>最大持仓 (%)</label>
              <input v-model.number="parameters.maxPositionRatio" type="number" min="50" max="90" step="5" class="mini-input">
            </div>
            <div class="config-item">
              <label>熊市保护</label>
              <input v-model="parameters.bearMarketProtection" type="checkbox" class="mini-checkbox">
            </div>
          </div>
            </div>
            
        <!-- 操作按钮 -->
        <div class="config-card action-card">
          <button @click="loadDataAndAnalyze" class="analyze-button" :disabled="isAnalyzing">
              {{ isAnalyzing ? '分析中...' : '开始分析' }}
            </button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧高级参数 -->
      <div class="advanced-config">
        <div class="section-card compact">
          <h3 class="section-title">高级配置</h3>

          <div class="advanced-form">
            <div class="form-group compact">
              <label>网格预览信息</label>
              <div class="grid-info">
                <span class="info-item">建仓价格: {{ getBasePositionPricePreview() }}</span>
                <span class="info-item">网格数量: {{ getGridCountPreview() }}条</span>
                <span class="info-item">价格范围: {{ getGridRangePreview() }}</span>
              </div>
            </div>
            
            <div class="form-group compact">
              <label>止盈比例 (%)</label>
              <input v-model.number="parameters.takeProfitRatio" type="number" min="0" max="100" step="5" class="form-input">
            </div>
            
            <div class="form-group compact">
              <label>最大回撤限制 (%)</label>
              <input v-model.number="parameters.maxDrawdownLimit" type="number" min="0" max="50" step="5" class="form-input">
            </div>
            
            <div class="checkbox-group compact">
              <input v-model="parameters.dynamicGrid" type="checkbox" id="dynamicGrid">
              <label for="dynamicGrid">动态网格调整</label>
            </div>
            
            <div v-if="parameters.dynamicGrid" class="form-group compact">
              <label>网格重置阈值 (%)</label>
              <input v-model.number="parameters.gridResetThreshold" type="number" min="5" max="30" step="5" class="form-input">
            </div>
            
            <div v-if="parameters.bearMarketProtection" class="form-group compact">
              <label>连续下跌天数阈值</label>
              <input v-model.number="parameters.bearMarketDays" type="number" min="3" max="15" class="form-input">
            </div>
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
            <div class="metric-card">
              <div class="metric-label">底仓建仓价</div>
              <div class="metric-value">¥{{ analysisResults.basePositionPrice ? analysisResults.basePositionPrice.toFixed(2) : '0.00' }}</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">期间最高价</div>
              <div class="metric-value">¥{{ analysisResults.periodHighPrice ? analysisResults.periodHighPrice.toFixed(2) : '0.00' }}</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">期间最低价</div>
              <div class="metric-value">¥{{ analysisResults.periodLowPrice ? analysisResults.periodLowPrice.toFixed(2) : '0.00' }}</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">夏普比率</div>
              <div class="metric-value">{{ analysisResults.sharpeRatio }}</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">网格重置次数</div>
              <div class="metric-value">{{ analysisResults.gridResetCount }} 次</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">最终持仓</div>
              <div class="metric-value">{{ analysisResults.finalPosition ? analysisResults.finalPosition.toFixed(0) : '0' }} 股</div>
            </div>
          </div>

          <!-- 详细统计信息 -->
          <div class="detailed-stats">
            <h4>详细统计</h4>
            <div class="stats-grid">
              <div class="stat-item">
                <span class="stat-label">最终现金：</span>
                <span class="stat-value">{{ analysisResults.finalCash ? analysisResults.finalCash.toFixed(2) : '0' }} 元</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">最终股票价值：</span>
                <span class="stat-value">{{ analysisResults.finalPosition && analysisResults.prices ? (analysisResults.finalPosition * analysisResults.prices[analysisResults.prices.length - 1]).toFixed(2) : '0' }} 元</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">资金利用率：</span>
                <span class="stat-value">{{ calculateCapitalUtilization() }}%</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">平均持仓周期：</span>
                <span class="stat-value">{{ calculateAverageHoldingPeriod() }} 天</span>
              </div>
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
            
            <div class="chart-card">
              <h4 class="chart-title">
                资金分布图
                <button @click="showChartDetail('allocation')" class="detail-button">查看详情</button>
              </h4>
              <canvas ref="allocationChart" class="chart-canvas"></canvas>
            </div>
          </div>
          
          <!-- 交易记录表格 -->
          <div class="trades-table-container">
            <h4 class="table-title">
              交易记录明细
              <span class="trade-stats">
                总交易: {{ analysisResults.tradeCount }} 次 | 
                买入: {{ getBuyTradesCount() }} 次 | 
                卖出: {{ getSellTradesCount() }} 次
              </span>
            </h4>
            <div class="table-wrapper">
              <table class="trades-table">
                <thead>
                  <tr>
                    <th>序号</th>
                    <th>日期</th>
                    <th>交易类型</th>
                    <th>价格</th>
                    <th>数量</th>
                    <th>金额</th>
                    <th>剩余现金</th>
                    <th>持仓数量</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(trade, index) in getDisplayTrades()" :key="index" :class="getTradeRowClass(trade.type)">
                    <td>{{ index + 1 }}</td>
                    <td>{{ trade.date }}</td>
                    <td>
                      <span :class="getTradeTypeClass(trade.type)">
                        {{ getTradeTypeText(trade.type) }}
                      </span>
                    </td>
                    <td>¥{{ trade.price.toFixed(2) }}</td>
                    <td>{{ trade.amount > 0 ? trade.amount.toFixed(0) : '-' }} 股</td>
                    <td>{{ calculateTradeAmount(trade).toFixed(2) }} 元</td>
                    <td>¥{{ trade.capital.toFixed(2) }}</td>
                    <td>{{ trade.position.toFixed(0) }} 股</td>
                  </tr>
                </tbody>
              </table>
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
      csvData: null,
      fileName: '',
      isAnalyzing: false,
      analysisResults: null,
      showDetailModal: false,
      detailTitle: '',
      currentDetailType: '',
      parameters: {
        // 数据来源配置
        dataSource: 'upload', // 'upload' 或 'project'
        projectFile: '', // 项目文件名
        
        // 基础配置
        initialCapital: 10, // 改为万元单位
        basePositionRatio: 20,
        gridLevels: 20,
        
        // 建仓时间配置
        basePositionMode: 'days', // 'days' 或 'date'
        basePositionDays: 5, // 数据开始后第几天建仓
        basePositionDate: '', // 指定的建仓日期
        
        // 交易配置
        singleTradeRatio: 10,
        feeRate: 0.1,
        
        // 网格配置
        gridSpacingMode: 'percentage', // 'percentage' 或 'fixed'
        gridSpacing: 5, // 等比间距默认5%，等差间距默认5元
        gridRange: 40, // 网格范围默认上下40%
        
        // 风险控制
        stopLossRatio: 0,
        maxPositionRatio: 70,
        bearMarketProtection: false,
        
        // 高级配置
        dynamicGrid: false,
        gridResetThreshold: 15,
        takeProfitRatio: 0,
        maxDrawdownLimit: 0,
        bearMarketDays: 7
      },
      charts: {
        profit: null,
        grid: null,
        drawdown: null,
        allocation: null,
        detail: null
      }
    }
  },
  watch: {
    // 监听数据源变化
    'parameters.dataSource'() {
      // 切换数据源时清空当前数据
      this.csvData = null
      this.fileName = ''
      this.analysisResults = null
    },
    'parameters.projectFile'() {
      // 选择项目文件时清空当前数据
      if (this.parameters.dataSource === 'project') {
        this.csvData = null
        this.fileName = ''
        this.analysisResults = null
      }
    },
    // 监听建仓模式和参数变化
    'parameters.basePositionMode'() {
      this.$forceUpdate() // 强制更新显示
    },
    'parameters.basePositionDays'() {
      this.$forceUpdate()
    },
    'parameters.basePositionDate'() {
      this.$forceUpdate()
    }
  },
  methods: {
    selectAlgorithm(type) {
      console.log('选择算法:', type)
    },
    
    async loadDataAndAnalyze() {
      if (this.isAnalyzing) return
      
      // 验证数据来源
      if (this.parameters.dataSource === 'upload' && !this.csvData) {
        alert('请先上传CSV文件')
        return
      }
      
      if (this.parameters.dataSource === 'project' && !this.parameters.projectFile) {
        alert('请选择项目文件')
        return
      }
      
      this.isAnalyzing = true
      
      try {
        // 根据数据来源加载数据
        if (this.parameters.dataSource === 'project' && !this.csvData) {
          await this.loadProjectFile()
        }
        
        if (this.csvData) {
          // 验证建仓时间
          const validationError = this.validateBasePositionDate()
          if (validationError) {
            alert(validationError)
            return
          }
          
          const results = this.calculateGridTrading()
          this.analysisResults = results
          
          // 渲染图表 - 确保DOM完全更新后再渲染
          this.$nextTick(() => {
            // 增加延迟确保DOM元素完全准备就绪
            setTimeout(() => {
              this.renderChartsWithRetry()
            }, 200)
          })
        }
        
      } catch (error) {
        console.error('加载和分析错误:', error)
        alert('数据加载或分析过程中出现错误')
      } finally {
        this.isAnalyzing = false
      }
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
    
    async loadProjectFile() {
      return new Promise((resolve, reject) => {
        const fileName = this.parameters.projectFile
        
        if (fileName === 'sample.csv') {
          // 生成示例数据
          const sampleData = this.generateSampleData()
          this.parseCSV(sampleData)
          this.fileName = fileName
          resolve()
        } else if (fileName === '600585历史数据.csv') {
          // 尝试从public目录加载文件
          fetch(`/${fileName}`)
            .then(response => {
              if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
              }
              return response.text()
            })
            .then(csvContent => {
              this.parseCSV(csvContent)
              this.fileName = fileName
              resolve()
            })
            .catch(error => {
              console.error('加载项目文件失败:', error)
              // 如果加载失败，使用示例数据
              alert(`无法加载项目文件 ${fileName}，将使用示例数据`)
              const sampleData = this.generateSampleData()
              this.parseCSV(sampleData)
              this.fileName = 'sample.csv'
              resolve()
            })
        } else {
          reject(new Error('未知的项目文件'))
        }
      })
    },
    

    
    generateSampleData() {
      // 生成示例数据（基于600585历史数据格式）
      let csvContent = '"日期","收盘","开盘","高","低","交易量","涨跌幅"\n'
      const startDate = new Date('2023-01-01')
      let basePrice = 25.0
      
      for (let i = 0; i < 250; i++) {
        const currentDate = new Date(startDate)
        currentDate.setDate(startDate.getDate() + i)
        
        // 模拟价格波动
        const change = (Math.random() - 0.5) * 0.08
        basePrice = Math.max(15, Math.min(35, basePrice * (1 + change)))
        
        const open = basePrice * (0.98 + Math.random() * 0.04)
        const close = basePrice * (0.98 + Math.random() * 0.04)
        const high = Math.max(open, close) * (1 + Math.random() * 0.03)
        const low = Math.min(open, close) * (1 - Math.random() * 0.03)
        const volume = (10 + Math.random() * 50).toFixed(2) + 'M'
        const changePercent = ((close - open) / open * 100).toFixed(2) + '%'
        
        // 格式化日期为YYYY-MM-DD
        const dateStr = currentDate.toISOString().slice(0, 10)
        csvContent += `"${dateStr}","${close.toFixed(2)}","${open.toFixed(2)}","${high.toFixed(2)}","${low.toFixed(2)}","${volume}","${changePercent}"\n`
      }
      
      return csvContent
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
    

    
    calculateGridTrading() {
      const params = this.parameters
      
      // 获取价格数据
      const prices = this.csvData.map(row => parseFloat(row['收盘']) || 0).reverse()
      const dates = this.csvData.map(row => row['日期']).reverse()
      // const volumes = this.csvData.map(row => parseFloat(row['交易量'].replace('M', '')) || 0).reverse()
      
      // 计算交易时间范围
      const basePositionIndex = this.getBasePositionIndex()
      const startIndex = basePositionIndex // 从建仓时间开始交易
      const endIndex = prices.length - 5 // 提前5天结束
      
      
      // 获取建仓价格（用于生成网格）
      const basePositionPrice = startIndex < prices.length ? prices[startIndex] : prices[0]
      
      // 生成初始网格线（以建仓价格为基准）
      let gridLines = this.generateGridLines(
        basePositionPrice, 
        params.gridSpacingMode, 
        params.gridSpacing, 
        params.gridRange
      )
      
      // 交易状态变量
      const actualCapital = params.initialCapital * 10000 // 转换为实际金额（万元转元）
      let capital = actualCapital
      let position = 0
      let totalProfit = 0
      let tradeCount = 0
      let basePrice = 0
      let consecutiveDownDays = 0
      let lastPrice = 0
      let stopTradingFlag = false
      let gridResetCount = 0
      
      const profitHistory = []
      const drawdownHistory = []
      const tradeHistory = []
      let maxCapital = actualCapital
      
      for (let i = 0; i < prices.length; i++) {
        const currentPrice = prices[i]
        const currentDate = dates[i]
        
        // 建立底仓
        if (i === basePositionIndex && !stopTradingFlag) {
          const baseAmount = capital * params.basePositionRatio / 100
          const shares = baseAmount / currentPrice
          const fee = baseAmount * params.feeRate / 100
          
          capital -= (baseAmount + fee)
          position += shares
          basePrice = currentPrice
          tradeCount++
          
          tradeHistory.push({
            date: currentDate,
            type: '建仓',
            price: currentPrice,
            amount: shares,
            capital: capital,
            position: position
          })
        }
        
        // 开始网格交易
        if (i >= startIndex && i < endIndex && !stopTradingFlag) {
          // 计算连续下跌天数
          if (i > 0) {
            if (currentPrice < lastPrice) {
              consecutiveDownDays++
            } else {
              consecutiveDownDays = 0
            }
          }
          
          // 熊市保护
          const bearMarketActive = params.bearMarketProtection && 
                                  consecutiveDownDays >= params.bearMarketDays
          
          // 动态网格重置
          if (params.dynamicGrid && 
              (currentPrice < gridLines[0] * (1 - params.gridResetThreshold / 100) ||
               currentPrice > gridLines[gridLines.length - 1] * (1 + params.gridResetThreshold / 100))) {
            
            // 使用当前价格作为新的基准价格重新生成网格
            gridLines = this.generateGridLines(
              currentPrice, 
              params.gridSpacingMode, 
              params.gridSpacing, 
              params.gridRange
            )
            gridResetCount++
          }
          
          // 网格交易逻辑
        for (let j = 0; j < gridLines.length - 1; j++) {
          const lowerGrid = gridLines[j]
          const upperGrid = gridLines[j + 1]
          
          // 价格下穿买入
            if (i > 0 && lastPrice > lowerGrid && currentPrice <= lowerGrid) {
              const availableCash = capital
              const positionValue = position * currentPrice
              const totalValue = availableCash + positionValue
              const currentPositionRatio = positionValue / totalValue * 100
              
              if (availableCash > 0 && 
                  currentPositionRatio < params.maxPositionRatio &&
                  (!bearMarketActive || Math.random() > 0.7)) { // 熊市时70%概率跳过买入
                
                let buyAmount = availableCash * params.singleTradeRatio / 100
                buyAmount = Math.min(buyAmount, availableCash * 0.9) // 保留10%现金
                
            const shares = buyAmount / currentPrice
                const fee = buyAmount * params.feeRate / 100
            
            capital -= (buyAmount + fee)
            position += shares
            tradeCount++
                
                tradeHistory.push({
                  date: currentDate,
                  type: '买入',
                  price: currentPrice,
                  amount: shares,
                  capital: capital,
                  position: position
                })
              }
          }
          
          // 价格上穿卖出
            if (i > 0 && lastPrice < upperGrid && currentPrice >= upperGrid && position > 0) {
              const sellRatio = Math.min(params.singleTradeRatio / 100, 0.3) // 最多卖出30%
              const sellShares = position * sellRatio
            const sellAmount = sellShares * currentPrice
              const fee = sellAmount * params.feeRate / 100
            
            capital += (sellAmount - fee)
            position -= sellShares
            tradeCount++
              
              tradeHistory.push({
                date: currentDate,
                type: '卖出',
                price: currentPrice,
                amount: sellShares,
                capital: capital,
                position: position
              })
            }
          }
        }
        
        // 计算当前总资产
        const currentValue = capital + position * currentPrice
        const profit = currentValue - actualCapital
        const profitRatio = profit / actualCapital * 100
        
        // 止损检查
        if (params.stopLossRatio > 0 && basePrice > 0) {
          const lossFromBase = (basePrice - currentPrice) / basePrice * 100
          if (lossFromBase >= params.stopLossRatio && !stopTradingFlag) {
            stopTradingFlag = true
            tradeHistory.push({
              date: currentDate,
              type: '止损',
              price: currentPrice,
              amount: 0,
              capital: capital,
              position: position
            })
          }
        }
        
        // 止盈检查
        if (params.takeProfitRatio > 0 && profitRatio >= params.takeProfitRatio && !stopTradingFlag) {
          stopTradingFlag = true
          tradeHistory.push({
            date: currentDate,
            type: '止盈',
            price: currentPrice,
            amount: 0,
            capital: capital,
            position: position
          })
        }
        
        profitHistory.push({
          date: currentDate,
          profit: profit,
          totalValue: currentValue,
          profitRatio: profitRatio,
          positionValue: position * currentPrice,
          cashValue: capital
        })
        
        // 计算回撤
        if (currentValue > maxCapital) {
          maxCapital = currentValue
        }
        const drawdown = (maxCapital - currentValue) / maxCapital * 100
        drawdownHistory.push({
          date: currentDate,
          drawdown: drawdown
        })
        
        // 最大回撤限制检查
        if (params.maxDrawdownLimit > 0 && drawdown >= params.maxDrawdownLimit && !stopTradingFlag) {
          stopTradingFlag = true
          tradeHistory.push({
            date: currentDate,
            type: '最大回撤止损',
            price: currentPrice,
            amount: 0,
            capital: capital,
            position: position
          })
        }
        
        totalProfit = profit
        lastPrice = currentPrice
      }
      
      // 计算统计指标
      const days = endIndex - startIndex
      const years = days / 365
      const annualReturn = years > 0 ? ((totalProfit + actualCapital) / actualCapital - 1) / years * 100 : 0
      const maxDrawdown = Math.max(...drawdownHistory.map(d => d.drawdown))
      const winRate = this.calculateWinRate(tradeHistory)
      const sharpeRatio = this.calculateSharpeRatio(profitHistory)
      
      // 计算新增的价格指标
      const finalBasePositionPrice = startIndex < prices.length ? prices[startIndex] : 0 // 底仓建仓价格
      const tradingPeriodPrices = prices.slice(startIndex, endIndex) // 交易期间的价格
      const periodHighPrice = tradingPeriodPrices.length > 0 ? Math.max(...tradingPeriodPrices) : 0 // 期间最高价
      const periodLowPrice = tradingPeriodPrices.length > 0 ? Math.min(...tradingPeriodPrices) : 0 // 期间最低价
      
      return {
        annualReturn: annualReturn.toFixed(2),
        totalProfit: totalProfit.toFixed(2),
        maxDrawdown: maxDrawdown.toFixed(2),
        tradeCount: tradeCount,
        winRate: winRate.toFixed(2),
        sharpeRatio: sharpeRatio.toFixed(2),
        gridResetCount: gridResetCount,
        profitHistory: profitHistory,
        drawdownHistory: drawdownHistory,
        tradeHistory: tradeHistory,
        gridLines: gridLines,
        prices: prices,
        dates: dates,
        finalPosition: position,
        finalCash: capital,
        basePositionPrice: finalBasePositionPrice,
        periodHighPrice: periodHighPrice,
        periodLowPrice: periodLowPrice
      }
    },
    
    generateGridLines(basePrice, spacingMode, spacing, range) {
      const gridLines = []
      
      // 计算网格范围（以建仓价格为中心）
      const rangeRatio = range / 100
      const minPrice = basePrice * (1 - rangeRatio)
      const maxPrice = basePrice * (1 + rangeRatio)
      
      if (spacingMode === 'percentage') {
        // 等比间距：每个网格间距为固定百分比
        const spacingRatio = spacing / 100
        
        // 向下生成网格线（从建仓价格向下）
        let currentPrice = basePrice
        while (currentPrice >= minPrice) {
          gridLines.unshift(currentPrice) // 插入到数组开头
          currentPrice = currentPrice * (1 - spacingRatio)
        }
        
        // 向上生成网格线（从建仓价格向上，跳过建仓价格本身）
        currentPrice = basePrice * (1 + spacingRatio)
        while (currentPrice <= maxPrice) {
          gridLines.push(currentPrice)
          currentPrice = currentPrice * (1 + spacingRatio)
        }
        
      } else {
        // 等差间距：每个网格间距为固定金额
        const fixedSpacing = spacing
        
        // 向下生成网格线（从建仓价格向下）
        let currentPrice = basePrice
        while (currentPrice >= minPrice) {
          gridLines.unshift(currentPrice) // 插入到数组开头
          currentPrice = currentPrice - fixedSpacing
        }
        
        // 向上生成网格线（从建仓价格向上，跳过建仓价格本身）
        currentPrice = basePrice + fixedSpacing
        while (currentPrice <= maxPrice) {
          gridLines.push(currentPrice)
          currentPrice = currentPrice + fixedSpacing
        }
      }
      
      // 确保网格线按价格升序排列
      gridLines.sort((a, b) => a - b)
      
      return gridLines
    },
    
    calculateWinRate(tradeHistory) {
      const trades = tradeHistory.filter(t => t.type === '卖出')
      if (trades.length === 0) return 0
      
      let winCount = 0
      let lastBuyPrice = 0
      
      for (const trade of tradeHistory) {
        if (trade.type === '买入' || trade.type === '建仓') {
          lastBuyPrice = trade.price
        } else if (trade.type === '卖出' && lastBuyPrice > 0) {
          if (trade.price > lastBuyPrice) {
            winCount++
          }
        }
      }
      
      return (winCount / trades.length) * 100
    },
    
    calculateSharpeRatio(profitHistory) {
      if (profitHistory.length < 2) return 0
      
      const returns = []
      for (let i = 1; i < profitHistory.length; i++) {
        const dailyReturn = (profitHistory[i].totalValue - profitHistory[i-1].totalValue) / profitHistory[i-1].totalValue
        returns.push(dailyReturn)
      }
      
      const avgReturn = returns.reduce((sum, r) => sum + r, 0) / returns.length
      const variance = returns.reduce((sum, r) => sum + Math.pow(r - avgReturn, 2), 0) / returns.length
      const volatility = Math.sqrt(variance)
      
      return volatility > 0 ? (avgReturn / volatility) * Math.sqrt(252) : 0 // 年化夏普比率
    },
    
    calculateCapitalUtilization() {
      if (!this.analysisResults) return '0'
      
      const finalCash = this.analysisResults.finalCash || 0
      const initialCapital = this.parameters.initialCapital * 10000 // 转换为实际金额
      const utilization = ((initialCapital - finalCash) / initialCapital) * 100
      
      return Math.max(0, utilization).toFixed(1)
    },
    
    calculateAverageHoldingPeriod() {
      if (!this.analysisResults || !this.analysisResults.tradeHistory) return '0'
      
      const trades = this.analysisResults.tradeHistory
      const buyTrades = trades.filter(t => t.type === '买入' || t.type === '建仓')
      const sellTrades = trades.filter(t => t.type === '卖出')
      
      if (buyTrades.length === 0 || sellTrades.length === 0) return '0'
      
      let totalHoldingDays = 0
      let completedTrades = 0
      
      for (let i = 0; i < Math.min(buyTrades.length, sellTrades.length); i++) {
        const buyDate = new Date(buyTrades[i].date)
        const sellDate = new Date(sellTrades[i].date)
        if (sellDate > buyDate) {
          totalHoldingDays += (sellDate - buyDate) / (1000 * 60 * 60 * 24)
          completedTrades++
        }
      }
      
      return completedTrades > 0 ? (totalHoldingDays / completedTrades).toFixed(1) : '0'
    },
    
    // 表格相关方法
    getBuyTradesCount() {
      if (!this.analysisResults || !this.analysisResults.tradeHistory) return 0
      return this.analysisResults.tradeHistory.filter(t => t.type === '买入' || t.type === '建仓').length
    },
    
    getSellTradesCount() {
      if (!this.analysisResults || !this.analysisResults.tradeHistory) return 0
      return this.analysisResults.tradeHistory.filter(t => t.type === '卖出').length
    },
    
    getDisplayTrades() {
      if (!this.analysisResults || !this.analysisResults.tradeHistory) return []
      return this.analysisResults.tradeHistory.slice(0, 50) // 只显示前50条交易记录
    },
    
    getTradeRowClass(type) {
      return {
        'trade-buy': type === '买入' || type === '建仓',
        'trade-sell': type === '卖出',
        'trade-other': !['买入', '建仓', '卖出'].includes(type)
      }
    },
    
    getTradeTypeClass(type) {
      return {
        'trade-type-buy': type === '买入' || type === '建仓',
        'trade-type-sell': type === '卖出',
        'trade-type-other': !['买入', '建仓', '卖出'].includes(type)
      }
    },
    
    getTradeTypeText(type) {
      const typeMap = {
        '建仓': '🏗️ 建仓',
        '买入': '📈 买入',
        '卖出': '📉 卖出',
        '止损': '🛑 止损',
        '止盈': '💰 止盈',
        '最大回撤止损': '⚠️ 回撤止损'
      }
      return typeMap[type] || type
    },
    
    calculateTradeAmount(trade) {
      if (trade.amount && trade.amount > 0) {
        return trade.amount * trade.price
      }
      return 0
    },
    
    // 建仓时间相关方法
    getMinDate() {
      if (!this.csvData || this.csvData.length === 0) return ''
      // 获取数据中的最早日期
      const dates = this.csvData.map(row => row['日期']).sort()
      return dates[0]
    },
    
    getMaxDate() {
      if (!this.csvData || this.csvData.length === 0) return ''
      // 获取数据中的最晚日期
      const dates = this.csvData.map(row => row['日期']).sort()
      return dates[dates.length - 1]
    },
    
    getCurrentBasePositionDate() {
      if (!this.csvData || this.csvData.length === 0) return '数据未加载'
      
      if (this.parameters.basePositionMode === 'date') {
        if (this.parameters.basePositionDate) {
          // 验证日期是否在数据范围内
          const minDate = this.getMinDate()
          const maxDate = this.getMaxDate()
          const selectedDate = this.parameters.basePositionDate
          
          if (selectedDate >= minDate && selectedDate <= maxDate) {
            return selectedDate
          } else {
            return `❌ 超出数据范围 (${minDate} ~ ${maxDate})`
          }
        } else {
          return '请选择日期'
        }
      } else {
        // 按天数计算
        const dates = this.csvData.map(row => row['日期']).reverse().sort()
        const targetIndex = Math.min(this.parameters.basePositionDays - 1, dates.length - 1)
        return dates[targetIndex] || '数据不足'
      }
    },
    
    getBasePositionIndex() {
      if (!this.csvData) return 0
      
      const dates = this.csvData.map(row => row['日期']).reverse()
      
      if (this.parameters.basePositionMode === 'date') {
        if (!this.parameters.basePositionDate) return 0
        
        const targetDate = this.parameters.basePositionDate
        const index = dates.findIndex(date => date === targetDate)
        return index >= 0 ? index : 0
      } else {
        // 按天数计算
        return Math.min(this.parameters.basePositionDays - 1, dates.length - 1)
      }
    },
    
    validateBasePositionDate() {
      if (this.parameters.basePositionMode === 'date' && this.parameters.basePositionDate) {
        const minDate = this.getMinDate()
        const maxDate = this.getMaxDate()
        const selectedDate = this.parameters.basePositionDate
        
        if (selectedDate < minDate || selectedDate > maxDate) {
          return `建仓日期必须在数据范围内 (${minDate} ~ ${maxDate})`
        }
      }
      return null
    },
    
    // 文件状态相关方法
    getFileStatus() {
      if (this.parameters.dataSource === 'upload') {
        return this.csvData ? `✅ ${this.fileName}` : '❌ 未上传文件'
      } else {
        if (!this.parameters.projectFile) {
          return '❌ 未选择文件'
        }
        return this.csvData ? `✅ ${this.fileName}` : '⏳ 待加载'
      }
    },
    
    getFileStatusClass() {
      const hasData = this.csvData && 
        ((this.parameters.dataSource === 'upload') || 
         (this.parameters.dataSource === 'project' && this.parameters.projectFile))
      
      return {
        'status-success': hasData,
        'status-error': !hasData,
        'status-pending': this.parameters.dataSource === 'project' && this.parameters.projectFile && !this.csvData
      }
    },
    
    renderChartsWithRetry(retryCount = 0) {
      const maxRetries = 3
      const retryDelay = 500
      
      try {
        const success = this.renderCharts()
        if (!success && retryCount < maxRetries) {
          console.warn(`图表渲染失败，${retryDelay}ms后重试 (${retryCount + 1}/${maxRetries})`)
          setTimeout(() => {
            this.renderChartsWithRetry(retryCount + 1)
          }, retryDelay)
        }
      } catch (error) {
        console.error('图表渲染错误:', error)
        if (retryCount < maxRetries) {
          console.warn(`发生错误，${retryDelay}ms后重试 (${retryCount + 1}/${maxRetries})`)
          setTimeout(() => {
            this.renderChartsWithRetry(retryCount + 1)
          }, retryDelay)
        }
      }
    },

    renderCharts() {
      try {
        console.log('开始渲染图表...')
        
        // 检查分析结果是否存在
        if (!this.analysisResults) {
          console.warn('分析结果不存在，跳过图表渲染')
          return false
        }
        
        // 检查所有必需的数据
        if (!this.analysisResults.profitHistory || !this.analysisResults.drawdownHistory) {
          console.warn('图表数据不完整，跳过渲染')
          return false
        }

        // 检查DOM是否已准备就绪
        if (!this.$refs.profitChart || !this.$refs.gridChart || !this.$refs.drawdownChart || !this.$refs.allocationChart) {
          console.warn('Canvas元素尚未准备就绪，跳过渲染')
          return false
        }

        // 检查Canvas元素是否在DOM中且可见
        const canvasElements = [this.$refs.profitChart, this.$refs.gridChart, this.$refs.drawdownChart, this.$refs.allocationChart]
        for (const canvas of canvasElements) {
          if (!canvas.offsetParent && canvas.style.display !== 'none') {
            console.warn('Canvas元素不可见，延迟渲染')
            return false
          }
        }
                
        this.renderProfitChart()
        this.renderGridChart()
        this.renderDrawdownChart()
        this.renderAllocationChart()
                
        console.log('图表渲染完成')
        return true
      } catch (error) {
        console.error('图表渲染错误:', error)
        return false
      }
    },
    
    renderProfitChart() {
      if (!this.validateCanvas(this.$refs.profitChart, 'profitChart')) {
        return
      }
      
      const ctx = this.getCanvasContext(this.$refs.profitChart, 'profitChart')
      if (!ctx) {
        return
      }
      
      // 安全销毁现有图表
      if (this.charts.profit && typeof this.charts.profit.destroy === 'function') {
        try {
          this.charts.profit.destroy()
        } catch (error) {
          console.warn('Error destroying existing profit chart:', error)
        }
        this.charts.profit = null
      }
      
      try {
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
            tension: 0.4,
            pointRadius: 0,
            pointHoverRadius: 4,
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            intersect: false,
            mode: 'index'
          },
          plugins: {
            legend: {
              display: false
            },
            filler: {
              propagate: false
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
      } catch (error) {
        console.error('创建收益图表失败:', error)
      }
    },
    
    renderGridChart() {
      if (!this.validateCanvas(this.$refs.gridChart, 'gridChart')) {
        return
      }
      
      const ctx = this.getCanvasContext(this.$refs.gridChart, 'gridChart')
      if (!ctx) {
        return
      }
      
      // 安全销毁现有图表
      if (this.charts.grid && typeof this.charts.grid.destroy === 'function') {
        try {
          this.charts.grid.destroy()
        } catch (error) {
          console.warn('Error destroying existing grid chart:', error)
        }
        this.charts.grid = null
      }
      
      const datasets = [{
        label: '股价',
        data: this.analysisResults.prices,
        borderColor: '#2196F3',
        backgroundColor: 'transparent',
        borderWidth: 2,
        pointRadius: 0,
        pointHoverRadius: 4,
        fill: false
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
          pointRadius: 0,
          pointHoverRadius: 0,
          fill: false
        })
      })
      
      try {
        this.charts.grid = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.analysisResults.dates.map(d => d.slice(5)),
          datasets: datasets
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            intersect: false,
            mode: 'index'
          },
          plugins: {
            legend: {
              display: false
            },
            filler: {
              propagate: false
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
      } catch (error) {
        console.error('创建网格图表失败:', error)
      }
    },
    
    renderDrawdownChart() {
      if (!this.validateCanvas(this.$refs.drawdownChart, 'drawdownChart')) {
        return
      }
      
      const ctx = this.getCanvasContext(this.$refs.drawdownChart, 'drawdownChart')
      if (!ctx) {
        return
      }
      
      // 安全销毁现有图表
      if (this.charts.drawdown && typeof this.charts.drawdown.destroy === 'function') {
        try {
          this.charts.drawdown.destroy()
        } catch (error) {
          console.warn('Error destroying existing drawdown chart:', error)
        }
        this.charts.drawdown = null
      }
      
      try {
        this.charts.drawdown = new Chart(ctx, {
        type: 'line',
        data: {
          labels: this.analysisResults.drawdownHistory.map(d => d.date.slice(5)),
          datasets: [{
            label: '回撤',
            data: this.analysisResults.drawdownHistory.map(d => -d.drawdown),
            borderColor: '#F44336',
            backgroundColor: 'rgba(244, 67, 54, 0.1)',
            fill: true,
            tension: 0.4,
            pointRadius: 0,
            pointHoverRadius: 4,
            borderWidth: 2
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            intersect: false,
            mode: 'index'
          },
          plugins: {
            legend: {
              display: false
            },
            filler: {
              propagate: false
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
      } catch (error) {
        console.error('创建回撤图表失败:', error)
      }
    },
    
    renderAllocationChart() {
      if (!this.validateCanvas(this.$refs.allocationChart, 'allocationChart')) {
        return
      }
      
      const ctx = this.getCanvasContext(this.$refs.allocationChart, 'allocationChart')
      if (!ctx) {
        return
      }
      
      // 安全销毁现有图表
      if (this.charts.allocation && typeof this.charts.allocation.destroy === 'function') {
        try {
          this.charts.allocation.destroy()
        } catch (error) {
          console.warn('Error destroying existing allocation chart:', error)
        }
        this.charts.allocation = null
      }
      
      // 创建资金分布数据（现金vs股票价值）
      const profitHistory = this.analysisResults.profitHistory
      const dates = profitHistory.map(p => p.date.slice(5))
      const cashData = profitHistory.map(p => p.cashValue)
      const positionData = profitHistory.map(p => p.positionValue)
      
      try {
        this.charts.allocation = new Chart(ctx, {
        type: 'line',
        data: {
          labels: dates,
          datasets: [
            {
              label: '现金',
              data: cashData,
              borderColor: '#4CAF50',
              backgroundColor: 'rgba(76, 175, 80, 0.1)',
              fill: '+1',
              tension: 0.4,
              pointRadius: 0,
              pointHoverRadius: 4,
              borderWidth: 2
            },
            {
              label: '股票价值',
              data: positionData,
              borderColor: '#2196F3',
              backgroundColor: 'rgba(33, 150, 243, 0.1)',
              fill: 'origin',
              tension: 0.4,
              pointRadius: 0,
              pointHoverRadius: 4,
              borderWidth: 2
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            intersect: false,
            mode: 'index'
          },
          plugins: {
            legend: {
              position: 'top'
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const value = context.parsed.y
                  return `${context.dataset.label}: ¥${value.toFixed(2)}`
                },
                footer: function(tooltipItems) {
                  if (tooltipItems.length >= 2) {
                    const total = tooltipItems.reduce((sum, item) => sum + item.parsed.y, 0)
                    return `总资产: ¥${total.toFixed(2)}`
                  }
                  return ''
                }
              }
            },
            filler: {
              propagate: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              stacked: true,
              grid: {
                color: '#f0f0f0'
              },
              ticks: {
                callback: function(value) {
                  return '¥' + (value / 1000).toFixed(0) + 'K'
                }
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
      } catch (error) {
        console.error('创建资金分布图表失败:', error)
      }
    },
    
    showChartDetail(type) {
      this.currentDetailType = type
      this.showDetailModal = true
      
      const titles = {
        profit: '累计收益曲线详情',
        grid: '价格与网格线详情',
        drawdown: '回撤分析详情',
        allocation: '资金分布详情'
      }
      this.detailTitle = titles[type]
      
      this.$nextTick(() => {
        // 添加延迟确保模态框完全渲染
        setTimeout(() => {
        this.renderDetailChart(type)
        }, 150)
      })
    },
    
    renderDetailChart(type) {
      if (!this.validateCanvas(this.$refs.detailChart, 'detailChart')) {
        return
      }
      
      const ctx = this.getCanvasContext(this.$refs.detailChart, 'detailChart')
      if (!ctx) {
        return
      }
      
      // 安全销毁现有图表
      if (this.charts.detail && typeof this.charts.detail.destroy === 'function') {
        try {
          this.charts.detail.destroy()
        } catch (error) {
          console.warn('Error destroying existing detail chart:', error)
        }
        this.charts.detail = null
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
        case 'trades': {
          const priceData = this.analysisResults.prices
          const dates = this.analysisResults.dates
          const trades = this.analysisResults.tradeHistory
          
          const buyPoints = []
          const sellPoints = []
          const otherPoints = []
          
          trades.forEach(trade => {
            const dateIndex = dates.findIndex(date => date === trade.date)
            if (dateIndex !== -1) {
              const point = {
                x: trade.date,
                y: trade.price
              }
              
              if (trade.type === '买入' || trade.type === '建仓') {
                buyPoints.push(point)
              } else if (trade.type === '卖出') {
                sellPoints.push(point)
              } else {
                otherPoints.push(point)
              }
            }
          })
          
          const tradeDatasets = [
            {
              label: '股价',
              data: priceData.map((price, index) => ({
                x: dates[index],
                y: price
              })),
              borderColor: '#2196F3',
              backgroundColor: 'transparent',
              borderWidth: 2,
              pointRadius: 0,
              pointHoverRadius: 4,
              fill: false
            }
          ]
          
          if (buyPoints.length > 0) {
            tradeDatasets.push({
              label: '买入点',
              data: buyPoints,
              backgroundColor: '#4CAF50',
              borderColor: '#4CAF50',
              pointRadius: 8,
              pointHoverRadius: 10,
              showLine: false,
              fill: false
            })
          }
          
          if (sellPoints.length > 0) {
            tradeDatasets.push({
              label: '卖出点',
              data: sellPoints,
              backgroundColor: '#F44336',
              borderColor: '#F44336',
              pointRadius: 8,
              pointHoverRadius: 10,
              showLine: false,
              fill: false
            })
          }
          
          if (otherPoints.length > 0) {
            tradeDatasets.push({
              label: '其他操作',
              data: otherPoints,
              backgroundColor: '#FF9800',
              borderColor: '#FF9800',
              pointRadius: 6,
              pointHoverRadius: 8,
              showLine: false,
              fill: false
            })
          }
          
          chartConfig = {
            type: 'line',
            data: {
              datasets: tradeDatasets
            },
            options: {
              interaction: {
                intersect: false,
                mode: 'point'
              },
              plugins: {
                tooltip: {
                  callbacks: {
                    title: function(context) {
                      return context[0].label
                    },
                    label: function(context) {
                      if (context.dataset.showLine === false) {
                        const trade = trades.find(t => t.date === context.label)
                        if (trade) {
                          return `${context.dataset.label}: ¥${context.parsed.y.toFixed(2)} (${trade.amount ? trade.amount.toFixed(0) + '股' : ''})`
                        }
                      }
                      return `${context.dataset.label}: ¥${context.parsed.y.toFixed(2)}`
                    }
                  }
                },
                filler: {
                  propagate: false
                }
              },
              scales: {
                x: {
                  type: 'time',
                  time: {
                    parser: 'YYYY-MM-DD',
                    displayFormats: {
                      day: 'MM-DD'
                    }
                  }
                }
              }
            }
          }
          break
        }
        case 'allocation': {
          const profitHistory = this.analysisResults.profitHistory
          const dates = profitHistory.map(p => p.date)
          const cashData = profitHistory.map(p => p.cashValue)
          const positionData = profitHistory.map(p => p.positionValue)
          const totalData = profitHistory.map(p => p.totalValue)
          
          chartConfig = {
            type: 'line',
            data: {
              labels: dates,
              datasets: [
                {
                  label: '现金',
                  data: cashData,
                  borderColor: '#4CAF50',
                  backgroundColor: 'rgba(76, 175, 80, 0.2)',
                  fill: '+1',
                  tension: 0.4,
                  pointRadius: 1,
                  pointHoverRadius: 6,
                  borderWidth: 2
                },
                {
                  label: '股票价值',
                  data: positionData,
                  borderColor: '#2196F3',
                  backgroundColor: 'rgba(33, 150, 243, 0.2)',
                  fill: 'origin',
                  tension: 0.4,
                  pointRadius: 1,
                  pointHoverRadius: 6,
                  borderWidth: 2
                },
                {
                  label: '总资产',
                  data: totalData,
                  borderColor: '#FF9800',
                  backgroundColor: 'transparent',
                  fill: false,
                  tension: 0.4,
                  pointRadius: 0,
                  pointHoverRadius: 4,
                  borderWidth: 3,
                  borderDash: [5, 5]
                }
              ]
            },
            options: {
              interaction: {
                intersect: false,
                mode: 'index'
              },
              plugins: {
                tooltip: {
                  callbacks: {
                    label: function(context) {
                      const value = context.parsed.y
                      return `${context.dataset.label}: ¥${value.toFixed(2)}`
                    },
                    footer: function(tooltipItems) {
                      const cashItem = tooltipItems.find(item => item.dataset.label === '现金')
                      const positionItem = tooltipItems.find(item => item.dataset.label === '股票价值')
                      
                      if (cashItem && positionItem) {
                        const total = cashItem.parsed.y + positionItem.parsed.y
                        const cashPercent = (cashItem.parsed.y / total * 100).toFixed(1)
                        const positionPercent = (positionItem.parsed.y / total * 100).toFixed(1)
                        return [
                          `现金占比: ${cashPercent}%`,
                          `股票占比: ${positionPercent}%`
                        ]
                      }
                      return ''
                    }
                  }
                },
                filler: {
                  propagate: false
                }
              },
              scales: {
                y: {
                  beginAtZero: true,
                  stacked: false,
                  ticks: {
                    callback: function(value) {
                      return '¥' + (value / 1000).toFixed(0) + 'K'
                    }
                  }
                }
              }
            }
          }
          break
        }
      }
      
      chartConfig.options = {
        ...chartConfig.options,
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          ...chartConfig.options?.plugins,
          legend: {
            position: 'top'
          },
          filler: {
            propagate: false
          }
        },
        scales: {
          ...chartConfig.options?.scales,
          y: {
            ...chartConfig.options?.scales?.y,
            grid: {
              color: '#f0f0f0'
            }
          },
          x: {
            ...chartConfig.options?.scales?.x,
            grid: {
              color: '#f0f0f0'
            }
          }
        }
      }
      
      // 验证图表配置
      if (!chartConfig || !chartConfig.data || !ctx) {
        console.warn('图表配置或上下文无效，跳过渲染')
        return
      }

      try {
        this.charts.detail = new Chart(ctx, chartConfig)
      } catch (error) {
        console.error('创建详情图表失败:', error)
      }
    },

    // 添加图表配置验证方法
    validateChartConfig(config) {
      if (!config || typeof config !== 'object') {
        return false
      }
      if (!config.data || !config.data.datasets) {
        return false
      }
      return true
    },

    // Canvas验证方法
    validateCanvas(canvas, chartName) {
      if (!canvas) {
        console.warn(`${chartName} canvas element not found`)
        return false
      }
      
      if (!canvas.getContext) {
        console.warn(`${chartName} canvas element does not support getContext`)
        return false
      }
      
      // 检查Canvas是否已连接到DOM
      if (!canvas.isConnected) {
        console.warn(`${chartName} canvas element not connected to DOM`)
        return false
      }
      
      // 检查Canvas尺寸
      if (canvas.offsetWidth === 0 || canvas.offsetHeight === 0) {
        console.warn(`${chartName} canvas element has zero dimensions`)
        return false
      }
      
      return true
    },

    // 获取Canvas上下文方法
    getCanvasContext(canvas, chartName) {
      try {
        const ctx = canvas.getContext('2d')
        if (!ctx) {
          console.warn(`Failed to get 2d context from ${chartName} canvas`)
          return null
        }
        
        // 验证上下文对象
        if (typeof ctx.save !== 'function' || typeof ctx.restore !== 'function') {
          console.warn(`${chartName} canvas context is invalid`)
          return null
        }
        
        return ctx
      } catch (error) {
        console.error(`Error getting context for ${chartName}:`, error)
        return null
      }
    },
    
    // 网格预览相关方法
    getBasePositionPricePreview() {
      if (!this.csvData || this.csvData.length === 0) return '未知'
      
      const prices = this.csvData.map(row => parseFloat(row['收盘']) || 0).reverse()
      const baseIndex = this.getBasePositionIndex()
      
      if (baseIndex < prices.length) {
        return '¥' + prices[baseIndex].toFixed(2)
      }
      return '未知'
    },
    
    getGridCountPreview() {
      const basePrice = this.getBasePositionPriceValue()
      if (!basePrice) return 0
      
      const tempGridLines = this.generateGridLines(
        basePrice,
        this.parameters.gridSpacingMode,
        this.parameters.gridSpacing,
        this.parameters.gridRange
      )
      return tempGridLines.length
    },
    
    getGridRangePreview() {
      const basePrice = this.getBasePositionPriceValue()
      if (!basePrice) return '未知'
      
      const rangeRatio = this.parameters.gridRange / 100
      const minPrice = basePrice * (1 - rangeRatio)
      const maxPrice = basePrice * (1 + rangeRatio)
      
      return `¥${minPrice.toFixed(2)} - ¥${maxPrice.toFixed(2)}`
    },
    
    getBasePositionPriceValue() {
      if (!this.csvData || this.csvData.length === 0) return null
      
      const prices = this.csvData.map(row => parseFloat(row['收盘']) || 0).reverse()
      const baseIndex = this.getBasePositionIndex()
      
      return baseIndex < prices.length ? prices[baseIndex] : null
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
    try {
      console.log('清理图表实例...')
      // 清理图表实例
      Object.keys(this.charts).forEach(key => {
        const chart = this.charts[key]
        if (chart && typeof chart.destroy === 'function') {
          try {
            chart.destroy()
          } catch (error) {
            console.warn(`清理${key}图表时出错:`, error)
          }
          this.charts[key] = null
        }
      })
      console.log('图表实例清理完成')
    } catch (error) {
      console.error('清理图表实例时出错:', error)
    }
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

/* 顶部配置区域 */
.top-config-section {
  max-width: 1600px;
  margin: 0 auto 20px auto;
  padding: 0 20px;
}

.config-cards-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

@media (max-width: 1200px) {
  .config-cards-row {
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  }
}

.config-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.config-card h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 1em;
  font-weight: 600;
}

.config-row {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.config-item label {
  font-size: 0.85em;
  color: #666;
  font-weight: 500;
}

.mini-input {
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9em;
  width: 100%;
}

.mini-input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.1);
}

.mini-checkbox {
  width: 16px;
  height: 16px;
}

.date-display {
  font-size: 0.9em;
  color: #333;
  font-weight: 600;
  padding: 6px 8px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.file-input-mini {
  font-size: 0.85em !important;
  padding: 4px 6px !important;
}

.file-status {
  font-size: 0.85em;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.status-success {
  color: #155724;
  background-color: #d4edda;
  border-color: #c3e6cb;
}

.status-error {
  color: #721c24;
  background-color: #f8d7da;
  border-color: #f5c6cb;
}

.status-pending {
  color: #856404;
  background-color: #fff3cd;
  border-color: #ffeaa7;
}

.action-card {
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-card .analyze-button {
  padding: 12px 24px;
  font-size: 1em;
  white-space: nowrap;
}

.main-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

/* 高级配置区域 */
.advanced-config {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.section-card.compact {
  padding: 15px;
}

.advanced-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-group.compact {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group.compact label {
  font-size: 0.85em;
  color: #666;
  font-weight: 500;
}

.form-input.mini {
  flex: 1;
  padding: 4px 6px;
  font-size: 0.85em;
}

.checkbox-group.compact {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 8px 0;
}

.checkbox-group.compact label {
  font-size: 0.85em;
  cursor: pointer;
}

/* 交易表格样式 */
.trades-table-container {
  margin-top: 25px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.table-title {
  padding: 20px 25px 15px 25px;
  margin: 0;
  color: #333;
  font-size: 1.2em;
  font-weight: 600;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.trade-stats {
  font-size: 0.9em;
  color: #666;
  font-weight: normal;
}

.table-wrapper {
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
}

.trades-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9em;
}

.trades-table th {
  background: #f8f9fa;
  color: #333;
  font-weight: 600;
  padding: 12px 8px;
  text-align: left;
  border-bottom: 2px solid #dee2e6;
  position: sticky;
  top: 0;
  z-index: 10;
}

.trades-table td {
  padding: 10px 8px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.trades-table tr:hover {
  background: #f8f9fa;
}

/* 交易类型行颜色 */
.trade-buy {
  background: rgba(76, 175, 80, 0.05);
}

.trade-buy:hover {
  background: rgba(76, 175, 80, 0.1);
}

.trade-sell {
  background: rgba(244, 67, 54, 0.05);
}

.trade-sell:hover {
  background: rgba(244, 67, 54, 0.1);
}

.trade-other {
  background: rgba(255, 152, 0, 0.05);
}

.trade-other:hover {
  background: rgba(255, 152, 0, 0.1);
}

/* 交易类型标签 */
.trade-type-buy {
  color: #4CAF50;
  font-weight: 600;
}

.trade-type-sell {
  color: #F44336;
  font-weight: 600;
}

.trade-type-other {
  color: #FF9800;
  font-weight: 600;
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
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 15px;
  margin-bottom: 25px;
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

/* 新增样式 */
.param-hint {
  display: block;
  font-size: 0.8em;
  color: #888;
  font-weight: normal;
  margin-top: 2px;
}

.price-range-inputs {
  display: flex;
  align-items: center;
  gap: 10px;
}

.half-width {
  flex: 1;
}

.range-separator {
  color: #666;
  font-weight: bold;
}

.checkbox-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-checkbox {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.detailed-stats {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.detailed-stats h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 1.1em;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.stat-label {
  color: #666;
  font-weight: 500;
}

.stat-value {
  color: #333;
  font-weight: 600;
}

/* 网格预览信息样式 */
.grid-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #dee2e6;
}

.info-item {
  display: block;
  font-size: 0.9em;
  color: #495057;
  margin-bottom: 4px;
  font-weight: 500;
}

.info-item:last-child {
  margin-bottom: 0;
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
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .price-range-inputs {
    flex-direction: column;
    gap: 8px;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
}
</style>