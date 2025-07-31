package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Test
    public void testUserService() {
        // 测试用户注册
        User user = new User("testuser", "password123", "test@example.com");
        String registerResult = userService.register(user);
        assertEquals("success", registerResult);

        // 测试用户登录
        boolean loginResult = userService.login(user);
        assertTrue(loginResult);

        // 测试查找用户
        Optional<User> foundUser = userService.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());

        // 测试检查用户名可用性
        assertFalse(userService.isUsernameAvailable("testuser"));
        assertTrue(userService.isUsernameAvailable("newuser"));

        // 测试统计活跃用户
        Long count = userService.countActiveUsers();
        assertTrue(count >= 1);
    }

    @Test
    public void testProfileService() {
        // 先创建用户
        User user = new User("profileuser", "password123", "profile@example.com");
        userService.register(user);
        Optional<User> savedUser = userService.findByUsername("profileuser");
        assertTrue(savedUser.isPresent());

        // 测试创建资料
        Profile profile = new Profile(savedUser.get().getId(), "测试昵称", "这是测试简介");
        profile.setJobTitle("软件工程师");
        profile.setYearsExperience(3);
        
        String result = profileService.saveOrUpdateProfile(profile);
        assertEquals("success", result);

        // 测试查找资料
        Optional<Profile> foundProfile = profileService.getProfileByUserId(savedUser.get().getId());
        assertTrue(foundProfile.isPresent());
        assertEquals("测试昵称", foundProfile.get().getNickname());

        // 测试昵称可用性检查
        assertFalse(profileService.isNicknameAvailable("测试昵称", null));
        assertTrue(profileService.isNicknameAvailable("测试昵称", savedUser.get().getId()));
    }

    @Test
    public void testSkillService() {
        // 先创建用户
        User user = new User("skilluser", "password123", "skill@example.com");
        userService.register(user);
        Optional<User> savedUser = userService.findByUsername("skilluser");
        assertTrue(savedUser.isPresent());

        // 测试创建技能
        Skill skill = new Skill(savedUser.get().getId(), "Java", 85, "后端开发");
        String result = skillService.saveOrUpdateSkill(skill);
        assertEquals("success", result);

        // 测试查找技能
        List<Skill> skills = skillService.getSkillsByUserId(savedUser.get().getId());
        assertFalse(skills.isEmpty());
        assertEquals("Java", skills.get(0).getName());

        // 测试检查技能是否存在
        assertTrue(skillService.hasSkill(savedUser.get().getId(), "Java"));
        assertFalse(skillService.hasSkill(savedUser.get().getId(), "Python"));

        // 测试获取技能平均等级
        Double avgLevel = skillService.getAverageSkillLevel(savedUser.get().getId());
        assertEquals(85.0, avgLevel);
    }

    @Test
    public void testTagService() {
        // 测试创建标签
        Tag tag = new Tag("测试标签", "#FF0000", "这是测试标签");
        String result = tagService.saveOrUpdateTag(tag);
        assertEquals("success", result);

        // 测试查找标签
        Optional<Tag> foundTag = tagService.getTagByName("测试标签");
        assertTrue(foundTag.isPresent());
        assertEquals("#FF0000", foundTag.get().getColor());

        // 测试标签名可用性检查
        assertFalse(tagService.isTagNameAvailable("测试标签", null));
        assertTrue(tagService.isTagNameAvailable("新标签", null));

        // 测试获取所有标签
        List<Tag> allTags = tagService.getAllTags();
        assertFalse(allTags.isEmpty());
    }

    @Test
    public void testArticleService() {
        // 先创建用户
        User user = new User("articleuser", "password123", "article@example.com");
        userService.register(user);
        Optional<User> savedUser = userService.findByUsername("articleuser");
        assertTrue(savedUser.isPresent());

        // 测试创建文章
        Article article = new Article("测试文章", "这是摘要", "这是文章内容", savedUser.get().getId());
        article.setStatus(1); // 已发布
        
        String result = articleService.saveOrUpdateArticle(article);
        assertEquals("success", result);

        // 测试获取作者文章
        List<Article> articles = articleService.getArticlesByAuthor(savedUser.get().getId());
        assertFalse(articles.isEmpty());
        assertEquals("测试文章", articles.get(0).getTitle());

        // 测试统计已发布文章数量
        Long count = articleService.countPublishedArticles();
        assertTrue(count >= 1);

        // 测试增加浏览量
        String viewResult = articleService.incrementViewCount(articles.get(0).getId());
        assertEquals("success", viewResult);
    }

    @Test
    public void testSystemConfigService() {
        // 测试初始化默认配置
        String initResult = systemConfigService.initializeDefaultConfigs();
        assertTrue("success".equals(initResult) || "配置已经初始化".equals(initResult));

        // 测试创建配置
        SystemConfig config = new SystemConfig("test_key", "test_value", "string", "测试配置", "test");
        String result = systemConfigService.saveOrUpdateConfig(config);
        assertEquals("success", result);

        // 测试获取配置值
        String value = systemConfigService.getConfigValue("test_key");
        assertEquals("test_value", value);

        // 测试带默认值的获取
        String valueWithDefault = systemConfigService.getConfigValue("non_existent_key", "default");
        assertEquals("default", valueWithDefault);

        // 测试更新配置值
        String updateResult = systemConfigService.updateConfigValue("test_key", "new_value");
        assertEquals("success", updateResult);

        // 验证更新结果
        String updatedValue = systemConfigService.getConfigValue("test_key");
        assertEquals("new_value", updatedValue);

        // 测试检查配置是否存在
        assertTrue(systemConfigService.configExists("test_key"));
        assertFalse(systemConfigService.configExists("non_existent_key"));
    }

    @Test
    public void testServiceIntegration() {
        // 测试服务间的集成
        
        // 1. 创建用户和资料
        User user = new User("integration", "password123", "integration@example.com");
        userService.register(user);
        Optional<User> savedUser = userService.findByUsername("integration");
        assertTrue(savedUser.isPresent());

        Profile profile = new Profile(savedUser.get().getId(), "集成测试", "集成测试简介");
        profileService.saveOrUpdateProfile(profile);

        // 2. 创建技能
        Skill skill = new Skill(savedUser.get().getId(), "集成测试技能", 90, "测试");
        skillService.saveOrUpdateSkill(skill);

        // 3. 创建标签
        Tag tag = new Tag("集成测试标签", "#00FF00", "集成测试标签");
        tagService.saveOrUpdateTag(tag);

        // 4. 创建文章
        Article article = new Article("集成测试文章", "集成测试摘要", "集成测试内容", savedUser.get().getId());
        articleService.saveOrUpdateArticle(article);

        // 5. 验证数据完整性
        Optional<Profile> userProfile = profileService.getProfileByUserId(savedUser.get().getId());
        assertTrue(userProfile.isPresent());

        List<Skill> userSkills = skillService.getSkillsByUserId(savedUser.get().getId());
        assertFalse(userSkills.isEmpty());

        List<Article> userArticles = articleService.getArticlesByAuthor(savedUser.get().getId());
        assertFalse(userArticles.isEmpty());

        Optional<Tag> createdTag = tagService.getTagByName("集成测试标签");
        assertTrue(createdTag.isPresent());
    }
} 