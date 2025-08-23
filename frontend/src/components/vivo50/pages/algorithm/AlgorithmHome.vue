<template>
  <div class="elegant-container">
    <!-- 页面标题 -->
    <div class="elegant-section algorithm-header">
      <div class="algorithm-title-section">
        <h1 class="elegant-title">🧠 算法与数据结构</h1>
        <p class="elegant-text-light">系统化学习数据结构与算法，提升编程思维与解题能力</p>
      </div>
      <div class="algorithm-quick-stats">
        <div class="quick-stat-item">
          <span class="stat-number">{{ overallStats.totalCompleted }}</span>
          <span class="stat-label">已完成</span>
        </div>
        <div class="quick-stat-item">
          <span class="stat-number">{{ overallStats.totalProblems }}</span>
          <span class="stat-label">总题数</span>
        </div>
        <div class="quick-stat-item">
          <span class="stat-number">{{ overallStats.studyHours }}h</span>
          <span class="stat-label">学习时长</span>
        </div>
      </div>
    </div>

    <!-- 学习进度概览 -->
    <div class="elegant-section">
      <div class="elegant-card-header">
        <h3 class="elegant-card-title">📊 学习进度概览</h3>
      </div>
      <div class="elegant-metrics-grid">
        <div class="elegant-metric-card highlight">
          <div class="elegant-metric-icon">🎯</div>
          <div class="elegant-metric-content">
            <div class="elegant-metric-label">总体进度</div>
            <div class="elegant-metric-value">{{ overallStats.overallProgress }}%</div>
          </div>
        </div>
        <div class="elegant-metric-card">
          <div class="elegant-metric-icon">⚡</div>
          <div class="elegant-metric-content">
            <div class="elegant-metric-label">连续学习</div>
            <div class="elegant-metric-value">{{ overallStats.streakDays }}天</div>
          </div>
        </div>
        <div class="elegant-metric-card">
          <div class="elegant-metric-icon">🏆</div>
          <div class="elegant-metric-content">
            <div class="elegant-metric-label">完成模块</div>
            <div class="elegant-metric-value">{{ overallStats.completedTopics }}/6</div>
          </div>
        </div>
        <div class="elegant-metric-card">
          <div class="elegant-metric-icon">📈</div>
          <div class="elegant-metric-content">
            <div class="elegant-metric-label">本周进度</div>
            <div class="elegant-metric-value">+{{ overallStats.weeklyProgress }}题</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 知识点模块 -->
    <div class="elegant-section">
      <div class="elegant-card-header">
        <h3 class="elegant-card-title">📚 知识点模块</h3>

      </div>
      
      <div class="algorithm-topics-grid">
        <div 
          v-for="topic in filteredTopics" 
          :key="topic.id" 
          class="algorithm-topic-card"
:class="`status-${topic.status}`"
          @click="handleTopicClick(topic)"
        >
          <div class="topic-header">
            <div class="topic-icon">{{ topic.icon }}</div>
            <div class="topic-info">
              <h4 class="topic-title">{{ topic.name }}</h4>
              <p class="topic-description">{{ topic.description }}</p>
            </div>
            <div class="topic-status">
              <span class="status-badge" :class="`status-${topic.status}`">
                {{ getStatusText(topic.status) }}
              </span>
            </div>
          </div>
          

          
          <div class="topic-tags">
            <span 
              v-for="tag in topic.tags" 
              :key="tag" 
              class="topic-tag"
            >
              {{ tag }}
            </span>
          </div>
          
          <div class="topic-footer">
            <div class="topic-stats">
            </div>
            <div class="topic-action">
              <span class="action-text">
                开始学习
              </span>
              <i class="action-arrow">→</i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习路径推荐 -->
    <div class="elegant-section">
      <div class="elegant-card-header">
        <h3 class="elegant-card-title">🗺️ 推荐学习路径</h3>
      </div>
      <div class="study-paths-grid">
        <div 
          v-for="path in studyPaths" 
          :key="path.id" 
          class="study-path-card"
          :class="`path-${path.difficulty}`"
        >
          <div class="path-header">
            <h4 class="path-title">{{ path.name }}</h4>
            <span class="path-duration">{{ path.estimatedWeeks }}周</span>
          </div>
          <p class="path-description">{{ path.description }}</p>
          <div class="path-topics">
            <span 
              v-for="topicId in path.topics" 
              :key="topicId" 
              class="path-topic"
            >
              {{ getTopicName(topicId) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近学习记录 -->
    <div class="elegant-section">
      <div class="elegant-card-header">
        <h3 class="elegant-card-title">📝 最近学习记录</h3>
        <button class="elegant-btn elegant-btn-secondary btn-small">查看全部</button>
      </div>
      <div class="recent-studies-list">
        <div 
          v-for="study in recentStudies" 
          :key="study.id" 
          class="study-record-item"
        >
          <div class="study-info">
            <div class="study-main">
              <h4 class="study-title">{{ study.problemTitle }}</h4>
              <span class="study-topic">{{ study.topic }}</span>
            </div>
            <div class="study-meta">
              <span class="study-date">{{ study.date }}</span>
              <span class="study-duration">{{ study.duration }}分钟</span>
              <span class="study-status" :class="`status-${study.status}`">
                {{ getStatusText(study.status) }}
              </span>
            </div>
          </div>
          <div class="study-notes" v-if="study.notes">
            <p>{{ study.notes }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { algorithmTopics, studyPaths, recentStudies } from '../../data/algorithmTopics.js'

export default {
  name: 'AlgorithmHome',
  data() {
    return {
      algorithmTopics,
      studyPaths,
      recentStudies,
      activeFilter: 'all'
    }
  },
  computed: {
    overallStats() {
      // 简化统计信息，使用静态数据
      return {
        totalProblems: 6, // 总模块数
        totalCompleted: 1, // 已学习模块数
        overallProgress: 17, // 静态进度
        completedTopics: 1, // 已完成模块数
        studyHours: 12, // 静态数据
        streakDays: 3,  // 静态数据
        weeklyProgress: 4 // 静态数据
      }
    },
    filteredTopics() {
      // 由于删除了进度信息，所有筛选都返回全部主题
      return this.algorithmTopics
    }
  },
  methods: {
    handleTopicClick(topic) {
      console.log('点击知识点:', topic.name)
      // 跳转到知识点详情页
      this.$router.push(`/vivo50/algorithm/topic/${topic.id}`)
    },
    getStatusText(status) {
      const map = {
        'completed': '已完成',
        'in_progress': '进行中',
        'pending': '待开始',
        'planned': '计划中'
      }
      return map[status] || status
    },
    getTopicName(topicId) {
      const topic = this.algorithmTopics.find(t => t.id === topicId)
      return topic ? topic.name : topicId
    }
  }
}
</script>

<style>
@import '../../../../assets/elegant-theme.css';

/* 算法模块专用样式 */
.algorithm-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 2rem;
}

.algorithm-title-section {
  flex: 1;
}

.algorithm-quick-stats {
  display: flex;
  gap: 1.5rem;
  flex-shrink: 0;
}

.quick-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
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
  margin-top: 0.25rem;
}



/* 知识点卡片网格 */
.algorithm-topics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
}

.algorithm-topic-card {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 1.5rem;
  border: 2px solid var(--border-light);
  transition: var(--transition);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.algorithm-topic-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  border-color: var(--accent);
}



/* 状态样式 */
.algorithm-topic-card.status-completed {
  background: linear-gradient(135deg, var(--bg-secondary), rgba(76, 175, 80, 0.05));
}

.algorithm-topic-card.status-in_progress {
  background: linear-gradient(135deg, var(--bg-secondary), rgba(255, 152, 0, 0.05));
}

.algorithm-topic-card.status-pending {
  background: linear-gradient(135deg, var(--bg-secondary), rgba(158, 158, 158, 0.05));
}

.topic-header {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1rem;
}

.topic-icon {
  font-size: 2.5em;
  flex-shrink: 0;
}

.topic-info {
  flex: 1;
}

.topic-title {
  font-size: 1.2em;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text);
}

.topic-description {
  color: var(--light-text);
  font-size: 0.9em;
  line-height: 1.4;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8em;
  font-weight: 500;
}

.status-badge.status-completed {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
}

.status-badge.status-in_progress {
  background: rgba(255, 152, 0, 0.1);
  color: #FF9800;
}

.status-badge.status-pending {
  background: rgba(158, 158, 158, 0.1);
  color: #9E9E9E;
}



/* 标签 */
.topic-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.topic-tag {
  background: var(--bg-accent);
  color: var(--text);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8em;
  font-weight: 500;
}

/* 底部信息 */
.topic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.85em;
  color: var(--light-text);
}

.topic-action {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--accent);
  font-size: 0.9em;
  font-weight: 500;
}

.action-arrow {
  font-style: normal;
  transition: transform 0.2s ease;
}

.algorithm-topic-card:hover .action-arrow {
  transform: translateX(4px);
}

/* 学习路径 */
.study-paths-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.study-path-card {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 1.25rem;
  border: 1px solid var(--border-light);
  transition: var(--transition);
}

.study-path-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.path-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.path-title {
  font-size: 1.1em;
  font-weight: 600;
  color: var(--text);
}

.path-duration {
  background: var(--bg-accent);
  color: var(--text);
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8em;
  font-weight: 500;
}

.path-description {
  color: var(--light-text);
  font-size: 0.9em;
  margin-bottom: 1rem;
  line-height: 1.4;
}

.path-topics {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.path-topic {
  background: var(--accent);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8em;
  font-weight: 500;
}

/* 学习记录 */
.recent-studies-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.study-record-item {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 1rem;
  border: 1px solid var(--border-light);
}

.study-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.study-main {
  flex: 1;
}

.study-title {
  font-size: 1em;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 0.25rem;
}

.study-topic {
  color: var(--accent);
  font-size: 0.85em;
  font-weight: 500;
}

.study-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.85em;
  color: var(--light-text);
}

.study-status {
  font-weight: 500;
}

.study-status.status-completed {
  color: #4CAF50;
}

.study-status.status-in_progress {
  color: #FF9800;
}

.study-notes {
  color: var(--light-text);
  font-size: 0.9em;
  font-style: italic;
}

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.85em;
}

/* 响应式 */
@media (max-width: 768px) {
  .algorithm-header {
    flex-direction: column;
    gap: 1rem;
  }
  
  .algorithm-quick-stats {
    justify-content: center;
    width: 100%;
  }
  
  .algorithm-topics-grid {
    grid-template-columns: 1fr;
  }
  
  .study-paths-grid {
    grid-template-columns: 1fr;
  }
  
  .study-info {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .study-meta {
    flex-wrap: wrap;
  }
}
</style>