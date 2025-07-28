<template>
  <div class="blog-home">
    <!-- 欢迎横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1>🎉 欢迎来到我的个人博客</h1>
        <p>用心记录技术成长之路，分享编程经验与心得</p>
        <div class="hero-stats">
          <span class="hero-stat">
            <strong>12</strong> 篇文章
          </span>
          <span class="hero-stat">
            <strong>256</strong> 次访问
          </span>
          <span class="hero-stat">
            <strong>{{ formatDate(new Date()) }}</strong> 最后更新
          </span>
        </div>
      </div>
    </div>

    <!-- 最新文章 -->
    <section class="section">
      <h2 class="section-title">📝 最新文章</h2>
      <div class="articles-grid">
        <article 
          v-for="article in latestArticles" 
          :key="article.id"
          class="article-card"
        >
          <div class="article-header">
            <h3 class="article-title">{{ article.title }}</h3>
            <span class="article-date">{{ article.date }}</span>
          </div>
          <p class="article-excerpt">{{ article.excerpt }}</p>
          <div class="article-tags">
            <span 
              v-for="tag in article.tags" 
              :key="tag"
              class="article-tag"
            >
              {{ tag }}
            </span>
          </div>
          <div class="article-footer">
            <span class="article-meta">📖 {{ article.readTime }} 分钟阅读</span>
            <button class="read-more-btn">阅读更多</button>
          </div>
        </article>
      </div>
    </section>

    <!-- 技术栈展示 -->
    <section class="section">
      <h2 class="section-title">🚀 技术栈</h2>
      <div class="tech-stack">
        <div class="tech-category">
          <h3>前端技术</h3>
          <div class="tech-items">
            <span class="tech-item">Vue.js</span>
            <span class="tech-item">JavaScript</span>
            <span class="tech-item">HTML5</span>
            <span class="tech-item">CSS3</span>
            <span class="tech-item">Element Plus</span>
          </div>
        </div>
        <div class="tech-category">
          <h3>后端技术</h3>
          <div class="tech-items">
            <span class="tech-item">Spring Boot</span>
            <span class="tech-item">Java</span>
            <span class="tech-item">MySQL</span>
            <span class="tech-item">MyBatis</span>
            <span class="tech-item">Redis</span>
          </div>
        </div>
        <div class="tech-category">
          <h3>开发工具</h3>
          <div class="tech-items">
            <span class="tech-item">IntelliJ IDEA</span>
            <span class="tech-item">VS Code</span>
            <span class="tech-item">Git</span>
            <span class="tech-item">Maven</span>
            <span class="tech-item">Docker</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 快速导航 -->
    <section class="section">
      <h2 class="section-title">🧭 快速导航</h2>
      <div class="quick-nav">
        <router-link to="/main/articles" class="quick-nav-item">
          <div class="nav-icon">📚</div>
          <h3>文章列表</h3>
          <p>查看所有技术文章</p>
        </router-link>
        <router-link to="/main/about" class="quick-nav-item">
          <div class="nav-icon">👨‍💻</div>
          <h3>关于我</h3>
          <p>了解更多个人信息</p>
        </router-link>
        <div class="quick-nav-item" @click="scrollToTop">
          <div class="nav-icon">⬆️</div>
          <h3>返回顶部</h3>
          <p>快速回到页面顶部</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'BlogHome',
  data() {
    return {
      latestArticles: [
        {
          id: 1,
          title: 'Vue3 + Spring Boot 全栈项目搭建指南',
          excerpt: '详细介绍如何从零开始搭建一个完整的前后端分离项目，包括环境配置、项目结构设计、接口开发等关键步骤...',
          date: '2024-01-15',
          tags: ['Vue.js', 'Spring Boot', '全栈开发'],
          readTime: 8
        },
        {
          id: 2,
          title: 'JavaScript 异步编程深度解析',
          excerpt: '深入探讨JavaScript中的异步编程模式，包括Promise、async/await的使用技巧和最佳实践...',
          date: '2024-01-10',
          tags: ['JavaScript', '异步编程', 'Promise'],
          readTime: 6
        },
        {
          id: 3,
          title: 'MySQL 数据库性能优化实战',
          excerpt: '分享MySQL数据库优化的实用技巧，包括索引优化、查询优化、配置调优等方面的经验总结...',
          date: '2024-01-05',
          tags: ['MySQL', '性能优化', '数据库'],
          readTime: 10
        }
      ]
    }
  },
  methods: {
    formatDate(date) {
      return date.toLocaleDateString('zh-CN')
    },
    scrollToTop() {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      })
    }
  }
}
</script>

<style scoped>
.blog-home {
  max-width: 100%;
}

/* 英雄区域 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 30px;
  text-align: center;
}

.hero-content h1 {
  font-size: 32px;
  margin-bottom: 15px;
  font-weight: bold;
}

.hero-content p {
  font-size: 18px;
  margin-bottom: 25px;
  opacity: 0.9;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.hero-stat {
  font-size: 16px;
  opacity: 0.9;
}

.hero-stat strong {
  color: #ffd700;
  font-weight: bold;
}

/* 章节样式 */
.section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  color: #495057;
  margin-bottom: 20px;
  font-weight: bold;
}

/* 文章网格 */
.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.article-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  cursor: pointer;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
  border-color: #667eea;
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.article-title {
  font-size: 18px;
  color: #495057;
  margin: 0;
  font-weight: bold;
  flex: 1;
  margin-right: 15px;
}

.article-date {
  font-size: 12px;
  color: #6c757d;
  white-space: nowrap;
}

.article-excerpt {
  color: #6c757d;
  line-height: 1.6;
  margin-bottom: 15px;
  font-size: 14px;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.article-tag {
  background-color: #667eea;
  color: white;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.article-meta {
  font-size: 12px;
  color: #6c757d;
}

.read-more-btn {
  background-color: #667eea;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.read-more-btn:hover {
  background-color: #5a6fd8;
  transform: translateX(2px);
}

/* 技术栈 */
.tech-stack {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.tech-category {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e9ecef;
}

.tech-category h3 {
  color: #495057;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
}

.tech-items {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tech-item {
  background-color: white;
  color: #495057;
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 13px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.tech-item:hover {
  background-color: #667eea;
  color: white;
  transform: translateY(-2px);
}

/* 快速导航 */
.quick-nav {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.quick-nav-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  text-align: center;
  text-decoration: none;
  color: inherit;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  cursor: pointer;
}

.quick-nav-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
  border-color: #667eea;
}

.nav-icon {
  font-size: 32px;
  margin-bottom: 15px;
}

.quick-nav-item h3 {
  color: #495057;
  margin-bottom: 10px;
  font-size: 16px;
}

.quick-nav-item p {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 24px;
  }
  
  .hero-content p {
    font-size: 16px;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-stats {
    gap: 15px;
  }
  
  .article-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .article-date {
    margin-top: 5px;
  }
}
</style> 