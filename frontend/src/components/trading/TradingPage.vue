<template>
  <div class="grid-trading-analyzer">
    <!-- 页面标题 -->
    <div class="header-section">
      <TopActionsBar title="网格交易策略分析系统" />
      <p class="page-subtitle">专业的量化交易回测平台</p>
    </div>

    <!-- 顶部配置区域 -->
    <div class="top-config-section">
      <ParamsBasic
        :parameters="parameters"
        :actual-data-count="actualDataCount"
        :get-stock-name="getStockName"
        :file-name="fileName"
        :earliest-date="getEarliestDate()"
        :latest-date="getLatestDate()"
        @file-upload="handleFileUpload"
        @update:parameters="val => parameters = val"
      />
      <ParamsOptional
        :parameters="parameters"
        :module-states="moduleStates"
        @update:parameters="val => parameters = val"
        @update:moduleStates="val => moduleStates = val"
      />
      
      <!-- 分析按钮 -->
      <div class="analyze-section">
        <button 
          @click="runGridTrading" 
          class="analyze-button"
          :disabled="!hasValidData || isAnalyzing"
        >
          <span v-if="isAnalyzing" class="loading-icon">⏳</span>
          {{ isAnalyzing ? '分析中...' : '🚀 开始分析' }}
        </button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 结果展示区域 -->
      <div v-if="!analysisResults" class="empty-state">
        <div class="empty-icon">📈</div>
        <div class="empty-text">配置参数后点击"开始分析"查看回测结果</div>
        <div class="empty-hint">支持上传CSV文件或使用项目示例数据</div>
      </div>
      
        <div v-else class="results-container">
          <CoreMetrics :analysis-results="analysisResults" />
          <PriceSummary :analysis-results="analysisResults" />

        <!-- 图表展示区域 -->
        <div class="charts-section">
          <h3 class="section-title">📈 可视化分析</h3>
          <div class="chart-preview-container">
            <div class="chart-preview-card">
              <div class="preview-header">
                <div class="preview-icon">📊</div>
                <div class="preview-content">
                  <h4 class="preview-title">专业图表分析中心</h4>
                  <p class="preview-description">
                    包含6种专业图表：累计收益、价格网格、回撤分析、资金分布、交易频率、收益分布
                  </p>
                </div>
              </div>
              <div class="preview-stats">
                <div class="stat-item">
                  <span class="stat-number">{{ analysisResults.tradeCount }}</span>
                  <span class="stat-label">交易次数</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ analysisResults.dates?.length || 0 }}</span>
                  <span class="stat-label">分析天数</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ analysisResults.annualReturn }}%</span>
                  <span class="stat-label">年化收益</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">4</span>
                  <span class="stat-label">图表数量</span>
                </div>
              </div>
              <div class="preview-actions">
                <button @click="openVisualizationCenter" class="visualization-button">
                  <span class="button-icon">🚀</span>
                  <span class="button-text">进入可视化分析中心</span>
                  <span class="button-arrow">→</span>
                </button>
                <div class="preview-hint">
                  <small>💡 大屏幕专业图表，更好的分析体验</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 交易记录表格 -->
        <TradesTable :trades="analysisResults.tradeHistory" />
      </div>
    </div>

    <!-- v3.0: 详情弹窗已移至ChartVisualization组件 -->
  </div>
</template>

<script>
import { parseCSV, calculateGridTrading } from '@/services/trading/gridTrading'
import CoreMetrics from './results/CoreMetrics.vue'
import PriceSummary from './results/PriceSummary.vue'
import TradesTable from './results/TradesTable.vue'
import ParamsBasic from './params/ParamsBasic.vue'
import ParamsOptional from './params/ParamsOptional.vue'
import TopActionsBar from '../common/TopActionsBar.vue'

export default {
  name: 'TradingPage',
  components: { CoreMetrics, PriceSummary, TradesTable, ParamsBasic, ParamsOptional, TopActionsBar },
  data() {
    return {
      fileName: '',
      csvData: null,
      isAnalyzing: false,
      analysisResults: null,
      projectDataInfo: {
        '600585': '1345',
        '002032': '1361',
        '700001': '1353'
      },
      // 模块启用状态管理
      moduleStates: {
        riskControl: false,        // 风险控制模块
        advancedStrategy: false,   // 高级策略模块  
        marketEnvironment: false,  // 市场环境模块
        fundManagement: false      // 资金管理模块
      },
      parameters: {
        // 数据源配置
        dataSource: 'project', // 'upload', 'project'
        selectedProjectFile: '600585', // 选择的项目文件
        
        // 资金配置
        initialCapital: 10,        // 初始资金(万元)
        basePositionRatio: 20,     // 底仓比例(%)
        singleTradeRatio: 10,      // 单次交易比例(%)
        maxPositionRatio: 70,      // 最大持仓比例(%)
        
        // 网格配置
        gridLevels: 20,           // 网格层数
        gridWidthMode: 'percentage', // 网格宽度模式: 'percentage'|'value'
        gridDensity: 2.0,         // 网格密度(%) - 百分比模式
        gridWidth: 50,            // 网格宽度(元) - 数值模式
        
        // 时间配置
        basePositionMode: 'days', // 建仓模式: 'days'|'date'
        basePositionDays: 5,      // 第N天建仓
        basePositionDate: '',     // 指定建仓日期
        
        // 风险控制
        stopLossRatio: 0,         // 止损比例(%)
        takeProfitRatio: 0,       // 止盈比例(%)
        maxDrawdownLimit: 0,      // 最大回撤限制(%)
        bearMarketProtection: false, // 熊市保护
        bearMarketDays: 7,        // 连续下跌天数阈值
        
        // 其他
        feeRate: 0.1,            // 手续费率(%)
        priceRangeMode: 'auto',  // 价格区间模式
        
        // 高级策略模块参数 (占位)
        advancedStrategy: {
          enableDynamicGrid: false,
          trendSensitivity: 50,
          volatilityThreshold: 2.0
        },
        
        // 市场环境模块参数 (占位)  
        marketEnvironment: {
          sectorRotation: false,
          marketSentiment: 'neutral',
          macroFactor: 1.0
        },
        
        // 资金管理模块参数 (占位)
        fundManagement: {
          batchBuilding: false,
          dynamicPosition: false,
          riskBudget: 10
        }
      }
      // v3.0: charts对象已移除，不再在TradingPage中渲染图表
    }
  },
  computed: {
    hasValidData() {
      if (this.parameters.dataSource === 'upload') {
        return this.csvData && this.csvData.length > 0
      }
      return true // 项目文件总是有效的
    },
    
    actualDataCount() {
      if (this.parameters.dataSource === 'upload') {
        return this.csvData ? this.csvData.length : '未上传'
      } else if (this.parameters.dataSource === 'project') {
        if (this.csvData && this.csvData.length > 0) {
          return this.csvData.length
        }
        // 如果数据还未加载，显示预估值
        return this.projectDataInfo[this.parameters.selectedProjectFile] || '加载中...'
      }
      return '未知'
    }
  },
  watch: {
    'parameters.dataSource'() {
      // 切换数据源时清空结果
      this.analysisResults = null
      this.csvData = null
      this.fileName = ''
    },
    'parameters.basePositionMode'() {
      // 切换建仓模式时，不需要强制更新，Vue的响应式系统会自动处理
    }
  },
  methods: {
    // 数据管理方法
    handleFileUpload(event) {
      const file = event.target.files[0]
      if (file) {
        this.fileName = file.name
        const reader = new FileReader()
        reader.onload = (e) => {
          this.csvData = parseCSV(e.target.result)
        }
        reader.readAsText(file, 'utf-8')
      }
    },

    async loadProjectFile() {
      try {
        const fileName = this.parameters.selectedProjectFile + '历史数据.csv'
        const response = await fetch(`/datas/${fileName}`)
        const content = await response.text()
        this.csvData = parseCSV(content)
        return true
      } catch (error) {
        console.error('加载项目文件失败:', error)
        return false
      }
    },

    // 获取股票名称
    getStockName(code) {
      const stockNames = {
        '600585': '海螺水泥 (600585)',
        '002032': '苏泊尔 (002032)',
        '700001': '东方财富 (700001)'
      }
      return stockNames[code] || code
    },



    // 获取最早日期
    getEarliestDate() {
      if (!this.csvData || this.csvData.length === 0) return ''
      const dates = this.csvData.map(row => row['日期']).sort()
      return dates[0] || ''
    },

    // 获取最晚日期  
    getLatestDate() {
      if (!this.csvData || this.csvData.length === 0) return ''
      const dates = this.csvData.map(row => row['日期']).sort()
      return dates[dates.length - 1] || ''
    },

    // CSV 解析已迁移至 services

    // 获取数据
    async getData() {
      switch (this.parameters.dataSource) {
        case 'upload':
          return this.csvData
        case 'project':
          if (!this.csvData) {
            const success = await this.loadProjectFile()
            if (!success) {
              alert('加载项目文件失败')
              return null
            }
          }
          return this.csvData
        default:
          return null
      }
    },

    // 主要分析方法
    async runGridTrading() {
      this.isAnalyzing = true
      
      try {
        // 获取数据
        const data = await this.getData()
        if (!data || data.length === 0) {
          alert('请先选择有效的数据源')
          return
        }
        
        // 模拟分析过程
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        // 执行网格交易算法
        const results = calculateGridTrading(data, this.parameters, this.moduleStates)
        this.analysisResults = results
        
        // v3.0: 不再在TradingPage中渲染图表，只显示预览卡片
        // 图表渲染已移至ChartVisualization组件
        
      } catch (error) {
        console.error('分析错误:', error)
        alert('分析过程中出现错误: ' + error.message)
      } finally {
        this.isAnalyzing = false
      }
    },

    // v3.0: 图表渲染方法已移至ChartVisualization组件
    // TradingPage现在只显示预览卡片，不直接渲染图表

    // 打开可视化分析中心
    openVisualizationCenter() {
      // 将分析结果数据存储到sessionStorage，供可视化组件使用
      if (this.analysisResults) {
        sessionStorage.setItem('tradingAnalysisResults', JSON.stringify(this.analysisResults))
        // 使用window.open在新标签页打开，或者可以配置路由跳转
        window.open('/chart-visualization', '_blank')
      } else {
        alert('请先完成分析后再查看可视化图表')
      }
    }
  }
  
  // v3.0: beforeUnmount中的图表清理已移除，因为不再在TradingPage中渲染图表
}
</script>

<style scoped>
/* CSS变量定义 - 与ElegantPortalPage保持一致 */
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

/* 组件根样式 - 与ElegantPortalPage背景保持一致 */
.grid-trading-analyzer {
  min-height: 100vh;
  background-color: var(--secondary);
  padding: 20px;
  font-family: 'Montserrat', sans-serif;
  color: var(--text);
  line-height: 1.6;
  overflow-x: hidden;
}

/* 页面标题区域 */
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

.top-actions .page-title { margin: 0 auto; }
.top-actions .nav-btn:last-child { justify-self: end; }

.page-title {
  color: var(--primary);
  font-size: 2.5em;
  margin-bottom: 10px;
  font-weight: 300;
  font-family: 'Cormorant Garamond', serif;
  letter-spacing: 0.5px;
}

.page-subtitle {
  color: var(--light-text);
  font-size: 1.2em;
  margin: 0;
  font-weight: 300;
}

/* 顶部配置区域 */
.top-config-section {
  background: rgba(248, 245, 242, 0.8);
  border-radius: var(--border-radius);
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: var(--card-shadow);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(212, 184, 160, 0.2);
}

/* 配置卡片网格布局 - 2x4对称布局 */
.config-cards-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 25px;
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .config-cards-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 900px) {
  .config-cards-row {
    grid-template-columns: 1fr;
  }
}

.config-card {
  background: rgba(255, 255, 255, 0.7);
  border-radius: var(--border-radius);
  padding: 20px;
  border: 1px solid rgba(212, 184, 160, 0.3);
  transition: var(--transition);
  backdrop-filter: blur(5px);
}

.config-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.config-title {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.1em;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 数据源选择 */
.data-source-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 15px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: var(--transition);
}

.radio-option:hover {
  background: rgba(212, 184, 160, 0.1);
}

.radio-option input[type="radio"] {
  margin: 0;
}

/* 文件上传区域 */
.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: var(--transition);
  cursor: pointer;
}

.upload-area:hover {
  border-color: var(--accent);
  background: rgba(212, 184, 160, 0.05);
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

/* 文件选择区域 */
.file-selection {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 文件信息展示 */
.file-info {
  background: rgba(212, 184, 160, 0.15);
  border-radius: 8px;
  padding: 15px;
  border: 1px solid rgba(212, 184, 160, 0.3);
  backdrop-filter: blur(3px);
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  color: #666;
  font-weight: 500;
}

.info-value {
  color: #333;
  font-weight: 600;
}

/* 表单网格布局 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-group label {
  color: #555;
  font-weight: 600;
  font-size: 0.9em;
}

.form-input, .form-select {
  padding: 10px 12px;
  border: 2px solid rgba(212, 184, 160, 0.3);
  border-radius: 6px;
  font-size: 0.9em;
  transition: var(--transition);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(3px);
}

.form-input:focus, .form-select:focus {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(212, 184, 160, 0.1);
}

.form-input::placeholder {
  color: #999;
  font-style: italic;
}

.form-hint {
  color: #888;
  font-size: 0.8em;
  margin-top: 4px;
  font-style: italic;
}

/* 复选框样式 */
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: var(--transition);
}

.checkbox-label:hover {
  background: rgba(212, 184, 160, 0.1);
}

.checkbox-label input[type="checkbox"] {
  margin: 0;
}

/* 可选模块样式 */
.optional-module {
  position: relative;
}

.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.module-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  background: rgba(212, 184, 160, 0.1);
  transition: var(--transition);
  font-size: 0.85em;
  font-weight: 500;
}

.module-toggle:hover {
  background: rgba(212, 184, 160, 0.2);
}

.module-toggle input[type="checkbox"] {
  margin: 0;
  transform: scale(0.9);
}

.toggle-text {
  color: #666;
  font-weight: 500;
}

/* 模块禁用状态 */
.module-disabled {
  opacity: 0.6;
  background: rgba(248, 245, 242, 0.4) !important;
  pointer-events: auto;
}

.module-disabled .form-input:disabled,
.module-disabled .form-select:disabled,
.module-disabled .checkbox-label input:disabled {
  background: rgba(200, 200, 200, 0.3);
  color: #999;
  cursor: not-allowed;
}

.module-disabled .checkbox-label {
  opacity: 0.5;
  cursor: not-allowed;
}

.module-disabled .module-toggle {
  opacity: 1;
  pointer-events: auto;
}

/* 分析按钮区域 */
.analyze-section {
  text-align: center;
  margin-top: 20px;
}

.analyze-button {
  padding: 15px 40px;
  background: var(--accent);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 1.2em;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  box-shadow: 0 4px 15px rgba(212, 184, 160, 0.3);
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.analyze-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(212, 184, 160, 0.4);
  opacity: 0.9;
}

.analyze-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 主要内容区域 */
.main-content {
  background: rgba(248, 245, 242, 0.8);
  border-radius: var(--border-radius);
  padding: 25px;
  box-shadow: var(--card-shadow);
  min-height: 400px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(212, 184, 160, 0.2);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-icon {
  font-size: 5em;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 1.2em;
  margin-bottom: 10px;
  color: #666;
}

.empty-hint {
  font-size: 1em;
  color: #999;
}

/* 结果容器 */
.results-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 章节标题 */
.section-title {
  color: #333;
  font-size: 1.3em;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}

/* 指标网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.metric-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(248, 245, 242, 0.9));
  padding: 20px;
  border-radius: var(--border-radius);
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  transition: var(--transition);
  border: 1px solid rgba(212, 184, 160, 0.2);
  backdrop-filter: blur(5px);
}

.metric-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 25px rgba(0,0,0,0.1);
}

.metric-card.highlight {
  background: linear-gradient(135deg, var(--accent), #c4a888);
  color: white;
}

.metric-icon {
  font-size: 2em;
  opacity: 0.8;
}

.metric-content {
  flex: 1;
}

.metric-label {
  font-size: 0.9em;
  opacity: 0.8;
  margin-bottom: 5px;
}

.metric-value {
  font-size: 1.6em;
  font-weight: 700;
}

/* 价格信息区域 */
.price-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  background: rgba(248, 245, 242, 0.7);
  padding: 20px;
  border-radius: var(--border-radius);
  border: 1px solid rgba(212, 184, 160, 0.3);
  backdrop-filter: blur(5px);
}

.price-info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border: 1px solid rgba(212, 184, 160, 0.2);
  backdrop-filter: blur(3px);
}

.price-label {
  color: #666;
  font-weight: 500;
}

.price-value {
  font-weight: 600;
  color: #333;
}

.price-value.high-price {
  color: var(--danger-color);
}

.price-value.low-price {
  color: var(--success-color);
}

.price-value.grid-center {
  color: var(--accent);
  font-weight: 700;
}

.price-value.grid-step {
  color: var(--warning-color);
}

.price-value.grid-range {
  color: #9C27B0;
  font-size: 0.9em;
}

.price-info-item.highlight-item {
  background: linear-gradient(135deg, rgba(212, 184, 160, 0.2), rgba(212, 184, 160, 0.1));
  border: 2px solid rgba(212, 184, 160, 0.4);
}

/* 图表预览容器 */
.chart-preview-container {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.chart-preview-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(248, 245, 242, 0.8));
  border-radius: var(--border-radius);
  padding: 30px;
  box-shadow: var(--card-shadow);
  border: 1px solid rgba(212, 184, 160, 0.3);
  backdrop-filter: blur(10px);
  max-width: 800px;
  width: 100%;
  transition: var(--transition);
}

.chart-preview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0,0,0,0.15);
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 25px;
}

.preview-icon {
  font-size: 3em;
  background: linear-gradient(135deg, var(--accent), #c4a888);
  border-radius: 50%;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(212, 184, 160, 0.3);
}

.preview-content {
  flex: 1;
}

.preview-title {
  color: var(--primary);
  font-size: 1.5em;
  font-weight: 600;
  margin: 0 0 10px 0;
  font-family: 'Cormorant Garamond', serif;
}

.preview-description {
  color: var(--light-text);
  font-size: 1em;
  margin: 0;
  line-height: 1.5;
}

.preview-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 20px;
  margin: 25px 0;
  padding: 20px;
  background: rgba(248, 245, 242, 0.5);
  border-radius: 10px;
  border: 1px solid rgba(212, 184, 160, 0.2);
}

.stat-item {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stat-number {
  font-size: 1.8em;
  font-weight: 700;
  color: var(--accent);
  font-family: 'Cormorant Garamond', serif;
}

.stat-label {
  font-size: 0.85em;
  color: var(--light-text);
  font-weight: 500;
}

.preview-actions {
  text-align: center;
  margin-top: 25px;
}

.visualization-button {
  background: linear-gradient(135deg, var(--accent), #c4a888);
  color: white;
  border: none;
  padding: 15px 30px;
  border-radius: 25px;
  font-size: 1.1em;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  box-shadow: 0 4px 15px rgba(212, 184, 160, 0.3);
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.visualization-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(212, 184, 160, 0.4);
  opacity: 0.9;
}

.visualization-button:active {
  transform: translateY(0);
}

.button-icon, .button-arrow {
  font-size: 1.2em;
}

.button-arrow {
  transition: var(--transition);
}

.visualization-button:hover .button-arrow {
  transform: translateX(5px);
}

.preview-hint {
  margin-top: 10px;
  color: var(--light-text);
  font-style: italic;
}

/* 已移除：图表容器相关样式（本页不再渲染图表） */

/* 交易记录表格 */
.trades-table-wrapper {
  overflow-x: auto;
  border-radius: var(--border-radius);
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.trades-table {
  width: 100%;
  border-collapse: collapse;
  background: rgba(255, 255, 255, 0.9);
  border-radius: var(--border-radius);
  overflow: hidden;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(212, 184, 160, 0.2);
}

.trades-table th {
  background: rgba(248, 245, 242, 0.8);
  color: #555;
  font-weight: 600;
  padding: 12px 8px;
  text-align: left;
  border-bottom: 2px solid rgba(212, 184, 160, 0.3);
  font-size: 0.9em;
}

.trades-table td {
  padding: 10px 8px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 0.9em;
}

.trades-table tr:hover {
  background: rgba(248, 245, 242, 0.6);
}

.trades-table tr.buy td {
  border-left: 3px solid var(--primary-color);
}

.trades-table tr.sell td {
  border-left: 3px solid var(--danger-color);
}

.trade-type {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8em;
  font-weight: 600;
}

.trade-type.buy {
  background: rgba(76, 175, 80, 0.2);
  color: var(--primary-color);
}

.trade-type.sell {
  background: rgba(244, 67, 54, 0.2);
  color: var(--danger-color);
}

/* 已移除：模态框样式（v3.0 已不使用） */

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .grid-trading-analyzer {
    padding: 15px;
  }
  
  .page-title {
    font-size: 2em;
  }
  
  .top-config-section,
  .main-content {
    padding: 20px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .price-info-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
    max-height: 90%;
  }
  
  .trades-table {
    font-size: 0.8em;
  }
  
  .trades-table th,
  .trades-table td {
    padding: 8px 4px;
  }
  
  /* 图表预览响应式 */
  .chart-preview-card {
    padding: 20px;
    margin: 10px;
  }
  
  .preview-header {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .preview-icon {
    width: 60px;
    height: 60px;
    font-size: 2em;
  }
  
  .preview-title {
    font-size: 1.3em;
  }
  
  .preview-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
    padding: 15px;
  }
  
  .stat-number {
    font-size: 1.5em;
  }
  
  .visualization-button {
    padding: 12px 24px;
    font-size: 1em;
  }
}

@media (max-width: 480px) {
  .analyze-button {
    padding: 12px 30px;
    font-size: 1.1em;
  }
  
  .metric-card {
    padding: 15px;
  }
  
  .metric-icon {
    font-size: 1.5em;
  }
  
  .metric-value {
    font-size: 1.4em;
  }
  
  .chart-wrapper {
    height: 200px;
  }
}

/* 深色模式支持(可选) - 保持与ElegantPortalPage一致的优雅风格 */
@media (prefers-color-scheme: dark) {
  .grid-trading-analyzer {
    background-color: #1a1a1a;
  }
  
  .top-config-section,
  .main-content {
    background: rgba(26, 26, 26, 0.9);
    color: var(--secondary);
    border-color: rgba(212, 184, 160, 0.4);
  }
  
  .config-card {
    background: rgba(26, 26, 26, 0.8);
    border-color: rgba(212, 184, 160, 0.3);
    color: var(--secondary);
  }
  
  .form-input,
  .form-select {
    background: rgba(26, 26, 26, 0.8);
    border-color: rgba(212, 184, 160, 0.4);
    color: var(--secondary);
  }
  
  .trades-table {
    background: rgba(26, 26, 26, 0.9);
    color: var(--secondary);
  }
  
  .trades-table th {
    background: rgba(26, 26, 26, 0.95);
    color: var(--secondary);
  }
  
  .modal-content {
    background: rgba(26, 26, 26, 0.95);
    color: var(--secondary);
  }
}

/* 打印样式 */
@media print {
  .grid-trading-analyzer {
    background: white !important;
    color: black !important;
  }
  
  .analyze-button,
  .detail-button {
    display: none;
  }
  
  .modal-overlay {
    display: none;
  }
  
  .chart-wrapper {
    height: 300px;
  }
}
</style>
