-- ================================
-- Vivo50 学习记录中心 - 算法模块数据库表设计
-- 基于现有 my_blog 数据库扩展
-- ================================

-- 使用现有数据库
USE `my_blog`;

-- 删除表（按依赖关系逆序删除）
DROP TABLE IF EXISTS `study_records`;
DROP TABLE IF EXISTS `user_topic_progress`;  
DROP TABLE IF EXISTS `algorithm_problems`;
DROP TABLE IF EXISTS `algorithm_topics`;

-- 1. 算法主题表 (algorithm_topics)
-- 存储算法知识点的基本信息
CREATE TABLE `algorithm_topics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `topic_key` varchar(50) NOT NULL COMMENT '主题标识符(如array, linkedlist)',
  `name` varchar(100) NOT NULL COMMENT '主题名称',
  `icon` varchar(10) DEFAULT NULL COMMENT '主题图标(emoji)',
  `description` text COMMENT '主题描述',
  `difficulty` enum('easy','medium','hard') DEFAULT 'easy' COMMENT '难度等级',
  `estimated_hours` int DEFAULT 0 COMMENT '预计学习时长(小时)',
  `total_problems` int DEFAULT 0 COMMENT '题目总数',
  `sort_order` int DEFAULT 0 COMMENT '排序权重',
  `status` tinyint DEFAULT 1 COMMENT '状态:1-启用,0-禁用',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_topic_key` (`topic_key`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='算法主题表';

-- 2. 算法题目表 (algorithm_problems)
-- 存储具体的算法题目信息
CREATE TABLE `algorithm_problems` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `topic_id` bigint NOT NULL COMMENT '所属主题ID',
  `title` varchar(200) NOT NULL COMMENT '题目标题',
  `description` text COMMENT '题目描述',
  `difficulty` enum('easy','medium','hard') DEFAULT 'easy' COMMENT '题目难度',
  `leetcode_id` varchar(20) DEFAULT NULL COMMENT 'LeetCode题号',
  `problem_url` varchar(500) DEFAULT NULL COMMENT '题目链接',
  `tags` json DEFAULT NULL COMMENT '标签数组(如["双指针","滑动窗口"])',
  `solution_code` text COMMENT '参考解答代码',
  `solution_explanation` text COMMENT '解题思路说明',
  `time_complexity` varchar(50) DEFAULT NULL COMMENT '时间复杂度',
  `space_complexity` varchar(50) DEFAULT NULL COMMENT '空间复杂度',
  `sort_order` int DEFAULT 0 COMMENT '在主题内的排序',
  `status` tinyint DEFAULT 1 COMMENT '状态:1-启用,0-禁用',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_leetcode_id` (`leetcode_id`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_problems_topic` FOREIGN KEY (`topic_id`) REFERENCES `algorithm_topics` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='算法题目表';

-- 3. 用户学习进度表 (user_topic_progress)
-- 记录用户对每个主题的学习进度
CREATE TABLE `user_topic_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `topic_id` bigint NOT NULL COMMENT '主题ID',
  `completed_problems` int DEFAULT 0 COMMENT '已完成题目数',
  `total_study_time` int DEFAULT 0 COMMENT '总学习时长(分钟)',
  `progress_percentage` decimal(5,2) DEFAULT 0.00 COMMENT '进度百分比(0-100)',
  `current_section` varchar(50) DEFAULT NULL COMMENT '当前学习章节',
  `last_study_date` date DEFAULT NULL COMMENT '最近学习日期',
  `mastery_level` enum('beginner','intermediate','advanced','expert') DEFAULT 'beginner' COMMENT '掌握程度',
  `notes` text COMMENT '学习笔记',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_topic` (`user_id`, `topic_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_progress` (`progress_percentage`),
  KEY `idx_last_study` (`last_study_date`),
  CONSTRAINT `fk_progress_user` FOREIGN KEY (`user_id`) REFERENCES `my_blog`.`user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_progress_topic` FOREIGN KEY (`topic_id`) REFERENCES `algorithm_topics` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户主题学习进度表';

-- 4. 学习记录表 (study_records)
-- 记录用户的具体学习活动
CREATE TABLE `study_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `topic_id` bigint NOT NULL COMMENT '主题ID',
  `problem_id` bigint DEFAULT NULL COMMENT '题目ID(可选)',
  `study_type` enum('theory','practice','review','note') DEFAULT 'practice' COMMENT '学习类型',
  `title` varchar(200) NOT NULL COMMENT '学习内容标题',
  `description` text COMMENT '学习内容描述',
  `duration_minutes` int DEFAULT 0 COMMENT '学习时长(分钟)',
  `status` enum('in_progress','completed','paused') DEFAULT 'in_progress' COMMENT '学习状态',
  `difficulty_rating` tinyint DEFAULT NULL COMMENT '难度评分(1-5)',
  `understanding_rating` tinyint DEFAULT NULL COMMENT '理解程度评分(1-5)',
  `notes` text COMMENT '学习心得笔记',
  `code_solution` text COMMENT '代码解答',
  `study_date` date NOT NULL COMMENT '学习日期',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_problem_id` (`problem_id`),
  KEY `idx_study_date` (`study_date`),
  KEY `idx_status` (`status`),
  KEY `idx_study_type` (`study_type`),
  CONSTRAINT `fk_records_user` FOREIGN KEY (`user_id`) REFERENCES `my_blog`.`user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_records_topic` FOREIGN KEY (`topic_id`) REFERENCES `algorithm_topics` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_records_problem` FOREIGN KEY (`problem_id`) REFERENCES `algorithm_problems` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习记录表';

-- ================================
-- 初始化数据
-- ================================

-- 插入算法主题基础数据(基于你的 algorithmTopics.js)
INSERT INTO `algorithm_topics` (`topic_key`, `name`, `icon`, `description`, `difficulty`, `estimated_hours`, `total_problems`, `sort_order`) VALUES
('array', '数组与哈希', '📊', '基础数据结构，掌握数组操作、哈希表应用', 'easy', 8, 25, 1),
('linkedlist', '链表', '🔗', '动态数据结构，掌握链表操作、指针技巧', 'medium', 6, 20, 2),
('stack-queue', '栈与队列', '📚', '线性数据结构，掌握栈队列的应用场景', 'medium', 5, 18, 3),
('tree', '二叉树', '🌳', '树形数据结构，掌握二叉树遍历与操作', 'medium', 10, 30, 4),
('graph', '图论算法', '🕸️', '复杂数据结构，掌握图的遍历和最短路径', 'hard', 12, 25, 5),
('dp', '动态规划', '🧮', '高级算法技巧，掌握状态转移和优化问题', 'hard', 15, 35, 6),
('greedy', '贪心算法', '🎯', '算法设计思想，掌握局部最优策略', 'medium', 6, 20, 7),
('backtrack', '回溯算法', '🔄', '暴力搜索优化，掌握剪枝和状态回退', 'hard', 8, 22, 8);

-- 插入一些示例题目数据
INSERT INTO `algorithm_problems` (`topic_id`, `title`, `description`, `difficulty`, `leetcode_id`, `tags`, `time_complexity`, `space_complexity`, `sort_order`) VALUES
(1, '两数之和', '给定一个整数数组和目标值，找出数组中和为目标值的两个数的索引', 'easy', '1', '["哈希表", "数组"]', 'O(n)', 'O(n)', 1),
(1, '三数之和', '给定一个数组，找出所有不重复的三元组，使得三个数的和为0', 'medium', '15', '["数组", "双指针", "排序"]', 'O(n²)', 'O(1)', 2),
(1, '盛水最多的容器', '给定n个非负整数，找出其中的两条线，使得它们与x轴构成的容器能够容纳最多的水', 'medium', '11', '["数组", "双指针"]', 'O(n)', 'O(1)', 3),
(2, '反转链表', '反转一个单链表', 'easy', '206', '["链表", "递归"]', 'O(n)', 'O(1)', 1),
(2, '合并两个有序链表', '将两个升序链表合并为一个新的升序链表并返回', 'easy', '21', '["链表", "递归"]', 'O(m+n)', 'O(1)', 2);

-- 为现有admin用户创建学习进度数据
-- 获取admin用户ID(基于现有数据库中的admin用户)
SET @admin_user_id = (SELECT id FROM user WHERE username = 'admin' LIMIT 1);

-- 插入用户学习进度数据
INSERT INTO `user_topic_progress` (`user_id`, `topic_id`, `completed_problems`, `total_study_time`, `progress_percentage`, `current_section`, `last_study_date`, `mastery_level`, `notes`) VALUES
(@admin_user_id, 1, 3, 180, 12.00, 'array-basic', CURDATE(), 'beginner', '已完成基础数组题目，开始学习哈希表应用'),
(@admin_user_id, 2, 1, 60, 5.00, 'linkedlist-basic', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'beginner', '开始学习链表基础操作');

-- 插入学习记录数据
INSERT INTO `study_records` (`user_id`, `topic_id`, `problem_id`, `study_type`, `title`, `description`, `duration_minutes`, `status`, `difficulty_rating`, `understanding_rating`, `notes`, `study_date`) VALUES
(@admin_user_id, 1, 1, 'practice', '两数之和', '学习了哈希表解法', 45, 'completed', 2, 4, '掌握了哈希表优化的思路，时间复杂度从O(n²)优化到O(n)', CURDATE()),
(@admin_user_id, 1, 2, 'practice', '三数之和', '练习双指针技巧', 60, 'in_progress', 3, 3, '双指针解法还需要练习，去重逻辑需要加强', DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(@admin_user_id, 2, 4, 'practice', '反转链表', '学习递归和迭代两种方法', 30, 'completed', 2, 5, '递归和迭代两种方法都掌握了，理解了指针操作的精髓', DATE_SUB(CURDATE(), INTERVAL 2 DAY));

-- ================================
-- 创建索引优化查询性能
-- ================================

-- 为JSON字段创建虚拟列索引(MySQL 5.7+)
-- ALTER TABLE algorithm_problems ADD COLUMN tags_generated JSON AS (tags) STORED;
-- CREATE INDEX idx_tags ON algorithm_problems ((CAST(tags_generated AS CHAR(255) ARRAY)));

-- ================================
-- 表结构说明
-- ================================

/*
设计理念：
1. **算法主题表** - 作为知识点的分类管理，对应前端的 algorithmTopics 数据
2. **算法题目表** - 每个主题下的具体练习题目，支持LeetCode集成
3. **用户学习进度表** - 记录用户对每个主题的整体学习进度
4. **学习记录表** - 记录用户的每次具体学习活动，支持多种学习类型

核心特性：
- 🔗 外键约束保证数据一致性
- 📊 支持进度百分比和统计分析
- 🏷️ JSON字段存储标签，灵活扩展
- 📅 时间维度记录，支持学习轨迹分析
- 🎯 多种学习类型，适应不同学习场景
- 📈 评分系统，量化学习效果

扩展性：
- 可以轻松添加新的算法主题
- 支持多种题目来源(LeetCode、自定义等)
- 学习记录支持代码存储和笔记
- 进度跟踪支持细粒度分析
*/