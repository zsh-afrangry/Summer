<template>
  <div class="query-console">
    <div class="console-header">
      <h2>SQL查询控制台</h2>
      <div class="header-actions">
        <button @click="clearQuery" class="action-btn clear">清空</button>
        <button @click="formatQuery" class="action-btn format">格式化</button>
        <button @click="saveQuery" class="action-btn save">保存</button>
        <button @click="loadQuery" class="action-btn load">加载</button>
      </div>
    </div>

    <!-- Query Templates -->
    <div class="templates-section">
      <h3>常用查询模板</h3>
      <div class="templates-grid">
        <button 
          v-for="template in queryTemplates" 
          :key="template.name"
          @click="applyTemplate(template)"
          class="template-btn"
          :title="template.description"
        >
          {{ template.name }}
        </button>
      </div>
    </div>

    <!-- Query Editor -->
    <div class="editor-section">
      <div class="editor-header">
        <h3>SQL编辑器</h3>
        <div class="editor-actions">
          <select v-model="selectedDatabase" class="database-select">
            <option value="">选择数据库...</option>
            <option value="my_blog">my_blog</option>
          </select>
          <button @click="executeQuery" :disabled="!sqlQuery.trim() || loading" class="execute-btn">
            <span v-if="loading">执行中...</span>
            <span v-else>执行查询 (Ctrl+Enter)</span>
          </button>
        </div>
      </div>
      
      <div class="editor-container">
        <textarea 
          v-model="sqlQuery"
          @keydown.ctrl.enter="executeQuery"
          class="sql-editor"
          placeholder="在这里输入你的SQL查询语句...&#10;&#10;例如：&#10;SELECT * FROM users LIMIT 10;&#10;SHOW TABLES;&#10;DESCRIBE table_name;"
          rows="12"
        ></textarea>
        
        <div class="editor-sidebar">
          <div class="query-info">
            <h4>查询信息</h4>
            <p><strong>行数:</strong> {{ sqlQuery.split('\n').length }}</p>
            <p><strong>字符数:</strong> {{ sqlQuery.length }}</p>
            <p><strong>查询类型:</strong> {{ queryType }}</p>
          </div>
          
          <div class="quick-actions">
            <h4>快捷操作</h4>
            <button @click="addTemplate('SELECT')" class="quick-btn">SELECT</button>
            <button @click="addTemplate('INSERT')" class="quick-btn">INSERT</button>
            <button @click="addTemplate('UPDATE')" class="quick-btn">UPDATE</button>
            <button @click="addTemplate('DELETE')" class="quick-btn">DELETE</button>
            <button @click="addTemplate('CREATE')" class="quick-btn">CREATE</button>
            <button @click="addTemplate('ALTER')" class="quick-btn">ALTER</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Query History -->
    <div class="history-section">
      <div class="history-header">
        <h3>查询历史</h3>
        <button @click="clearHistory" class="clear-history-btn">清空历史</button>
      </div>
      <div class="history-list">
        <div 
          v-for="(query, index) in queryHistory" 
          :key="index"
          @click="loadHistoryQuery(query)"
          class="history-item"
          :class="{ success: query.status === 'success', error: query.status === 'error' }"
        >
          <div class="history-content">
            <div class="history-query">{{ query.sql.substring(0, 100) }}{{ query.sql.length > 100 ? '...' : '' }}</div>
            <div class="history-meta">
              <span class="timestamp">{{ formatTime(query.timestamp) }}</span>
              <span class="status">{{ query.status === 'success' ? '成功' : '失败' }}</span>
              <span v-if="query.duration" class="duration">{{ query.duration }}ms</span>
            </div>
          </div>
          <button @click.stop="removeHistoryItem(index)" class="remove-btn">×</button>
        </div>
      </div>
    </div>

    <!-- Results Section -->
    <div v-if="queryResults" class="results-section">
      <div class="results-header">
        <h3>查询结果</h3>
        <div class="results-meta">
          <span v-if="queryResults.duration" class="duration">执行时间: {{ queryResults.duration }}ms</span>
          <span v-if="queryResults.affectedRows !== undefined" class="affected">
            影响行数: {{ queryResults.affectedRows }}
          </span>
          <span v-if="queryResults.data && queryResults.data.length" class="count">
            返回记录: {{ queryResults.data.length }}条
          </span>
        </div>
      </div>

      <!-- Success Results -->
      <div v-if="queryResults.success" class="results-content">
        <!-- Table Results -->
        <div v-if="queryResults.data && queryResults.data.length" class="table-results">
          <div class="table-container">
            <table class="results-table">
              <thead>
                <tr>
                  <th v-for="column in Object.keys(queryResults.data[0])" :key="column">
                    {{ column }}
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, index) in queryResults.data" :key="index">
                  <td v-for="column in Object.keys(row)" :key="column" class="result-cell">
                    <div class="cell-content">{{ formatCellValue(row[column]) }}</div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div class="export-actions">
            <button @click="exportCSV" class="export-btn">导出CSV</button>
            <button @click="exportJSON" class="export-btn">导出JSON</button>
          </div>
        </div>

        <!-- Non-Table Results -->
        <div v-else-if="queryResults.message" class="message-results">
          <div class="success-message">
            <span class="icon">✓</span>
            {{ queryResults.message }}
          </div>
        </div>
      </div>

      <!-- Error Results -->
      <div v-else class="error-results">
        <div class="error-message">
          <span class="icon">✗</span>
          <div class="error-content">
            <strong>查询执行失败:</strong>
            <p>{{ queryResults.error }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Saved Queries Modal -->
    <div v-if="showSavedQueries" class="modal-overlay" @click="closeSavedQueries">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>保存的查询</h3>
          <button @click="closeSavedQueries" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div v-if="savedQueries.length === 0" class="empty-state">
            <p>暂无保存的查询</p>
          </div>
          <div v-else class="saved-queries-list">
            <div 
              v-for="(saved, index) in savedQueries" 
              :key="index"
              class="saved-query-item"
            >
              <div class="saved-query-content">
                <h4>{{ saved.name }}</h4>
                <p>{{ saved.sql.substring(0, 150) }}{{ saved.sql.length > 150 ? '...' : '' }}</p>
                <span class="saved-date">保存于: {{ formatTime(saved.timestamp) }}</span>
              </div>
              <div class="saved-query-actions">
                <button @click="loadSavedQuery(saved)" class="load-saved-btn">加载</button>
                <button @click="deleteSavedQuery(index)" class="delete-saved-btn">删除</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { dbService } from './services/dbService.js'

export default {
  name: 'QueryConsole',
  data() {
    return {
      sqlQuery: '',
      queryResults: null,
      loading: false,
      selectedDatabase: 'my_blog',
      queryHistory: [],
      savedQueries: [],
      showSavedQueries: false,
      
      queryTemplates: [
        {
          name: '查看所有表',
          sql: 'SHOW TABLES;',
          description: '显示当前数据库中的所有表'
        },
        {
          name: '查看表结构',
          sql: 'DESCRIBE table_name;',
          description: '显示指定表的结构信息'
        },
        {
          name: '查询前10条',
          sql: 'SELECT * FROM table_name LIMIT 10;',
          description: '查询表中的前10条记录'
        },
        {
          name: '计数查询',
          sql: 'SELECT COUNT(*) as total FROM table_name;',
          description: '统计表中的记录总数'
        },
        {
          name: '条件查询',
          sql: 'SELECT * FROM table_name WHERE column_name = \'value\';',
          description: '根据条件查询数据'
        },
        {
          name: '插入数据',
          sql: 'INSERT INTO table_name (column1, column2) VALUES (\'value1\', \'value2\');',
          description: '向表中插入新数据'
        },
        {
          name: '更新数据',
          sql: 'UPDATE table_name SET column1 = \'new_value\' WHERE condition;',
          description: '更新表中的数据'
        },
        {
          name: '删除数据',
          sql: 'DELETE FROM table_name WHERE condition;',
          description: '删除表中的数据'
        }
      ]
    }
  },
  computed: {
    queryType() {
      const trimmed = this.sqlQuery.trim().toUpperCase()
      if (trimmed.startsWith('SELECT')) return 'SELECT'
      if (trimmed.startsWith('INSERT')) return 'INSERT'
      if (trimmed.startsWith('UPDATE')) return 'UPDATE'
      if (trimmed.startsWith('DELETE')) return 'DELETE'
      if (trimmed.startsWith('CREATE')) return 'CREATE'
      if (trimmed.startsWith('ALTER')) return 'ALTER'
      if (trimmed.startsWith('DROP')) return 'DROP'
      if (trimmed.startsWith('SHOW')) return 'SHOW'
      if (trimmed.startsWith('DESCRIBE')) return 'DESCRIBE'
      return '未知'
    }
  },
  mounted() {
    this.loadQueryHistory()
    this.loadSavedQueries()
  },
  methods: {
    async executeQuery() {
      if (!this.sqlQuery.trim()) return
      
      this.loading = true
      const startTime = Date.now()
      
      try {
        const result = await dbService.executeQuery(this.sqlQuery)
        const duration = Date.now() - startTime
        
        this.queryResults = {
          ...result,
          duration
        }
        
        // 添加到历史记录
        this.addToHistory(this.sqlQuery, 'success', duration)
        
        this.$emit('query-executed', {
          sql: this.sqlQuery,
          result,
          type: this.queryType
        })
        
      } catch (error) {
        const duration = Date.now() - startTime
        this.queryResults = {
          success: false,
          error: error.message || '查询执行失败',
          duration
        }
        
        this.addToHistory(this.sqlQuery, 'error', duration)
      } finally {
        this.loading = false
      }
    },

    clearQuery() {
      this.sqlQuery = ''
      this.queryResults = null
    },

    formatQuery() {
      // 简单的SQL格式化
      let formatted = this.sqlQuery
        .replace(/\bSELECT\b/gi, '\nSELECT')
        .replace(/\bFROM\b/gi, '\nFROM')
        .replace(/\bWHERE\b/gi, '\nWHERE')
        .replace(/\bAND\b/gi, '\n  AND')
        .replace(/\bOR\b/gi, '\n  OR')
        .replace(/\bORDER BY\b/gi, '\nORDER BY')
        .replace(/\bGROUP BY\b/gi, '\nGROUP BY')
        .replace(/\bHAVING\b/gi, '\nHAVING')
        .replace(/\bLIMIT\b/gi, '\nLIMIT')
        .replace(/\bJOIN\b/gi, '\nJOIN')
        .replace(/\bLEFT JOIN\b/gi, '\nLEFT JOIN')
        .replace(/\bRIGHT JOIN\b/gi, '\nRIGHT JOIN')
        .replace(/\bINNER JOIN\b/gi, '\nINNER JOIN')
        .trim()
      
      this.sqlQuery = formatted
    },

    applyTemplate(template) {
      this.sqlQuery = template.sql
    },

    addTemplate(type) {
      const templates = {
        'SELECT': 'SELECT * FROM table_name WHERE condition;',
        'INSERT': 'INSERT INTO table_name (column1, column2) VALUES (value1, value2);',
        'UPDATE': 'UPDATE table_name SET column1 = value1 WHERE condition;',
        'DELETE': 'DELETE FROM table_name WHERE condition;',
        'CREATE': 'CREATE TABLE table_name (\n  id INT PRIMARY KEY AUTO_INCREMENT,\n  column1 VARCHAR(255),\n  column2 TEXT\n);',
        'ALTER': 'ALTER TABLE table_name ADD COLUMN new_column VARCHAR(255);'
      }
      
      if (this.sqlQuery.trim()) {
        this.sqlQuery += '\n\n' + templates[type]
      } else {
        this.sqlQuery = templates[type]
      }
    },

    saveQuery() {
      if (!this.sqlQuery.trim()) return
      
      const name = prompt('请输入查询名称:')
      if (!name) return
      
      const savedQuery = {
        name,
        sql: this.sqlQuery,
        timestamp: Date.now()
      }
      
      this.savedQueries.unshift(savedQuery)
      this.saveSavedQueries()
      this.$message?.success('查询已保存')
    },

    loadQuery() {
      this.showSavedQueries = true
    },

    loadSavedQuery(savedQuery) {
      this.sqlQuery = savedQuery.sql
      this.closeSavedQueries()
    },

    deleteSavedQuery(index) {
      if (confirm('确定要删除这个保存的查询吗？')) {
        this.savedQueries.splice(index, 1)
        this.saveSavedQueries()
      }
    },

    closeSavedQueries() {
      this.showSavedQueries = false
    },

    addToHistory(sql, status, duration) {
      const historyItem = {
        sql,
        status,
        duration,
        timestamp: Date.now()
      }
      
      this.queryHistory.unshift(historyItem)
      
      // 限制历史记录数量
      if (this.queryHistory.length > 50) {
        this.queryHistory = this.queryHistory.slice(0, 50)
      }
      
      this.saveQueryHistory()
    },

    loadHistoryQuery(historyItem) {
      this.sqlQuery = historyItem.sql
    },

    removeHistoryItem(index) {
      this.queryHistory.splice(index, 1)
      this.saveQueryHistory()
    },

    clearHistory() {
      if (confirm('确定要清空查询历史吗？')) {
        this.queryHistory = []
        this.saveQueryHistory()
      }
    },

    formatCellValue(value) {
      if (value === null) return 'NULL'
      if (value === undefined) return ''
      if (typeof value === 'object') return JSON.stringify(value)
      if (typeof value === 'string' && value.length > 200) {
        return value.substring(0, 200) + '...'
      }
      return value
    },

    formatTime(timestamp) {
      return new Date(timestamp).toLocaleString('zh-CN')
    },

    exportCSV() {
      if (!this.queryResults.data || !this.queryResults.data.length) return
      
      const headers = Object.keys(this.queryResults.data[0])
      const csvContent = [
        headers.join(','),
        ...this.queryResults.data.map(row => 
          headers.map(header => {
            const value = row[header]
            if (value === null) return 'NULL'
            if (typeof value === 'string' && value.includes(',')) {
              return `"${value.replace(/"/g, '""')}"`
            }
            return value
          }).join(',')
        )
      ].join('\n')
      
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', `query_result_${Date.now()}.csv`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },

    exportJSON() {
      if (!this.queryResults.data) return
      
      const jsonContent = JSON.stringify(this.queryResults.data, null, 2)
      const blob = new Blob([jsonContent], { type: 'application/json;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', `query_result_${Date.now()}.json`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },

    loadQueryHistory() {
      const stored = localStorage.getItem('db_query_history')
      if (stored) {
        this.queryHistory = JSON.parse(stored)
      }
    },

    saveQueryHistory() {
      localStorage.setItem('db_query_history', JSON.stringify(this.queryHistory))
    },

    loadSavedQueries() {
      const stored = localStorage.getItem('db_saved_queries')
      if (stored) {
        this.savedQueries = JSON.parse(stored)
      }
    },

    saveSavedQueries() {
      localStorage.setItem('db_saved_queries', JSON.stringify(this.savedQueries))
    }
  }
}
</script>

<style scoped>
.query-console {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.console-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 2px solid #e2e8f0;
}

.console-header h2 {
  color: #2d3748;
  font-size: 1.5rem;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.action-btn.clear { background: #ef4444; color: white; }
.action-btn.format { background: #8b5cf6; color: white; }
.action-btn.save { background: #10b981; color: white; }
.action-btn.load { background: #3b82f6; color: white; }

.templates-section {
  background: #f8fafc;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.templates-section h3 {
  color: #374151;
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
}

.template-btn {
  padding: 10px 15px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.template-btn:hover {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.editor-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.editor-header h3 {
  color: #374151;
  font-size: 1.2rem;
}

.editor-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.database-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: white;
}

.execute-btn {
  padding: 10px 20px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.execute-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.execute-btn:hover:not(:disabled) {
  background: #059669;
}

.editor-container {
  display: grid;
  grid-template-columns: 1fr 250px;
  gap: 20px;
  flex: 1;
}

.sql-editor {
  width: 100%;
  padding: 15px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.95rem;
  line-height: 1.5;
  resize: vertical;
  background: #fafafa;
}

.sql-editor:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.editor-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.query-info, .quick-actions {
  background: white;
  padding: 15px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.query-info h4, .quick-actions h4 {
  color: #374151;
  margin-bottom: 10px;
  font-size: 1rem;
}

.query-info p {
  margin: 5px 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.quick-actions {
  display: flex;
  flex-direction: column;
}

.quick-btn {
  margin-bottom: 8px;
  padding: 6px 12px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s ease;
}

.quick-btn:hover {
  background: #e5e7eb;
}

.history-section {
  max-height: 300px;
  overflow-y: auto;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.history-header h3 {
  color: #374151;
  font-size: 1.1rem;
}

.clear-history-btn {
  padding: 6px 12px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.history-list {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: background 0.2s ease;
}

.history-item:hover {
  background: #f8fafc;
}

.history-item.success {
  border-left: 4px solid #10b981;
}

.history-item.error {
  border-left: 4px solid #ef4444;
}

.history-content {
  flex: 1;
}

.history-query {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.85rem;
  color: #374151;
  margin-bottom: 5px;
}

.history-meta {
  display: flex;
  gap: 15px;
  font-size: 0.75rem;
  color: #6b7280;
}

.status {
  font-weight: 500;
}

.remove-btn {
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  cursor: pointer;
  font-size: 0.9rem;
}

.results-section {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
}

.results-header h3 {
  color: #374151;
  font-size: 1.2rem;
}

.results-meta {
  display: flex;
  gap: 20px;
  font-size: 0.9rem;
  color: #6b7280;
}

.results-content {
  padding: 20px;
}

.table-container {
  max-height: 400px;
  overflow: auto;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  margin-bottom: 15px;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.results-table th {
  background: #f3f4f6;
  padding: 10px 12px;
  text-align: left;
  border-bottom: 2px solid #e2e8f0;
  font-weight: 600;
  color: #374151;
  position: sticky;
  top: 0;
}

.results-table td {
  padding: 8px 12px;
  border-bottom: 1px solid #f3f4f6;
}

.result-cell {
  max-width: 300px;
}

.cell-content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.export-actions {
  display: flex;
  gap: 10px;
}

.export-btn {
  padding: 8px 16px;
  background: #6366f1;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
}

.message-results, .error-results {
  padding: 20px;
}

.success-message, .error-message {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 15px;
  border-radius: 6px;
}

.success-message {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.error-message {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.icon {
  font-weight: bold;
  font-size: 1.1rem;
}

.error-content strong {
  display: block;
  margin-bottom: 5px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  max-width: 800px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  color: #2d3748;
  font-size: 1.4rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: #718096;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  padding: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

.saved-queries-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.saved-query-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 15px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #f8fafc;
}

.saved-query-content h4 {
  color: #374151;
  margin-bottom: 8px;
}

.saved-query-content p {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 8px;
}

.saved-date {
  font-size: 0.75rem;
  color: #9ca3af;
}

.saved-query-actions {
  display: flex;
  gap: 8px;
}

.load-saved-btn, .delete-saved-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.load-saved-btn {
  background: #3b82f6;
  color: white;
}

.delete-saved-btn {
  background: #ef4444;
  color: white;
}

@media (max-width: 1024px) {
  .editor-container {
    grid-template-columns: 1fr;
  }
  
  .templates-grid {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  }
}
</style>