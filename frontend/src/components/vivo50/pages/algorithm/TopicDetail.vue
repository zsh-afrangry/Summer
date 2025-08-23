<template>
  <div class="elegant-container topic-detail-container">
    <div class="topic-detail-layout">
      <!-- 左侧导航栏 -->
      <aside class="topic-sidebar">
        <div class="sidebar-header">
          <button class="back-btn" @click="goBack">
            <i>←</i> 返回
          </button>
          <h3 class="sidebar-title">{{ currentTopic.name }}</h3>
        </div>
        
        <div class="sidebar-content">
          <div class="sidebar-section">
            <h4 class="section-title">📚 学习大纲</h4>
            <ul class="outline-list">
              <li 
                v-for="section in learningSections" 
                :key="section.id"
                class="outline-item"
                :class="{ active: activeSection === section.id }"
                @click="scrollToSection(section.id)"
              >
                <span class="outline-icon">{{ section.icon }}</span>
                <span class="outline-text">{{ section.title }}</span>
                <span class="outline-status" :class="section.status">
                  {{ getStatusIcon(section.status) }}
                </span>
              </li>
            </ul>
          </div>
        </div>
      </aside>

      <!-- 主内容区域 -->
      <main class="topic-main-content">
        <!-- 页面头部 -->
        <header class="topic-header" id="header">
          <div class="topic-title-section">
            <div class="topic-icon-large">{{ currentTopic.icon }}</div>
            <div class="topic-title-info">
              <h1 class="topic-main-title">{{ currentTopic.name }}</h1>
              <p class="topic-subtitle">{{ currentTopic.description }}</p>
              <div class="topic-meta">
                <span class="meta-item">
                  <i>⏱️</i> 预计 {{ currentTopic.estimatedHours }}h
                </span>
                <span class="meta-item">
                  <i>📊</i> {{ currentTopic.totalProblems }} 道题
                </span>
                <span class="meta-item difficulty" :class="`difficulty-${currentTopic.difficulty}`">
                  <i>🎯</i> {{ getDifficultyText(currentTopic.difficulty) }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="topic-progress-section">
            <div class="progress-circle">
              <svg viewBox="0 0 36 36" class="circular-chart">
                <path class="circle-bg"
                  d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path class="circle"
                  :stroke-dasharray="`${currentTopic.progress}, 100`"
                  d="M18 2.0845
                    a 15.9155 15.9155 0 0 1 0 31.831
                    a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <text x="18" y="20.35" class="percentage">{{ currentTopic.progress }}%</text>
              </svg>
            </div>
          </div>
        </header>

        <!-- 内容区域 -->
        <div class="topic-content">
          <!-- 数组基础 -->
          <section class="content-section" id="array-basic">
            <h2 class="section-heading">
              <span class="heading-icon">📊</span>
              数组基础
            </h2>
            <div class="notion-block">
              <div class="section-content">
                <!-- 文字部分 -->
                <div class="text-content">
                  <h3>核心概念</h3>
                  <p>数组是最基础的数据结构，将相同类型的元素存储在连续的内存空间中。数组支持随机访问，时间复杂度为 O(1)。</p>
                  
                  <h3>主要特点</h3>
                  <ul>
                    <li><strong>连续存储</strong>：元素在内存中连续存放</li>
                    <li><strong>随机访问</strong>：通过索引可以直接访问任意元素</li>
                    <li><strong>固定大小</strong>：创建后大小通常不可变</li>
                    <li><strong>类型统一</strong>：所有元素必须是同一类型</li>
                  </ul>

                  <h3>时间复杂度</h3>
                  <div class="complexity-table">
                    <div class="complexity-item">
                      <span class="operation">访问</span>
                      <span class="time">O(1)</span>
                    </div>
                    <div class="complexity-item">
                      <span class="operation">查找</span>
                      <span class="time">O(n)</span>
                    </div>
                    <div class="complexity-item">
                      <span class="operation">插入</span>
                      <span class="time">O(n)</span>
                    </div>
                    <div class="complexity-item">
                      <span class="operation">删除</span>
                      <span class="time">O(n)</span>
                    </div>
                  </div>
                </div>

                <!-- 代码部分 -->
                <div class="text-content">
                  <h3>Java 代码实现</h3>
                  <div class="code-display">
                    <div class="code-header">
                      <span class="code-filename">ArrayBasic.java</span>
                    </div>
                    <pre class="code-content"><code class="language-java">代码演示</code></pre>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- 哈希表基础 -->
          <section class="content-section" id="hash-basic">
            <h2 class="section-heading">
              <span class="heading-icon">🔑</span>
              哈希表基础
            </h2>
            <div class="notion-block">
              <div class="section-content">
                <!-- 文字部分 -->
                <div class="text-content">
                  <h3>核心概念</h3>
                  <p>哈希表（Hash Table）是根据键（Key）直接访问在内存储存位置的数据结构。通过哈希函数计算出键对应的索引，实现快速的插入、删除和查找操作。</p>
                  
                  <h3>主要特点</h3>
                  <ul>
                    <li><strong>快速访问</strong>：平均时间复杂度 O(1)</li>
                    <li><strong>键值对存储</strong>：通过键来访问对应的值</li>
                    <li><strong>哈希函数</strong>：将键映射到数组索引</li>
                    <li><strong>冲突处理</strong>：处理不同键映射到相同索引的情况</li>
                  </ul>

                  <h3>冲突解决方法</h3>
                  <div class="method-grid">
                    <div class="method-item">
                      <strong>链地址法</strong>
                      <p>在冲突位置维护一个链表</p>
                    </div>
                    <div class="method-item">
                      <strong>开放地址法</strong>
                      <p>线性探测找到下一个空位</p>
                    </div>
                  </div>
                </div>

                <!-- 代码部分 -->
                <div class="text-content">
                  <h3>Java 代码实现</h3>
                  <div class="code-display">
                    <div class="code-header">
                      <span class="code-filename">HashTableBasic.java</span>
                    </div>
                    <pre class="code-content"><code class="language-java">代码演示</code></pre>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- 双指针技巧 -->
          <section class="content-section" id="two-pointer">
            <h2 class="section-heading">
              <span class="heading-icon">👥</span>
              双指针技巧
            </h2>
            <div class="notion-block">
              <div class="section-content">
                <!-- 文字部分 -->
                <div class="text-content">
                  <h3>核心概念</h3>
                  <p>双指针是一种常用的算法技巧，通过维护两个指针来解决数组和字符串问题。可以有效降低时间复杂度，通常将 O(n²) 的问题优化为 O(n)。</p>
                  
                  <h3>常见模式</h3>
                  <div class="pattern-grid">
                    <div class="pattern-item">
                      <strong>对撞指针</strong>
                      <p>两个指针从数组两端向中间移动</p>
                      <span class="use-case">用于：回文判断、两数之和</span>
                    </div>
                    <div class="pattern-item">
                      <strong>快慢指针</strong>
                      <p>两个指针以不同速度移动</p>
                      <span class="use-case">用于：环检测、找中点</span>
                    </div>
                    <div class="pattern-item">
                      <strong>同向指针</strong>
                      <p>两个指针同方向移动</p>
                      <span class="use-case">用于：滑动窗口、去重</span>
                    </div>
                  </div>
                </div>

                <!-- 代码部分 -->
                <div class="text-content">
                  <h3>Java 代码实现</h3>
                  <div class="code-display">
                    <div class="code-header">
                      <span class="code-filename">TwoPointer.java</span>
                    </div>
                    <pre class="code-content"><code class="language-java">代码演示</code></pre>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { algorithmTopics } from '../../data/algorithmTopics.js'

export default {
  name: 'TopicDetail',
  data() {
    return {
      currentTopic: {},
      activeSection: 'array-basic',
      learningSections: [
        { id: 'array-basic', title: '数组基础', icon: '📊', status: 'completed' },
        { id: 'hash-basic', title: '哈希表基础', icon: '🔑', status: 'in_progress' },
        { id: 'two-pointer', title: '双指针技巧', icon: '👥', status: 'pending' }
      ]
    }
  },
  created() {
    this.loadTopicData()
  },
  methods: {
    loadTopicData() {
      const topicId = this.$route.params.topicId || 'array'
      this.currentTopic = algorithmTopics.find(topic => topic.id === topicId) || algorithmTopics[0]
    },
    goBack() {
      this.$router.go(-1)
    },
    scrollToSection(sectionId) {
      this.activeSection = sectionId
      const element = document.getElementById(sectionId)
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    },
    getDifficultyText(difficulty) {
      const map = { 'easy': '简单', 'medium': '中等', 'hard': '困难' }
      return map[difficulty] || difficulty
    },
    getStatusIcon(status) {
      const map = { 'completed': '✅', 'in_progress': '🟡', 'pending': '⚪' }
      return map[status] || '⚪'
    }
  }
}
</script>

<style>
@import '../../../../assets/elegant-theme.css';

/* 继承优雅主题的基础布局 */
.topic-detail-container {
  padding: 0;
  background: var(--secondary);
}

.topic-detail-layout {
  display: flex;
  min-height: 100vh;
  max-width: 1400px;
  margin: 0 auto;
}

/* 侧边栏样式 - 使用主题中的卡片样式 */
.topic-sidebar {
  width: 280px;
  background: var(--bg-primary);
  border-right: 1px solid var(--border-light);
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  flex-shrink: 0;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-light);
}

/* 使用主题的按钮样式基础 */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background: var(--bg-secondary);
  color: var(--text);
  border: 1px solid var(--border-medium);
  border-radius: 8px;
  font-size: 0.9em;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  text-decoration: none;
  margin-bottom: 1rem;
}

.back-btn:hover {
  background: var(--accent);
  color: white;
}

.sidebar-title {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
  font-size: 1.1em;
  margin: 0;
  color: var(--text);
}

.sidebar-content {
  padding: 1rem;
}

.sidebar-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 0.9em;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* 导航列表 - 简化样式 */
.outline-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.outline-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  transition: var(--transition);
  font-size: 0.9em;
}

.outline-item:hover {
  background: var(--bg-accent);
}

.outline-item.active {
  background: var(--accent);
  color: white;
}

.outline-icon {
  font-size: 1.1em;
}

.outline-text {
  flex: 1;
  font-weight: 500;
}

.outline-status {
  font-size: 0.8em;
}

/* 主内容区域 - 使用主题样式 */
.topic-main-content {
  flex: 1;
  padding: 0;
  background: white;
  overflow-y: auto;
}

/* 页面头部 - 简化样式 */
.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 3rem;
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary));
  border-bottom: 1px solid var(--border-light);
  box-shadow: var(--card-shadow);
  backdrop-filter: blur(10px);
}

.topic-title-section {
  display: flex;
  gap: 1.5rem;
  flex: 1;
}

.topic-icon-large {
  font-size: 4rem;
  flex-shrink: 0;
}

.topic-main-title {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  color: var(--text);
}

.topic-subtitle {
  font-size: 1.1rem;
  color: var(--light-text);
  margin-bottom: 1rem;
  line-height: 1.5;
}

.topic-meta {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: var(--light-text);
}

/* 使用主题的功能色彩 */
.meta-item.difficulty.difficulty-easy { color: var(--success-color); }
.meta-item.difficulty.difficulty-medium { color: var(--warning-color); }
.meta-item.difficulty.difficulty-hard { color: var(--danger-color); }

/* 进度圆环 - 保持原有样式 */
.topic-progress-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.progress-circle {
  width: 80px;
  height: 80px;
}

.circular-chart {
  display: block;
  margin: 0 auto;
  max-width: 80%;
  max-height: 250px;
}

.circle-bg {
  fill: none;
  stroke: var(--border-light);
  stroke-width: 2.8;
}

.circle {
  fill: none;
  stroke: var(--accent);
  stroke-width: 2.8;
  stroke-linecap: round;
  animation: progress 1s ease-out forwards;
}

.percentage {
  fill: var(--text);
  font-family: sans-serif;
  font-size: 0.5em;
  text-anchor: middle;
  font-weight: 600;
}

@keyframes progress {
  0% { stroke-dasharray: 0 100; }
}

/* 内容区域 - 使用主题样式 */
.topic-content {
  padding: 2rem 3rem 3rem;
}

.content-section {
  background: var(--bg-primary);
  border-radius: var(--border-radius);
  padding: 25px;
  margin-bottom: 2rem;
  box-shadow: var(--card-shadow);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-light);
}

.section-heading {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: var(--text);
}

.heading-icon {
  font-size: 1.2em;
}

/* 内容块 - 使用主题卡片样式 */
.notion-block {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 1.5rem;
  border: 1px solid var(--border-medium);
  transition: var(--transition);
  backdrop-filter: blur(5px);
}

/* 关键点网格 - 使用主题网格系统 */
.key-points-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.key-point-card {
  background: linear-gradient(135deg, var(--bg-secondary), var(--bg-primary));
  padding: 20px;
  border-radius: var(--border-radius);
  display: flex;
  align-items: center;
  gap: 0.75rem;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  transition: var(--transition);
  border: 1px solid var(--border-light);
  backdrop-filter: blur(5px);
}

.point-icon {
  font-size: 1.2em;
  opacity: 0.8;
}

.point-text {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text);
}

/* 按钮样式 - 统一使用主题按钮 */
.edit-btn,
.refresh-btn,
.toolbar-btn {
  padding: 12px 25px;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  background: var(--bg-secondary);
  color: var(--text);
  border: 1px solid var(--border-medium);
}

.edit-btn:hover,
.refresh-btn:hover,
.toolbar-btn:hover {
  background: var(--accent);
  color: white;
}

.edit-btn.active {
  background: var(--accent);
  color: white;
  box-shadow: 0 4px 15px rgba(212, 184, 160, 0.3);
}

/* 表单输入 - 使用主题样式 */
.notes-textarea,
.file-selector {
  padding: 10px 12px;
  border: 2px solid var(--border-medium);
  border-radius: 6px;
  font-size: 0.9em;
  transition: var(--transition);
  background: var(--bg-secondary);
  backdrop-filter: blur(3px);
  font-family: 'Consolas', 'Monaco', monospace;
}

.notes-textarea:focus,
.file-selector:focus {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(212, 184, 160, 0.1);
}

.notes-textarea {
  min-height: 300px;
  resize: vertical;
}

/* 内容样式简化 */
.notes-content h2 {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
  font-size: 1.3rem;
  margin: 1.5rem 0 1rem;
  border-bottom: 2px solid var(--border-light);
  padding-bottom: 0.5rem;
  color: var(--text);
}

.notes-content h3 {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
  font-size: 1.1rem;
  margin: 1.2rem 0 0.8rem;
  color: var(--text);
}

.notes-content code {
  background: var(--bg-accent);
  padding: 0.2rem 0.4rem;
  border-radius: 3px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.9em;
}

/* 布局辅助类 */
.editor-toolbar {
  display: flex;
  gap: 0.5rem;
}

.code-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-left: auto;
}

/* 代码显示区域 - 简化样式 */
.code-display {
  background: #1e1e1e;
  border-radius: var(--border-radius);
  overflow: hidden;
  box-shadow: var(--card-shadow);
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: #2d2d2d;
  border-bottom: 1px solid #404040;
}

.code-filename {
  color: #ffffff;
  font-weight: 500;
  font-family: 'Consolas', 'Monaco', monospace;
}

.code-content {
  padding: 1.5rem;
  margin: 0;
  color: #d4d4d4;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.9rem;
  line-height: 1.5;
  overflow-x: auto;
  background: #1e1e1e;
}

/* 章节内容 - 使用简化的布局 */
.section-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.text-content h3 {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
  font-size: 1.2rem;
  margin-bottom: 1rem;
  margin-top: 1.5rem;
  color: var(--text);
}

.text-content h3:first-child {
  margin-top: 0;
}

.text-content p {
  font-size: 1rem;
  line-height: 1.6;
  color: var(--text);
  margin-bottom: 1rem;
}

.text-content ul {
  margin-bottom: 1.5rem;
  padding-left: 1.5rem;
}

.text-content li {
  margin-bottom: 0.5rem;
  line-height: 1.5;
}

.text-content li strong {
  color: var(--accent);
}

/* 各种网格布局 - 统一使用主题样式 */
.complexity-table {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  background: var(--bg-accent);
  padding: 20px;
  border-radius: var(--border-radius);
  border: 1px solid var(--border-medium);
  backdrop-filter: blur(5px);
  margin-top: 1rem;
}

.complexity-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: var(--bg-secondary);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border: 1px solid var(--border-light);
  backdrop-filter: blur(3px);
}

.operation {
  font-weight: 500;
  color: var(--text);
}

.time {
  font-family: 'Consolas', 'Monaco', monospace;
  font-weight: 600;
  color: var(--accent);
  background: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.method-grid,
.pattern-grid {
  display: grid;
  gap: 15px;
  background: var(--bg-accent);
  padding: 20px;
  border-radius: var(--border-radius);
  border: 1px solid var(--border-medium);
  backdrop-filter: blur(5px);
  margin-top: 1rem;
}

.method-grid {
  grid-template-columns: 1fr 1fr;
}

.pattern-grid {
  grid-template-columns: 1fr;
}

.method-item,
.pattern-item {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 1rem;
  border: 1px solid var(--border-medium);
  transition: var(--transition);
  backdrop-filter: blur(5px);
}

.method-item strong,
.pattern-item strong {
  color: var(--accent);
  font-size: 1rem;
  display: block;
  margin-bottom: 0.5rem;
}

.method-item p {
  color: var(--light-text);
  font-size: 0.9rem;
  margin: 0;
}

.pattern-item p {
  font-size: 0.9rem;
  color: var(--text);
  margin-bottom: 0.5rem;
}

.use-case {
  color: var(--light-text);
  font-size: 0.8rem;
  font-style: italic;
}



/* 响应式设计 - 简化版本 */
@media (max-width: 1024px) {
  .topic-sidebar {
    width: 240px;
  }
  
  .topic-header {
    padding: 2rem;
  }
  
  .topic-content {
    padding: 1.5rem 2rem 2rem;
  }
}

@media (max-width: 768px) {
  .topic-detail-layout {
    flex-direction: column;
  }
  
  .topic-sidebar {
    width: 100%;
    height: auto;
    position: static;
    border-right: none;
    border-bottom: 1px solid var(--border-light);
  }
  
  .topic-header {
    flex-direction: column;
    gap: 1.5rem;
    padding: 1.5rem;
  }
  
  .topic-content {
    padding: 1rem;
  }
  
  .complexity-table,
  .method-grid {
    grid-template-columns: 1fr;
  }
}
</style>