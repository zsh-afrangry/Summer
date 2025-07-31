package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleStatsRepository articleStatsRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Test
    public void testUserRepository() {
        // 创建测试用户
        User user = new User("testuser", "password123", "test@example.com");
        user = entityManager.persistAndFlush(user);

        // 测试根据用户名查找
        Optional<User> foundByUsername = userRepository.findByUsername("testuser");
        assertTrue(foundByUsername.isPresent());
        assertEquals("testuser", foundByUsername.get().getUsername());

        // 测试根据邮箱查找
        Optional<User> foundByEmail = userRepository.findByEmail("test@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals("test@example.com", foundByEmail.get().getEmail());

        // 测试用户名或邮箱查找
        Optional<User> foundByIdentifier = userRepository.findByUsernameOrEmail("testuser");
        assertTrue(foundByIdentifier.isPresent());

        // 测试检查用户名是否存在
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    public void testProfileRepository() {
        // 创建测试用户
        User user = new User("profileuser", "password123", "profile@example.com");
        user = entityManager.persistAndFlush(user);

        // 创建用户资料
        Profile profile = new Profile(user.getId(), "测试昵称", "这是测试简介");
        profile.setJobTitle("软件工程师");
        profile.setYearsExperience(3);
        profile = entityManager.persistAndFlush(profile);

        // 测试根据用户ID查找
        Optional<Profile> foundProfile = profileRepository.findByUserId(user.getId());
        assertTrue(foundProfile.isPresent());
        assertEquals("测试昵称", foundProfile.get().getNickname());

        // 测试根据昵称查找
        Optional<Profile> foundByNickname = profileRepository.findByNickname("测试昵称");
        assertTrue(foundByNickname.isPresent());

        // 测试检查昵称是否存在
        assertTrue(profileRepository.existsByNickname("测试昵称"));
    }

    @Test
    public void testSkillRepository() {
        // 创建测试用户
        User user = new User("skilluser", "password123", "skill@example.com");
        user = entityManager.persistAndFlush(user);

        // 创建技能
        Skill skill = new Skill(user.getId(), "Java", 85, "后端开发");
        skill = entityManager.persistAndFlush(skill);

        // 测试根据用户ID查找技能
        var skills = skillRepository.findByUserId(user.getId());
        assertFalse(skills.isEmpty());
        assertEquals("Java", skills.get(0).getName());

        // 测试根据分类查找
        var backendSkills = skillRepository.findByCategory("后端开发");
        assertFalse(backendSkills.isEmpty());

        // 测试检查技能是否存在
        assertTrue(skillRepository.existsByUserIdAndName(user.getId(), "Java"));
    }

    @Test
    public void testTagRepository() {
        // 创建标签
        Tag tag = new Tag("测试标签", "#FF0000", "这是测试标签");
        tag = entityManager.persistAndFlush(tag);

        // 测试根据名称查找
        Optional<Tag> foundTag = tagRepository.findByName("测试标签");
        assertTrue(foundTag.isPresent());
        assertEquals("#FF0000", foundTag.get().getColor());

        // 测试检查标签是否存在
        assertTrue(tagRepository.existsByName("测试标签"));
        assertFalse(tagRepository.existsByName("不存在的标签"));
    }

    @Test
    public void testArticleRepository() {
        // 创建测试用户
        User user = new User("articleuser", "password123", "article@example.com");
        user = entityManager.persistAndFlush(user);

        // 创建文章
        Article article = new Article("测试文章", "这是摘要", "这是文章内容", user.getId());
        article.setStatus(1); // 已发布
        article.setPublishedAt(LocalDateTime.now());
        article = entityManager.persistAndFlush(article);

        // 测试根据作者ID查找
        var articles = articleRepository.findByAuthorId(user.getId());
        assertFalse(articles.isEmpty());
        assertEquals("测试文章", articles.get(0).getTitle());

        // 测试根据状态查找
        var publishedArticles = articleRepository.findByStatus(1);
        assertFalse(publishedArticles.isEmpty());

        // 测试标题搜索
        var searchResults = articleRepository.findByTitleContainingIgnoreCase("测试");
        assertFalse(searchResults.isEmpty());
    }

    @Test
    public void testArticleStatsRepository() {
        // 创建测试用户和文章
        User user = new User("statsuser", "password123", "stats@example.com");
        user = entityManager.persistAndFlush(user);

        Article article = new Article("统计测试文章", "摘要", "内容", user.getId());
        article = entityManager.persistAndFlush(article);

        // 创建文章统计
        ArticleStats stats = new ArticleStats(article.getId());
        stats.setViewCount(100);
        stats.setLikeCount(10);
        stats = entityManager.persistAndFlush(stats);

        // 测试根据文章ID查找
        Optional<ArticleStats> foundStats = articleStatsRepository.findByArticleId(article.getId());
        assertTrue(foundStats.isPresent());
        assertEquals(Integer.valueOf(100), foundStats.get().getViewCount());

        // 测试检查统计是否存在
        assertTrue(articleStatsRepository.existsByArticleId(article.getId()));
    }

    @Test
    public void testSystemConfigRepository() {
        // 创建系统配置
        SystemConfig config = new SystemConfig("test_key", "test_value", "string", "测试配置", "test");
        config = entityManager.persistAndFlush(config);

        // 测试根据配置键查找
        Optional<SystemConfig> foundConfig = systemConfigRepository.findByConfigKey("test_key");
        assertTrue(foundConfig.isPresent());
        assertEquals("test_value", foundConfig.get().getConfigValue());

        // 测试根据分类查找
        var testConfigs = systemConfigRepository.findByCategory("test");
        assertFalse(testConfigs.isEmpty());

        // 测试检查配置键是否存在
        assertTrue(systemConfigRepository.existsByConfigKey("test_key"));
        assertFalse(systemConfigRepository.existsByConfigKey("nonexistent_key"));
    }
} 