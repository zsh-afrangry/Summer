<template>
  <div class="db-manager">
    <!-- Header -->
    <div class="db-header">
      <h1>数据库管理器</h1>
      <p>管理Docker MySQL容器中的数据库</p>
      <div class="connection-status" :class="{ connected: isConnected }">
        <span v-if="isConnected">✓ 已连接</span>
        <span v-else>✗ 未连接</span>
      </div>
    </div>

    <!-- Database Overview -->
    <div class="db-overview">
      <div class="info-card">
        <h3>数据库信息</h3>
        <p><strong>Host:</strong> {{ dbInfo.host }}</p>
        <p><strong>Port:</strong> {{ dbInfo.port }}</p>
        <p><strong>Database:</strong> {{ dbInfo.database }}</p>
        <p><strong>User:</strong> {{ dbInfo.user }}</p>
      </div>
      
      <div class="stats-card">
        <h3>统计信息</h3>
        <p><strong>表数量:</strong> {{ tables.length }}</p>
        <p><strong>总记录数:</strong> {{ totalRecords }}</p>
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
      </button>
    </div>

    <!-- Content Area -->
    <div class="content-area">
      <!-- Tables List -->
      <div v-if="activeTab === 'tables'" class="tables-section">
        <div class="section-header">
          <h2>数据库表</h2>
          <button @click="refreshTables" class="refresh-btn">刷新</button>
        </div>
        
        <div class="tables-grid">
          <div 
            v-for="table in tables" 
            :key="table.name"
            @click="selectTable(table)"
            class="table-card"
            :class="{ selected: selectedTable?.name === table.name }"
          >
            <h3>{{ table.name }}</h3>
            <p>{{ table.rows }} 条记录</p>
            <div class="table-actions">
              <button @click.stop="viewTableData(table)" class="action-btn view">查看</button>
              <button @click.stop="editTableStructure(table)" class="action-btn edit">结构</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Table Data View -->
      <div v-if="activeTab === 'data'" class="data-section">
        <TableDataViewer 
          v-if="selectedTable"
          :table="selectedTable"
          @refresh="refreshTableData"
        />
        <div v-else class="no-selection">
          <p>请先选择一个表</p>
        </div>
      </div>

      <!-- Query Console -->
      <div v-if="activeTab === 'query'" class="query-section">
        <QueryConsole 
          @query-executed="onQueryExecuted"
        />
      </div>

      <!-- Table Structure -->
      <div v-if="activeTab === 'structure'" class="structure-section">
        <TableStructureViewer 
          v-if="selectedTable"
          :table="selectedTable"
        />
        <div v-else class="no-selection">
          <p>请先选择一个表</p>
        </div>
      </div>
    </div>

    <!-- Loading Overlay -->
    <div v-if="loading" class="loading-overlay">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
import TableDataViewer from './TableDataViewer.vue'
import QueryConsole from './QueryConsole.vue'
import TableStructureViewer from './TableStructureViewer.vue'
import { dbService } from './services/dbService.js'

export default {
  name: 'DatabaseManager',
  components: {
    TableDataViewer,
    QueryConsole,
    TableStructureViewer
  },
  data() {
    return {
      isConnected: false,
      loading: false,
      activeTab: 'tables',
      selectedTable: null,
      tables: [],
      totalRecords: 0,
      dbInfo: {
        host: 'localhost',
        port: '3307',
        database: 'my_blog',
        user: 'root'
      },
      tabs: [
        { id: 'tables', label: '表列表' },
        { id: 'data', label: '数据浏览' },
        { id: 'structure', label: '表结构' },
        { id: 'query', label: 'SQL查询' }
      ]
    }
  },
  async mounted() {
    await this.initConnection()
  },
  methods: {
    async initConnection() {
      this.loading = true
      try {
        const connectionResult = await dbService.testConnection()
        this.isConnected = connectionResult.success
        if (this.isConnected) {
          await this.loadTables()
        }
      } catch (error) {
        console.error('数据库连接失败:', error)
        this.$message?.error('数据库连接失败')
      } finally {
        this.loading = false
      }
    },

    async loadTables() {
      try {
        this.tables = await dbService.getTables()
        this.totalRecords = this.tables.reduce((sum, table) => sum + table.rows, 0)
      } catch (error) {
        console.error('加载表列表失败:', error)
        this.$message?.error('加载表列表失败')
      }
    },

    async refreshTables() {
      await this.loadTables()
    },

    selectTable(table) {
      this.selectedTable = table
    },

    viewTableData(table) {
      this.selectedTable = table
      this.activeTab = 'data'
    },

    editTableStructure(table) {
      this.selectedTable = table
      this.activeTab = 'structure'
    },

    async refreshTableData() {
      if (this.selectedTable) {
        try {
          const updatedTable = await dbService.getTableInfo(this.selectedTable.name)
          this.selectedTable = { ...this.selectedTable, ...updatedTable }
          await this.loadTables() // 更新总览信息
        } catch (error) {
          console.error('刷新表数据失败:', error)
        }
      }
    },

    onQueryExecuted(result) {
      // 查询执行后的回调
      if (result.type === 'DDL' || result.type === 'DML') {
        this.loadTables() // 如果是结构修改或数据修改，刷新表列表
      }
    }
  }
}
</script>

<style scoped>
.db-manager {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.db-header {
  background: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  text-align: center;
  position: relative;
}

.db-header h1 {
  color: #2d3748;
  margin-bottom: 10px;
  font-size: 2.5rem;
}

.db-header p {
  color: #718096;
  font-size: 1.1rem;
}

.connection-status {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: bold;
  font-size: 0.9rem;
  background: #fed7d7;
  color: #c53030;
}

.connection-status.connected {
  background: #c6f6d5;
  color: #25543e;
}

.db-overview {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.info-card, .stats-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.info-card h3, .stats-card h3 {
  color: #2d3748;
  margin-bottom: 15px;
  font-size: 1.3rem;
}

.info-card p, .stats-card p {
  margin: 8px 0;
  color: #4a5568;
}

.tab-navigation {
  display: flex;
  background: white;
  border-radius: 12px;
  padding: 8px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #718096;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

.tab-btn:hover:not(.active) {
  background: #edf2f7;
  color: #4a5568;
}

.content-area {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  min-height: 500px;
  position: relative;
}

.tables-section {
  padding: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-header h2 {
  color: #2d3748;
  font-size: 1.8rem;
}

.refresh-btn {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: transform 0.2s ease;
}

.refresh-btn:hover {
  transform: translateY(-2px);
}

.tables-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.table-card {
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f9fafb;
}

.table-card:hover {
  border-color: #667eea;
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.table-card.selected {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

.table-card h3 {
  color: #2d3748;
  margin-bottom: 10px;
  font-size: 1.3rem;
}

.table-card p {
  color: #718096;
  margin-bottom: 15px;
}

.table-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.action-btn.view {
  background: #3182ce;
  color: white;
}

.action-btn.edit {
  background: #d69e2e;
  color: white;
}

.action-btn:hover {
  transform: scale(1.05);
}

.data-section, .query-section, .structure-section {
  padding: 30px;
}

.no-selection {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
  font-size: 1.1rem;
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
  border-radius: 12px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e2e8f0;
  border-left: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .db-overview {
    grid-template-columns: 1fr;
  }
  
  .tab-navigation {
    flex-wrap: wrap;
  }
  
  .tables-grid {
    grid-template-columns: 1fr;
  }
}
</style>