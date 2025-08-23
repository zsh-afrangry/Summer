-- ========================================
-- 个人博客系统数据库初始化脚本
-- Vue3 + Spring Boot 全栈项目
-- 作者：张斯涵
-- 创建时间：2024年1月
-- ========================================

-- 1. 创建数据库
-- ========================================
DROP DATABASE IF EXISTS `my_blog`;
CREATE DATABASE `my_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `my_blog`;

-- 2. 创建基础表结构
-- ========================================

-- 2.1 用户基础表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础表';

-- 2.2 用户资料表
DROP TABLE IF EXISTS `profile`;
CREATE TABLE `profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资料ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `bio` text COMMENT '个人简介',
  `job_title` varchar(100) DEFAULT NULL COMMENT '职位标题',
  `company` varchar(100) DEFAULT NULL COMMENT '公司',
  `location` varchar(100) DEFAULT NULL COMMENT '所在地',
  `website` varchar(255) DEFAULT NULL COMMENT '个人网站',
  `github` varchar(255) DEFAULT NULL COMMENT 'GitHub地址',
  `linkedin` varchar(255) DEFAULT NULL COMMENT 'LinkedIn地址',
  `twitter` varchar(255) DEFAULT NULL COMMENT 'Twitter地址',
  `motto` varchar(255) DEFAULT NULL COMMENT '个人座右铭',
  `years_experience` int DEFAULT '0' COMMENT '工作年限',
  `project_count` int DEFAULT '0' COMMENT '项目数量',
  `article_count` int DEFAULT '0' COMMENT '文章数量',
  `code_commits` int DEFAULT '0' COMMENT '代码提交数',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_nickname` (`nickname`),
  CONSTRAINT `fk_profile_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表';

-- 2.3 技能表
DROP TABLE IF EXISTS `skill`;
CREATE TABLE `skill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '技能ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '技能名称',
  `level` int DEFAULT '0' COMMENT '技能等级(0-100)',
  `category` varchar(50) DEFAULT NULL COMMENT '技能分类',
  `description` text COMMENT '技能描述',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category` (`category`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_skill_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能表';

-- 2.4 标签表
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `color` varchar(20) DEFAULT '#667eea' COMMENT '标签颜色',
  `description` varchar(255) DEFAULT NULL COMMENT '标签描述',
  `article_count` int DEFAULT '0' COMMENT '文章数量',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_article_count` (`article_count`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 2.5 文章表
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '文章摘要',
  `content` longtext NOT NULL COMMENT '文章内容',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-发布，0-草稿，-1-删除',
  `read_time` int DEFAULT '0' COMMENT '预估阅读时间(分钟)',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶：1-是，0-否',
  `allow_comment` tinyint DEFAULT '1' COMMENT '是否允许评论：1-是，0-否',
  `published_at` timestamp NULL DEFAULT NULL COMMENT '发布时间',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_status` (`status`),
  KEY `idx_published_at` (`published_at`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_created_at` (`created_at`),
  FULLTEXT KEY `ft_title_content` (`title`,`content`),
  CONSTRAINT `fk_article_author_id` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 2.6 文章标签关联表
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_tag` (`article_id`,`tag_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_tag_id` (`tag_id`),
  CONSTRAINT `fk_article_tag_article_id` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_tag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 2.7 文章统计表
DROP TABLE IF EXISTS `article_stats`;
CREATE TABLE `article_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `like_count` int DEFAULT '0' COMMENT '点赞次数',
  `comment_count` int DEFAULT '0' COMMENT '评论次数',
  `share_count` int DEFAULT '0' COMMENT '分享次数',
  `collect_count` int DEFAULT '0' COMMENT '收藏次数',
  `last_view_at` timestamp NULL DEFAULT NULL COMMENT '最后浏览时间',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_id` (`article_id`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_like_count` (`like_count`),
  KEY `idx_last_view_at` (`last_view_at`),
  CONSTRAINT `fk_article_stats_article_id` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章统计表';

-- 2.8 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_type` varchar(20) DEFAULT 'string' COMMENT '配置类型',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `category` varchar(50) DEFAULT 'system' COMMENT '配置分类',
  `is_editable` tinyint DEFAULT '1' COMMENT '是否可编辑：1-是，0-否',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 3. 插入初始化数据
-- ========================================

-- 3.1 系统配置初始数据
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`, `category`) VALUES
('site_title', '我的个人博客', 'string', '网站标题', 'site'),
('site_description', '用心记录技术成长之路', 'string', '网站描述', 'site'),
('site_keywords', 'Vue,Spring Boot,全栈开发,技术博客', 'string', '网站关键词', 'site'),
('admin_email', 'admin@example.com', 'string', '管理员邮箱', 'admin'),
('articles_per_page', '10', 'number', '每页文章数', 'pagination'),
('comment_enabled', '1', 'boolean', '是否启用评论', 'feature'),
('registration_enabled', '1', 'boolean', '是否允许注册', 'feature'),
('site_logo', '', 'string', '网站Logo', 'site'),
('site_favicon', '', 'string', '网站图标', 'site'),
('analytics_code', '', 'string', '统计代码', 'analytics');

-- 3.2 默认标签数据
INSERT INTO `tag` (`name`, `color`, `description`, `sort_order`) VALUES
('Vue.js', '#4FC08D', '渐进式JavaScript框架', 1),
('Spring Boot', '#6DB33F', 'Java企业级应用框架', 2),
('JavaScript', '#F7DF1E', 'Web前端编程语言', 3),
('MySQL', '#4479A1', '关系型数据库管理系统', 4),
('全栈开发', '#667eea', '前后端全栈技术', 5),
('前端开发', '#61DAFB', '前端技术相关', 6),
('后端开发', '#ED8B00', '后端技术相关', 7),
('数据库', '#336791', '数据库相关技术', 8),
('性能优化', '#FF6B6B', '性能优化相关', 9),
('项目实战', '#4ECDC4', '实际项目开发经验', 10),
('HTML5', '#E34F26', 'HTML5相关技术', 11),
('CSS3', '#1572B6', 'CSS3样式技术', 12),
('Java', '#ED8B00', 'Java编程语言', 13),
('Git', '#F05032', '版本控制工具', 14),
('Docker', '#2496ED', '容器化技术', 15);

-- 3.3 创建默认管理员用户
INSERT INTO `user` (`username`, `password`, `email`, `status`) VALUES
('admin', '123456', 'admin@example.com', 1);

-- 获取刚插入的用户ID
SET @admin_user_id = LAST_INSERT_ID();

-- 3.4 创建管理员资料
INSERT INTO `profile` (
  `user_id`, `nickname`, `real_name`, `bio`, `job_title`, `company`, 
  `location`, `website`, `github`, `linkedin`, `motto`,
  `years_experience`, `project_count`, `article_count`, `code_commits`
) VALUES (
  @admin_user_id, '张斯涵', '张斯涵', 
  '热爱技术的全栈开发工程师，专注于Vue.js和Spring Boot技术栈，致力于分享技术经验和项目实战心得。',
  '全栈开发工程师', '某科技公司', '北京', 
  'https://example.com', 'https://github.com/zhangsihan', 'https://linkedin.com/in/zhangsihan',
  '代码改变世界，技术创造未来',
  3, 50, 12, 1000
);

-- 3.5 创建管理员技能数据
INSERT INTO `skill` (`user_id`, `name`, `level`, `category`, `sort_order`) VALUES
(@admin_user_id, 'Vue.js', 90, '前端开发', 1),
(@admin_user_id, 'JavaScript', 85, '前端开发', 2),
(@admin_user_id, 'HTML/CSS', 80, '前端开发', 3),
(@admin_user_id, 'Spring Boot', 88, '后端开发', 4),
(@admin_user_id, 'Java', 82, '后端开发', 5),
(@admin_user_id, 'MySQL', 75, '后端开发', 6),
(@admin_user_id, 'Git', 85, '开发工具', 7),
(@admin_user_id, 'Docker', 70, '开发工具', 8),
(@admin_user_id, 'Linux', 75, '开发工具', 9);

-- 3.6 创建示例文章数据
INSERT INTO `article` (
  `title`, `summary`, `content`, `author_id`, `status`, `read_time`, 
  `is_top`, `published_at`
) VALUES 
(
  'Vue3 + Spring Boot 全栈项目搭建指南',
  '详细介绍如何从零开始搭建一个完整的前后端分离项目，包括环境配置、项目结构设计、接口开发、数据库设计等关键步骤。通过实际案例演示，帮助开发者快速掌握全栈开发技能。',
  '# Vue3 + Spring Boot 全栈项目搭建指南\n\n## 项目概述\n\n本文将详细介绍如何使用Vue3和Spring Boot搭建一个现代化的前后端分离项目...\n\n## 环境准备\n\n### 前端环境\n- Node.js 16+\n- Vue CLI 5+\n- VS Code\n\n### 后端环境\n- JDK 17+\n- Maven 3.6+\n- IntelliJ IDEA\n- MySQL 8.0\n\n## 项目初始化\n\n### 1. 创建Spring Boot项目\n\n使用Spring Initializr创建项目，选择以下依赖：\n- Spring Web\n- Spring Data JPA\n- MySQL Driver\n- Lombok\n\n### 2. 创建Vue3项目\n\n```bash\nvue create frontend\ncd frontend\nnpm run serve\n```\n\n## 数据库设计\n\n设计用户表、文章表等核心表结构...\n\n## 总结\n\n通过本文的学习，你应该能够：\n1. 搭建完整的开发环境\n2. 理解前后端分离架构\n3. 实现基础的CRUD操作\n4. 掌握项目部署流程',
  @admin_user_id, 1, 8, 1, NOW()
),
(
  'JavaScript 异步编程深度解析',
  '深入探讨JavaScript中的异步编程模式，包括Promise、async/await的使用技巧和最佳实践。通过多个实例展示如何优雅地处理异步操作，避免回调地狱问题。',
  '# JavaScript 异步编程深度解析\n\n## 异步编程的重要性\n\nJavaScript是单线程语言，异步编程是处理耗时操作的关键...\n\n## Promise详解\n\n### 基本用法\n\n```javascript\nconst promise = new Promise((resolve, reject) => {\n  // 异步操作\n  setTimeout(() => {\n    resolve("操作成功");\n  }, 1000);\n});\n\npromise.then(result => {\n  console.log(result);\n}).catch(error => {\n  console.error(error);\n});\n```\n\n## async/await语法\n\n### 简化异步代码\n\n```javascript\nasync function fetchData() {\n  try {\n    const response = await fetch("/api/data");\n    const data = await response.json();\n    return data;\n  } catch (error) {\n    console.error("请求失败:", error);\n  }\n}\n```\n\n## 最佳实践\n\n1. 优先使用async/await\n2. 合理的错误处理\n3. 避免不必要的串行等待\n4. 使用Promise.all处理并发请求\n\n## 总结\n\n掌握异步编程是JavaScript开发的必备技能...',
  @admin_user_id, 1, 6, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)
),
(
  'MySQL 数据库性能优化实战',
  '分享MySQL数据库优化的实用技巧，包括索引优化、查询优化、配置调优等方面的经验总结。通过实际案例分析，展示如何诊断和解决数据库性能问题。',
  '# MySQL 数据库性能优化实战\n\n## 性能优化概述\n\n数据库性能优化是系统优化的重要环节...\n\n## 索引优化\n\n### 索引类型\n\n1. **主键索引**：自动创建，唯一且非空\n2. **唯一索引**：保证数据唯一性\n3. **普通索引**：提高查询效率\n4. **复合索引**：多字段组合索引\n\n### 索引设计原则\n\n```sql\n-- 为经常查询的字段创建索引\nCREATE INDEX idx_username ON user(username);\n\n-- 复合索引遵循最左前缀原则\nCREATE INDEX idx_user_status_time ON user(status, created_at);\n```\n\n## 查询优化\n\n### 使用EXPLAIN分析查询\n\n```sql\nEXPLAIN SELECT * FROM article WHERE author_id = 1;\n```\n\n### 避免全表扫描\n\n1. 合理使用WHERE条件\n2. 避免在WHERE子句中使用函数\n3. 使用LIMIT限制结果集\n\n## 配置优化\n\n### 关键参数调优\n\n```ini\n# my.cnf配置示例\ninnodb_buffer_pool_size = 128M\nmax_connections = 100\nquery_cache_size = 32M\n```\n\n## 监控与诊断\n\n### 慢查询日志\n\n```sql\n-- 开启慢查询日志\nSET GLOBAL slow_query_log = 1;\nSET GLOBAL long_query_time = 1;\n```\n\n## 总结\n\n数据库性能优化是一个持续的过程...',
  @admin_user_id, 1, 10, 0, DATE_SUB(NOW(), INTERVAL 10 DAY)
);

-- 获取插入的文章ID
SET @start_id = LAST_INSERT_ID(); -- 获取第一条插入的ID，例如 1
SET @article1_id = @start_id;      -- 结果是 1
SET @article2_id = @start_id + 1;  -- 结果是 2
SET @article3_id = @start_id + 2;  -- 结果是 3

-- 3.7 创建文章标签关联
INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES
(@article1_id, (SELECT id FROM tag WHERE name = 'Vue.js')),
(@article1_id, (SELECT id FROM tag WHERE name = 'Spring Boot')),
(@article1_id, (SELECT id FROM tag WHERE name = '全栈开发')),
(@article2_id, (SELECT id FROM tag WHERE name = 'JavaScript')),
(@article2_id, (SELECT id FROM tag WHERE name = '前端开发')),
(@article3_id, (SELECT id FROM tag WHERE name = 'MySQL')),
(@article3_id, (SELECT id FROM tag WHERE name = '数据库')),
(@article3_id, (SELECT id FROM tag WHERE name = '性能优化'));

-- 3.8 创建文章统计数据
INSERT INTO `article_stats` (`article_id`, `view_count`, `like_count`, `comment_count`) VALUES
(@article1_id, 156, 23, 5),
(@article2_id, 203, 31, 8),
(@article3_id, 189, 28, 12);

-- 3.9 更新标签文章数量
UPDATE `tag` SET `article_count` = (
  SELECT COUNT(*) FROM `article_tag` WHERE `tag_id` = `tag`.`id`
);

-- 4. 验证数据完整性
-- ========================================

-- 4.1 检查表结构
SHOW TABLES;

-- 4.2 检查数据统计
SELECT 
  '用户数' as item, COUNT(*) as count FROM user
UNION ALL
SELECT 
  '标签数' as item, COUNT(*) as count FROM tag
UNION ALL
SELECT 
  '文章数' as item, COUNT(*) as count FROM article
UNION ALL
SELECT 
  '技能数' as item, COUNT(*) as count FROM skill;

-- 4.3 检查关联数据
SELECT 
  a.title,
  GROUP_CONCAT(t.name) as tags,
  s.view_count,
  s.like_count
FROM article a
LEFT JOIN article_tag at ON a.id = at.article_id
LEFT JOIN tag t ON at.tag_id = t.id
LEFT JOIN article_stats s ON a.id = s.article_id
GROUP BY a.id;

-- ========================================
-- 数据库初始化完成
-- ========================================

-- 注意事项：
-- 1. 默认管理员账户: admin/123456 (密码已加密)
-- 2. 已创建基础标签和示例文章
-- 3. 所有外键约束已正确设置
-- 4. 索引已按照性能要求创建
-- 5. 字符集使用utf8mb4支持emoji和中文 

UPDATE profile SET bio = LEFT(bio, 65535) WHERE LENGTH(bio) > 65535;