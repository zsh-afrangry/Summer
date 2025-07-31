package com.zshyyds.backend.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class EntityTest {
    
    @Test
    public void testUserEntity() {
        User user = new User("testuser", "password123", "test@example.com");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Integer.valueOf(1), user.getStatus());
    }
    
    @Test
    public void testProfileEntity() {
        Profile profile = new Profile(1L, "张三", "这是个人简介");
        assertNotNull(profile);
        assertEquals(Long.valueOf(1), profile.getUserId());
        assertEquals("张三", profile.getNickname());
        assertEquals("这是个人简介", profile.getBio());
        assertEquals(Integer.valueOf(0), profile.getYearsExperience());
    }
    
    @Test
    public void testSkillEntity() {
        Skill skill = new Skill(1L, "Java", 85, "后端开发");
        assertNotNull(skill);
        assertEquals(Long.valueOf(1), skill.getUserId());
        assertEquals("Java", skill.getName());
        assertEquals(Integer.valueOf(85), skill.getLevel());
        assertEquals("后端开发", skill.getCategory());
    }
    
    @Test
    public void testTagEntity() {
        Tag tag = new Tag("Vue.js", "#4FC08D", "前端框架");
        assertNotNull(tag);
        assertEquals("Vue.js", tag.getName());
        assertEquals("#4FC08D", tag.getColor());
        assertEquals("前端框架", tag.getDescription());
        assertEquals(Integer.valueOf(0), tag.getArticleCount());
    }
    
    @Test
    public void testArticleEntity() {
        Article article = new Article("测试标题", "测试摘要", "测试内容", 1L);
        assertNotNull(article);
        assertEquals("测试标题", article.getTitle());
        assertEquals("测试摘要", article.getSummary());
        assertEquals("测试内容", article.getContent());
        assertEquals(Long.valueOf(1), article.getAuthorId());
        assertEquals(Integer.valueOf(1), article.getStatus());
    }
    
    @Test
    public void testArticleTagEntity() {
        ArticleTag articleTag = new ArticleTag(1L, 2L);
        assertNotNull(articleTag);
        assertEquals(Long.valueOf(1), articleTag.getArticleId());
        assertEquals(Long.valueOf(2), articleTag.getTagId());
    }
    
    @Test
    public void testArticleStatsEntity() {
        ArticleStats stats = new ArticleStats(1L);
        assertNotNull(stats);
        assertEquals(Long.valueOf(1), stats.getArticleId());
        assertEquals(Integer.valueOf(0), stats.getViewCount());
        assertEquals(Integer.valueOf(0), stats.getLikeCount());
        
        // 测试便捷方法
        stats.incrementViewCount();
        assertEquals(Integer.valueOf(1), stats.getViewCount());
        assertNotNull(stats.getLastViewAt());
        
        stats.incrementLikeCount();
        assertEquals(Integer.valueOf(1), stats.getLikeCount());
        
        stats.decrementLikeCount();
        assertEquals(Integer.valueOf(0), stats.getLikeCount());
    }
    
    @Test
    public void testSystemConfigEntity() {
        SystemConfig config = new SystemConfig("site_title", "我的博客", "string", "网站标题", "site");
        assertNotNull(config);
        assertEquals("site_title", config.getConfigKey());
        assertEquals("我的博客", config.getConfigValue());
        assertEquals("string", config.getConfigType());
        assertEquals("网站标题", config.getDescription());
        assertEquals("site", config.getCategory());
        
        // 测试便捷方法
        assertEquals("我的博客", config.getStringValue());
        
        SystemConfig intConfig = new SystemConfig("max_articles", "10", "number", "最大文章数", "system");
        assertEquals(Integer.valueOf(10), intConfig.getIntValue());
        
        SystemConfig boolConfig = new SystemConfig("enabled", "1", "boolean", "是否启用", "feature");
        assertTrue(boolConfig.getBooleanValue());
    }
} 