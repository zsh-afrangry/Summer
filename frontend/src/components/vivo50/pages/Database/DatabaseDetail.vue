<template>
  <div class="elegant-container topic-detail-container">
    <div class="topic-detail-layout">
      <!-- 主内容区域 -->
      <main class="topic-main-content">
        <!-- 页面头部 -->
        <header class="topic-header" id="header">
          <div class="header-top">
            <button class="back-btn" @click="goBack">
              <i>←</i> 返回
            </button>
          </div>
          
          <div class="topic-title-section">
            <div class="topic-icon-large">{{ currentTopic.icon }}</div>
            <div class="topic-title-info">
              <h1 class="topic-main-title">{{ currentTopic.name }}</h1>
              <p class="topic-subtitle">{{ currentTopic.subtitle || '深入理解数据库核心技术' }}</p>
            </div>
          </div>
        </header>

        <!-- 内容区域 -->
        <div class="topic-content">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <p>正在加载内容...</p>
          </div>
          
          <!-- 错误状态 -->
          <div v-else-if="error" class="error-container">
            <div class="error-icon">⚠️</div>
            <p>{{ error }}</p>
            <button @click="loadContent" class="retry-btn">重试</button>
          </div>
          
          <!-- 动态内容 -->
          <div v-else class="dynamic-content" v-html="contentHtml"></div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { databaseTopics } from '../../data/databaseTopics.js'
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import 'highlight.js/styles/vs2015.css' // 代码高亮样式

export default {
  name: 'DatabaseDetail',
  data() {
    return {
      currentTopic: {},
      contentHtml: '', // 存储渲染后的内容
      loading: false,
      error: null,
      md: null // Markdown 解析器实例
    }
  },
  created() {
    this.initMarkdownParser()
    this.loadTopicData()
    this.loadContent()
  },
  methods: {
    initMarkdownParser() {
      this.md = new MarkdownIt({
        html: true, // 允许 HTML 标签
        linkify: true, // 自动识别链接
        typographer: true, // 自动替换标点符号
        highlight: function (str, lang) {
          if (lang && hljs.getLanguage(lang)) {
            try {
              return hljs.highlight(str, { language: lang }).value
            } catch (__) {
              // 高亮失败时忽略错误，使用默认转义
            }
          }
          return '' // 使用外部默认转义
        }
      })
    },
    async loadContent() {
      this.loading = true
      this.error = null
      
      const topicId = this.$route.params.topicId || 'sql-basics' // 默认使用 sql-basics 文件
      
      // 根据topicId确定文件所在的目录
      const getFilePath = (topicId, fileType) => {
        if (topicId === 'sql-basics') {
          return `/vivo50/resources/database/database1/${topicId}.${fileType}`
        } else if (topicId === 'index-optimization') {
          return `/vivo50/resources/database/database2/${topicId}.${fileType}`
        } else {
          // 其他主题默认尝试database1
          return `/vivo50/resources/database/database1/${topicId}.${fileType}`
        }
      }
      
      try {
        // 优先尝试加载 Markdown 文件
        const mdPath = getFilePath(topicId, 'md')
        const mdResponse = await fetch(mdPath)
        if (mdResponse.ok) {
          const mdContent = await mdResponse.text()
          this.contentHtml = DOMPurify.sanitize(this.md.render(mdContent))
          return
        }
        
        // 如果 MD 文件不存在，尝试加载 HTML 文件
        const htmlPath = getFilePath(topicId, 'html')
        const htmlResponse = await fetch(htmlPath)
        if (htmlResponse.ok) {
          const htmlContent = await htmlResponse.text()
          // 对于 HTML 文件，提取 body 内容并清理
          const bodyMatch = htmlContent.match(/<body[^>]*>([\s\S]*?)<\/body>/i)
          const content = bodyMatch ? bodyMatch[1] : htmlContent
          this.contentHtml = DOMPurify.sanitize(content)
          return
        }
        
        // 如果都不存在，显示错误信息
        this.error = `未找到主题 "${topicId}" 的内容文件`
        
      } catch (error) {
        console.error('加载内容失败:', error)
        this.error = `加载内容时出错: ${error.message}`
      } finally {
        this.loading = false
      }
    },
    loadTopicData() {
      const topicId = this.$route.params.topicId || 'sql-basics'
      this.currentTopic = databaseTopics.find(topic => topic.id === topicId) || databaseTopics[0]
    },
    goBack() {
      this.$router.go(-1)
    }
  },
  watch: {
    '$route'() {
      // 路由变化时重新加载内容
      this.loadTopicData()
      this.loadContent()
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
  min-height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
  background: var(--bg);
  position: relative;
}

.topic-main-content {
  padding: 2rem;
  min-height: 100vh;
}

/* 页面头部 */
.topic-header {
  background: linear-gradient(135deg, var(--bg-secondary), var(--bg-accent));
  border-radius: var(--border-radius);
  padding: 2rem;
  margin-bottom: 2rem;
  border: 1px solid var(--border-light);
  backdrop-filter: blur(10px);
}

.header-top {
  margin-bottom: 1.5rem;
}

.back-btn {
  background: transparent;
  border: 1px solid var(--border-light);
  color: var(--text);
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9em;
  transition: var(--transition);
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.back-btn:hover {
  background: var(--bg-accent);
  border-color: var(--accent);
  transform: translateX(-2px);
}

.topic-title-section {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.topic-icon-large {
  font-size: 4em;
  opacity: 0.9;
  flex-shrink: 0;
}

.topic-title-info {
  flex: 1;
}

.topic-main-title {
  font-size: 2.5em;
  font-weight: 700;
  color: var(--text);
  margin: 0 0 0.5rem 0;
  font-family: 'Cormorant Garamond', serif;
}

.topic-subtitle {
  font-size: 1.1em;
  color: var(--light-text);
  margin: 0;
  font-weight: 400;
}

/* 内容区域 */
.topic-content {
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 2rem;
  border: 1px solid var(--border-light);
  min-height: 400px;
}

/* 加载和错误状态 */
.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  gap: 1rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border-light);
  border-top: 4px solid var(--accent);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon {
  font-size: 3em;
  opacity: 0.7;
}

.retry-btn {
  background: var(--accent);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: var(--border-radius);
  cursor: pointer;
  font-size: 0.9em;
  font-weight: 500;
  transition: var(--transition);
}

.retry-btn:hover {
  background: var(--accent-dark);
  transform: translateY(-2px);
}

/* 动态内容样式 */
.dynamic-content {
  color: var(--text);
  line-height: 1.7;
  font-size: 1rem;
}

.dynamic-content h1,
.dynamic-content h2,
.dynamic-content h3,
.dynamic-content h4,
.dynamic-content h5,
.dynamic-content h6 {
  color: var(--text);
  margin: 2rem 0 1rem 0;
  font-family: 'Cormorant Garamond', serif;
}

.dynamic-content h1 { font-size: 2.2em; font-weight: 700; }
.dynamic-content h2 { font-size: 1.8em; font-weight: 600; }
.dynamic-content h3 { font-size: 1.4em; font-weight: 600; }
.dynamic-content h4 { font-size: 1.2em; font-weight: 500; }

.dynamic-content p {
  margin: 1rem 0;
  text-align: justify;
}

.dynamic-content code {
  background: var(--bg-accent);
  color: var(--accent);
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
  font-size: 0.9em;
  border: 1px solid var(--border-light);
}

.dynamic-content pre {
  background: var(--bg-dark);
  color: var(--text-light);
  padding: 1.5rem;
  border-radius: var(--border-radius);
  overflow-x: auto;
  margin: 1.5rem 0;
  border: 1px solid var(--border);
}

.dynamic-content pre code {
  background: none;
  color: inherit;
  padding: 0;
  border: none;
  font-size: 0.85em;
}

.dynamic-content blockquote {
  border-left: 4px solid var(--accent);
  background: var(--bg-accent);
  padding: 1rem 1.5rem;
  margin: 1.5rem 0;
  border-radius: 0 var(--border-radius) var(--border-radius) 0;
  font-style: italic;
}

.dynamic-content ul, .dynamic-content ol {
  margin: 1rem 0;
  padding-left: 2rem;
}

.dynamic-content li {
  margin: 0.5rem 0;
}

.dynamic-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 1.5rem 0;
  background: var(--bg);
  border-radius: var(--border-radius);
  overflow: hidden;
  border: 1px solid var(--border-light);
}

.dynamic-content th,
.dynamic-content td {
  padding: 0.75rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--border-light);
}

.dynamic-content th {
  background: var(--bg-accent);
  font-weight: 600;
  color: var(--text);
}

.dynamic-content img {
  max-width: 100%;
  height: auto;
  border-radius: var(--border-radius);
  margin: 1rem 0;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .topic-main-content {
    padding: 1rem;
  }
  
  .topic-header {
    padding: 1.5rem;
  }
  
  .topic-title-section {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
  
  .topic-main-title {
    font-size: 2em;
  }
  
  .topic-content {
    padding: 1.5rem;
  }
  
  .dynamic-content {
    font-size: 0.95rem;
  }
  
  .dynamic-content h1 { font-size: 1.8em; }
  .dynamic-content h2 { font-size: 1.5em; }
  .dynamic-content h3 { font-size: 1.3em; }
}

@media (max-width: 480px) {
  .topic-main-content {
    padding: 0.75rem;
  }
  
  .topic-header {
    padding: 1rem;
  }
  
  .topic-content {
    padding: 1rem;
  }
  
  .dynamic-content table {
    font-size: 0.85rem;
  }
  
  .dynamic-content th,
  .dynamic-content td {
    padding: 0.5rem;
  }
}
</style>