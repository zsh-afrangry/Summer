<template>
  <div class="blog-list">
    <div class="page-header">
      <h1>📚 文章列表</h1>
      <p>共 {{ articles.length }} 篇文章</p>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filters">
      <div class="search-box">
        <input 
          v-model="searchKeyword"
          type="text" 
          placeholder="搜索文章标题或内容..."
          class="search-input"
        />
        <button class="search-btn">🔍</button>
      </div>
      
      <div class="filter-tags">
        <button 
          :class="['tag-filter', { active: selectedTag === '' }]"
          @click="selectedTag = ''"
        >
          全部
        </button>
        <button 
          v-for="tag in allTags"
          :key="tag"
          :class="['tag-filter', { active: selectedTag === tag }]"
          @click="selectedTag = tag"
        >
          {{ tag }}
        </button>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles-container">
      <article 
        v-for="article in filteredArticles" 
        :key="article.id"
        class="article-item"
      >
        <div class="article-content">
          <div class="article-header">
            <h2 class="article-title">{{ article.title }}</h2>
            <div class="article-meta">
              <span class="article-date">📅 {{ article.date }}</span>
              <span class="article-reading">📖 {{ article.readTime }} 分钟阅读</span>
              <span class="article-views">👁️ {{ article.views }} 次查看</span>
            </div>
          </div>
          
          <p class="article-summary">{{ article.summary }}</p>
          
          <div class="article-tags">
            <span 
              v-for="tag in article.tags" 
              :key="tag"
              class="article-tag"
            >
              {{ tag }}
            </span>
          </div>
          
          <div class="article-actions">
            <button class="read-btn">阅读全文</button>
            <div class="article-stats">
              <span class="stat">❤️ {{ article.likes }}</span>
              <span class="stat">💬 {{ article.comments }}</span>
            </div>
          </div>
        </div>
      </article>

      <!-- 空状态 -->
      <div v-if="filteredArticles.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>暂无相关文章</h3>
        <p>尝试调整搜索条件或查看其他内容</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="filteredArticles.length > 0">
      <button class="page-btn" :disabled="currentPage === 1">上一页</button>
      <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
      <button class="page-btn" :disabled="currentPage === totalPages">下一页</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BlogList',
  data() {
    return {
      searchKeyword: '',
      selectedTag: '',
      currentPage: 1,
      pageSize: 5,
      articles: [
        {
          id: 1,
          title: 'Vue3 + Spring Boot 全栈项目搭建指南',
          summary: '详细介绍如何从零开始搭建一个完整的前后端分离项目，包括环境配置、项目结构设计、接口开发、数据库设计等关键步骤。通过实际案例演示，帮助开发者快速掌握全栈开发技能。',
          date: '2024-01-15',
          tags: ['Vue.js', 'Spring Boot', '全栈开发'],
          readTime: 8,
          views: 156,
          likes: 23,
          comments: 5
        },
        {
          id: 2,
          title: 'JavaScript 异步编程深度解析',
          summary: '深入探讨JavaScript中的异步编程模式，包括Promise、async/await的使用技巧和最佳实践。通过多个实例展示如何优雅地处理异步操作，避免回调地狱问题。',
          date: '2024-01-10',
          tags: ['JavaScript', '异步编程', 'Promise'],
          readTime: 6,
          views: 203,
          likes: 31,
          comments: 8
        },
        {
          id: 3,
          title: 'MySQL 数据库性能优化实战',
          summary: '分享MySQL数据库优化的实用技巧，包括索引优化、查询优化、配置调优等方面的经验总结。通过实际案例分析，展示如何诊断和解决数据库性能问题。',
          date: '2024-01-05',
          tags: ['MySQL', '性能优化', '数据库'],
          readTime: 10,
          views: 189,
          likes: 28,
          comments: 12
        },
        {
          id: 4,
          title: 'Vue Router 路由管理最佳实践',
          summary: '介绍Vue Router在单页面应用中的高级使用技巧，包括路由守卫、动态路由、懒加载等功能的实现。帮助开发者构建更加健壮的前端应用。',
          date: '2023-12-28',
          tags: ['Vue.js', '路由管理', '前端开发'],
          readTime: 7,
          views: 142,
          likes: 19,
          comments: 6
        },
        {
          id: 5,
          title: 'Spring Security 安全框架详解',
          summary: '全面介绍Spring Security框架的核心概念和使用方法，包括认证、授权、会话管理等功能的配置和实现。为企业级应用提供完整的安全解决方案。',
          date: '2023-12-20',
          tags: ['Spring Boot', '安全框架', '后端开发'],
          readTime: 12,
          views: 167,
          likes: 25,
          comments: 9
        },
        {
          id: 6,
          title: 'CSS Grid 布局完全指南',
          summary: 'CSS Grid是现代网页布局的强大工具，本文详细介绍Grid布局的各种属性和使用场景，通过实例演示如何创建复杂的响应式布局。',
          date: '2023-12-15',
          tags: ['CSS3', '布局', '前端开发'],
          readTime: 9,
          views: 198,
          likes: 33,
          comments: 7
        }
      ]
    }
  },
  computed: {
    allTags() {
      const tags = new Set()
      this.articles.forEach(article => {
        article.tags.forEach(tag => tags.add(tag))
      })
      return Array.from(tags)
    },
    filteredArticles() {
      let filtered = this.articles

      // 按标签筛选
      if (this.selectedTag) {
        filtered = filtered.filter(article => 
          article.tags.includes(this.selectedTag)
        )
      }

      // 按关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filtered = filtered.filter(article =>
          article.title.toLowerCase().includes(keyword) ||
          article.summary.toLowerCase().includes(keyword)
        )
      }

      return filtered
    },
    totalPages() {
      return Math.ceil(this.filteredArticles.length / this.pageSize)
    }
  }
}
</script>

<style scoped>
.blog-list {
  max-width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #495057;
  margin-bottom: 10px;
}

.page-header p {
  color: #6c757d;
  font-size: 16px;
}

/* 筛选区域 */
.filters {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  border: 1px solid #e9ecef;
}

.search-box {
  display: flex;
  margin-bottom: 20px;
  max-width: 400px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px 0 0 6px;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #667eea;
}

.search-btn {
  background-color: #667eea;
  color: white;
  border: none;
  padding: 12px 16px;
  border-radius: 0 6px 6px 0;
  cursor: pointer;
  font-size: 14px;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-filter {
  background-color: white;
  color: #495057;
  border: 1px solid #ddd;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
}

.tag-filter:hover,
.tag-filter.active {
  background-color: #667eea;
  color: white;
  border-color: #667eea;
}

/* 文章列表 */
.articles-container {
  space-y: 20px;
}

.article-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.article-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.1);
  border-color: #667eea;
}

.article-header {
  margin-bottom: 15px;
}

.article-title {
  font-size: 20px;
  color: #495057;
  margin-bottom: 10px;
  font-weight: bold;
  cursor: pointer;
  transition: color 0.3s ease;
}

.article-title:hover {
  color: #667eea;
}

.article-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #6c757d;
  flex-wrap: wrap;
}

.article-summary {
  color: #6c757d;
  line-height: 1.6;
  margin-bottom: 15px;
  font-size: 14px;
}

.article-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.article-tag {
  background-color: #667eea;
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.article-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.read-btn {
  background-color: #667eea;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.read-btn:hover {
  background-color: #5a6fd8;
  transform: translateX(2px);
}

.article-stats {
  display: flex;
  gap: 15px;
}

.stat {
  font-size: 13px;
  color: #6c757d;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin-bottom: 10px;
  color: #495057;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 40px;
  padding: 20px;
}

.page-btn {
  background-color: #667eea;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background-color: #5a6fd8;
}

.page-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.page-info {
  color: #6c757d;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article-meta {
    gap: 10px;
  }
  
  .article-actions {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .pagination {
    flex-direction: column;
    gap: 10px;
  }
  
  .filters {
    padding: 20px;
  }
  
  .search-box {
    max-width: 100%;
  }
}
</style> 