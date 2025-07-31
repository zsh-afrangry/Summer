<template>
  <div class="blog-list">
    <div class="page-header">
      <h1>📚 文章列表</h1>
      <p>共 {{ totalArticles }} 篇文章</p>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filters">
      <div class="search-box">
        <input 
          v-model="searchKeyword"
          type="text" 
          placeholder="搜索文章标题或内容..."
          class="search-input"
          @keyup.enter="performSearch"
        />
        <button class="search-btn" @click="performSearch">🔍</button>
      </div>
      
      <div class="filter-tags">
        <button 
          :class="['tag-filter', { active: selectedTag === '' }]"
          @click="selectTag('')"
        >
          全部
        </button>
        <button 
          v-for="tag in allTags"
          :key="tag.id"
          :class="['tag-filter', { active: selectedTag === tag.name }]"
          @click="selectTag(tag.name)"
        >
          {{ tag.name }} ({{ tag.articleCount }})
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      🔄 加载文章中...
    </div>

    <!-- 文章列表 -->
    <div class="articles-container" v-else>
      <article 
        v-for="article in articles" 
        :key="article.id"
        class="article-item"
      >
        <div class="article-content">
          <div class="article-header">
            <h2 class="article-title" @click="viewArticle(article.id)">{{ article.title }}</h2>
            <div class="article-meta">
              <span class="article-date">📅 {{ formatDate(article.createdAt) }}</span>
              <span class="article-reading">👁️ {{ article.viewCount || 0 }} 次浏览</span>
              <span class="article-author">✍️ {{ article.author?.username || '作者' }}</span>
            </div>
          </div>
          
          <p class="article-summary">{{ article.summary || article.content?.substring(0, 200) + '...' }}</p>
          
          <div class="article-tags">
            <span 
              v-for="tag in article.tags || []"
              :key="tag.id || tag"
              class="article-tag"
              @click="selectTag(tag.name || tag)"
            >
              {{ tag.name || tag }}
            </span>
          </div>
          
          <div class="article-actions">
            <button class="read-btn" @click="viewArticle(article.id)">
              阅读全文 →
            </button>
            <div class="article-stats">
              <span>💬 {{ article.commentCount || 0 }}</span>
              <span>👍 {{ article.likeCount || 0 }}</span>
            </div>
          </div>
        </div>
      </article>
      
      <!-- 空状态 -->
      <div v-if="articles.length === 0" class="empty-state">
        <p>📝 暂无文章</p>
        <p>{{ searchKeyword || selectedTag ? '没有找到符合条件的文章' : '还没有发布任何文章' }}</p>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination" v-if="totalPages > 1">
      <button 
        :disabled="currentPage <= 1" 
        @click="goToPage(currentPage - 1)"
        class="page-btn"
      >
        上一页
      </button>
      
      <span class="page-info">
        第 {{ currentPage }} 页，共 {{ totalPages }} 页
      </span>
      
      <button 
        :disabled="currentPage >= totalPages" 
        @click="goToPage(currentPage + 1)"
        class="page-btn"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
import { articleApi, tagApi } from '@/api'
import message from '@/utils/message'

export default {
  name: 'BlogList',
  data() {
    return {
      searchKeyword: '',
      selectedTag: '',
      currentPage: 1,
      pageSize: 10,
      articles: [],
      loading: false,
      totalArticles: 0,
      allTags: []
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalArticles / this.pageSize)
    }
  },
  async mounted() {
    // 检查路由参数
    if (this.$route.query.tag) {
      this.selectedTag = this.$route.query.tag
    }
    
    await this.loadData()
  },
  methods: {
    // 加载数据
    async loadData() {
      await Promise.all([
        this.loadArticles(),
        this.loadTags()
      ])
    },

    // 加载文章列表
    async loadArticles() {
      this.loading = true
      try {
        let result
        
        if (this.searchKeyword) {
          // 搜索文章
          result = await articleApi.searchArticles(
            this.searchKeyword, 
            this.currentPage - 1, 
            this.pageSize
          )
        } else if (this.selectedTag) {
          // 按标签筛选
          result = await articleApi.getArticlesByTagName(this.selectedTag)
        } else {
          // 获取已发布文章
          result = await articleApi.getPublishedArticles(
            this.currentPage - 1, 
            this.pageSize
          )
        }
        
        if (result.success) {
          this.articles = result.data.content || result.data
          this.totalArticles = result.data.totalElements || result.data.length
        }
      } catch (error) {
        console.error('加载文章失败:', error)
        message.error('加载文章失败')
      } finally {
        this.loading = false
      }
    },

    // 加载标签列表
    async loadTags() {
      try {
        const result = await tagApi.getPopularTags(20)
        if (result.success) {
          this.allTags = result.data
        }
      } catch (error) {
        console.error('加载标签失败:', error)
      }
    },

    // 执行搜索
    async performSearch() {
      this.currentPage = 1
      await this.loadArticles()
    },

    // 选择标签
    async selectTag(tag) {
      this.selectedTag = tag
      this.currentPage = 1
      
      // 更新路由参数
      if (tag) {
        this.$router.push({ query: { tag } })
      } else {
        this.$router.push({ query: {} })
      }
      
      await this.loadArticles()
    },

    // 查看文章
    viewArticle(articleId) {
      // 增加浏览量
      articleApi.incrementViewCount(articleId)
      
      // 跳转到文章详情页
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

    // 跳转到指定页面
    async goToPage(page) {
      this.currentPage = page
      await this.loadArticles()
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

/* 标签筛选样式 */
.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 15px;
  align-items: center; /* 垂直居中对齐 */
}

.tag-filter {
  background: #f8f9fa;
  color: #666;
  border: 1px solid #dee2e6;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  text-decoration: none;
  display: inline-flex; /* 使用inline-flex确保对齐 */
  align-items: center; /* 垂直居中 */
  line-height: 1.2; /* 统一行高 */
  min-height: 32px; /* 最小高度保证一致性 */
  white-space: nowrap; /* 防止换行 */
}

.tag-filter:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
  transform: translateY(-1px);
}

.tag-filter.active {
  background: #667eea;
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

/* 加载状态样式 */
.loading {
  text-align: center;
  padding: 40px;
  font-size: 16px;
  color: #666;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p:first-child {
  font-size: 18px;
  margin-bottom: 10px;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 40px;
  padding: 20px;
}

.page-btn {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #764ba2;
  transform: translateY(-1px);
}

.page-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.page-info {
  font-size: 14px;
  color: #666;
}

/* 文章标题点击样式 */
.article-title {
  cursor: pointer;
  transition: color 0.3s ease;
}

.article-title:hover {
  color: #667eea;
}

/* 标签点击样式 */
.article-tag {
  cursor: pointer;
  transition: all 0.3s ease;
}

.article-tag:hover {
  background: #667eea;
  color: white;
  transform: translateY(-1px);
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