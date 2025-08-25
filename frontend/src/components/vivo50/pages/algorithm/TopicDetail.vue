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
              <p class="topic-subtitle">{{ currentTopic.subtitle || '副标题副标题副标题' }}</p>
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
import { algorithmTopics } from '../../data/algorithmTopics.js'
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import 'highlight.js/styles/vs2015.css' // 代码高亮样式

export default {
  name: 'TopicDetail',
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
      
      const topicId = this.$route.params.topicId || 'array' // 默认使用 array 文件
      
      try {
        // 优先尝试加载 Markdown 文件
        const mdResponse = await fetch(`/vivo50/resources/algorithm/MD/${topicId}.md`)
        if (mdResponse.ok) {
          const mdContent = await mdResponse.text()
          this.contentHtml = DOMPurify.sanitize(this.md.render(mdContent))
          return
        }
        
        // 如果 MD 文件不存在，尝试加载 HTML 文件
        const htmlResponse = await fetch(`/vivo50/resources/algorithm/HTML/${topicId}.html`)
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
      const topicId = this.$route.params.topicId || 'array'
      this.currentTopic = algorithmTopics.find(topic => topic.id === topicId) || algorithmTopics[0]
    },
    goBack() {
      this.$router.go(-1)
    },



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
}

/* 主内容区域 - 使用主题样式 */
.topic-main-content {
  width: 100%;
  padding: 0;
  background: white;
  overflow-y: auto;
}

/* 页面头部 - 简化样式 */
.topic-header {
  padding: 2rem 3rem 3rem;
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary));
  border-bottom: 1px solid var(--border-light);
  box-shadow: var(--card-shadow);
  backdrop-filter: blur(10px);
}

.header-top {
  margin-bottom: 2rem;
}

/* 返回按钮样式 */
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
}

.back-btn:hover {
  background: var(--accent);
  color: white;
}

.topic-title-section {
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
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



/* 动态内容样式 */
.dynamic-content {
  background: var(--bg-primary);
  border-radius: var(--border-radius);
  padding: 2rem;
  box-shadow: var(--card-shadow);
  border: 1px solid var(--border-light);
  backdrop-filter: blur(10px);
}

/* 确保动态内容中的样式正确 */
.dynamic-content h1,
.dynamic-content h2,
.dynamic-content h3,
.dynamic-content h4,
.dynamic-content h5,
.dynamic-content h6 {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: var(--text);
  margin-top: 1.5rem;
  margin-bottom: 1rem;
}

.dynamic-content h1 { font-size: 2rem; }
.dynamic-content h2 { font-size: 1.6rem; }
.dynamic-content h3 { font-size: 1.3rem; }

.dynamic-content p {
  line-height: 1.6;
  margin-bottom: 1rem;
  color: var(--text);
}

.dynamic-content ul,
.dynamic-content ol {
  padding-left: 1.5rem;
  margin-bottom: 1rem;
}

.dynamic-content li {
  margin-bottom: 0.5rem;
  line-height: 1.5;
}

.dynamic-content code {
  background: var(--bg-accent);
  padding: 0.2rem 0.4rem;
  border-radius: 3px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.9em;
  color: #eb5757;
}

.dynamic-content pre {
  background: #1e1e1e;
  border-radius: var(--border-radius);
  padding: 1.5rem;
  overflow-x: auto;
  margin: 1rem 0;
}

.dynamic-content pre code {
  background: transparent;
  color: #d4d4d4;
  padding: 0;
  font-size: 0.9rem;
  line-height: 1.5;
}

.dynamic-content blockquote {
  border-left: 3px solid var(--accent);
  padding-left: 1rem;
  margin: 1rem 0;
  color: var(--light-text);
  font-style: italic;
}

.dynamic-content hr {
  border: none;
  border-top: 1px solid var(--border-light);
  margin: 2rem 0;
}

.dynamic-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
}

.dynamic-content th,
.dynamic-content td {
  border: 1px solid var(--border-medium);
  padding: 0.75rem;
  text-align: left;
}

.dynamic-content th {
  background: var(--bg-secondary);
  font-weight: 600;
}

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border-light);
  border-top: 4px solid var(--accent);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误状态样式 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  background: var(--bg-primary);
  border-radius: var(--border-radius);
  border: 1px solid var(--border-light);
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.error-container p {
  color: var(--light-text);
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.retry-btn {
  padding: 0.75rem 1.5rem;
  background: var(--accent);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
}

.retry-btn:hover {
  background: var(--accent);
  opacity: 0.8;
}

/* 响应式设计 - 简化版本 */
@media (max-width: 1024px) {
  .topic-header {
    padding: 2rem;
  }
  
  .topic-content {
    padding: 1.5rem 2rem 2rem;
  }
}

@media (max-width: 768px) {
  .topic-header {
    padding: 1.5rem;
  }
  
  .topic-title-section {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
  
  .topic-content {
    padding: 1rem;
  }
  
  .dynamic-content {
    padding: 1.5rem;
  }
}
</style>