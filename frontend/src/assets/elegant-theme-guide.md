# 🎨 Elegant Theme - 优雅主题使用指南

## 📋 概述

Elegant Theme 是基于 ElegantPortalPage 和 TradingPage 设计语言提取的通用样式系统，提供一致的视觉体验和组件样式。

## 🚀 快速开始

### 1. 引入主题文件

在你的 Vue 组件中引入主题样式：

```vue
<style>
@import '../assets/elegant-theme.css';
</style>
```

或在 `main.js` 中全局引入：

```javascript
import './assets/elegant-theme.css'
```

### 2. 使用主题容器

```vue
<template>
  <div class="elegant-container">
    <!-- 你的内容 -->
  </div>
</template>
```

## 🏗️ 核心组件

### 📦 容器系统

```vue
<!-- 主容器 -->
<div class="elegant-container">
  <!-- 主内容区 -->
  <div class="elegant-main-content">
    <h1 class="elegant-title">页面标题</h1>
    
    <!-- 分节内容 -->
    <div class="elegant-section">
      <h2>节标题</h2>
      <!-- 内容 -->
    </div>
  </div>
</div>
```

### 🃏 卡片系统

```vue
<!-- 基础卡片 -->
<div class="elegant-card">
  <div class="elegant-card-header">
    <h3 class="elegant-card-title">📊 卡片标题</h3>
  </div>
  <p>卡片内容...</p>
</div>

<!-- 指标卡片网格 -->
<div class="elegant-metrics-grid">
  <div class="elegant-metric-card">
    <div class="elegant-metric-icon">📈</div>
    <div class="elegant-metric-content">
      <div class="elegant-metric-label">年化收益率</div>
      <div class="elegant-metric-value">6.07%</div>
    </div>
  </div>
  
  <div class="elegant-metric-card highlight">
    <div class="elegant-metric-icon">💰</div>
    <div class="elegant-metric-content">
      <div class="elegant-metric-label">总收益</div>
      <div class="elegant-metric-value">5688元</div>
    </div>
  </div>
</div>
```

### 📝 表单系统

```vue
<div class="elegant-form-group">
  <label class="elegant-form-label">初始资金 (万元)</label>
  <input 
    class="elegant-form-input" 
    type="number" 
    placeholder="请输入金额"
  >
</div>

<div class="elegant-form-group">
  <label class="elegant-form-label">交易模式</label>
  <select class="elegant-form-select">
    <option>网格交易</option>
    <option>定投策略</option>
  </select>
</div>
```

### 🔘 按钮系统

```vue
<!-- 主要按钮 -->
<button class="elegant-btn elegant-btn-primary">
  🚀 开始分析
</button>

<!-- 次要按钮 -->
<button class="elegant-btn elegant-btn-secondary">
  查看详情
</button>
```

### 📊 表格系统

```vue
<div class="elegant-table-wrapper">
  <table class="elegant-table">
    <thead>
      <tr>
        <th>日期</th>
        <th>类型</th>
        <th>金额</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>2024-01-01</td>
        <td>买入</td>
        <td>1000.00</td>
      </tr>
    </tbody>
  </table>
</div>
```

### 💬 模态框系统

```vue
<div v-if="showModal" class="elegant-modal-overlay" @click="closeModal">
  <div class="elegant-modal-content" @click.stop>
    <div class="elegant-card-header">
      <h3 class="elegant-card-title">详情查看</h3>
    </div>
    <!-- 模态框内容 -->
  </div>
</div>
```

### ℹ️ 信息展示系统

```vue
<div class="elegant-info-grid">
  <div class="elegant-info-item">
    <span class="elegant-text-light">建仓价格:</span>
    <span class="elegant-text-primary">25.50元</span>
  </div>
  <div class="elegant-info-item">
    <span class="elegant-text-light">最高价:</span>
    <span class="elegant-text-accent">28.90元</span>
  </div>
</div>
```

## 🎨 色彩系统

### 主色彩变量

```css
--primary: #1a1a1a        /* 深黑色 - 主色 */
--secondary: #f8f5f2      /* 米黄色 - 背景主色 */
--accent: #d4b8a0         /* 米色 - 强调色 */
--text: #333333           /* 文本主色 */
--light-text: #777777     /* 浅色文本 */
```

### 功能色彩

```css
--success-color: #4CAF50  /* 成功色 */
--warning-color: #FF9800  /* 警告色 */
--danger-color: #F44336   /* 危险色 */
--info-color: #2196F3     /* 信息色 */
```

### 工具类使用

```vue
<!-- 文本颜色 -->
<span class="elegant-text-primary">主色文本</span>
<span class="elegant-text-accent">强调色文本</span>
<span class="elegant-text-light">浅色文本</span>

<!-- 背景色 -->
<div class="elegant-bg-primary">主背景</div>
<div class="elegant-bg-secondary">次背景</div>
<div class="elegant-bg-accent">强调背景</div>

<!-- 边框 -->
<div class="elegant-border-light">浅边框</div>
<div class="elegant-border-medium">中等边框</div>
<div class="elegant-border-strong">强边框</div>

<!-- 阴影 -->
<div class="elegant-shadow-sm">小阴影</div>
<div class="elegant-shadow-md">中等阴影</div>
<div class="elegant-shadow-lg">大阴影</div>
```

## ✨ 动画效果

```vue
<!-- 淡入动画 -->
<div class="elegant-fade-in">内容</div>

<!-- 向上滑入动画 -->
<div class="elegant-slide-up">内容</div>

<!-- 加载动画 -->
<div class="elegant-loading">⏳</div>
```

## 📱 响应式支持

主题自带响应式设计，支持：
- 桌面端 (>1200px)
- 平板端 (768px-1200px)  
- 移动端 (<768px)

## 🌙 深色模式

主题自动支持系统深色模式偏好，无需额外配置。

## 🔧 自定义扩展

### 覆盖 CSS 变量

```vue
<style scoped>
.my-component {
  --accent: #your-color;
  --border-radius: 8px;
}
</style>
```

### 扩展工具类

```vue
<style>
.my-custom-card {
  @extend .elegant-card;
  /* 你的自定义样式 */
}
</style>
```

## 📚 完整示例

```vue
<template>
  <div class="elegant-container">
    <div class="elegant-main-content">
      <!-- 页面标题 -->
      <div class="elegant-section">
        <h1 class="elegant-title">数据分析系统</h1>
        <p class="elegant-text-light">专业的数据分析平台</p>
      </div>

      <!-- 配置区域 -->
      <div class="elegant-section">
        <h2 class="elegant-card-title">📊 参数配置</h2>
        
        <div class="elegant-form-group">
          <label class="elegant-form-label">初始资金</label>
          <input class="elegant-form-input" type="number" v-model="capital">
        </div>
        
        <button class="elegant-btn elegant-btn-primary" @click="analyze">
          🚀 开始分析
        </button>
      </div>

      <!-- 结果展示 -->
      <div class="elegant-section" v-if="results">
        <h2 class="elegant-card-title">📈 分析结果</h2>
        
        <div class="elegant-metrics-grid">
          <div class="elegant-metric-card highlight">
            <div class="elegant-metric-icon">📊</div>
            <div class="elegant-metric-content">
              <div class="elegant-metric-label">收益率</div>
              <div class="elegant-metric-value">{{ results.return }}%</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
@import '../assets/elegant-theme.css';
</style>
```

## 🆚 迁移现有组件

如果要将现有组件迁移到 Elegant Theme：

1. **替换根容器类名**：`.your-container` → `.elegant-container`
2. **替换卡片类名**：`.your-card` → `.elegant-card`
3. **替换表单类名**：`.your-input` → `.elegant-form-input`
4. **使用主题色彩变量**：硬编码颜色 → CSS 变量
5. **应用工具类**：简化重复样式代码

---

## 💡 提示

- 主题基于 Montserrat 和 Cormorant Garamond 字体
- 支持现代浏览器的 backdrop-filter 效果
- 所有组件都具有平滑的过渡动画
- 自动适配深色模式和打印样式
- 完全响应式设计，移动端友好

享受优雅一致的设计体验！✨