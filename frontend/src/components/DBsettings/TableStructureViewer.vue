<template>
  <div class="table-structure-viewer">
    <div class="structure-header">
      <h2>{{ table.name }} - 表结构</h2>
      <div class="header-actions">
        <button @click="refreshStructure" class="action-btn refresh">刷新</button>
        <button @click="exportStructure" class="action-btn export">导出DDL</button>
        <button @click="showAddColumn = true" class="action-btn add">添加列</button>
      </div>
    </div>

    <!-- Table Information -->
    <div class="table-info-section">
      <div class="info-grid">
        <div class="info-card">
          <h3>基本信息</h3>
          <div class="info-content">
            <p><strong>表名:</strong> {{ table.name }}</p>
            <p><strong>引擎:</strong> {{ tableInfo.engine || 'InnoDB' }}</p>
            <p><strong>字符集:</strong> {{ tableInfo.collation || 'utf8mb4_unicode_ci' }}</p>
            <p><strong>行数:</strong> {{ table.rows || 0 }}</p>
          </div>
        </div>
        
        <div class="info-card">
          <h3>存储信息</h3>
          <div class="info-content">
            <p><strong>数据大小:</strong> {{ formatSize(tableInfo.dataLength) }}</p>
            <p><strong>索引大小:</strong> {{ formatSize(tableInfo.indexLength) }}</p>
            <p><strong>总大小:</strong> {{ formatSize(tableInfo.dataLength + tableInfo.indexLength) }}</p>
            <p><strong>平均行长:</strong> {{ tableInfo.avgRowLength || 0 }} bytes</p>
          </div>
        </div>
        
        <div class="info-card">
          <h3>统计信息</h3>
          <div class="info-content">
            <p><strong>列数:</strong> {{ columns.length }}</p>
            <p><strong>索引数:</strong> {{ indexes.length }}</p>
            <p><strong>外键数:</strong> {{ foreignKeys.length }}</p>
            <p><strong>创建时间:</strong> {{ formatDate(tableInfo.createTime) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Navigation Tabs -->
    <div class="tab-navigation">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        @click="activeTab = tab.id"
        :class="{ active: activeTab === tab.id }"
        class="tab-btn"
      >
        {{ tab.label }}
        <span class="tab-count">{{ getTabCount(tab.id) }}</span>
      </button>
    </div>

    <!-- Content Sections -->
    <div class="content-section">
      <!-- Columns Tab -->
      <div v-if="activeTab === 'columns'" class="columns-section">
        <div class="section-header">
          <h3>列定义</h3>
          <div class="search-box">
            <input 
              v-model="columnSearch" 
              placeholder="搜索列名..."
              class="search-input"
            >
          </div>
        </div>
        
        <div class="table-container">
          <table class="structure-table">
            <thead>
              <tr>
                <th>列名</th>
                <th>数据类型</th>
                <th>长度</th>
                <th>默认值</th>
                <th>空值</th>
                <th>键</th>
                <th>额外</th>
                <th>注释</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="column in filteredColumns" 
                :key="column.name"
                class="column-row"
                :class="{ 'primary-key': column.key === 'PRI', 'unique-key': column.key === 'UNI' }"
              >
                <td class="column-name">
                  <span class="name-text">{{ column.name }}</span>
                  <span v-if="column.key === 'PRI'" class="key-badge primary">PK</span>
                  <span v-else-if="column.key === 'UNI'" class="key-badge unique">UK</span>
                  <span v-else-if="column.key === 'MUL'" class="key-badge index">IDX</span>
                </td>
                <td class="column-type">{{ column.type }}</td>
                <td class="column-length">{{ getColumnLength(column.type) }}</td>
                <td class="column-default">
                  <span v-if="column.default !== null" class="default-value">{{ column.default }}</span>
                  <span v-else class="null-value">NULL</span>
                </td>
                <td class="column-nullable">
                  <span :class="{ 'not-null': !column.nullable, 'nullable': column.nullable }">
                    {{ column.nullable ? 'YES' : 'NO' }}
                  </span>
                </td>
                <td class="column-key">{{ column.key || '-' }}</td>
                <td class="column-extra">{{ column.extra || '-' }}</td>
                <td class="column-comment">{{ column.comment || '-' }}</td>
                <td class="column-actions">
                  <button @click="editColumn(column)" class="action-icon edit" title="编辑">✏️</button>
                  <button @click="deleteColumn(column)" class="action-icon delete" title="删除">🗑️</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Indexes Tab -->
      <div v-if="activeTab === 'indexes'" class="indexes-section">
        <div class="section-header">
          <h3>索引信息</h3>
          <button @click="showAddIndex = true" class="action-btn add">添加索引</button>
        </div>
        
        <div class="indexes-list">
          <div 
            v-for="index in indexes" 
            :key="index.name"
            class="index-card"
            :class="{ 'primary-index': index.keyName === 'PRIMARY' }"
          >
            <div class="index-header">
              <h4>{{ index.keyName }}</h4>
              <div class="index-type">{{ getIndexType(index) }}</div>
            </div>
            <div class="index-details">
              <p><strong>列:</strong> {{ index.columns.join(', ') }}</p>
              <p><strong>类型:</strong> {{ index.indexType || 'BTREE' }}</p>
              <p><strong>唯一:</strong> {{ index.nonUnique ? '否' : '是' }}</p>
              <p><strong>基数:</strong> {{ index.cardinality || 0 }}</p>
            </div>
            <div class="index-actions">
              <button v-if="index.keyName !== 'PRIMARY'" @click="dropIndex(index)" class="drop-index-btn">删除</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Foreign Keys Tab -->
      <div v-if="activeTab === 'foreign-keys'" class="foreign-keys-section">
        <div class="section-header">
          <h3>外键约束</h3>
          <button @click="showAddForeignKey = true" class="action-btn add">添加外键</button>
        </div>
        
        <div v-if="foreignKeys.length === 0" class="empty-state">
          <p>此表没有外键约束</p>
        </div>
        
        <div v-else class="foreign-keys-list">
          <div 
            v-for="fk in foreignKeys" 
            :key="fk.name"
            class="foreign-key-card"
          >
            <div class="fk-header">
              <h4>{{ fk.name }}</h4>
            </div>
            <div class="fk-details">
              <p><strong>本表列:</strong> {{ fk.columnName }}</p>
              <p><strong>引用表:</strong> {{ fk.referencedTableName }}</p>
              <p><strong>引用列:</strong> {{ fk.referencedColumnName }}</p>
              <p><strong>删除规则:</strong> {{ fk.deleteRule || 'RESTRICT' }}</p>
              <p><strong>更新规则:</strong> {{ fk.updateRule || 'RESTRICT' }}</p>
            </div>
            <div class="fk-actions">
              <button @click="dropForeignKey(fk)" class="drop-fk-btn">删除</button>
            </div>
          </div>
        </div>
      </div>

      <!-- DDL Tab -->
      <div v-if="activeTab === 'ddl'" class="ddl-section">
        <div class="section-header">
          <h3>DDL语句</h3>
          <div class="ddl-actions">
            <button @click="generateCreateTable" class="action-btn generate">生成CREATE</button>
            <button @click="copyDDL" class="action-btn copy">复制</button>
          </div>
        </div>
        
        <div class="ddl-container">
          <pre class="ddl-content">{{ ddlStatement }}</pre>
        </div>
      </div>
    </div>

    <!-- Add Column Modal -->
    <div v-if="showAddColumn" class="modal-overlay" @click="closeAddColumn">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>添加新列</h3>
          <button @click="closeAddColumn" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="addColumn">
            <div class="form-group">
              <label>列名 *</label>
              <input v-model="newColumn.name" type="text" required class="form-input">
            </div>
            <div class="form-group">
              <label>数据类型 *</label>
              <select v-model="newColumn.type" required class="form-select">
                <option value="">选择类型...</option>
                <option value="VARCHAR(255)">VARCHAR(255)</option>
                <option value="TEXT">TEXT</option>
                <option value="INT">INT</option>
                <option value="BIGINT">BIGINT</option>
                <option value="DECIMAL(10,2)">DECIMAL(10,2)</option>
                <option value="DATETIME">DATETIME</option>
                <option value="DATE">DATE</option>
                <option value="TIMESTAMP">TIMESTAMP</option>
                <option value="BOOLEAN">BOOLEAN</option>
              </select>
            </div>
            <div class="form-group">
              <label>默认值</label>
              <input v-model="newColumn.default" type="text" class="form-input">
            </div>
            <div class="form-group">
              <label>
                <input v-model="newColumn.nullable" type="checkbox"> 允许NULL
              </label>
            </div>
            <div class="form-group">
              <label>注释</label>
              <input v-model="newColumn.comment" type="text" class="form-input">
            </div>
            <div class="form-actions">
              <button type="button" @click="closeAddColumn" class="cancel-btn">取消</button>
              <button type="submit" class="submit-btn">添加</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import { dbService } from './services/dbService.js'

export default {
  name: 'TableStructureViewer',
  props: {
    table: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      loading: false,
      activeTab: 'columns',
      columnSearch: '',
      
      tableInfo: {},
      columns: [],
      indexes: [],
      foreignKeys: [],
      ddlStatement: '',
      
      showAddColumn: false,
      showAddIndex: false,
      showAddForeignKey: false,
      
      newColumn: {
        name: '',
        type: '',
        default: '',
        nullable: true,
        comment: ''
      },
      
      tabs: [
        { id: 'columns', label: '列' },
        { id: 'indexes', label: '索引' },
        { id: 'foreign-keys', label: '外键' },
        { id: 'ddl', label: 'DDL' }
      ]
    }
  },
  computed: {
    filteredColumns() {
      if (!this.columnSearch) return this.columns
      return this.columns.filter(column => 
        column.name.toLowerCase().includes(this.columnSearch.toLowerCase())
      )
    }
  },
  async mounted() {
    await this.loadTableStructure()
  },
  methods: {
    async loadTableStructure() {
      this.loading = true
      try {
        // 并行加载所有结构信息
        const [columns, indexes, foreignKeys, tableInfo] = await Promise.all([
          dbService.getTableColumns(this.table.name),
          dbService.getTableIndexes(this.table.name),
          dbService.getTableForeignKeys(this.table.name),
          dbService.getTableInfo(this.table.name)
        ])
        
        this.columns = columns
        this.indexes = indexes
        this.foreignKeys = foreignKeys
        this.tableInfo = tableInfo
        
        await this.generateCreateTable()
        
      } catch (error) {
        console.error('加载表结构失败:', error)
        this.$message?.error('加载表结构失败')
      } finally {
        this.loading = false
      }
    },

    async refreshStructure() {
      await this.loadTableStructure()
    },

    getTabCount(tabId) {
      switch (tabId) {
        case 'columns': return this.columns.length
        case 'indexes': return this.indexes.length
        case 'foreign-keys': return this.foreignKeys.length
        case 'ddl': return '1'
        default: return '0'
      }
    },

    getColumnLength(type) {
      const match = type.match(/\((\d+)\)/)
      return match ? match[1] : '-'
    },

    getIndexType(index) {
      if (index.keyName === 'PRIMARY') return 'PRIMARY KEY'
      if (!index.nonUnique) return 'UNIQUE'
      return 'INDEX'
    },

    formatSize(bytes) {
      if (!bytes) return '0 B'
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(1024))
      return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
    },

    formatDate(dateString) {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleString('zh-CN')
    },

    async editColumn(column) {
      // 实现列编辑功能
      const newType = prompt(`编辑列 ${column.name} 的类型:`, column.type)
      if (newType && newType !== column.type) {
        try {
          await dbService.alterColumn(this.table.name, column.name, {
            type: newType
          })
          this.$message?.success('列修改成功')
          await this.refreshStructure()
        } catch (error) {
          console.error('列修改失败:', error)
          this.$message?.error('列修改失败')
        }
      }
    },

    async deleteColumn(column) {
      if (!confirm(`确定要删除列 "${column.name}" 吗？这个操作不可撤销！`)) return
      
      try {
        await dbService.dropColumn(this.table.name, column.name)
        this.$message?.success('列删除成功')
        await this.refreshStructure()
      } catch (error) {
        console.error('列删除失败:', error)
        this.$message?.error('列删除失败')
      }
    },

    async addColumn() {
      try {
        await dbService.addColumn(this.table.name, this.newColumn)
        this.$message?.success('列添加成功')
        this.closeAddColumn()
        await this.refreshStructure()
      } catch (error) {
        console.error('列添加失败:', error)
        this.$message?.error('列添加失败')
      }
    },

    closeAddColumn() {
      this.showAddColumn = false
      this.newColumn = {
        name: '',
        type: '',
        default: '',
        nullable: true,
        comment: ''
      }
    },

    async dropIndex(index) {
      if (!confirm(`确定要删除索引 "${index.keyName}" 吗？`)) return
      
      try {
        await dbService.dropIndex(this.table.name, index.keyName)
        this.$message?.success('索引删除成功')
        await this.refreshStructure()
      } catch (error) {
        console.error('索引删除失败:', error)
        this.$message?.error('索引删除失败')
      }
    },

    async dropForeignKey(fk) {
      if (!confirm(`确定要删除外键约束 "${fk.name}" 吗？`)) return
      
      try {
        await dbService.dropForeignKey(this.table.name, fk.name)
        this.$message?.success('外键删除成功')
        await this.refreshStructure()
      } catch (error) {
        console.error('外键删除失败:', error)
        this.$message?.error('外键删除失败')
      }
    },

    async generateCreateTable() {
      try {
        const ddl = await dbService.showCreateTable(this.table.name)
        this.ddlStatement = ddl
      } catch (error) {
        console.error('生成DDL失败:', error)
        this.ddlStatement = '-- DDL生成失败'
      }
    },

    copyDDL() {
      navigator.clipboard.writeText(this.ddlStatement).then(() => {
        this.$message?.success('DDL已复制到剪贴板')
      }).catch(() => {
        this.$message?.error('复制失败')
      })
    },

    exportStructure() {
      const blob = new Blob([this.ddlStatement], { type: 'text/sql' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', `${this.table.name}_structure.sql`)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }
  }
}
</script>

<style scoped>
.table-structure-viewer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.structure-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e2e8f0;
}

.structure-header h2 {
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

.action-btn.refresh { background: #4299e1; color: white; }
.action-btn.export { background: #9f7aea; color: white; }
.action-btn.add { background: #48bb78; color: white; }
.action-btn.generate { background: #ed8936; color: white; }
.action-btn.copy { background: #38b2ac; color: white; }

.table-info-section {
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.info-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 20px;
}

.info-card h3 {
  color: #374151;
  margin-bottom: 15px;
  font-size: 1.1rem;
  border-bottom: 1px solid #f3f4f6;
  padding-bottom: 8px;
}

.info-content p {
  margin: 8px 0;
  color: #4b5563;
  font-size: 0.95rem;
}

.tab-navigation {
  display: flex;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  margin-bottom: 20px;
  overflow: hidden;
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #6b7280;
  border-right: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.tab-btn:last-child {
  border-right: none;
}

.tab-btn.active {
  background: #3b82f6;
  color: white;
}

.tab-btn:hover:not(.active) {
  background: #f9fafb;
  color: #374151;
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
}

.tab-btn.active .tab-count {
  background: rgba(255, 255, 255, 0.3);
}

.content-section {
  flex: 1;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
}

.section-header h3 {
  color: #374151;
  font-size: 1.2rem;
}

.search-box {
  display: flex;
  gap: 10px;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9rem;
  width: 200px;
}

.table-container {
  overflow: auto;
  max-height: 500px;
}

.structure-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.structure-table th {
  background: #f3f4f6;
  padding: 12px 8px;
  text-align: left;
  border-bottom: 2px solid #e2e8f0;
  font-weight: 600;
  color: #374151;
  position: sticky;
  top: 0;
  z-index: 10;
}

.structure-table td {
  padding: 10px 8px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: top;
}

.column-row:hover {
  background: #f8fafc;
}

.column-row.primary-key {
  background: rgba(16, 185, 129, 0.05);
  border-left: 3px solid #10b981;
}

.column-row.unique-key {
  background: rgba(59, 130, 246, 0.05);
  border-left: 3px solid #3b82f6;
}

.column-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name-text {
  font-weight: 500;
  color: #374151;
}

.key-badge {
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
}

.key-badge.primary {
  background: #d1fae5;
  color: #065f46;
}

.key-badge.unique {
  background: #dbeafe;
  color: #1e40af;
}

.key-badge.index {
  background: #f3e8ff;
  color: #6b21a8;
}

.column-type {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  color: #6366f1;
  font-weight: 500;
}

.default-value {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f3f4f6;
  padding: 2px 4px;
  border-radius: 3px;
  font-size: 0.8rem;
}

.null-value {
  color: #9ca3af;
  font-style: italic;
}

.not-null {
  color: #dc2626;
  font-weight: 500;
}

.nullable {
  color: #16a34a;
}

.column-actions {
  display: flex;
  gap: 5px;
}

.action-icon {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  font-size: 0.9rem;
}

.action-icon:hover {
  background: #f3f4f6;
}

.indexes-section, .foreign-keys-section {
  padding: 20px;
}

.indexes-list, .foreign-keys-list {
  display: grid;
  gap: 15px;
}

.index-card, .foreign-key-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 15px;
  background: #f8fafc;
}

.index-card.primary-index {
  border-color: #10b981;
  background: rgba(16, 185, 129, 0.05);
}

.index-header, .fk-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.index-header h4, .fk-header h4 {
  color: #374151;
  font-size: 1.1rem;
}

.index-type {
  background: #e5e7eb;
  color: #374151;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.index-details, .fk-details {
  margin-bottom: 10px;
}

.index-details p, .fk-details p {
  margin: 5px 0;
  color: #4b5563;
  font-size: 0.9rem;
}

.drop-index-btn, .drop-fk-btn {
  background: #ef4444;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

.ddl-section {
  padding: 20px;
}

.ddl-actions {
  display: flex;
  gap: 10px;
}

.ddl-container {
  background: #1f2937;
  border-radius: 8px;
  padding: 20px;
  margin-top: 15px;
  max-height: 400px;
  overflow: auto;
}

.ddl-content {
  color: #f9fafb;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 0;
  white-space: pre-wrap;
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
  max-width: 500px;
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

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #374151;
}

.form-input, .form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn, .submit-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.cancel-btn {
  background: #f3f4f6;
  color: #374151;
}

.submit-btn {
  background: #3b82f6;
  color: white;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-left: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .structure-table {
    font-size: 0.8rem;
  }
  
  .structure-table th,
  .structure-table td {
    padding: 8px 6px;
  }
}
</style>