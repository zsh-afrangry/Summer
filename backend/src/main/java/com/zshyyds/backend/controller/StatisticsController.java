package com.zshyyds.backend.controller;

import com.zshyyds.backend.common.ApiResponse;
import com.zshyyds.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    private final UserService userService;
    private final ArticleService articleService;
    private final TagService tagService;
    private final ProfileService profileService;
    private final SystemConfigService systemConfigService;
    
    @Autowired
    public StatisticsController(UserService userService, 
                              ArticleService articleService,
                              TagService tagService,
                              ProfileService profileService,
                              SystemConfigService systemConfigService) {
        this.userService = userService;
        this.articleService = articleService;
        this.tagService = tagService;
        this.profileService = profileService;
        this.systemConfigService = systemConfigService;
    }
    
    @GetMapping("/overview")
    public Map<String, Object> getOverviewStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 用户统计
            stats.put("userCount", userService.countActiveUsers());
            
            // 文章统计
            stats.put("articleCount", articleService.countPublishedArticles());
            
            // 标签统计
            stats.put("tagCount", tagService.countTags());
            stats.put("tagWithArticlesCount", tagService.countTagsWithArticles());
            
            // 资料统计
            stats.put("profileCount", profileService.countProfiles());
            
            // 配置统计
            stats.put("configCount", systemConfigService.countConfigs());
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // 基础统计
            dashboard.put("userCount", userService.countActiveUsers());
            dashboard.put("articleCount", articleService.countPublishedArticles());
            dashboard.put("tagCount", tagService.countTags());
            
            // 最新数据
            dashboard.put("recentUsers", userService.getRecentUsers(5));
            dashboard.put("latestArticles", articleService.getLatestArticles(5));
            dashboard.put("popularTags", tagService.getPopularTags(10));
            
            // 统计图表数据
            dashboard.put("monthlyArticleStats", articleService.getMonthlyArticleStats());
            dashboard.put("authorArticleStats", articleService.getAuthorArticleStats());
            dashboard.put("tagCloudData", tagService.getTagCloudData());
            
            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/articles")
    public Map<String, Object> getArticleStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("totalCount", articleService.countPublishedArticles());
            stats.put("monthlyStats", articleService.getMonthlyArticleStats());
            stats.put("authorStats", articleService.getAuthorArticleStats());
            stats.put("popularArticles", articleService.getPopularArticles(10));
            stats.put("recommendedArticles", articleService.getRecommendedArticles(10));
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/tags")
    public Map<String, Object> getTagStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("totalCount", tagService.countTags());
            stats.put("withArticlesCount", tagService.countTagsWithArticles());
            stats.put("popularTags", tagService.getPopularTags(20));
            stats.put("tagCloudData", tagService.getTagCloudData());
            stats.put("unusedTags", tagService.getUnusedTags());
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/users")
    public Map<String, Object> getUserStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("activeUserCount", userService.countActiveUsers());
            stats.put("profileCount", profileService.countProfiles());
            stats.put("recentUsers", userService.getRecentUsers(10));
            stats.put("topProfilesByExperience", profileService.getProfilesByExperience(10));
            stats.put("topProfilesByProjects", profileService.getProfilesByProjectCount(10));
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/system")
    public Map<String, Object> getSystemStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("configCount", systemConfigService.countConfigs());
            stats.put("editableConfigCount", systemConfigService.countEditableConfigs());
            stats.put("configsByCategory", systemConfigService.countConfigsByCategory());
            stats.put("configsByType", systemConfigService.countConfigsByType());
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/skills/user/{userId}")
    public Map<String, Object> getUserSkillStatistics(@PathVariable Long userId) {
        try {
            // 这里需要注入SkillService，先简化处理
            Map<String, Object> stats = new HashMap<>();
            stats.put("message", "技能统计功能暂未实现");
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/site-info")
    public Map<String, Object> getSiteInfo() {
        try {
            Map<String, Object> siteInfo = new HashMap<>();
            
            // 从系统配置获取站点信息
            siteInfo.put("title", systemConfigService.getConfigValue("site_title", "我的博客"));
            siteInfo.put("description", systemConfigService.getConfigValue("site_description", "记录技术成长"));
            siteInfo.put("keywords", systemConfigService.getConfigValue("site_keywords", "技术博客"));
            
            // 基础统计数据
            siteInfo.put("articleCount", articleService.countPublishedArticles());
            siteInfo.put("tagCount", tagService.countTags());
            siteInfo.put("userCount", userService.countActiveUsers());
            
            // 最新内容
            siteInfo.put("latestArticles", articleService.getLatestArticles(5));
            siteInfo.put("tagCloud", tagService.getTagCloudData());
            
            return ApiResponse.success(siteInfo);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/recent-activities")
    public Map<String, Object> getRecentActivities() {
        try {
            Map<String, Object> activities = new HashMap<>();
            
            activities.put("recentUsers", userService.getRecentUsers(5));
            activities.put("latestArticles", articleService.getLatestArticles(10));
            activities.put("popularArticles", articleService.getPopularArticles(5));
            
            return ApiResponse.success(activities);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
} 