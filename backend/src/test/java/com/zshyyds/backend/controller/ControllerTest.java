package com.zshyyds.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zshyyds.backend.entity.User;
import com.zshyyds.backend.entity.Profile;
import com.zshyyds.backend.entity.Tag;
import com.zshyyds.backend.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUserController() throws Exception {
        // 测试用户注册
        User user = new User("testuser", "password123", "test@example.com");
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));

        // 测试用户登录
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"));

                 // 测试检查用户名可用性
         mockMvc.perform(get("/api/users/check/username")
                 .param("username", "newuser"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.code").value(200))
                 .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void testProfileController() throws Exception {
        // 先创建用户
        User user = new User("profileuser", "password123", "profile@example.com");
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // 创建用户资料
        Profile profile = new Profile(1L, "测试昵称", "测试简介");
        profile.setJobTitle("软件工程师");
        
                 mockMvc.perform(post("/api/profiles")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(profile)))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.code").value(200))
                 .andExpect(jsonPath("$.message").value("资料创建成功"));

         // 测试获取用户资料
         mockMvc.perform(get("/api/profiles/user/1"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.code").value(200))
                 .andExpect(jsonPath("$.data.nickname").value("测试昵称"));
    }

    @Test
    public void testTagController() throws Exception {
        // 测试创建标签
        Tag tag = new Tag("测试标签", "#FF0000", "测试标签描述");
        
                 mockMvc.perform(post("/api/tags")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(tag)))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.code").value(200))
                 .andExpect(jsonPath("$.message").value("标签创建成功"));

        // 测试获取所有标签
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 测试标签云数据
        mockMvc.perform(get("/api/tags/cloud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testArticleController() throws Exception {
        // 先创建用户
        User user = new User("articleuser", "password123", "article@example.com");
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // 创建文章
        Article article = new Article("测试文章", "测试摘要", "测试内容", 1L);
        article.setStatus(1);
        
                 mockMvc.perform(post("/api/articles")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(article)))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.code").value(200))
                 .andExpect(jsonPath("$.message").value("文章创建成功"));

        // 测试获取已发布文章
        mockMvc.perform(get("/api/articles/published")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 测试搜索文章
        mockMvc.perform(get("/api/articles/search")
                .param("keyword", "测试")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testSystemConfigController() throws Exception {
        // 测试初始化默认配置
        mockMvc.perform(post("/api/system-config/initialize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 测试获取配置值
        mockMvc.perform(get("/api/system-config/site_title/value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 测试获取网站配置
        mockMvc.perform(get("/api/system-config/site"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testStatisticsController() throws Exception {
        // 测试概览统计
        mockMvc.perform(get("/api/statistics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 测试仪表板数据
        mockMvc.perform(get("/api/statistics/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 测试站点信息
        mockMvc.perform(get("/api/statistics/site-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 测试文章统计
        mockMvc.perform(get("/api/statistics/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testApiResponseFormat() throws Exception {
        // 测试API响应格式的一致性
        
                 // 成功响应格式
         mockMvc.perform(get("/api/tags"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.code").exists())
                 .andExpect(jsonPath("$.message").exists())
                 .andExpect(jsonPath("$.data").exists());

         // 错误响应格式
         mockMvc.perform(get("/api/users/999"))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.code").value(404))
                 .andExpect(jsonPath("$.message").value("资源不存在"));
    }

    @Test
    public void testCrossOrigin() throws Exception {
                 // 测试跨域设置
         mockMvc.perform(options("/api/users")
                 .header("Origin", "http://localhost:8080")
                 .header("Access-Control-Request-Method", "GET"))
                 .andExpect(status().isOk())
                 .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
} 