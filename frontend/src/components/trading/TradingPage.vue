<template>
  <div class="grid-trading-analyzer">
    <!-- 页面标题 -->
    <div class="header-section">
      <h1 class="page-title">网格交易策略分析系统</h1>
      <p class="page-subtitle">专业的量化交易回测平台</p>
    </div>

    <!-- 顶部配置区域 -->
    <div class="top-config-section">
      <!-- 数据源配置卡片 -->
      <div class="config-cards-row">
        <div class="config-card">
          <h3 class="config-title">📁 数据源选择</h3>
          <div class="data-source-options">
            <label class="radio-option">
              <input type="radio" v-model="parameters.dataSource" value="upload" />
              <span>本地文件上传</span>
            </label>
            <label class="radio-option">
              <input type="radio" v-model="parameters.dataSource" value="project" />
              <span>项目文件</span>
            </label>
          </div>
          
          <!-- 文件上传区域 -->
          <div v-if="parameters.dataSource === 'upload'" class="upload-area">
            <input 
              type="file" 
              id="csvFile" 
              accept=".csv" 
              @change="handleFileUpload"
              class="file-input"
            >
            <label for="csvFile" class="file-upload-label">
              <div class="upload-icon">📄</div>
              <div class="upload-text">
                {{ fileName || '点击选择CSV文件' }}
              </div>
            </label>
          </div>
          
          <!-- 项目文件选择 -->
          <div v-else-if="parameters.dataSource === 'project'" class="file-selection">
            <div class="form-group">
              <label>选择项目文件</label>
              <select v-model="parameters.selectedProjectFile" class="form-select">
                <option value="600585">海螺水泥 (600585)</option>
                <option value="002032">苏泊尔 (002032)</option>
                <option value="700001">东方财富 (700001)</option>
              </select>
            </div>
            <div class="file-info">
              <div class="info-item">
                <span class="info-label">文件:</span>
                <span class="info-value">{{ parameters.selectedProjectFile }}历史数据.csv</span>
              </div>
              <div class="info-item">
                <span class="info-label">股票:</span>
                <span class="info-value">{{ getStockName(parameters.selectedProjectFile) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">数据量:</span>
                <span class="info-value">{{ actualDataCount }}条记录</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 资金配置卡片 -->
        <div class="config-card">
          <h3 class="config-title">💰 资金配置</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>初始资金 (万元)</label>
              <input 
                v-model.number="parameters.initialCapital" 
                type="number" 
                min="1" 
                step="1"
                class="form-input"
              >
            </div>
            <div class="form-group">
              <label>底仓比例 (%)</label>
              <input 
                v-model.number="parameters.basePositionRatio" 
                type="number" 
                min="0" 
                max="50"
                step="5"
                class="form-input"
              >
            </div>
            <div class="form-group">
              <label>单次交易比例 (%)</label>
              <input 
                v-model.number="parameters.singleTradeRatio" 
                type="number" 
                min="1" 
                max="20"
                step="1"
                class="form-input"
              >
            </div>
            <div class="form-group">
              <label>最大持仓比例 (%)</label>
              <input 
                v-model.number="parameters.maxPositionRatio" 
                type="number" 
                min="50" 
                max="95"
                step="5"
                class="form-input"
              >
            </div>
          </div>
        </div>

        <!-- 网格配置卡片 -->
        <div class="config-card">
          <h3 class="config-title">📊 网格配置</h3>
          <div class="form-grid">
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
            <div class="form-group full-width">
              <label>网格宽度模式</label>
              <select v-model="parameters.gridWidthMode" class="form-select">
                <option value="percentage">百分比模式</option>
                <option value="value">数值模式</option>
              </select>
            </div>
            <div v-if="parameters.gridWidthMode === 'percentage'" class="form-group">
              <label>网格密度 (%)</label>
              <input 
                v-model.number="parameters.gridDensity" 
                type="number" 
                min="0.5" 
                max="10"
                step="0.5"
                class="form-input"
                placeholder="例如：2.0"
              >
              <small class="form-hint">网格覆盖价格波动的百分比范围</small>
            </div>
            <div v-else class="form-group">
              <label>网格宽度 (元)</label>
              <input 
                v-model.number="parameters.gridWidth" 
                type="number" 
                min="0.01" 
                step="0.01"
                class="form-input"
                placeholder="例如：50"
              >
              <small class="form-hint">每个网格的固定价格间距</small>
            </div>
          </div>
        </div>

        <!-- 时间配置卡片 -->
        <div class="config-card">
          <h3 class="config-title">⏰ 建仓配置</h3>
          <div class="form-grid">
            <div class="form-group full-width">
              <label>建仓模式</label>
              <select v-model="parameters.basePositionMode" class="form-select">
                <option value="days">从最早日期向后N天建仓</option>
                <option value="date">指定具体建仓日期</option>
              </select>
            </div>
            <div v-if="parameters.basePositionMode === 'days'" class="form-group">
              <label>建仓天数 (从最早日期开始)</label>
              <input 
                v-model.number="parameters.basePositionDays" 
                type="number" 
                min="1" 
                max="100"
                class="form-input"
                placeholder="例如：5表示第5天建仓"
              >
            </div>
            <div v-else class="form-group">
              <label>建仓日期 (选择具体日期)</label>
              <input 
                v-model="parameters.basePositionDate" 
                type="date"
                class="form-input"
                :min="getEarliestDate()"
                :max="getLatestDate()"
              >
            </div>
          </div>
        </div>

        <!-- 风险控制卡片 -->
        <div class="config-card optional-module" :class="{ 'module-disabled': !moduleStates.riskControl }">
          <div class="module-header">
            <h3 class="config-title">🛡️ 风险控制</h3>
            <label class="module-toggle">
              <input type="checkbox" v-model="moduleStates.riskControl" />
              <span class="toggle-text">启用</span>
            </label>
          </div>
          <div class="form-grid">
            <div class="form-group">
              <label>止损比例 (%)</label>
              <input 
                v-model.number="parameters.stopLossRatio" 
                type="number" 
                min="0" 
                max="20"
                step="1"
                class="form-input"
                placeholder="0=不止损"
                :disabled="!moduleStates.riskControl"
              >
            </div>
            <div class="form-group">
              <label>止盈比例 (%)</label>
              <input 
                v-model.number="parameters.takeProfitRatio" 
                type="number" 
                min="0" 
                max="50"
                step="5"
                class="form-input"
                placeholder="0=不止盈"
                :disabled="!moduleStates.riskControl"
              >
            </div>
            <div class="form-group">
              <label>最大回撤限制 (%)</label>
              <input 
                v-model.number="parameters.maxDrawdownLimit" 
                type="number" 
                min="0" 
                max="30"
                step="5"
                class="form-input"
                placeholder="0=无限制"
                :disabled="!moduleStates.riskControl"
              >
            </div>
            <div class="form-group">
              <label>手续费率 (%)</label>
              <input 
                v-model.number="parameters.feeRate" 
                type="number" 
                min="0" 
                max="1"
                step="0.01"
                class="form-input"
                :disabled="!moduleStates.riskControl"
              >
            </div>
          </div>
          
          <div class="form-group full-width">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                v-model="parameters.bearMarketProtection" 
                :disabled="!moduleStates.riskControl"
              />
              <span>熊市保护 (连续下跌{{ parameters.bearMarketDays }}天暂停买入)</span>
            </label>
          </div>
        </div>

        <!-- 高级策略卡片 (占位) -->
        <div class="config-card optional-module" :class="{ 'module-disabled': !moduleStates.advancedStrategy }">
          <div class="module-header">
            <h3 class="config-title">📈 高级策略</h3>
            <label class="module-toggle">
              <input type="checkbox" v-model="moduleStates.advancedStrategy" />
              <span class="toggle-text">启用</span>
            </label>
          </div>
          <div class="form-grid">
            <div class="form-group">
              <label>标题标题标题</label>
              <input 
                v-model.number="parameters.advancedStrategy.trendSensitivity" 
                type="number" 
                min="0" 
                max="100"
                class="form-input"
                placeholder="正文正文正文"
                :disabled="!moduleStates.advancedStrategy"
              >
            </div>
            <div class="form-group">
              <label>标题标题标题</label>
              <input 
                v-model.number="parameters.advancedStrategy.volatilityThreshold" 
                type="number" 
                min="0" 
                step="0.1"
                class="form-input"
                placeholder="正文正文正文"
                :disabled="!moduleStates.advancedStrategy"
              >
            </div>
          </div>
          <div class="form-group full-width">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                v-model="parameters.advancedStrategy.enableDynamicGrid" 
                :disabled="!moduleStates.advancedStrategy"
              />
              <span>标题标题标题 (正文正文正文)</span>
            </label>
          </div>
        </div>

        <!-- 市场环境卡片 (占位) -->
        <div class="config-card optional-module" :class="{ 'module-disabled': !moduleStates.marketEnvironment }">
          <div class="module-header">
            <h3 class="config-title">🌍 市场环境</h3>
            <label class="module-toggle">
              <input type="checkbox" v-model="moduleStates.marketEnvironment" />
              <span class="toggle-text">启用</span>
            </label>
          </div>
          <div class="form-grid">
            <div class="form-group">
              <label>标题标题标题</label>
              <select 
                v-model="parameters.marketEnvironment.marketSentiment" 
                class="form-select"
                :disabled="!moduleStates.marketEnvironment"
              >
                <option value="bullish">正文正文正文 A</option>
                <option value="neutral">正文正文正文 B</option>
                <option value="bearish">正文正文正文 C</option>
              </select>
            </div>
            <div class="form-group">
              <label>标题标题标题</label>
              <input 
                v-model.number="parameters.marketEnvironment.macroFactor" 
                type="number" 
                min="0" 
                step="0.1"
                class="form-input"
                placeholder="正文正文正文"
                :disabled="!moduleStates.marketEnvironment"
              >
            </div>
          </div>
          <div class="form-group full-width">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                v-model="parameters.marketEnvironment.sectorRotation" 
                :disabled="!moduleStates.marketEnvironment"
              />
              <span>标题标题标题 (正文正文正文)</span>
            </label>
          </div>
        </div>

        <!-- 资金管理卡片 (占位) -->
        <div class="config-card optional-module" :class="{ 'module-disabled': !moduleStates.fundManagement }">
          <div class="module-header">
            <h3 class="config-title">💼 资金管理</h3>
            <label class="module-toggle">
              <input type="checkbox" v-model="moduleStates.fundManagement" />
              <span class="toggle-text">启用</span>
            </label>
          </div>
          <div class="form-grid">
            <div class="form-group">
              <label>标题标题标题</label>
              <input 
                v-model.number="parameters.fundManagement.riskBudget" 
                type="number" 
                min="0" 
                max="100"
                class="form-input"
                placeholder="正文正文正文"
                :disabled="!moduleStates.fundManagement"
              >
            </div>
            <div class="form-group">
              <label>标题标题标题</label>
              <select 
                v-model="parameters.fundManagement.dynamicPosition" 
                class="form-select"
                :disabled="!moduleStates.fundManagement"
              >
                <option :value="false">正文正文正文 A</option>
                <option :value="true">正文正文正文 B</option>
              </select>
            </div>
          </div>
          <div class="form-group full-width">
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                v-model="parameters.fundManagement.batchBuilding" 
                :disabled="!moduleStates.fundManagement"
              />
              <span>标题标题标题 (正文正文正文)</span>
            </label>
          </div>
        </div>
      </div>
      
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
        <!-- 关键指标展示 -->
        <div class="metrics-section">
          <h3 class="section-title">📊 核心指标</h3>
          <div class="metrics-grid">
            <div class="metric-card highlight">
              <div class="metric-icon">📈</div>
              <div class="metric-content">
                <div class="metric-label">年化收益率</div>
                <div class="metric-value">{{ analysisResults.annualReturn }}%</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">💰</div>
              <div class="metric-content">
                <div class="metric-label">总收益</div>
                <div class="metric-value">{{ analysisResults.totalProfit }} 元</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">📉</div>
              <div class="metric-content">
                <div class="metric-label">最大回撤</div>
                <div class="metric-value">{{ analysisResults.maxDrawdown }}%</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">🔄</div>
              <div class="metric-content">
                <div class="metric-label">交易次数</div>
                <div class="metric-value">{{ analysisResults.tradeCount }} 次</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">⚡</div>
              <div class="metric-content">
                <div class="metric-label">夏普比率</div>
                <div class="metric-value">{{ analysisResults.sharpeRatio }}</div>
              </div>
            </div>
            <div class="metric-card">
              <div class="metric-icon">🎯</div>
              <div class="metric-content">
                <div class="metric-label">胜率</div>
                <div class="metric-value">{{ analysisResults.winRate }}%</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 价格指标展示 -->
        <div class="price-info-section">
          <h3 class="section-title">💹 价格信息</h3>
          <div class="price-info-grid">
            <div class="price-info-item">
              <span class="price-label">底仓建仓价:</span>
              <span class="price-value">{{ analysisResults.basePositionPrice }}元</span>
            </div>
            <div class="price-info-item highlight-item">
              <span class="price-label">网格基准价:</span>
              <span class="price-value grid-center">{{ analysisResults.gridCenterPrice }}元</span>
            </div>
            <div class="price-info-item">
              <span class="price-label">期间最高价:</span>
              <span class="price-value high-price">{{ analysisResults.periodHighPrice }}元</span>
            </div>
            <div class="price-info-item">
              <span class="price-label">期间最低价:</span>
              <span class="price-value low-price">{{ analysisResults.periodLowPrice }}元</span>
            </div>
            <div class="price-info-item">
              <span class="price-label">网格间距:</span>
              <span class="price-value grid-step">{{ analysisResults.gridStep }}元</span>
            </div>
            <div class="price-info-item">
              <span class="price-label">网格覆盖范围:</span>
              <span class="price-value grid-range">{{ analysisResults.gridRange }}</span>
            </div>
          </div>
        </div>

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
                  <span class="stat-number">6</span>
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
        <div class="trades-section">
          <h3 class="section-title">📋 交易记录 (最近{{ Math.min(analysisResults.tradeHistory.length, 20) }}笔)</h3>
          <div class="trades-table-wrapper">
            <table class="trades-table">
              <thead>
                <tr>
                  <th>日期</th>
                  <th>类型</th>
                  <th>价格</th>
                  <th>数量</th>
                  <th>金额</th>
                  <th>手续费</th>
                  <th>余额</th>
                  <th>持仓</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="(trade, index) in analysisResults.tradeHistory.slice(-20)" 
                  :key="index"
                  :class="trade.type"
                >
                  <td>{{ trade.date }}</td>
                  <td>
                    <span class="trade-type" :class="trade.type">
                      {{ trade.type === 'buy' ? '买入' : '卖出' }}
                    </span>
                  </td>
                  <td>{{ trade.price.toFixed(2) }}</td>
                  <td>{{ trade.shares.toFixed(0) }}</td>
                  <td>{{ trade.amount.toFixed(2) }}</td>
                  <td>{{ trade.fee.toFixed(2) }}</td>
                  <td>{{ trade.capital.toFixed(2) }}</td>
                  <td>{{ trade.position.toFixed(0) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- v3.0: 详情弹窗已移至ChartVisualization组件 -->
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js'

Chart.register(...registerables)

export default {
  name: 'TradingPage',
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
          this.parseCSV(e.target.result)
        }
        reader.readAsText(file, 'utf-8')
      }
    },

    async loadProjectFile() {
      try {
        const fileName = this.parameters.selectedProjectFile + '历史数据.csv'
        const response = await fetch(`/datas/${fileName}`)
        const content = await response.text()
        this.parseCSV(content)
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



    parseCSV(content) {
      try {
        const lines = content.split('\n').filter(line => line.trim())
        const headers = lines[0].split(',').map(h => h.replace(/"/g, '').trim())
        
        const data = []
        for (let i = 1; i < lines.length; i++) {
          const values = lines[i].split(',').map(v => v.replace(/"/g, '').trim())
          // 容忍列数不完全匹配的情况
          if (values.length >= 2 && values.length <= headers.length + 2) {
            const row = {}
            headers.forEach((header, index) => {
              row[header] = index < values.length ? values[index] : ''
            })
            // 确保必需的列存在
            if (row['日期'] && row['收盘']) {
              data.push(row)
            }
          }
        }
        
        this.csvData = data
        console.log('CSV数据解析完成:', data.length, '条记录')
      } catch (error) {
        console.error('CSV解析错误:', error)
        alert('CSV文件解析失败，请检查文件格式')
      }
    },

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
        const results = this.calculateGridTrading(data)
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

    // 增强的网格交易算法
    calculateGridTrading(data) {
      const params = this.parameters
      const initialCapital = params.initialCapital * 10000 // 转换为元
      
      // 获取价格数据并反转(最新日期在后)
      const prices = data.map(row => parseFloat(row['收盘']) || 0).reverse()
      const dates = data.map(row => row['日期']).reverse()
      
      if (prices.length === 0) {
        throw new Error('价格数据为空')
      }
      
      // 获取建仓索引和建仓价格
      const basePositionIndex = this.getBasePositionIndex(dates, params)
      
      // 获取建仓价格作为网格基准点
      let gridCenterPrice
      if (basePositionIndex >= 0 && basePositionIndex < prices.length) {
        gridCenterPrice = prices[basePositionIndex]
      } else {
        // 如果没有有效的建仓索引，使用第一个价格作为基准
        gridCenterPrice = prices[0]
        console.warn('使用首日价格作为网格基准点')
      }
      
      // 计算网格间距
      let gridStep
      if (params.gridWidthMode === 'percentage') {
        // 百分比模式：基于建仓价格计算固定比例间距
        gridStep = gridCenterPrice * params.gridDensity / 100
      } else {
        // 数值模式：使用固定的价格间距
        gridStep = params.gridWidth
      }
      
      // 以建仓价格为中心生成网格线
      const gridLines = []
      const halfLevels = Math.floor(params.gridLevels / 2)
      
      // 生成下方网格线（买入区域）
      for (let i = halfLevels; i >= 0; i--) {
        gridLines.push(gridCenterPrice - i * gridStep)
      }
      
      // 生成上方网格线（卖出区域）
      for (let i = 1; i <= halfLevels; i++) {
        gridLines.push(gridCenterPrice + i * gridStep)
      }
      
      // 排序网格线确保从低到高
      gridLines.sort((a, b) => a - b)
      
      // 记录网格信息用于调试
      console.log('网格基准价格:', gridCenterPrice.toFixed(2))
      console.log('网格间距:', gridStep.toFixed(2))
      console.log('网格线范围:', gridLines[0].toFixed(2), '-', gridLines[gridLines.length-1].toFixed(2))
      
      // 初始化变量
      let capital = initialCapital
      let position = 0
      let totalProfit = 0
      let tradeCount = 0
      let winCount = 0
      const profitHistory = []
      const drawdownHistory = []
      const tradeHistory = []
      const allocationHistory = []
      let maxCapital = initialCapital
      
      // 买入价格记录(用于计算胜率)
      const buyPrices = []
      
      // 连续下跌天数
      let consecutiveDownDays = 0
      
      for (let i = 0; i < prices.length; i++) {
        const currentPrice = prices[i]
        const currentDate = dates[i]
        let traded = false
        
        // 建立底仓
        if (i === basePositionIndex && params.basePositionRatio > 0) {
          const baseAmount = initialCapital * params.basePositionRatio / 100
          const shares = baseAmount / currentPrice
          const feeRate = this.moduleStates.riskControl ? params.feeRate : 0
          const fee = baseAmount * feeRate / 100
          
          capital -= (baseAmount + fee)
          position += shares
          tradeCount++
          
          tradeHistory.push({
            date: currentDate,
            type: 'buy',
            price: currentPrice,
            shares: shares,
            amount: baseAmount,
            fee: fee,
            capital: capital,
            position: position,
            reason: '底仓建立'
          })
          
          buyPrices.push(currentPrice)
          traded = true
        }
        
        // 检查连续下跌(熊市保护) - 仅在风险控制模块启用时生效
        if (i > 0 && this.moduleStates.riskControl && params.bearMarketProtection) {
          if (prices[i] < prices[i-1]) {
            consecutiveDownDays++
          } else {
            consecutiveDownDays = 0
          }
        }
        
        // 网格交易逻辑
        if (i > 0 && !traded) {
          const lastPrice = prices[i - 1]
          
          for (let j = 0; j < gridLines.length - 1; j++) {
            const lowerGrid = gridLines[j]
            const upperGrid = gridLines[j + 1]
            
            // 价格下穿买入(考虑熊市保护)
            if (lastPrice > lowerGrid && currentPrice <= lowerGrid) {
              const shouldBuy = (!this.moduleStates.riskControl || !params.bearMarketProtection) || consecutiveDownDays < params.bearMarketDays
              
              if (shouldBuy && capital > 0) {
                const currentPositionValue = position * currentPrice
                const totalAssets = capital + currentPositionValue
                const positionRatio = currentPositionValue / totalAssets * 100
                
                if (positionRatio < params.maxPositionRatio) {
                  const buyAmount = Math.min(
                    capital * params.singleTradeRatio / 100,
                    capital * 0.9 // 保留10%现金
                  )
                  
                  if (buyAmount > 100) { // 最小交易金额
                    const shares = buyAmount / currentPrice
                    const feeRate = this.moduleStates.riskControl ? params.feeRate : 0
                    const fee = buyAmount * feeRate / 100
                    
                    capital -= (buyAmount + fee)
                    position += shares
                    tradeCount++
                    
                    tradeHistory.push({
                      date: currentDate,
                      type: 'buy',
                      price: currentPrice,
                      shares: shares,
                      amount: buyAmount,
                      fee: fee,
                      capital: capital,
                      position: position,
                      reason: `网格买入 (${lowerGrid.toFixed(2)})`
                    })
                    
                    buyPrices.push(currentPrice)
                    break
                  }
                }
              }
            }
            
            // 价格上穿卖出
            if (lastPrice < upperGrid && currentPrice >= upperGrid && position > 0) {
              const sellShares = position * params.singleTradeRatio / 100
              
              if (sellShares >= 1) { // 最小卖出1股
                const sellAmount = sellShares * currentPrice
                const feeRate = this.moduleStates.riskControl ? params.feeRate : 0
                const fee = sellAmount * feeRate / 100
                
                capital += (sellAmount - fee)
                position -= sellShares
                tradeCount++
                
                // 计算胜率(如果有买入记录)
                if (buyPrices.length > 0) {
                  const avgBuyPrice = buyPrices.reduce((a, b) => a + b, 0) / buyPrices.length
                  if (currentPrice > avgBuyPrice) {
                    winCount++
                  }
                }
                
                tradeHistory.push({
                  date: currentDate,
                  type: 'sell',
                  price: currentPrice,
                  shares: sellShares,
                  amount: sellAmount,
                  fee: fee,
                  capital: capital,
                  position: position,
                  reason: `网格卖出 (${upperGrid.toFixed(2)})`
                })
                
                break
              }
            }
          }
        }
        
        // 计算当前总资产
        const currentValue = capital + position * currentPrice
        const profit = currentValue - initialCapital
        
        // 风险控制检查 (仅在模块启用时执行)
        if (this.moduleStates.riskControl) {
          if (params.stopLossRatio > 0 && profit < -initialCapital * params.stopLossRatio / 100) {
            // 触发止损
            console.log('触发止损:', profit, -initialCapital * params.stopLossRatio / 100)
          }
          
          if (params.takeProfitRatio > 0 && profit > initialCapital * params.takeProfitRatio / 100) {
            // 触发止盈
            console.log('触发止盈:', profit, initialCapital * params.takeProfitRatio / 100)
          }
        }
        
        // 其他可选模块的算法集成点 (占位)
        if (this.moduleStates.advancedStrategy) {
          // TODO: 集成高级策略算法
          // console.log('高级策略模块已启用:', params.advancedStrategy)
        }
        
        if (this.moduleStates.marketEnvironment) {
          // TODO: 集成市场环境分析
          // console.log('市场环境模块已启用:', params.marketEnvironment)
        }
        
        if (this.moduleStates.fundManagement) {
          // TODO: 集成资金管理策略
          // console.log('资金管理模块已启用:', params.fundManagement)
        }
        
        profitHistory.push({
          date: currentDate,
          profit: profit,
          totalValue: currentValue,
          profitRatio: (profit / initialCapital * 100).toFixed(2)
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
        
        // 资金分布历史
        allocationHistory.push({
          date: currentDate,
          capital: capital,
          position: position * currentPrice,
          total: currentValue
        })
        
        totalProfit = profit
      }
      
      // 计算统计指标
      const days = prices.length
      const years = days / 365
      const annualReturn = years > 0 ? ((totalProfit + initialCapital) / initialCapital - 1) / years * 100 : 0
      const maxDrawdown = Math.max(...drawdownHistory.map(d => d.drawdown))
      const winRate = tradeCount > 0 ? (winCount / tradeCount * 100).toFixed(2) : '0.00'
      const sharpeRatio = this.calculateSharpeRatio(profitHistory)
      
      // 计算价格范围用于显示
      const maxPrice = Math.max(...prices)
      const minPrice = Math.min(...prices)
      
      return {
        // 基础指标
        annualReturn: annualReturn.toFixed(2),
        totalProfit: totalProfit.toFixed(2),
        maxDrawdown: maxDrawdown.toFixed(2),
        tradeCount: tradeCount,
        winRate: winRate,
        sharpeRatio: sharpeRatio,
        
        // 价格指标
        basePositionPrice: basePositionIndex >= 0 ? prices[basePositionIndex].toFixed(2) : '未建仓',
        gridCenterPrice: gridCenterPrice.toFixed(2), // 新增：网格基准价格
        periodHighPrice: maxPrice.toFixed(2),
        periodLowPrice: minPrice.toFixed(2),
        
        // 网格信息
        gridStep: gridStep.toFixed(2), // 新增：网格间距
        gridRange: `${gridLines[0].toFixed(2)} - ${gridLines[gridLines.length-1].toFixed(2)}`, // 新增：网格覆盖范围
        
        // 历史数据
        profitHistory: profitHistory,
        drawdownHistory: drawdownHistory,
        tradeHistory: tradeHistory,
        allocationHistory: allocationHistory,
        gridLines: gridLines,
        prices: prices,
        dates: dates
      }
    },

    // 工具方法
    getBasePositionIndex(dates, params) {
      if (params.basePositionMode === 'days') {
        return Math.min(params.basePositionDays - 1, dates.length - 1)
      } else if (params.basePositionMode === 'date' && params.basePositionDate) {
        const targetDate = params.basePositionDate
        const index = dates.findIndex(date => date === targetDate)
        return index >= 0 ? index : -1
      }
      return -1
    },

    calculateSharpeRatio(profitHistory) {
      if (profitHistory.length < 2) return '0.00'
      
      const returns = []
      for (let i = 1; i < profitHistory.length; i++) {
        const dailyReturn = (profitHistory[i].totalValue - profitHistory[i-1].totalValue) / profitHistory[i-1].totalValue
        returns.push(dailyReturn)
      }
      
      const avgReturn = returns.reduce((a, b) => a + b, 0) / returns.length
      const variance = returns.reduce((a, b) => a + Math.pow(b - avgReturn, 2), 0) / returns.length
      const volatility = Math.sqrt(variance)
      
      const sharpeRatio = volatility > 0 ? (avgReturn / volatility) * Math.sqrt(252) : 0
      return sharpeRatio.toFixed(2)
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

/* 图表容器 - 保留但不使用 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  background: rgba(248, 245, 242, 0.7);
  border-radius: var(--border-radius);
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  transition: var(--transition);
  border: 1px solid rgba(212, 184, 160, 0.3);
  backdrop-filter: blur(5px);
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chart-title {
  color: #333;
  font-size: 1.1em;
  font-weight: 600;
  margin: 0;
}

.detail-button {
  background: var(--accent);
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8em;
  cursor: pointer;
  transition: var(--transition);
}

.detail-button:hover {
  background: #c4a888;
  transform: scale(1.05);
}

.chart-wrapper {
  position: relative;
  height: 250px;
}

.chart-canvas {
  width: 100% !important;
  height: 100% !important;
}

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

/* 模态框 */
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
  backdrop-filter: blur(5px);
}

.modal-content {
  background: rgba(248, 245, 242, 0.95);
  border-radius: var(--border-radius);
  width: 90%;
  max-width: 1200px;
  max-height: 85%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  animation: modalSlideIn 0.3s ease-out;
  border: 2px solid rgba(212, 184, 160, 0.4);
  backdrop-filter: blur(10px);
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
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
  transition: var(--transition);
}

.close-button:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 25px;
  flex: 1;
  overflow: auto;
}

.detail-chart-canvas {
  width: 100% !important;
  height: 450px !important;
}

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
