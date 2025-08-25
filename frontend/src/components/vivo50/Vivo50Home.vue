<template>
  <div class="elegant-container">
    <div class="elegant-main-content elegant-fade-in">
      <!-- 页面标题与简介 -->
      <div class="elegant-section">
        <h1 class="elegant-title">Vivo50 学习记录中心</h1>
        <p class="elegant-text-light">算法与数据结构｜软件工程｜计网｜操作系统｜数据库｜系统设计｜八股文速览</p>
      </div>

      <!-- 学习概览指标 -->
      <div class="elegant-section">
        <div class="elegant-metrics-grid">
          <div class="elegant-metric-card">
            <div class="elegant-metric-icon">📅</div>
            <div class="elegant-metric-content">
              <div class="elegant-metric-label">学习天数</div>
              <div class="elegant-metric-value">{{ overview.studyDays }}</div>
            </div>
          </div>
          <div class="elegant-metric-card highlight">
            <div class="elegant-metric-icon">✅</div>
            <div class="elegant-metric-content">
              <div class="elegant-metric-label">已完成章节</div>
              <div class="elegant-metric-value">{{ overview.completedChapters }}</div>
            </div>
          </div>
          <div class="elegant-metric-card">
            <div class="elegant-metric-icon">🧠</div>
            <div class="elegant-metric-content">
              <div class="elegant-metric-label">累计刷题</div>
              <div class="elegant-metric-value">{{ overview.totalProblems }}</div>
            </div>
          </div>
          <div class="elegant-metric-card">
            <div class="elegant-metric-icon">⏰</div>
            <div class="elegant-metric-content">
              <div class="elegant-metric-label">最近更新</div>
              <div class="elegant-metric-value">{{ overview.lastUpdate }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习入口 -->
      <div class="elegant-section">
        <div class="elegant-card-header">
          <h3 class="elegant-card-title">📚 学习入口</h3>
        </div>
        <div class="vivo50-study-modules">
          <div class="vivo50-module-card" @click="handleModuleClick('algorithm')">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">🧠</div>
              <h4 class="vivo50-module-title">算法与数据结构</h4>
            </div>
            <p class="vivo50-module-desc">数组、链表、栈队列、树、图、动态规划</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">双指针</span>
              <span class="vivo50-tag">递归</span>
              <span class="vivo50-tag">DP</span>
            </div>
          </div>
          
          <div class="vivo50-module-card">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">⚙️</div>
              <h4 class="vivo50-module-title">软件工程</h4>
            </div>
            <p class="vivo50-module-desc">需求分析、设计模式、测试、CI/CD、重构</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">设计模式</span>
              <span class="vivo50-tag">测试</span>
              <span class="vivo50-tag">架构</span>
            </div>
          </div>
          
          <div class="vivo50-module-card">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">🌐</div>
              <h4 class="vivo50-module-title">计算机网络</h4>
            </div>
            <p class="vivo50-module-desc">TCP/IP、HTTP/HTTPS、DNS、CDN</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">协议栈</span>
              <span class="vivo50-tag">安全</span>
              <span class="vivo50-tag">性能</span>
            </div>
          </div>
          
          <div class="vivo50-module-card">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">💻</div>
              <h4 class="vivo50-module-title">操作系统</h4>
            </div>
            <p class="vivo50-module-desc">进程线程、内存管理、文件系统</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">并发</span>
              <span class="vivo50-tag">调度</span>
              <span class="vivo50-tag">存储</span>
            </div>
          </div>
          
          <div class="vivo50-module-card" @click="handleModuleClick('database')">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">🗄️</div>
              <h4 class="vivo50-module-title">数据库</h4>
            </div>
            <p class="vivo50-module-desc">主要是SQL语句、索引、事务、锁机制，触发器和游标的缺陷</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">CRUD</span>
              <span class="vivo50-tag">ACID</span>
              <span class="vivo50-tag">索引</span>
            </div>
          </div>
          
          <div class="vivo50-module-card">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">🏗️</div>
              <h4 class="vivo50-module-title">系统设计</h4>
            </div>
            <p class="vivo50-module-desc">可用性、扩展性、一致性、缓存</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">高并发</span>
              <span class="vivo50-tag">分布式</span>
              <span class="vivo50-tag">缓存</span>
            </div>
          </div>
          
          <div class="vivo50-module-card">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">📝</div>
              <h4 class="vivo50-module-title">八股文速览</h4>
            </div>
            <p class="vivo50-module-desc">高频面试点精炼速记</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">面试</span>
              <span class="vivo50-tag">速记</span>
              <span class="vivo50-tag">高频</span>
            </div>
          </div>
          
          <div class="vivo50-module-card">
            <div class="vivo50-module-header">
              <div class="vivo50-module-icon">💡</div>
              <h4 class="vivo50-module-title">Java代码基础</h4>
            </div>
            <p class="vivo50-module-desc">大概就是Spring三层架构、JPA规范、Hibernate框架之类吧</p>
            <div class="vivo50-module-tags">
              <span class="vivo50-tag">Spring</span>
              <span class="vivo50-tag">JPA</span>
              <span class="vivo50-tag">Hibernate</span>
            </div>
          </div>
        </div>
        
        <div class="vivo50-actions">
          <button class="elegant-btn elegant-btn-primary">🚀 开始学习</button>
          <button class="elegant-btn elegant-btn-secondary">📖 查看题单</button>
          <button class="elegant-btn elegant-btn-secondary">📊 学习进度</button>
        </div>
      </div>

      <!-- 学习路线与记录（示例静态表格） -->
      <div class="elegant-section">
        <div class="elegant-card-header">
          <h3 class="elegant-card-title">🗂️ 学习路线与记录</h3>
        </div>
        <div class="elegant-table-wrapper">
          <table class="elegant-table">
            <thead>
              <tr>
                <th>日期</th>
                <th>主题</th>
                <th>耗时</th>
                <th>状态</th>
                <th>备注</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>2025-03-01</td>
                <td>数组与双指针</td>
                <td>2h</td>
                <td>进行中</td>
                <td>整理模板与常见题型</td>
              </tr>
              <tr>
                <td>2025-03-02</td>
                <td>链表与快慢指针</td>
                <td>1.5h</td>
                <td>计划</td>
                <td>环检测、反转、合并</td>
              </tr>
              <tr>
                <td>2025-03-03</td>
                <td>操作系统 - 进程与线程</td>
                <td>2h</td>
                <td>计划</td>
                <td>调度算法对比</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 题单规划（静态卡片） -->
      <div class="elegant-section">
        <div class="elegant-card-header">
          <h3 class="elegant-card-title">🧾 题单规划</h3>
        </div>
        <div class="elegant-metrics-grid">
          <div class="elegant-card">
            <div class="elegant-card-header">
              <h3 class="elegant-card-title">数组与哈希</h3>
            </div>
            <p class="elegant-text-light">经典题型：两数之和、滑动窗口、前缀和...</p>
          </div>
          <div class="elegant-card">
            <div class="elegant-card-header">
              <h3 class="elegant-card-title">树与图</h3>
            </div>
            <p class="elegant-text-light">二叉树遍历、最近公共祖先、并查集、最短路...</p>
          </div>
          <div class="elegant-card">
            <div class="elegant-card-header">
              <h3 class="elegant-card-title">动态规划</h3>
            </div>
            <p class="elegant-text-light">背包、区间 DP、状态压缩、数位 DP...</p>
          </div>
          <div class="elegant-card">
            <div class="elegant-card-header">
              <h3 class="elegant-card-title">系统设计与工程化</h3>
            </div>
            <p class="elegant-text-light">高并发、缓存、消息队列、灰度发布、可观测性...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  
</template>

<script>
export default {
  name: 'Vivo50Home',
  data() {
    return {
      overview: {
        studyDays: 1,
        completedChapters: 4,
        totalProblems: 12,
        lastUpdate: '刚刚'
      }
    }
  },
  methods: {
    handleModuleClick(moduleType) {
      if (moduleType === 'algorithm') {
        this.$router.push('/vivo50/algorithm')
      } else if (moduleType === 'database') {
        this.$router.push('/vivo50/database')
      }
      // 其他模块可以在这里添加
    }
  }
}
</script>

<style>
@import '../../assets/elegant-theme.css';

/* Vivo50 专用样式 */
.vivo50-study-modules {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.vivo50-module-card {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 24px;
  border: 1px solid var(--border-light);
  transition: var(--transition);
  cursor: pointer;
  backdrop-filter: blur(5px);
}

.vivo50-module-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0,0,0,0.12);
  border-color: var(--accent);
}

.vivo50-module-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.vivo50-module-icon {
  font-size: 2em;
  opacity: 0.8;
}

.vivo50-module-title {
  font-size: 1.2em;
  font-weight: 600;
  color: var(--text);
  margin: 0;
  font-family: 'Cormorant Garamond', serif;
}

.vivo50-module-desc {
  color: var(--light-text);
  font-size: 0.95em;
  line-height: 1.5;
  margin-bottom: 16px;
}

.vivo50-module-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.vivo50-tag {
  background: var(--bg-accent);
  color: var(--text);
  font-size: 0.8em;
  padding: 4px 12px;
  border-radius: 16px;
  border: 1px solid var(--border-light);
  font-weight: 500;
  transition: var(--transition);
}

.vivo50-tag:hover {
  background: var(--accent);
  color: white;
}

.vivo50-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .vivo50-study-modules {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .vivo50-module-card {
    padding: 20px;
  }
  
  .vivo50-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .vivo50-actions .elegant-btn {
    justify-content: center;
  }
}

@media (max-width: 1024px) {
  .vivo50-study-modules {
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  }
}
</style>

