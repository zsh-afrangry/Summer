<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <h1>✨ 我的个人博客</h1>
        </div>
        <nav class="nav">
          <router-link to="/main/home" class="nav-link">首页</router-link>
          <router-link to="/main/articles" class="nav-link">文章</router-link>
          <router-link to="/main/about" class="nav-link">关于</router-link>
        </nav>
        <div class="user-menu">
          <span class="username">👤 {{ currentUser }}</span>
          <button @click="logout" class="logout-btn">退出</button>
        </div>
      </div>
    </header>

    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <div class="sidebar-section">
          <h3>📊 统计信息</h3>
          <div class="stats">
            <div class="stat-item">
              <span class="stat-number">{{ stats.articleCount || 0 }}</span>
              <span class="stat-label">文章总数</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ stats.viewCount || 0 }}</span>
              <span class="stat-label">访问量</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ stats.tagCount || 0 }}</span>
              <span class="stat-label">标签数量</span>
            </div>
          </div>
        </div>

        <div class="sidebar-section">
          <h3>🏷️ 标签云</h3>
          <div class="tags">
            <span 
              v-for="tag in tagCloud" 
              :key="tag.id"
              class="tag"
              :style="{ fontSize: getTagSize(tag.articleCount) + 'px' }"
              @click="filterByTag(tag.name)"
            >
              {{ tag.name }}
            </span>
          </div>
          <div v-if="loading.tags" class="loading">
            🔄 加载标签中...
          </div>
        </div>

        <div class="sidebar-section">
          <h3>📅 最近更新</h3>
          <div class="recent-posts">
            <div 
              v-for="article in recentArticles" 
              :key="article.id"
              class="recent-post"
            >
              <a @click="viewArticle(article.id)">{{ article.title }}</a>
              <span class="post-date">{{ formatDate(article.createdAt) }}</span>
            </div>
          </div>
          <div v-if="loading.articles" class="loading">
            🔄 加载文章中...
          </div>
        </div>
      </aside>

      <!-- 主要内容区域 -->
      <main class="content">
        <router-view></router-view>
      </main>
    </div>

    <!-- 底部 -->
    <footer class="footer">
      <p>&copy; 2024 我的个人博客 | 用心记录技术成长之路</p>
    </footer>
  </div>
</template>

<script>
import { statisticsApi, tagApi, articleApi } from '@/api'
import message from '@/utils/message'

export default {
  name: 'MainLayout',
  data() {
    return {
      currentUser: '',
      stats: {
        articleCount: 0,
        viewCount: 0,
        tagCount: 0
      },
      tagCloud: [],
      recentArticles: [],
      loading: {
        stats: false,
        tags: false,
        articles: false
      }
    }
  },
  async mounted() {
    // 获取当前用户信息
    this.currentUser = sessionStorage.getItem('currentUser') || '用户'
    
    // 加载数据
    await this.loadDashboardData()
  },
  methods: {
    // 加载仪表板数据
    async loadDashboardData() {
      await Promise.all([
        this.loadStatistics(),
        this.loadTagCloud(),
        this.loadRecentArticles()
      ])
    },

    // 加载统计信息
    async loadStatistics() {
      this.loading.stats = true
      try {
        const result = await statisticsApi.getOverviewStatistics()
        if (result.success) {
          this.stats = result.data
        }
      } catch (error) {
        console.error('加载统计信息失败:', error)
      } finally {
        this.loading.stats = false
      }
    },

    // 加载标签云
    async loadTagCloud() {
      this.loading.tags = true
      try {
        const result = await tagApi.getTagCloudData()
        if (result.success) {
          this.tagCloud = result.data.slice(0, 10) // 只显示前10个标签
        }
      } catch (error) {
        console.error('加载标签云失败:', error)
      } finally {
        this.loading.tags = false
      }
    },

    // 加载最近文章
    async loadRecentArticles() {
      this.loading.articles = true
      try {
        const result = await articleApi.getLatestArticles(5)
        if (result.success) {
          this.recentArticles = result.data
        }
      } catch (error) {
        console.error('加载最近文章失败:', error)
      } finally {
        this.loading.articles = false
      }
    },

    // 根据标签文章数量计算字体大小
    getTagSize(articleCount) {
      const minSize = 12
      const maxSize = 18
      const maxCount = Math.max(...this.tagCloud.map(tag => tag.articleCount))
      
      if (maxCount === 0) return minSize
      
      const ratio = articleCount / maxCount
      return Math.round(minSize + (maxSize - minSize) * ratio)
    },

    // 根据标签筛选
    filterByTag(tagName) {
      // 如果当前在文章列表页，传递标签参数
      if (this.$route.name === 'articles') {
        this.$router.push({ name: 'articles', query: { tag: tagName } })
      } else {
        // 跳转到文章列表页并筛选
        this.$router.push({ path: '/main/articles', query: { tag: tagName } })
      }
    },

    // 查看文章详情
    viewArticle(articleId) {
      // 跳转到文章详情页（后续实现）
      this.$router.push(`/main/article/${articleId}`)
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },

    // 退出登录
    logout() {
      // 清除登录状态
      sessionStorage.removeItem('currentUser')
      localStorage.removeItem('userToken')
      localStorage.removeItem('userId')
      
      message.success('已退出登录')
      
      // 跳转到登录页
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
}

/* 头部样式 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.logo h1 {
  font-size: 24px;
  font-weight: bold;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.nav-link:hover,
.nav-link.router-link-active {
  background-color: rgba(255,255,255,0.2);
  transform: translateY(-1px);
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-weight: 500;
}

.logout-btn {
  background-color: rgba(255,255,255,0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background-color: rgba(255,255,255,0.3);
}

/* 主体内容样式 */
.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 30px;
  padding: 30px 20px;
}

/* 侧边栏样式 */
.sidebar {
  background: white;
  border-radius: 12px;
  padding: 25px;
  height: fit-content;
  box-shadow: 0 2px 20px rgba(0,0,0,0.05);
  border: 1px solid #e9ecef;
}

.sidebar-section {
  margin-bottom: 30px;
}

.sidebar-section:last-child {
  margin-bottom: 0;
}

.sidebar-section h3 {
  color: #495057;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.stats {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 8px;
  flex: 1;
  margin: 0 5px;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
}

/* 标签云样式 */
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.tag:hover {
  background: linear-gradient(135deg, #764ba2, #667eea);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

/* 加载状态样式 */
.loading {
  text-align: center;
  color: #666;
  font-size: 14px;
  padding: 10px;
}

/* 最近文章样式更新 */
.recent-post a {
  cursor: pointer;
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s ease;
}

.recent-post a:hover {
  color: #764ba2;
  text-decoration: underline;
}

.recent-post {
  padding: 10px 0;
  border-bottom: 1px solid #e9ecef;
}

.recent-post:last-child {
  border-bottom: none;
}

.recent-post a {
  color: #495057;
  text-decoration: none;
  font-size: 14px;
  display: block;
  margin-bottom: 5px;
  transition: color 0.3s ease;
}

.recent-post a:hover {
  color: #667eea;
}

.post-date {
  font-size: 12px;
  color: #6c757d;
}

/* 内容区域样式 */
.content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 20px rgba(0,0,0,0.05);
  border: 1px solid #e9ecef;
  min-height: 500px;
}

/* 底部样式 */
.footer {
  background-color: #495057;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .main-content {
    grid-template-columns: 1fr;
    padding: 20px 10px;
  }
  
  .nav {
    gap: 15px;
  }
}
</style> 