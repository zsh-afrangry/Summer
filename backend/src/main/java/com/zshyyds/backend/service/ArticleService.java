package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ArticleService {
    
    /**
     * 根据ID获取文章
     * @param id 文章ID
     * @return 文章信息
     */
    Optional<Article> getArticleById(Long id);
    
    /**
     * 保存或更新文章
     * @param article 文章信息
     * @return 操作结果
     */
    String saveOrUpdateArticle(Article article);
    
    /**
     * 发布文章
     * @param id 文章ID
     * @return 发布结果
     */
    String publishArticle(Long id);
    
    /**
     * 撤回文章
     * @param id 文章ID
     * @return 撤回结果
     */
    String unpublishArticle(Long id);
    
    /**
     * 删除文章（软删除）
     * @param id 文章ID
     * @return 删除结果
     */
    String deleteArticle(Long id);
    
    /**
     * 根据作者ID获取文章列表
     * @param authorId 作者ID
     * @return 文章列表
     */
    List<Article> getArticlesByAuthor(Long authorId);
    
    /**
     * 根据状态获取文章列表
     * @param status 状态
     * @param pageable 分页信息
     * @return 文章分页列表
     */
    Page<Article> getArticlesByStatus(Integer status, Pageable pageable);
    
    /**
     * 获取已发布的文章列表
     * @param pageable 分页信息
     * @return 文章分页列表
     */
    Page<Article> getPublishedArticles(Pageable pageable);
    
    /**
     * 获取置顶文章
     * @return 置顶文章列表
     */
    List<Article> getTopArticles();
    
    /**
     * 设置文章置顶
     * @param id 文章ID
     * @param isTop 是否置顶
     * @return 操作结果
     */
    String setArticleTop(Long id, Integer isTop);
    
    /**
     * 搜索文章
     * @param keyword 关键词
     * @param pageable 分页信息
     * @return 搜索结果
     */
    Page<Article> searchArticles(String keyword, Pageable pageable);
    
    /**
     * 根据标签获取文章
     * @param tagId 标签ID
     * @param pageable 分页信息
     * @return 文章列表
     */
    Page<Article> getArticlesByTag(Long tagId, Pageable pageable);
    
    /**
     * 根据标签名获取文章
     * @param tagName 标签名
     * @return 文章列表
     */
    List<Article> getArticlesByTagName(String tagName);
    
    /**
     * 获取最新文章
     * @param limit 限制数量
     * @return 文章列表
     */
    List<Article> getLatestArticles(int limit);
    
    /**
     * 获取推荐文章
     * @param limit 限制数量
     * @return 文章列表
     */
    List<Article> getRecommendedArticles(int limit);
    
    /**
     * 获取热门文章
     * @param limit 限制数量
     * @return 文章列表
     */
    List<Article> getPopularArticles(int limit);
    
    /**
     * 获取相关文章
     * @param articleId 文章ID
     * @param limit 限制数量
     * @return 相关文章列表
     */
    List<Article> getRelatedArticles(Long articleId, int limit);
    
    /**
     * 增加文章浏览量
     * @param id 文章ID
     * @return 操作结果
     */
    String incrementViewCount(Long id);
    
    /**
     * 统计已发布文章数量
     * @return 文章数量
     */
    Long countPublishedArticles();
    
    /**
     * 统计作者的文章数量
     * @param authorId 作者ID
     * @return 文章数量
     */
    Long countArticlesByAuthor(Long authorId);
    
    /**
     * 获取月度文章统计
     * @return 统计数据
     */
    List<Map<String, Object>> getMonthlyArticleStats();
    
    /**
     * 获取作者文章统计
     * @return 统计数据
     */
    List<Map<String, Object>> getAuthorArticleStats();
    
    /**
     * 根据时间范围获取文章
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 文章列表
     */
    List<Article> getArticlesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
} 