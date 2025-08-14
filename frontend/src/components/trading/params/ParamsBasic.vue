<template>
  <div class="config-cards-row">
    <!-- 数据源配置卡片 -->
    <div class="config-card">
      <h3 class="config-title">📁 数据源选择</h3>
      <div class="data-source-options">
        <label class="radio-option">
          <input type="radio" v-model="localParameters.dataSource" value="upload" />
          <span>本地文件上传</span>
        </label>
        <label class="radio-option">
          <input type="radio" v-model="localParameters.dataSource" value="project" />
          <span>项目文件</span>
        </label>
      </div>

      <!-- 文件上传区域 -->
      <div v-if="localParameters.dataSource === 'upload'" class="upload-area">
        <input 
          type="file" 
          id="csvFile" 
          accept=".csv" 
          @change="onFileChange"
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
      <div v-else-if="localParameters.dataSource === 'project'" class="file-selection">
        <div class="form-group">
          <label>选择项目文件</label>
          <select v-model="localParameters.selectedProjectFile" class="form-select">
            <option value="600585">海螺水泥 (600585)</option>
            <option value="002032">苏泊尔 (002032)</option>
            <option value="700001">东方财富 (700001)</option>
          </select>
        </div>
        <div class="file-info">
          <div class="info-item">
            <span class="info-label">文件:</span>
            <span class="info-value">{{ localParameters.selectedProjectFile }}历史数据.csv</span>
          </div>
          <div class="info-item">
            <span class="info-label">股票:</span>
            <span class="info-value">{{ getStockName(localParameters.selectedProjectFile) }}</span>
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
          <input v-model.number="localParameters.initialCapital" type="number" min="1" step="1" class="form-input">
        </div>
        <div class="form-group">
          <label>底仓比例 (%)</label>
          <input v-model.number="localParameters.basePositionRatio" type="number" min="0" max="50" step="5" class="form-input">
        </div>
        <div class="form-group">
          <label>单次交易比例 (%)</label>
          <input v-model.number="localParameters.singleTradeRatio" type="number" min="1" max="20" step="1" class="form-input">
        </div>
        <div class="form-group">
          <label>最大持仓比例 (%)</label>
          <input v-model.number="localParameters.maxPositionRatio" type="number" min="50" max="95" step="5" class="form-input">
        </div>
      </div>
    </div>

    <!-- 网格配置卡片 -->
    <div class="config-card">
      <h3 class="config-title">📊 网格配置</h3>
      <div class="form-grid">
        <div class="form-group">
          <label>网格层数</label>
          <input v-model.number="localParameters.gridLevels" type="number" min="5" max="50" class="form-input">
        </div>
        <div class="form-group full-width">
          <label>网格宽度模式</label>
          <select v-model="localParameters.gridWidthMode" class="form-select">
            <option value="percentage">百分比模式</option>
            <option value="value">数值模式</option>
          </select>
        </div>
        <div v-if="localParameters.gridWidthMode === 'percentage'" class="form-group">
          <label>网格密度 (%)</label>
          <input v-model.number="localParameters.gridDensity" type="number" min="0.5" max="10" step="0.5" class="form-input" placeholder="例如：2.0">
          <small class="form-hint">网格覆盖价格波动的百分比范围</small>
        </div>
        <div v-else class="form-group">
          <label>网格宽度 (元)</label>
          <input v-model.number="localParameters.gridWidth" type="number" min="0.01" step="0.01" class="form-input" placeholder="例如：50">
          <small class="form-hint">每个网格的固定价格间距</small>
        </div>
      </div>
    </div>

    <!-- 建仓配置卡片 -->
    <div class="config-card">
      <h3 class="config-title">⏰ 建仓配置</h3>
      <div class="form-grid">
        <div class="form-group full-width">
          <label>建仓模式</label>
          <select v-model="localParameters.basePositionMode" class="form-select">
            <option value="days">从最早日期向后N天建仓</option>
            <option value="date">指定具体建仓日期</option>
          </select>
        </div>
        <div v-if="localParameters.basePositionMode === 'days'" class="form-group full-width">
          <label>建仓天数 (从最早日期开始)</label>
          <input v-model.number="localParameters.basePositionDays" type="number" min="1" max="100" class="form-input" placeholder="例如：5表示第5天建仓">
        </div>
        <div v-else class="form-group">
          <label>建仓日期 (选择具体日期)</label>
          <input v-model="localParameters.basePositionDate" type="date" class="form-input" :min="earliestDate || undefined" :max="latestDate || undefined">
        </div>
      </div>
      <div class="form-group full-width">
        <label>建仓时间</label>
        <input :value="computedBuildDate" class="form-input" readonly>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ParamsBasic',
  props: {
    parameters: { type: Object, required: true },
    actualDataCount: { type: [String, Number], required: false, default: '' },
    getStockName: { type: Function, required: true },
    fileName: { type: String, required: false, default: '' },
    earliestDate: { type: String, required: false, default: '' },
    latestDate: { type: String, required: false, default: '' }
  },
  emits: ['file-upload', 'update:parameters'],
  data() {
    return {
      localParameters: JSON.parse(JSON.stringify(this.parameters || {})),
      isSyncingFromParent: false
    }
  },
  computed: {
    computedBuildDate() {
      const mode = this.localParameters?.basePositionMode
      if (mode === 'date') {
        return this.localParameters?.basePositionDate || ''
      }
      const baseDays = Number(this.localParameters?.basePositionDays || 0)
      const start = this.earliestDate
      if (!start || !baseDays || Number.isNaN(baseDays)) return ''
      const d = new Date(start)
      // 减1是因为第1天即最早日期本身
      d.setDate(d.getDate() + Math.max(baseDays - 1, 0))
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    }
  },
  watch: {
    parameters: {
      deep: true,
      handler(newVal) {
        this.isSyncingFromParent = true
        this.localParameters = JSON.parse(JSON.stringify(newVal || {}))
        this.$nextTick(() => { this.isSyncingFromParent = false })
      }
    },
    localParameters: {
      deep: true,
      handler(newVal) {
        if (this.isSyncingFromParent) return
        this.$emit('update:parameters', JSON.parse(JSON.stringify(newVal)))
      }
    }
  },
  methods: {
    onFileChange(e) { this.$emit('file-upload', e) }
  }
}
</script>

<style scoped>
/* 复用父级样式体系的局部定义，以确保拆分后视觉一致 */
.config-cards-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 25px; }
@media (max-width: 1400px) { .config-cards-row { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 900px) { .config-cards-row { grid-template-columns: 1fr; } }
.config-card { background: rgba(255,255,255,0.7); border-radius: var(--border-radius); padding: 20px; border: 1px solid rgba(212,184,160,0.3); transition: var(--transition); backdrop-filter: blur(5px); }
.config-card:hover { transform: translateY(-2px); box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.config-title { color: #333; margin-bottom: 15px; font-size: 1.1em; font-weight: 600; display: flex; align-items: center; gap: 8px; }
.data-source-options { display: flex; flex-direction: column; gap: 10px; margin-bottom: 15px; }
.radio-option { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 8px; border-radius: 6px; transition: var(--transition); }
.radio-option:hover { background: rgba(212,184,160,0.1); }
.radio-option input[type="radio"] { margin: 0; }
.upload-area { border: 2px dashed #ddd; border-radius: 8px; padding: 20px; text-align: center; transition: var(--transition); cursor: pointer; }
.upload-area:hover { border-color: var(--accent); background: rgba(212,184,160,0.05); }
.file-input { display: none; }
.file-upload-label { cursor: pointer; display: block; }
.upload-icon { font-size: 2em; margin-bottom: 10px; }
.upload-text { color: #666; font-weight: 500; }
.file-selection { display: flex; flex-direction: column; gap: 15px; }
.file-info { background: rgba(212,184,160,0.15); border-radius: 8px; padding: 15px; border: 1px solid rgba(212,184,160,0.3); backdrop-filter: blur(3px); }
.info-item { display: flex; justify-content: space-between; margin-bottom: 8px; }
.info-item:last-child { margin-bottom: 0; }
.info-label { color: #666; font-weight: 500; }
.info-value { color: #333; font-weight: 600; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 15px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group.full-width { grid-column: 1 / -1; }
.form-group label { color: #555; font-weight: 600; font-size: 0.9em; }
.form-input, .form-select { padding: 10px 12px; border: 2px solid rgba(212,184,160,0.3); border-radius: 6px; font-size: 0.9em; transition: var(--transition); background: rgba(255,255,255,0.8); backdrop-filter: blur(3px); }
.form-input:focus, .form-select:focus { outline: none; border-color: var(--accent); box-shadow: 0 0 0 3px rgba(212,184,160,0.1); }
.form-input::placeholder { color: #999; font-style: italic; }
.form-hint { color: #888; font-size: 0.8em; margin-top: 4px; font-style: italic; }
</style>

