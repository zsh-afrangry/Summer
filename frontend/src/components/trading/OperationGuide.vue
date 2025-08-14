<template>
  <div class="guide-container">
    <div class="guide-header">
      <button class="back-btn" @click="$router.back()">← 返回</button>
      <h1 class="guide-title">网格交易策略分析系统操作说明</h1>
    </div>

    <!-- 顶部快捷 -->
    <div class="guide-section">
      <h2>顶部快捷</h2>
      <ul>
        <li>← 返回首页：回到入口页</li>
        <li>操作说明：打开本指南页面</li>
      </ul>
    </div>

    <!-- 参数配置 -->
    <div class="guide-section">
      <h2>参数配置</h2>
      <ul>
        <li>必选模块（4项）：数据源、资金配置、网格配置、建仓配置</li>
        <li>可选模块（4项）：风险控制、高级策略、市场环境、资金管理（勾选启用）</li>
        <li>建仓模式：
          <ul>
            <li>从最早日期向后N天建仓：输入天数，系统在“建仓时间”中自动计算</li>
            <li>指定具体建仓日期：直接选择日期</li>
          </ul>
        </li>
        <li>开始分析：点击“🚀 开始分析”运行回测；完成后可进入可视化中心查看大图</li>
      </ul>
    </div>

    <!-- 指标说明 -->
    <div class="guide-section">
      <h2>指标说明</h2>
      <ul>
        <li>年化收益率：回测期间折算到年的收益率</li>
        <li>总收益：收益金额（页面展示为取整）</li>
        <li>最大回撤：回测期间的最大资产回撤比例</li>
        <li>交易次数：买入与卖出总次数</li>
        <li>夏普比率：风险调整后收益，越高越好</li>
        <li>胜率：盈利交易占比</li>
        <li>价格信息：底仓建仓价、建仓时间（开始分析后显示）、期间最高/最低、网格间距与覆盖范围</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OperationGuide'
}
</script>

<style scoped>
.guide-container {
  min-height: 100vh;
  background: var(--secondary);
  color: var(--text);
  padding: 24px;
}
.guide-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.guide-title {
  margin: 0 auto;
  font-weight: 600;
}
.back-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 16px;
  cursor: pointer;
}
.guide-section {
  background: rgba(255,255,255,0.8);
  border-radius: var(--border-radius);
  padding: 16px 20px;
  box-shadow: var(--card-shadow);
  margin-bottom: 16px;
}
.legend {
  display: inline-block;
  width: 26px;
  height: 4px;
  border-radius: 2px;
  margin: 0 6px;
}
.legend-profit { background: #4CAF50; }
.legend-benchmark { background: #9E9E9E; border-bottom: 2px dashed #9E9E9E; }
.legend-price { background: #2196F3; }
.legend-grid { background: transparent; border-bottom: 2px dashed #999; width: 40px; }
.legend-dd { background: #FF9800; }
.legend-cash { background: rgba(156, 39, 176, 0.8); }
.legend-pos { background: rgba(33, 150, 243, 0.8); }
</style>

