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
              <span class="stat-number">12</span>
              <span class="stat-label">文章总数</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">256</span>
              <span class="stat-label">访问量</span>
            </div>
          </div>
        </div>

        <div class="sidebar-section">
          <h3>🏷️ 标签云</h3>
          <div class="tags">
            <span class="tag">Vue.js</span>
            <span class="tag">JavaScript</span>
            <span class="tag">Spring Boot</span>
            <span class="tag">MySQL</span>
            <span class="tag">前端开发</span>
            <span class="tag">后端开发</span>
          </div>
        </div>

        <div class="sidebar-section">
          <h3>📅 最近更新</h3>
          <div class="recent-posts">
            <div class="recent-post">
              <a href="#">Vue3 + Spring Boot 项目搭建</a>
              <span class="post-date">2024-01-15</span>
            </div>
            <div class="recent-post">
              <a href="#">JavaScript 异步编程详解</a>
              <span class="post-date">2024-01-10</span>
            </div>
            <div class="recent-post">
              <a href="#">MySQL 性能优化技巧</a>
              <span class="post-date">2024-01-05</span>
            </div>
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
export default {
  name: 'MainLayout',
  data() {
    return {
      currentUser: ''
    }
  },
  mounted() {
    // 获取当前用户信息
    this.currentUser = sessionStorage.getItem('currentUser') || '用户'
  },
  methods: {
    logout() {
      // 清除登录状态
      sessionStorage.removeItem('currentUser')
      localStorage.removeItem('userToken')
      
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

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background-color: #e9ecef;
  color: #495057;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tag:hover {
  background-color: #667eea;
  color: white;
  transform: translateY(-1px);
}

.recent-posts {
  space-y: 10px;
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