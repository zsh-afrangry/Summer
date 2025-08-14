### trading 模块说明

#### 目录结构

```
src/components/trading/
├─ TradingPage.vue                 # 主页面，负责参数收集、触发分析、展示结果与入口
├─ ChartVisualization.vue          # 可视化分析中心（ECharts 累计收益等图表）
│
├─ params/                         # 参数配置区（表单）
│  ├─ ParamsBasic.vue              # 固定功能块：数据源/资金/网格/建仓
│  └─ ParamsOptional.vue           # 可选功能块：风险控制/高级策略/市场环境/资金管理
│
└─ results/                        # 结果展示区（纯展示组件）
   ├─ CoreMetrics.vue              # 核心指标（年化、总收益、回撤、夏普、胜率、次数）
   ├─ PriceSummary.vue             # 价格信息（建仓价、基准价、最高/最低、网格参数）
   └─ TradesTable.vue              # 交易记录表格（显示全部）

src/services/trading/
└─ gridTrading.js                  # 纯函数：CSV 解析、网格算法、夏普比率等
```

#### 各文件职责

- TradingPage.vue
  - 组合页面：引入表单组件与结果组件
  - 读取/加载 CSV，调用 `calculateGridTrading` 生成 `analysisResults`
  - 预览卡片跳转到可视化分析中心，写入 `sessionStorage`

- ChartVisualization.vue
  - 从 `props.analysisResults` 或 `sessionStorage` 读取数据
  - 使用 ECharts 渲染图表（当前保留“累计收益曲线”）

- params/ParamsBasic.vue
  - 固定参数表单块：数据源（上传/项目文件）、资金、网格、建仓
  - 通过 `@file-upload` 把上传事件抛给父组件处理

- params/ParamsOptional.vue
  - 可选模块表单块：风险控制/高级策略/市场环境/资金管理
  - 仅负责表单绑定与开关控制

- results/CoreMetrics.vue
  - 纯展示核心指标

- results/PriceSummary.vue
  - 纯展示价格信息/网格信息

- results/TradesTable.vue
  - 交易记录表格（全量渲染，已去除“仅20笔”的限制）

- services/trading/gridTrading.js
  - `parseCSV(content)`：CSV 文本 → 数据数组
  - `calculateGridTrading(data, params, moduleStates)`：网格交易主算法（纯函数）
  - `calculateSharpeRatio(profitHistory)` 等工具函数

#### 数据流与交互

- 参数表单通过 `v-model` 直接绑定到 `TradingPage` 的 `parameters/moduleStates`
- 点击“开始分析”触发：加载数据 → 执行 `calculateGridTrading` → 生成 `analysisResults`
- 进入“可视化分析中心”：将 `analysisResults` 写入 `sessionStorage`，再跳转到 `/chart-visualization`
  - 如需更稳妥的数据传递，可改为同标签路由跳转或使用 `localStorage`/路由 `state`

#### 主题与样式

- 已有主题文件：`src/assets/elegant-theme.css` 与指南 `src/assets/elegant-theme-guide.md`
- 推荐做法：在 `main.js` 全局引入一次，统一变量与样式

```js
// src/main.js
import './assets/elegant-theme.css'
```

- 小提示：若组件内仍有 `:root` 变量定义，可逐步移除，统一使用全局主题变量，减少重复与冲突

