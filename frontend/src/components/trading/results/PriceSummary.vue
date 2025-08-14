<template>
  <div class="price-info-section">
    <h3 class="section-title">💹 价格信息</h3>
    <div class="price-info-grid">
      <div class="price-info-item">
        <span class="price-label">底仓建仓价:</span>
        <span class="price-value">{{ analysisResults.basePositionPrice }}元</span>
      </div>
      <div class="price-info-item">
        <span class="price-label">建仓时间:</span>
        <span class="price-value">{{ analysisResults.basePositionDate || '（开始分析后显示）' }}</span>
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
</template>

<script>
export default {
  name: 'PriceSummary',
  props: {
    analysisResults: { type: Object, required: true }
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

.price-label { color: #666; font-weight: 500; }
.price-value { font-weight: 600; color: #333; }
.price-value.high-price { color: var(--danger-color); }
.price-value.low-price { color: var(--success-color); }
.price-value.grid-center { color: var(--accent); font-weight: 700; }
.price-value.grid-step { color: var(--warning-color); }
.price-value.grid-range { color: #9C27B0; font-size: 0.9em; }
.price-info-item.highlight-item {
  background: linear-gradient(135deg, rgba(212, 184, 160, 0.2), rgba(212, 184, 160, 0.1));
  border: 2px solid rgba(212, 184, 160, 0.4);
}
</style>

