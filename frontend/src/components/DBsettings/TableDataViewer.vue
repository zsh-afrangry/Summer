<template>
  <div class="table-data-viewer">
    <div class="viewer-header">
      <h2>{{ table.name }} - 数据浏览</h2>
      <div class="header-actions">
        <button @click="addNewRecord" class="action-btn add">新增记录</button>
        <button @click="refreshData" class="action-btn refresh">刷新</button>
        <select v-model="pageSize" @change="loadData" class="page-size-select">
          <option value="10">10条/页</option>
          <option value="25">25条/页</option>
          <option value="50">50条/页</option>
          <option value="100">100条/页</option>
        </select>
      </div>
    </div>

    <!-- Search and Filter -->
    <div class="search-section">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          @input="debounceSearch"
          placeholder="搜索数据..."
          class="search-input"
        >
        <button @click="clearSearch" class="clear-btn">清除</button>
      </div>
      
      <div class="filter-section">
        <select v-model="filterColumn" class="filter-select">
          <option value="">选择筛选列...</option>
          <option v-for="column in columns" :key="column.name" :value="column.name">
            {{ column.name }} ({{ column.type }})
          </option>
        </select>
        
        <input 
          v-if="filterColumn" 
          v-model="filterValue"
          @input="applyFilter"
          :placeholder="`筛选 ${filterColumn}...`"
          class="filter-input"
        >
      </div>
    </div>

    <!-- Data Table -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th class="row-number">#</th>
            <th v-for="column in columns" :key="column.name" class="column-header">
              <div class="column-info">
                <span class="column-name">{{ column.name }}</span>
                <span class="column-type">{{ column.type }}</span>
                <div class="column-actions">
                  <button @click="sortBy(column.name)" class="sort-btn">
                    <span v-if="sortColumn === column.name">
                      {{ sortDirection === 'ASC' ? '↑' : '↓' }}
                    </span>
                    <span v-else>⇅</span>
                  </button>
                </div>
              </div>
            </th>
            <th class="actions-column">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in displayData" :key="index" class="data-row">
            <td class="row-number">{{ (currentPage - 1) * pageSize + index + 1 }}</td>
            <td v-for="column in columns" :key="column.name" class="data-cell">
              <div 
                v-if="editingRow === index && editingColumn === column.name"
                class="edit-cell"
              >
                <input 
                  v-if="column.type.includes('varchar') || column.type.includes('text')"
                  v-model="editValue"
                  @keyup.enter="saveEdit"
                  @keyup.escape="cancelEdit"
                  class="edit-input"
                  :type="getInputType(column.type)"
                >
                <input 
                  v-else-if="column.type.includes('int') || column.type.includes('decimal')"
                  v-model="editValue"
                  @keyup.enter="saveEdit"
                  @keyup.escape="cancelEdit"
                  class="edit-input"
                  type="number"
                >
                <input 
                  v-else-if="column.type.includes('date')"
                  v-model="editValue"
                  @keyup.enter="saveEdit"
                  @keyup.escape="cancelEdit"
                  class="edit-input"
                  type="datetime-local"
                >
                <textarea 
                  v-else
                  v-model="editValue"
                  @keyup.enter="saveEdit"
                  @keyup.escape="cancelEdit"
                  class="edit-textarea"
                ></textarea>
                <div class="edit-actions">
                  <button @click="saveEdit" class="save-btn">✓</button>
                  <button @click="cancelEdit" class="cancel-btn">✗</button>
                </div>
              </div>
              <div 
                v-else 
                @dblclick="startEdit(index, column.name, row[column.name])"
                class="cell-content"
                :class="{ 'null-value': row[column.name] === null }"
              >
                {{ formatCellValue(row[column.name], column.type) }}
              </div>
            </td>
            <td class="actions-column">
              <button @click="editRow(index)" class="row-action edit-action">编辑</button>
              <button @click="deleteRow(index, row)" class="row-action delete-action">删除</button>
              <button @click="duplicateRow(row)" class="row-action duplicate-action">复制</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <div class="pagination-info">
        显示 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, totalRecords) }} 
        共 {{ totalRecords }} 条记录
      </div>
      <div class="pagination-controls">
        <button 
          @click="goToPage(1)" 
          :disabled="currentPage === 1"
          class="page-btn"
        >首页</button>
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 1"
          class="page-btn"
        >上一页</button>
        
        <span class="page-numbers">
          <button 
            v-for="page in visiblePages" 
            :key="page"
            @click="goToPage(page)"
            :class="{ active: page === currentPage }"
            class="page-number"
          >{{ page }}</button>
        </span>
        
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage === totalPages"
          class="page-btn"
        >下一页</button>
        <button 
          @click="goToPage(totalPages)" 
          :disabled="currentPage === totalPages"
          class="page-btn"
        >末页</button>
      </div>
    </div>

    <!-- Add/Edit Record Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ modalMode === 'add' ? '新增记录' : '编辑记录' }}</h3>
          <button @click="closeModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div v-for="column in columns" :key="column.name" class="form-group">
            <label :for="column.name" class="form-label">
              {{ column.name }}
              <span class="column-info">({{ column.type }})</span>
              <span v-if="!column.nullable" class="required">*</span>
            </label>
            <input 
              v-if="column.type.includes('varchar') || column.type.includes('char')"
              v-model="formData[column.name]"
              :id="column.name"
              :required="!column.nullable"
              class="form-input"
              type="text"
            >
            <input 
              v-else-if="column.type.includes('int')"
              v-model="formData[column.name]"
              :id="column.name"
              :required="!column.nullable"
              class="form-input"
              type="number"
            >
            <input 
              v-else-if="column.type.includes('decimal') || column.type.includes('float')"
              v-model="formData[column.name]"
              :id="column.name"
              :required="!column.nullable"
              class="form-input"
              type="number"
              step="0.01"
            >
            <input 
              v-else-if="column.type.includes('date')"
              v-model="formData[column.name]"
              :id="column.name"
              :required="!column.nullable"
              class="form-input"
              type="datetime-local"
            >
            <textarea 
              v-else
              v-model="formData[column.name]"
              :id="column.name"
              :required="!column.nullable"
              class="form-textarea"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeModal" class="cancel-button">取消</button>
          <button @click="saveRecord" class="save-button">保存</button>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import { dbService } from './services/dbService.js'

export default {
  name: 'TableDataViewer',
  props: {
    table: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      loading: false,
      columns: [],
      data: [],
      displayData: [],
      currentPage: 1,
      pageSize: 25,
      totalRecords: 0,
      searchQuery: '',
      filterColumn: '',
      filterValue: '',
      sortColumn: '',
      sortDirection: 'ASC',
      
      // 编辑相关
      editingRow: -1,
      editingColumn: '',
      editValue: '',
      originalValue: '',
      
      // 模态框相关
      showModal: false,
      modalMode: 'add', // 'add' or 'edit'
      formData: {},
      editingRecordId: null,
      
      searchTimeout: null
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalRecords / this.pageSize)
    },
    visiblePages() {
      const pages = []
      const start = Math.max(1, this.currentPage - 2)
      const end = Math.min(this.totalPages, start + 4)
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    }
  },
  async mounted() {
    await this.loadTableStructure()
    await this.loadData()
  },
  methods: {
    async loadTableStructure() {
      try {
        this.columns = await dbService.getTableColumns(this.table.name)
      } catch (error) {
        console.error('加载表结构失败:', error)
        this.$message?.error('加载表结构失败')
      }
    },

    async loadData() {
      this.loading = true
      try {
        const result = await dbService.getTableData(this.table.name, {
          page: this.currentPage,
          pageSize: this.pageSize,
          search: this.searchQuery,
          filterColumn: this.filterColumn,
          filterValue: this.filterValue,
          sortColumn: this.sortColumn,
          sortDirection: this.sortDirection
        })
        
        this.data = result.data
        this.displayData = result.data
        this.totalRecords = result.total
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message?.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },

    async refreshData() {
      await this.loadData()
      this.$emit('refresh')
    },

    debounceSearch() {
      clearTimeout(this.searchTimeout)
      this.searchTimeout = setTimeout(() => {
        this.currentPage = 1
        this.loadData()
      }, 500)
    },

    clearSearch() {
      this.searchQuery = ''
      this.currentPage = 1
      this.loadData()
    },

    applyFilter() {
      this.currentPage = 1
      this.loadData()
    },

    sortBy(column) {
      if (this.sortColumn === column) {
        this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC'
      } else {
        this.sortColumn = column
        this.sortDirection = 'ASC'
      }
      this.loadData()
    },

    goToPage(page) {
      this.currentPage = page
      this.loadData()
    },

    formatCellValue(value, type) {
      if (value === null) return 'NULL'
      if (value === undefined) return ''
      
      if (type.includes('date') || type.includes('time')) {
        return new Date(value).toLocaleString('zh-CN')
      }
      
      if (typeof value === 'string' && value.length > 100) {
        return value.substring(0, 100) + '...'
      }
      
      return value
    },

    getInputType(columnType) {
      if (columnType.includes('email')) return 'email'
      if (columnType.includes('url')) return 'url'
      if (columnType.includes('password')) return 'password'
      return 'text'
    },

    // 编辑相关方法
    startEdit(rowIndex, columnName, value) {
      this.editingRow = rowIndex
      this.editingColumn = columnName
      this.editValue = value
      this.originalValue = value
    },

    async saveEdit() {
      try {
        const rowData = this.displayData[this.editingRow]
        const primaryKey = this.getPrimaryKey(rowData)
        
        await dbService.updateRecord(this.table.name, primaryKey, {
          [this.editingColumn]: this.editValue
        })
        
        // 更新本地数据
        this.displayData[this.editingRow][this.editingColumn] = this.editValue
        
        this.cancelEdit()
        this.$message?.success('更新成功')
      } catch (error) {
        console.error('更新失败:', error)
        this.$message?.error('更新失败')
      }
    },

    cancelEdit() {
      this.editingRow = -1
      this.editingColumn = ''
      this.editValue = ''
      this.originalValue = ''
    },

    addNewRecord() {
      this.modalMode = 'add'
      this.formData = {}
      this.columns.forEach(column => {
        this.formData[column.name] = column.default || ''
      })
      this.showModal = true
    },

    editRow(index) {
      this.modalMode = 'edit'
      this.editingRecordId = index
      this.formData = { ...this.displayData[index] }
      this.showModal = true
    },

    async deleteRow(index, row) {
      if (!confirm('确定要删除这条记录吗？')) return
      
      try {
        const primaryKey = this.getPrimaryKey(row)
        await dbService.deleteRecord(this.table.name, primaryKey)
        
        this.displayData.splice(index, 1)
        this.totalRecords--
        
        this.$message?.success('删除成功')
      } catch (error) {
        console.error('删除失败:', error)
        this.$message?.error('删除失败')
      }
    },

    duplicateRow(row) {
      this.modalMode = 'add'
      this.formData = { ...row }
      
      // 清除主键字段
      const primaryKeyColumn = this.columns.find(col => col.key === 'PRI')
      if (primaryKeyColumn) {
        this.formData[primaryKeyColumn.name] = ''
      }
      
      this.showModal = true
    },

    async saveRecord() {
      try {
        if (this.modalMode === 'add') {
          await dbService.insertRecord(this.table.name, this.formData)
          this.$message?.success('新增成功')
        } else {
          const rowData = this.displayData[this.editingRecordId]
          const primaryKey = this.getPrimaryKey(rowData)
          await dbService.updateRecord(this.table.name, primaryKey, this.formData)
          this.$message?.success('更新成功')
        }
        
        this.closeModal()
        await this.refreshData()
      } catch (error) {
        console.error('保存失败:', error)
        this.$message?.error('保存失败')
      }
    },

    closeModal() {
      this.showModal = false
      this.formData = {}
      this.editingRecordId = null
    },

    getPrimaryKey(row) {
      const primaryKeyColumn = this.columns.find(col => col.key === 'PRI')
      if (primaryKeyColumn) {
        return { [primaryKeyColumn.name]: row[primaryKeyColumn.name] }
      }
      
      // 如果没有主键，使用所有字段作为条件
      return row
    }
  }
}
</script>

<style scoped>
.table-data-viewer {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.viewer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e2e8f0;
}

.viewer-header h2 {
  color: #2d3748;
  font-size: 1.5rem;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.action-btn.add {
  background: #48bb78;
  color: white;
}

.action-btn.refresh {
  background: #4299e1;
  color: white;
}

.page-size-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: white;
}

.search-section {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  align-items: center;
}

.search-box {
  display: flex;
  gap: 10px;
  flex: 1;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.95rem;
}

.clear-btn {
  padding: 10px 15px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.filter-section {
  display: flex;
  gap: 10px;
}

.filter-select, .filter-input {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
}

.table-container {
  flex: 1;
  overflow: auto;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  margin-bottom: 20px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.column-header {
  background: #f7fafc;
  padding: 12px 8px;
  border-bottom: 2px solid #e2e8f0;
  text-align: left;
  position: sticky;
  top: 0;
  z-index: 10;
}

.column-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.column-name {
  font-weight: 600;
  color: #2d3748;
}

.column-type {
  font-size: 0.75rem;
  color: #718096;
}

.sort-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px 4px;
  border-radius: 3px;
  font-size: 0.8rem;
}

.sort-btn:hover {
  background: #e2e8f0;
}

.row-number {
  background: #f7fafc;
  padding: 8px;
  text-align: center;
  font-weight: 500;
  color: #718096;
  width: 60px;
}

.data-row:nth-child(even) {
  background: #f9fafb;
}

.data-row:hover {
  background: #edf2f7;
}

.data-cell {
  padding: 8px;
  border-bottom: 1px solid #e2e8f0;
  vertical-align: top;
}

.cell-content {
  min-height: 20px;
  cursor: text;
  padding: 4px;
  border-radius: 3px;
}

.cell-content:hover {
  background: #e2e8f0;
}

.null-value {
  color: #a0aec0;
  font-style: italic;
}

.edit-cell {
  display: flex;
  gap: 5px;
  align-items: flex-start;
}

.edit-input, .edit-textarea {
  flex: 1;
  padding: 4px 6px;
  border: 1px solid #4299e1;
  border-radius: 3px;
  font-size: 0.9rem;
}

.edit-textarea {
  resize: vertical;
  min-height: 60px;
}

.edit-actions {
  display: flex;
  gap: 2px;
}

.save-btn, .cancel-btn {
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.save-btn {
  background: #48bb78;
  color: white;
}

.cancel-btn {
  background: #ef4444;
  color: white;
}

.actions-column {
  padding: 8px;
  text-align: center;
  width: 200px;
}

.row-action {
  padding: 4px 8px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  font-size: 0.8rem;
  margin: 0 2px;
}

.edit-action {
  background: #4299e1;
  color: white;
}

.delete-action {
  background: #ef4444;
  color: white;
}

.duplicate-action {
  background: #9f7aea;
  color: white;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-top: 1px solid #e2e8f0;
}

.pagination-info {
  color: #718096;
  font-size: 0.9rem;
}

.pagination-controls {
  display: flex;
  gap: 5px;
  align-items: center;
}

.page-btn, .page-number {
  padding: 6px 12px;
  border: 1px solid #d1d5db;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 0.9rem;
}

.page-btn:hover:not(:disabled), .page-number:hover {
  background: #f3f4f6;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-number.active {
  background: #4299e1;
  color: white;
  border-color: #4299e1;
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
  max-width: 600px;
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
  max-height: 400px;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #374151;
}

.column-info {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: normal;
}

.required {
  color: #ef4444;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.form-textarea {
  min-height: 80px;
  resize: vertical;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #e2e8f0;
}

.cancel-button, .save-button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.cancel-button {
  background: #f3f4f6;
  color: #374151;
}

.save-button {
  background: #4299e1;
  color: white;
}

.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #718096;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e2e8f0;
  border-left: 3px solid #4299e1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>