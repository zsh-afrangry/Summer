/**
 * 数据库服务 - 处理与后端API的数据库交互
 */

// 基础API URL
const BASE_URL = '/api/database'

class DatabaseService {
  constructor() {
    this.baseURL = BASE_URL
  }

  /**
   * 发送HTTP请求的通用方法
   */
  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`
    const defaultOptions = {
      headers: {
        'Content-Type': 'application/json',
      },
    }

    const config = { ...defaultOptions, ...options }

    try {
      const response = await fetch(url, config)
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: 'Network error' }))
        throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`)
      }

      const contentType = response.headers.get('content-type')
      if (contentType && contentType.includes('application/json')) {
        return await response.json()
      } else {
        return await response.text()
      }
    } catch (error) {
      console.error('Database service request failed:', error)
      throw error
    }
  }

  /**
   * 测试数据库连接
   */
  async testConnection() {
    try {
      const result = await this.request('/connection/test')
      return {
        success: true,
        message: '连接成功',
        data: result
      }
    } catch (error) {
      return {
        success: false,
        message: error.message
      }
    }
  }

  /**
   * 获取所有表列表
   */
  async getTables() {
    try {
      const tables = await this.request('/tables')
      
      // 模拟数据，因为后端API可能还未实现
      if (!tables || tables.length === 0) {
        return this.getMockTables()
      }
      
      return tables
    } catch (error) {
      console.warn('获取表列表失败，使用模拟数据:', error)
      return this.getMockTables()
    }
  }

  /**
   * 获取表的详细信息
   */
  async getTableInfo(tableName) {
    try {
      return await this.request(`/tables/${tableName}/info`)
    } catch (error) {
      console.warn('获取表信息失败，使用模拟数据:', error)
      return this.getMockTableInfo(tableName)
    }
  }

  /**
   * 获取表的列信息
   */
  async getTableColumns(tableName) {
    try {
      return await this.request(`/tables/${tableName}/columns`)
    } catch (error) {
      console.warn('获取表列信息失败，使用模拟数据:', error)
      return this.getMockTableColumns(tableName)
    }
  }

  /**
   * 获取表数据
   */
  async getTableData(tableName, options = {}) {
    try {
      const params = new URLSearchParams()
      
      if (options.page) params.append('page', options.page)
      if (options.pageSize) params.append('pageSize', options.pageSize)
      if (options.search) params.append('search', options.search)
      if (options.filterColumn) params.append('filterColumn', options.filterColumn)
      if (options.filterValue) params.append('filterValue', options.filterValue)
      if (options.sortColumn) params.append('sortColumn', options.sortColumn)
      if (options.sortDirection) params.append('sortDirection', options.sortDirection)

      const queryString = params.toString()
      const endpoint = `/tables/${tableName}/data${queryString ? '?' + queryString : ''}`
      
      return await this.request(endpoint)
    } catch (error) {
      console.warn('获取表数据失败，使用模拟数据:', error)
      return this.getMockTableData(tableName, options)
    }
  }

  /**
   * 获取表索引信息
   */
  async getTableIndexes(tableName) {
    try {
      return await this.request(`/tables/${tableName}/indexes`)
    } catch (error) {
      console.warn('获取表索引失败，使用模拟数据:', error)
      return this.getMockTableIndexes(tableName)
    }
  }

  /**
   * 获取表外键信息
   */
  async getTableForeignKeys(tableName) {
    try {
      return await this.request(`/tables/${tableName}/foreign-keys`)
    } catch (error) {
      console.warn('获取表外键失败，使用模拟数据:', error)
      return this.getMockTableForeignKeys(tableName)
    }
  }

  /**
   * 显示创建表的DDL语句
   */
  async showCreateTable(tableName) {
    try {
      const result = await this.request(`/tables/${tableName}/ddl`)
      return result.ddl || result
    } catch (error) {
      console.warn('获取DDL失败，使用模拟数据:', error)
      return this.getMockCreateTableDDL(tableName)
    }
  }

  /**
   * 执行SQL查询
   */
  async executeQuery(sql) {
    try {
      return await this.request('/query', {
        method: 'POST',
        body: JSON.stringify({ sql })
      })
    } catch (error) {
      console.warn('执行查询失败，返回错误信息:', error)
      throw error
    }
  }

  /**
   * 插入记录
   */
  async insertRecord(tableName, data) {
    return await this.request(`/tables/${tableName}/records`, {
      method: 'POST',
      body: JSON.stringify(data)
    })
  }

  /**
   * 更新记录
   */
  async updateRecord(tableName, whereCondition, data) {
    return await this.request(`/tables/${tableName}/records`, {
      method: 'PUT',
      body: JSON.stringify({ where: whereCondition, data })
    })
  }

  /**
   * 删除记录
   */
  async deleteRecord(tableName, whereCondition) {
    return await this.request(`/tables/${tableName}/records`, {
      method: 'DELETE',
      body: JSON.stringify({ where: whereCondition })
    })
  }

  /**
   * 添加列
   */
  async addColumn(tableName, columnDef) {
    return await this.request(`/tables/${tableName}/columns`, {
      method: 'POST',
      body: JSON.stringify(columnDef)
    })
  }

  /**
   * 修改列
   */
  async alterColumn(tableName, columnName, columnDef) {
    return await this.request(`/tables/${tableName}/columns/${columnName}`, {
      method: 'PUT',
      body: JSON.stringify(columnDef)
    })
  }

  /**
   * 删除列
   */
  async dropColumn(tableName, columnName) {
    return await this.request(`/tables/${tableName}/columns/${columnName}`, {
      method: 'DELETE'
    })
  }

  /**
   * 删除索引
   */
  async dropIndex(tableName, indexName) {
    return await this.request(`/tables/${tableName}/indexes/${indexName}`, {
      method: 'DELETE'
    })
  }

  /**
   * 删除外键
   */
  async dropForeignKey(tableName, foreignKeyName) {
    return await this.request(`/tables/${tableName}/foreign-keys/${foreignKeyName}`, {
      method: 'DELETE'
    })
  }

  // ===================== 模拟数据方法 =====================

  /**
   * 获取模拟表列表
   */
  getMockTables() {
    return [
      {
        name: 'users',
        rows: 150,
        engine: 'InnoDB',
        collation: 'utf8mb4_unicode_ci'
      },
      {
        name: 'posts',
        rows: 89,
        engine: 'InnoDB',
        collation: 'utf8mb4_unicode_ci'
      },
      {
        name: 'categories',
        rows: 25,
        engine: 'InnoDB',
        collation: 'utf8mb4_unicode_ci'
      },
      {
        name: 'comments',
        rows: 302,
        engine: 'InnoDB',
        collation: 'utf8mb4_unicode_ci'
      }
    ]
  }

  /**
   * 获取模拟表信息
   */
  getMockTableInfo(tableName) {
    const mockInfo = {
      users: {
        engine: 'InnoDB',
        collation: 'utf8mb4_unicode_ci',
        dataLength: 16384,
        indexLength: 8192,
        avgRowLength: 120,
        createTime: '2024-01-15T10:30:00Z'
      },
      posts: {
        engine: 'InnoDB',
        collation: 'utf8mb4_unicode_ci',
        dataLength: 32768,
        indexLength: 16384,
        avgRowLength: 250,
        createTime: '2024-01-16T09:15:00Z'
      }
    }
    
    return mockInfo[tableName] || mockInfo.users
  }

  /**
   * 获取模拟表列信息
   */
  getMockTableColumns(tableName) {
    const mockColumns = {
      users: [
        {
          name: 'id',
          type: 'bigint(20)',
          nullable: false,
          key: 'PRI',
          default: null,
          extra: 'auto_increment',
          comment: '用户ID'
        },
        {
          name: 'username',
          type: 'varchar(50)',
          nullable: false,
          key: 'UNI',
          default: null,
          extra: '',
          comment: '用户名'
        },
        {
          name: 'email',
          type: 'varchar(100)',
          nullable: false,
          key: 'UNI',
          default: null,
          extra: '',
          comment: '邮箱'
        },
        {
          name: 'password',
          type: 'varchar(255)',
          nullable: false,
          key: '',
          default: null,
          extra: '',
          comment: '密码'
        },
        {
          name: 'created_at',
          type: 'timestamp',
          nullable: false,
          key: '',
          default: 'CURRENT_TIMESTAMP',
          extra: '',
          comment: '创建时间'
        },
        {
          name: 'updated_at',
          type: 'timestamp',
          nullable: false,
          key: '',
          default: 'CURRENT_TIMESTAMP',
          extra: 'on update CURRENT_TIMESTAMP',
          comment: '更新时间'
        }
      ],
      posts: [
        {
          name: 'id',
          type: 'bigint(20)',
          nullable: false,
          key: 'PRI',
          default: null,
          extra: 'auto_increment',
          comment: '文章ID'
        },
        {
          name: 'title',
          type: 'varchar(200)',
          nullable: false,
          key: '',
          default: null,
          extra: '',
          comment: '文章标题'
        },
        {
          name: 'content',
          type: 'text',
          nullable: true,
          key: '',
          default: null,
          extra: '',
          comment: '文章内容'
        },
        {
          name: 'user_id',
          type: 'bigint(20)',
          nullable: false,
          key: 'MUL',
          default: null,
          extra: '',
          comment: '作者ID'
        },
        {
          name: 'status',
          type: 'enum(\'draft\',\'published\')',
          nullable: false,
          key: '',
          default: 'draft',
          extra: '',
          comment: '状态'
        },
        {
          name: 'created_at',
          type: 'timestamp',
          nullable: false,
          key: '',
          default: 'CURRENT_TIMESTAMP',
          extra: '',
          comment: '创建时间'
        }
      ]
    }
    
    return mockColumns[tableName] || mockColumns.users
  }

  /**
   * 获取模拟表数据
   */
  getMockTableData(tableName, options = {}) {
    const { page = 1, pageSize = 25 } = options
    
    const mockData = {
      users: Array.from({ length: 150 }, (_, i) => ({
        id: i + 1,
        username: `user${i + 1}`,
        email: `user${i + 1}@example.com`,
        password: `password_hash_${i + 1}`,
        created_at: new Date(Date.now() - Math.random() * 10000000000).toISOString(),
        updated_at: new Date(Date.now() - Math.random() * 1000000000).toISOString()
      })),
      posts: Array.from({ length: 89 }, (_, i) => ({
        id: i + 1,
        title: `文章标题 ${i + 1}`,
        content: `这是文章 ${i + 1} 的内容...`,
        user_id: Math.floor(Math.random() * 150) + 1,
        status: Math.random() > 0.5 ? 'published' : 'draft',
        created_at: new Date(Date.now() - Math.random() * 10000000000).toISOString()
      }))
    }
    
    const tableData = mockData[tableName] || mockData.users
    const startIndex = (page - 1) * pageSize
    const endIndex = startIndex + pageSize
    
    return {
      data: tableData.slice(startIndex, endIndex),
      total: tableData.length,
      page,
      pageSize
    }
  }

  /**
   * 获取模拟表索引
   */
  getMockTableIndexes(tableName) {
    const mockIndexes = {
      users: [
        {
          keyName: 'PRIMARY',
          columns: ['id'],
          nonUnique: false,
          indexType: 'BTREE',
          cardinality: 150
        },
        {
          keyName: 'username_unique',
          columns: ['username'],
          nonUnique: false,
          indexType: 'BTREE',
          cardinality: 150
        },
        {
          keyName: 'email_unique',
          columns: ['email'],
          nonUnique: false,
          indexType: 'BTREE',
          cardinality: 150
        }
      ],
      posts: [
        {
          keyName: 'PRIMARY',
          columns: ['id'],
          nonUnique: false,
          indexType: 'BTREE',
          cardinality: 89
        },
        {
          keyName: 'user_id_index',
          columns: ['user_id'],
          nonUnique: true,
          indexType: 'BTREE',
          cardinality: 50
        }
      ]
    }
    
    return mockIndexes[tableName] || []
  }

  /**
   * 获取模拟表外键
   */
  getMockTableForeignKeys(tableName) {
    const mockForeignKeys = {
      posts: [
        {
          name: 'posts_user_id_foreign',
          columnName: 'user_id',
          referencedTableName: 'users',
          referencedColumnName: 'id',
          deleteRule: 'CASCADE',
          updateRule: 'CASCADE'
        }
      ]
    }
    
    return mockForeignKeys[tableName] || []
  }

  /**
   * 获取模拟DDL语句
   */
  getMockCreateTableDDL(tableName) {
    const mockDDL = {
      users: `CREATE TABLE \`users\` (
  \`id\` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  \`username\` varchar(50) NOT NULL COMMENT '用户名',
  \`email\` varchar(100) NOT NULL COMMENT '邮箱',
  \`password\` varchar(255) NOT NULL COMMENT '密码',
  \`created_at\` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  \`updated_at\` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (\`id\`),
  UNIQUE KEY \`username_unique\` (\`username\`),
  UNIQUE KEY \`email_unique\` (\`email\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';`,
      
      posts: `CREATE TABLE \`posts\` (
  \`id\` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  \`title\` varchar(200) NOT NULL COMMENT '文章标题',
  \`content\` text COMMENT '文章内容',
  \`user_id\` bigint(20) NOT NULL COMMENT '作者ID',
  \`status\` enum('draft','published') NOT NULL DEFAULT 'draft' COMMENT '状态',
  \`created_at\` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (\`id\`),
  KEY \`user_id_index\` (\`user_id\`),
  CONSTRAINT \`posts_user_id_foreign\` FOREIGN KEY (\`user_id\`) REFERENCES \`users\` (\`id\`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';`
    }
    
    return mockDDL[tableName] || mockDDL.users
  }
}

// 创建并导出服务实例
export const dbService = new DatabaseService()
export default dbService