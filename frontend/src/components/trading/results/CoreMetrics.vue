<template>
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
          <div class="metric-value">{{ formattedTotalProfit }} 元</div>
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
  
</template>

<script>
export default {
  name: 'CoreMetrics',
  props: {
    analysisResults: { type: Object, required: true }
  },
  computed: {
    formattedTotalProfit() {
      const raw = Number(this.analysisResults?.totalProfit ?? 0)
      const truncated = Number.isFinite(raw) ? Math.trunc(raw) : 0
      return truncated.toLocaleString()
    }
  }
}
</script>

<style scoped>
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

.metric-icon { font-size: 2em; opacity: 0.8; }
.metric-content { flex: 1; }
.metric-label { font-size: 0.9em; opacity: 0.8; margin-bottom: 5px; }
.metric-value { font-size: 1.6em; font-weight: 700; }
</style>

