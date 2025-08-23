// 算法与数据结构知识点数据
export const algorithmTopics = [
  {
    id: 'array',
    name: '数组与哈希',
    icon: '📊',
    description: '基础数据结构，掌握数组操作、哈希表应用',
    tags: ['双指针', '滑动窗口', '前缀和'],
    difficulty: 'easy',
    status: 'in_progress',
    totalProblems: 25,
    completedProblems: 3,
    estimatedHours: 8,
    progress: 12,
    keyPoints: [
      '双指针技巧',
      '滑动窗口模式',
      '前缀和应用',
      '哈希表优化'
    ]
  },
  {
    id: 'linkedlist',
    name: '链表',
    icon: '🔗',
    description: '动态数据结构，掌握链表操作、指针技巧',
    tags: ['快慢指针', '反转', '合并'],
    difficulty: 'medium',
    status: 'completed',
    totalProblems: 20,
    completedProblems: 1,
    estimatedHours: 6,
    progress: 5,
    keyPoints: [
      '快慢指针技巧',
      '链表反转',
      '链表合并',
      '环检测算法'
    ]
  },
  {
    id: 'stack-queue',
    name: '栈与队列',
    icon: '📚',
    description: '线性数据结构，掌握栈队列的应用场景',
    tags: ['单调栈', '优先队列', 'BFS'],
    difficulty: 'medium',
    status: 'pending',
    totalProblems: 18,
    completedProblems: 0,
    estimatedHours: 5,
    progress: 0,
    keyPoints: [
      '单调栈应用',
      '优先队列',
      '括号匹配',
      'BFS遍历'
    ]
  },
  {
    id: 'tree',
    name: '二叉树',
    icon: '🌳',
    description: '树形数据结构，掌握二叉树遍历与操作',
    tags: ['DFS', 'BFS', '递归'],
    difficulty: 'medium',
    status: 'pending',
    totalProblems: 30,
    completedProblems: 0,
    estimatedHours: 10,
    progress: 0,
    keyPoints: [
      '前中后序遍历',
      '层序遍历',
      '递归与迭代',
      '二叉搜索树'
    ]
  },
  {
    id: 'graph',
    name: '图论算法',
    icon: '🕸️',
    description: '图数据结构，掌握图的遍历与最短路算法',
    tags: ['DFS', 'BFS', '最短路'],
    difficulty: 'hard',
    status: 'pending',
    totalProblems: 22,
    completedProblems: 0,
    estimatedHours: 12,
    progress: 0,
    keyPoints: [
      '图的遍历',
      '拓扑排序',
      '最短路算法',
      '并查集'
    ]
  },
  {
    id: 'dp',
    name: '动态规划',
    icon: '🧮',
    description: '算法设计技巧，掌握状态转移与优化',
    tags: ['状态转移', '背包', '区间DP'],
    difficulty: 'hard',
    status: 'pending',
    totalProblems: 35,
    completedProblems: 0,
    estimatedHours: 15,
    progress: 0,
    keyPoints: [
      '状态定义',
      '转移方程',
      '背包问题',
      '区间DP'
    ]
  }
]

// 学习路径推荐
export const studyPaths = [
  {
    id: 'beginner',
    name: '新手入门路径',
    description: '适合算法初学者，从基础开始逐步提升',
    topics: ['array', 'linkedlist', 'stack-queue'],
    estimatedWeeks: 4,
    difficulty: 'easy'
  },
  {
    id: 'interview',
    name: '面试准备路径',
    description: '针对技术面试，覆盖高频考点',
    topics: ['array', 'linkedlist', 'tree', 'dp'],
    estimatedWeeks: 6,
    difficulty: 'medium'
  },
  {
    id: 'advanced',
    name: '进阶提升路径',
    description: '适合有基础的同学，挑战困难算法',
    topics: ['tree', 'graph', 'dp'],
    estimatedWeeks: 8,
    difficulty: 'hard'
  }
]

// 最近学习记录
export const recentStudies = [
  {
    id: 1,
    topic: '数组与哈希',
    problemTitle: '两数之和',
    date: '今天',
    duration: 45,
    status: 'completed',
    notes: '掌握了哈希表优化的思路'
  },
  {
    id: 2,
    topic: '数组与哈希',
    problemTitle: '三数之和',
    date: '昨天',
    duration: 60,
    status: 'in_progress',
    notes: '双指针解法还需要练习'
  },
  {
    id: 3,
    topic: '链表',
    problemTitle: '反转链表',
    date: '前天',
    duration: 30,
    status: 'completed',
    notes: '递归和迭代两种方法都掌握了'
  }
]