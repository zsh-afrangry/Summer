package com.zshyyds.backend.controller;

import com.zshyyds.backend.common.ApiResponse;
import com.zshyyds.backend.entity.Article;
import com.zshyyds.backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {
    
    private final ArticleService articleService;
    
    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getArticleById(@PathVariable Long id) {
        try {
            Optional<Article> article = articleService.getArticleById(id);
            if (article.isPresent()) {
                // 增加浏览量
                articleService.incrementViewCount(id);
                return ApiResponse.success(article.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping
    public Map<String, Object> createArticle(@RequestBody Article article) {
        try {
            String result = articleService.saveOrUpdateArticle(article);
            
            if ("success".equals(result)) {
                return ApiResponse.success("文章创建成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        try {
            article.setId(id);
            String result = articleService.saveOrUpdateArticle(article);
            
            if ("success".equals(result)) {
                return ApiResponse.success("文章更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}/publish")
    public Map<String, Object> publishArticle(@PathVariable Long id) {
        try {
            String result = articleService.publishArticle(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("文章发布成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}/unpublish")
    public Map<String, Object> unpublishArticle(@PathVariable Long id) {
        try {
            String result = articleService.unpublishArticle(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("文章撤回成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteArticle(@PathVariable Long id) {
        try {
            String result = articleService.deleteArticle(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("文章删除成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/author/{authorId}")
    public Map<String, Object> getArticlesByAuthor(@PathVariable Long authorId) {
        try {
            List<Article> articles = articleService.getArticlesByAuthor(authorId);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/status/{status}")
    public Map<String, Object> getArticlesByStatus(@PathVariable Integer status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Article> articles = articleService.getArticlesByStatus(status, pageable);
            return ApiResponse.page(articles.getContent(), articles.getTotalElements(), page, size);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/published")
    public Map<String, Object> getPublishedArticles(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Article> articles = articleService.getPublishedArticles(pageable);
            return ApiResponse.page(articles.getContent(), articles.getTotalElements(), page, size);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/top")
    public Map<String, Object> getTopArticles() {
        try {
            List<Article> articles = articleService.getTopArticles();
            return ApiResponse.success(articles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}/top")
    public Map<String, Object> setArticleTop(@PathVariable Long id, @RequestParam Integer isTop) {
        try {
            String result = articleService.setArticleTop(id, isTop);
            
            if ("success".equals(result)) {
                return ApiResponse.success("置顶设置成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/search")
    public Map<String, Object> searchArticles(@RequestParam String keyword,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Article> articles = articleService.searchArticles(keyword, pageable);
            return ApiResponse.page(articles.getContent(), articles.getTotalElements(), page, size);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/tag/{tagId}")
    public Map<String, Object> getArticlesByTag(@PathVariable Long tagId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Article> articles = articleService.getArticlesByTag(tagId, pageable);
            return ApiResponse.page(articles.getContent(), articles.getTotalElements(), page, size);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/tag-name/{tagName}")
    public Map<String, Object> getArticlesByTagName(@PathVariable String tagName) {
        try {
            List<Article> articles = articleService.getArticlesByTagName(tagName);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/latest")
    public Map<String, Object> getLatestArticles(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Article> articles = articleService.getLatestArticles(limit);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/recommended")
    public Map<String, Object> getRecommendedArticles(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Article> articles = articleService.getRecommendedArticles(limit);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/popular")
    public Map<String, Object> getPopularArticles(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<Article> articles = articleService.getPopularArticles(limit);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/{id}/related")
    public Map<String, Object> getRelatedArticles(@PathVariable Long id, 
                                                 @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Article> articles = articleService.getRelatedArticles(id, limit);
            return ApiResponse.success(articles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping("/{id}/view")
    public Map<String, Object> incrementViewCount(@PathVariable Long id) {
        try {
            String result = articleService.incrementViewCount(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("浏览量更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count/published")
    public Map<String, Object> countPublishedArticles() {
        try {
            Long count = articleService.countPublishedArticles();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count/author/{authorId}")
    public Map<String, Object> countArticlesByAuthor(@PathVariable Long authorId) {
        try {
            Long count = articleService.countArticlesByAuthor(authorId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/stats/monthly")
    public Map<String, Object> getMonthlyArticleStats() {
        try {
            List<Map<String, Object>> stats = articleService.getMonthlyArticleStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/stats/author")
    public Map<String, Object> getAuthorArticleStats() {
        try {
            List<Map<String, Object>> stats = articleService.getAuthorArticleStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
} 