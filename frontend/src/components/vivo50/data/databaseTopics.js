// 数据库知识点数据
export const databaseTopics = [
  {
    id: 'sql-basics',
    name: 'SQL基础',
    icon: '📝',
    description: '掌握SQL语法、查询语句、数据操作',
    tags: ['SELECT', 'JOIN', 'GROUP BY'],
    difficulty: 'easy',
    status: 'in_progress',
    totalProblems: 20,
    completedProblems: 5,
    estimatedHours: 6,
    progress: 25,
    keyPoints: [
      'SQL语法基础',
      '查询语句',
      '数据插入更新',
      '约束与索引'
    ]
  },
  {
    id: 'index-optimization',
    name: '索引与优化',
    icon: '⚡',
    description: '理解索引原理、查询优化、性能调优',
    tags: ['B+树', '执行计划', '优化器'],
    difficulty: 'medium',
    status: 'pending',
    totalProblems: 15,
    completedProblems: 0,
    estimatedHours: 8,
    progress: 0,
    keyPoints: [
      'B+树索引',
      '查询执行计划',
      '索引选择策略',
      '性能监控'
    ]
  },
  {
    id: 'transaction-acid',
    name: '事务与ACID',
    icon: '🔒',
    description: '掌握事务特性、隔离级别、并发控制',
    tags: ['ACID', '隔离级别', '锁机制'],
    difficulty: 'medium',
    status: 'pending',
    totalProblems: 18,
    completedProblems: 0,
    estimatedHours: 10,
    progress: 0,
    keyPoints: [
      'ACID特性',
      '事务隔离级别',
      '锁机制',
      '死锁处理'
    ]
  },
  {
    id: 'distributed-db',
    name: '分布式数据库',
    icon: '🌐',
    description: '分布式数据库架构、分片、一致性',
    tags: ['分片', 'CAP定理', '一致性'],
    difficulty: 'hard',
    status: 'pending',
    totalProblems: 12,
    completedProblems: 0,
    estimatedHours: 12,
    progress: 0,
    keyPoints: [
      '分库分表',
      'CAP定理',
      '分布式事务',
      '数据一致性'
    ]
  },
  {
    id: 'nosql',
    name: 'NoSQL数据库',
    icon: '📊',
    description: 'MongoDB、Redis、Elasticsearch等NoSQL',
    tags: ['MongoDB', 'Redis', '文档存储'],
    difficulty: 'medium',
    status: 'pending',
    totalProblems: 16,
    completedProblems: 0,
    estimatedHours: 8,
    progress: 0,
    keyPoints: [
      '文档数据库',
      '键值存储',
      '搜索引擎',
      '缓存策略'
    ]
  },
  {
    id: 'db-design',
    name: '数据库设计',
    icon: '🏗️',
    description: 'ER图、范式理论、数据建模',
    tags: ['ER图', '范式', '建模'],
    difficulty: 'medium',
    status: 'pending',
    totalProblems: 14,
    completedProblems: 0,
    estimatedHours: 7,
    progress: 0,
    keyPoints: [
      'ER图设计',
      '范式理论',
      '数据建模',
      '性能考量'
    ]
  }
]

// 学习路径推荐
export const studyPaths = [
  {
    id: 'beginner',
    name: '数据库入门路径',
    description: '适合数据库初学者，从SQL基础开始',
    topics: ['sql-basics', 'db-design', 'index-optimization'],
    estimatedWeeks: 5,
    difficulty: 'easy'
  },
  {
    id: 'advanced',
    name: '高级数据库路径',
    description: '深入理解事务、分布式、性能优化',
    topics: ['transaction-acid', 'index-optimization', 'distributed-db'],
    estimatedWeeks: 8,
    difficulty: 'hard'
  },
  {
    id: 'nosql-focus',
    name: 'NoSQL专项路径',
    description: '专注NoSQL数据库技术栈',
    topics: ['nosql', 'distributed-db', 'index-optimization'],
    estimatedWeeks: 6,
    difficulty: 'medium'
  }
]

// 最近学习记录
export const recentStudies = [
  {
    id: 1,
    topic: 'SQL基础',
    problemTitle: 'JOIN查询优化',
    date: '今天',
    duration: 40,
    status: 'completed',
    notes: '理解了内连接和外连接的区别'
  },
  {
    id: 2,
    topic: 'SQL基础',
    problemTitle: '复杂查询练习',
    date: '昨天',
    duration: 55,
    status: 'in_progress',
    notes: '子查询和窗口函数需要加强'
  },
  {
    id: 3,
    topic: '索引与优化',
    problemTitle: 'B+树索引原理',
    date: '前天',
    duration: 35,
    status: 'completed',
    notes: '掌握了索引的存储结构'
  }
]