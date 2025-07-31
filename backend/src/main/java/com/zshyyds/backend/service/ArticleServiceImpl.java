package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Article;
import com.zshyyds.backend.entity.ArticleStats;
import com.zshyyds.backend.repository.ArticleRepository;
import com.zshyyds.backend.repository.ArticleStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {
    
    private final ArticleRepository articleRepository;
    private final ArticleStatsRepository articleStatsRepository;
    
    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleStatsRepository articleStatsRepository) {
        this.articleRepository = articleRepository;
        this.articleStatsRepository = articleStatsRepository;
    }
    
    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }
    
    @Override
    @Transactional
    public String saveOrUpdateArticle(Article article) {
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            return "文章标题不能为空";
        }
        
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            return "文章内容不能为空";
        }
        
        if (article.getAuthorId() == null) {
            return "作者ID不能为空";
        }
        
        // 验证字段长度
        if (article.getTitle().length() > 200) {
            return "标题长度不能超过200字符";
        }
        
        if (article.getSummary() != null && article.getSummary().length() > 500) {
            return "摘要长度不能超过500字符";
        }
        
        try {
            // 如果是新文章且状态为发布，设置发布时间
            if (article.getId() == null && article.getStatus() == 1) {
                article.setPublishedAt(LocalDateTime.now());
            }
            
            Article savedArticle = articleRepository.save(article);
            
            // 如果是新文章，创建对应的统计记录
            if (article.getId() == null) {
                createArticleStats(savedArticle.getId());
            }
            
            return "success";
        } catch (Exception e) {
            return "保存失败，请重试";
        }
    }
    
    @Override
    public String publishArticle(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (!articleOptional.isPresent()) {
            return "文章不存在";
        }
        
        try {
            Article article = articleOptional.get();
            article.setStatus(1);
            if (article.getPublishedAt() == null) {
                article.setPublishedAt(LocalDateTime.now());
            }
            articleRepository.save(article);
            return "success";
        } catch (Exception e) {
            return "发布失败";
        }
    }
    
    @Override
    public String unpublishArticle(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (!articleOptional.isPresent()) {
            return "文章不存在";
        }
        
        try {
            Article article = articleOptional.get();
            article.setStatus(0); // 设为草稿
            articleRepository.save(article);
            return "success";
        } catch (Exception e) {
            return "撤回失败";
        }
    }
    
    @Override
    public String deleteArticle(Long id) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (!articleOptional.isPresent()) {
            return "文章不存在";
        }
        
        try {
            Article article = articleOptional.get();
            article.setStatus(-1); // 软删除
            articleRepository.save(article);
            return "success";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    @Override
    public List<Article> getArticlesByAuthor(Long authorId) {
        return articleRepository.findByAuthorIdAndStatus(authorId, 1);
    }
    
    @Override
    public Page<Article> getArticlesByStatus(Integer status, Pageable pageable) {
        return articleRepository.findByStatus(status, pageable);
    }
    
    @Override
    public Page<Article> getPublishedArticles(Pageable pageable) {
        // 如果没有指定排序，默认按发布时间倒序
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), 
                                    Sort.by(Sort.Direction.DESC, "publishedAt"));
        }
        return articleRepository.findByStatus(1, pageable);
    }
    
    @Override
    public List<Article> getTopArticles() {
        return articleRepository.findByIsTopAndStatusOrderByPublishedAtDesc(1, 1);
    }
    
    @Override
    public String setArticleTop(Long id, Integer isTop) {
        Optional<Article> articleOptional = articleRepository.findById(id);
        if (!articleOptional.isPresent()) {
            return "文章不存在";
        }
        
        try {
            Article article = articleOptional.get();
            article.setIsTop(isTop);
            articleRepository.save(article);
            return "success";
        } catch (Exception e) {
            return "设置失败";
        }
    }
    
    @Override
    public Page<Article> searchArticles(String keyword, Pageable pageable) {
        return articleRepository.searchByKeywordWithPagination(keyword, pageable);
    }
    
    @Override
    public Page<Article> getArticlesByTag(Long tagId, Pageable pageable) {
        return articleRepository.findByTagIdWithPagination(tagId, pageable);
    }
    
    @Override
    public List<Article> getArticlesByTagName(String tagName) {
        return articleRepository.findByTagName(tagName);
    }
    
    @Override
    public List<Article> getLatestArticles(int limit) {
        List<Article> articles = articleRepository.findLatestPublishedArticles();
        return articles.size() > limit ? articles.subList(0, limit) : articles;
    }
    
    @Override
    public List<Article> getRecommendedArticles(int limit) {
        List<Article> articles = articleRepository.findRecommendedArticles();
        return articles.size() > limit ? articles.subList(0, limit) : articles;
    }
    
    @Override
    public List<Article> getPopularArticles(int limit) {
        List<Article> articles = articleRepository.findPopularArticles();
        return articles.size() > limit ? articles.subList(0, limit) : articles;
    }
    
    @Override
    public List<Article> getRelatedArticles(Long articleId, int limit) {
        List<Article> articles = articleRepository.findRelatedArticles(articleId);
        return articles.size() > limit ? articles.subList(0, limit) : articles;
    }
    
    @Override
    @Transactional
    public String incrementViewCount(Long id) {
        try {
            // 检查文章统计是否存在
            if (!articleStatsRepository.existsByArticleId(id)) {
                createArticleStats(id);
            }
            
            articleStatsRepository.incrementViewCount(id, LocalDateTime.now());
            return "success";
        } catch (Exception e) {
            return "更新失败";
        }
    }
    
    @Override
    public Long countPublishedArticles() {
        return articleRepository.countPublishedArticles();
    }
    
    @Override
    public Long countArticlesByAuthor(Long authorId) {
        return articleRepository.countPublishedArticlesByAuthor(authorId);
    }
    
    @Override
    public List<Map<String, Object>> getMonthlyArticleStats() {
        List<Object[]> results = articleRepository.getMonthlyArticleStats();
        return convertToMapList(results, new String[]{"year", "month", "count"});
    }
    
    @Override
    public List<Map<String, Object>> getAuthorArticleStats() {
        List<Object[]> results = articleRepository.getAuthorArticleStats();
        return convertToMapList(results, new String[]{"authorId", "count"});
    }
    
    @Override
    public List<Article> getArticlesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return articleRepository.findByPublishedAtBetween(startDate, endDate);
    }
    
    // 私有辅助方法
    private void createArticleStats(Long articleId) {
        ArticleStats stats = new ArticleStats(articleId);
        articleStatsRepository.save(stats);
    }
    
    private List<Map<String, Object>> convertToMapList(List<Object[]> results, String[] keys) {
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < keys.length && i < result.length; i++) {
                        map.put(keys[i], result[i]);
                    }
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }
} 