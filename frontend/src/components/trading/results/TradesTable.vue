<template>
  <div class="trades-section">
    <h3 class="section-title">📋 交易记录 (共{{ trades.length }}笔)</h3>
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
          <tr v-for="(trade, index) in trades" :key="index" :class="trade.type">
            <td>{{ trade.date }}</td>
            <td>
              <span class="trade-type" :class="trade.type">
                {{ trade.type === 'buy' ? '买入' : '卖出' }}
              </span>
            </td>
            <td>{{ toFixed(trade.price, 2) }}</td>
            <td>{{ toFixed(trade.shares, 0) }}</td>
            <td>{{ toFixed(trade.amount, 2) }}</td>
            <td>{{ toFixed(trade.fee, 2) }}</td>
            <td>{{ toFixed(trade.capital, 2) }}</td>
            <td>{{ toFixed(trade.position, 0) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TradesTable',
  props: {
    trades: { type: Array, required: true }
  },
  methods: {
    toFixed(value, digits) {
      if (value === undefined || value === null || Number.isNaN(Number(value))) return '-'
      return Number(value).toFixed(digits)
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

.trades-table-wrapper { overflow-x: auto; border-radius: var(--border-radius); box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.trades-table { width: 100%; border-collapse: collapse; background: rgba(255,255,255,0.9); border-radius: var(--border-radius); overflow: hidden; backdrop-filter: blur(5px); border: 1px solid rgba(212,184,160,0.2); }
.trades-table th { background: rgba(248,245,242,0.8); color: #555; font-weight: 600; padding: 12px 8px; text-align: left; border-bottom: 2px solid rgba(212,184,160,0.3); font-size: 0.9em; }
.trades-table td { padding: 10px 8px; border-bottom: 1px solid #f0f0f0; font-size: 0.9em; }
.trades-table tr:hover { background: rgba(248,245,242,0.6); }
.trades-table tr.buy td { border-left: 3px solid var(--primary-color); }
.trades-table tr.sell td { border-left: 3px solid var(--danger-color); }
.trade-type { padding: 4px 8px; border-radius: 12px; font-size: 0.8em; font-weight: 600; }
.trade-type.buy { background: rgba(76,175,80,0.2); color: var(--primary-color); }
.trade-type.sell { background: rgba(244,67,54,0.2); color: var(--danger-color); }
</style>

