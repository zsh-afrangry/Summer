<template>
  <div class="config-cards-row">
    <!-- 风险控制卡片 -->
    <div class="config-card optional-module" :class="{ 'module-disabled': !localModuleStates.riskControl }">
      <div class="module-header">
        <h3 class="config-title">🛡️ 风险控制</h3>
        <label class="module-toggle">
          <input type="checkbox" v-model="localModuleStates.riskControl" />
          <span class="toggle-text">启用</span>
        </label>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label>止损比例 (%)</label>
          <input v-model.number="localParameters.stopLossRatio" type="number" min="0" max="20" step="1" class="form-input" placeholder="0=不止损" :disabled="!localModuleStates.riskControl">
        </div>
        <div class="form-group">
          <label>止盈比例 (%)</label>
          <input v-model.number="localParameters.takeProfitRatio" type="number" min="0" max="50" step="5" class="form-input" placeholder="0=不止盈" :disabled="!localModuleStates.riskControl">
        </div>
        <div class="form-group">
          <label>最大回撤限制 (%)</label>
          <input v-model.number="localParameters.maxDrawdownLimit" type="number" min="0" max="30" step="5" class="form-input" placeholder="0=无限制" :disabled="!localModuleStates.riskControl">
        </div>
        <div class="form-group">
          <label>手续费率 (%)</label>
          <input v-model.number="localParameters.feeRate" type="number" min="0" max="1" step="0.01" class="form-input" :disabled="!localModuleStates.riskControl">
        </div>
      </div>
      <div class="form-group full-width">
        <label class="checkbox-label">
          <input type="checkbox" v-model="localParameters.bearMarketProtection" :disabled="!localModuleStates.riskControl" />
          <span>熊市保护 (连续下跌{{ parameters.bearMarketDays }}天暂停买入)</span>
        </label>
      </div>
    </div>

    <!-- 高级策略卡片 (占位) -->
    <div class="config-card optional-module" :class="{ 'module-disabled': !localModuleStates.advancedStrategy }">
      <div class="module-header">
        <h3 class="config-title">📈 高级策略</h3>
        <label class="module-toggle">
          <input type="checkbox" v-model="localModuleStates.advancedStrategy" />
          <span class="toggle-text">启用</span>
        </label>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label>标题标题标题</label>
          <input v-model.number="localParameters.advancedStrategy.trendSensitivity" type="number" min="0" max="100" class="form-input" placeholder="正文正文正文" :disabled="!localModuleStates.advancedStrategy">
        </div>
        <div class="form-group">
          <label>标题标题标题</label>
          <input v-model.number="localParameters.advancedStrategy.volatilityThreshold" type="number" min="0" step="0.1" class="form-input" placeholder="正文正文正文" :disabled="!localModuleStates.advancedStrategy">
        </div>
      </div>
      <div class="form-group full-width">
        <label class="checkbox-label">
          <input type="checkbox" v-model="localParameters.advancedStrategy.enableDynamicGrid" :disabled="!localModuleStates.advancedStrategy" />
          <span>标题标题标题 (正文正文正文)</span>
        </label>
      </div>
    </div>

    <!-- 市场环境卡片 (占位) -->
    <div class="config-card optional-module" :class="{ 'module-disabled': !localModuleStates.marketEnvironment }">
      <div class="module-header">
        <h3 class="config-title">🌍 市场环境</h3>
        <label class="module-toggle">
          <input type="checkbox" v-model="localModuleStates.marketEnvironment" />
          <span class="toggle-text">启用</span>
        </label>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label>标题标题标题</label>
          <select v-model="localParameters.marketEnvironment.marketSentiment" class="form-select" :disabled="!localModuleStates.marketEnvironment">
            <option value="bullish">正文正文正文 A</option>
            <option value="neutral">正文正文正文 B</option>
            <option value="bearish">正文正文正文 C</option>
          </select>
        </div>
        <div class="form-group">
          <label>标题标题标题</label>
          <input v-model.number="localParameters.marketEnvironment.macroFactor" type="number" min="0" step="0.1" class="form-input" placeholder="正文正文正文" :disabled="!localModuleStates.marketEnvironment">
        </div>
      </div>
      <div class="form-group full-width">
        <label class="checkbox-label">
          <input type="checkbox" v-model="localParameters.marketEnvironment.sectorRotation" :disabled="!localModuleStates.marketEnvironment" />
          <span>标题标题标题 (正文正文正文)</span>
        </label>
      </div>
    </div>

    <!-- 资金管理卡片 (占位) -->
    <div class="config-card optional-module" :class="{ 'module-disabled': !localModuleStates.fundManagement }">
      <div class="module-header">
        <h3 class="config-title">💼 资金管理</h3>
        <label class="module-toggle">
          <input type="checkbox" v-model="localModuleStates.fundManagement" />
          <span class="toggle-text">启用</span>
        </label>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label>标题标题标题</label>
          <input v-model.number="localParameters.fundManagement.riskBudget" type="number" min="0" max="100" class="form-input" placeholder="正文正文正文" :disabled="!localModuleStates.fundManagement">
        </div>
        <div class="form-group">
          <label>标题标题标题</label>
          <select v-model="localParameters.fundManagement.dynamicPosition" class="form-select" :disabled="!localModuleStates.fundManagement">
            <option :value="false">正文正文正文 A</option>
            <option :value="true">正文正文正文 B</option>
          </select>
        </div>
      </div>
      <div class="form-group full-width">
        <label class="checkbox-label">
          <input type="checkbox" v-model="localParameters.fundManagement.batchBuilding" :disabled="!localModuleStates.fundManagement" />
          <span>标题标题标题 (正文正文正文)</span>
        </label>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ParamsOptional',
  props: {
    parameters: { type: Object, required: true },
    moduleStates: { type: Object, required: true }
  },
  emits: ['update:parameters', 'update:moduleStates'],
  data() {
    return {
      localParameters: JSON.parse(JSON.stringify(this.parameters || {})),
      localModuleStates: JSON.parse(JSON.stringify(this.moduleStates || {})),
      isSyncingFromParent: false,
      isSyncingFromParentStates: false
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
    moduleStates: {
      deep: true,
      handler(newVal) {
        this.isSyncingFromParentStates = true
        this.localModuleStates = JSON.parse(JSON.stringify(newVal || {}))
        this.$nextTick(() => { this.isSyncingFromParentStates = false })
      }
    },
    localParameters: {
      deep: true,
      handler(newVal) {
        if (this.isSyncingFromParent) return
        this.$emit('update:parameters', JSON.parse(JSON.stringify(newVal)))
      }
    },
    localModuleStates: {
      deep: true,
      handler(newVal) {
        if (this.isSyncingFromParentStates) return
        this.$emit('update:moduleStates', JSON.parse(JSON.stringify(newVal)))
      }
    }
  }
}
</script>

<style scoped>
.config-cards-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 25px; }
@media (max-width: 1400px) { .config-cards-row { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 900px) { .config-cards-row { grid-template-columns: 1fr; } }
.config-card { background: rgba(255,255,255,0.7); border-radius: var(--border-radius); padding: 20px; border: 1px solid rgba(212,184,160,0.3); transition: var(--transition); backdrop-filter: blur(5px); }
.config-card:hover { transform: translateY(-2px); box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.config-title { color: #333; margin-bottom: 15px; font-size: 1.1em; font-weight: 600; display: flex; align-items: center; gap: 8px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 15px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group.full-width { grid-column: 1 / -1; }
.form-group label { color: #555; font-weight: 600; font-size: 0.9em; }
.form-input, .form-select { padding: 10px 12px; border: 2px solid rgba(212,184,160,0.3); border-radius: 6px; font-size: 0.9em; transition: var(--transition); background: rgba(255,255,255,0.8); backdrop-filter: blur(3px); }
.form-input:focus, .form-select:focus { outline: none; border-color: var(--accent); box-shadow: 0 0 0 3px rgba(212,184,160,0.1); }
.checkbox-label { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 8px; border-radius: 6px; transition: var(--transition); }
.checkbox-label:hover { background: rgba(212,184,160,0.1); }
.optional-module { position: relative; }
.module-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
.module-toggle { display: flex; align-items: center; gap: 6px; cursor: pointer; padding: 4px 8px; border-radius: 6px; background: rgba(212,184,160,0.1); transition: var(--transition); font-size: 0.85em; font-weight: 500; }
.module-toggle:hover { background: rgba(212,184,160,0.2); }
.module-disabled { opacity: 0.6; background: rgba(248,245,242,0.4) !important; pointer-events: auto; }
.module-disabled .form-input:disabled, .module-disabled .form-select:disabled, .module-disabled .checkbox-label input:disabled { background: rgba(200,200,200,0.3); color: #999; cursor: not-allowed; }
.module-disabled .checkbox-label { opacity: 0.5; cursor: not-allowed; }
.module-disabled .module-toggle { opacity: 1; pointer-events: auto; }
</style>

