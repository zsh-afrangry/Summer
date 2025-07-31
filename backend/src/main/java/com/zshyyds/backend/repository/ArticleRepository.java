package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    // 根据作者ID查找文章
    List<Article> findByAuthorId(Long authorId);
    
    // 根据作者ID和状态查找文章
    List<Article> findByAuthorIdAndStatus(Long authorId, Integer status);
    
    // 根据状态查找文章
    List<Article> findByStatus(Integer status);
    
    // 根据状态查找文章并分页
    Page<Article> findByStatus(Integer status, Pageable pageable);
    
    // 根据状态查找文章并按发布时间排序
    List<Article> findByStatusOrderByPublishedAtDesc(Integer status);
    
    // 根据置顶状态查找文章
    List<Article> findByIsTop(Integer isTop);
    
    // 获取置顶文章（已发布）
    List<Article> findByIsTopAndStatusOrderByPublishedAtDesc(Integer isTop, Integer status);
    
    // 根据标题模糊查询
    List<Article> findByTitleContainingIgnoreCase(String title);
    
    // 根据内容模糊查询（注意：对于大文本字段，建议谨慎使用）
    @Query("SELECT a FROM Article a WHERE a.content LIKE CONCAT('%', :content, '%')")
    List<Article> findByContentContaining(@Param("content") String content);
    
    // 根据标题或内容模糊查询
    @Query("SELECT a FROM Article a WHERE " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "a.content LIKE CONCAT('%', :keyword, '%')")
    List<Article> searchByKeyword(@Param("keyword") String keyword);
    
    // 根据标题或内容模糊查询并分页
    @Query("SELECT a FROM Article a WHERE a.status = 1 AND (" +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "a.content LIKE CONCAT('%', :keyword, '%')) " +
           "ORDER BY a.publishedAt DESC")
    Page<Article> searchByKeywordWithPagination(@Param("keyword") String keyword, Pageable pageable);
    
    // 根据发布时间范围查找文章
    List<Article> findByPublishedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 根据标签查找文章
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.id = :tagId AND a.status = 1")
    List<Article> findByTagId(@Param("tagId") Long tagId);
    
    // 根据标签名查找文章
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.name = :tagName AND a.status = 1")
    List<Article> findByTagName(@Param("tagName") String tagName);
    
    // 根据标签查找文章并分页
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t WHERE t.id = :tagId AND a.status = 1 ORDER BY a.publishedAt DESC")
    Page<Article> findByTagIdWithPagination(@Param("tagId") Long tagId, Pageable pageable);
    
    // 获取最新发布的文章
    @Query("SELECT a FROM Article a WHERE a.status = 1 ORDER BY a.publishedAt DESC")
    List<Article> findLatestPublishedArticles();
    
    // 获取推荐文章（基于浏览量）
    @Query("SELECT a FROM Article a JOIN a.stats s WHERE a.status = 1 ORDER BY s.viewCount DESC")
    List<Article> findRecommendedArticles();
    
    // 获取热门文章（基于点赞数）
    @Query("SELECT a FROM Article a JOIN a.stats s WHERE a.status = 1 ORDER BY s.likeCount DESC")
    List<Article> findPopularArticles();
    
    // 获取相关文章（同标签）
    @Query("SELECT DISTINCT a FROM Article a JOIN a.tags t1 " +
           "WHERE a.id != :articleId AND a.status = 1 " +
           "AND t1.id IN (SELECT t2.id FROM Article a2 JOIN a2.tags t2 WHERE a2.id = :articleId) " +
           "ORDER BY a.publishedAt DESC")
    List<Article> findRelatedArticles(@Param("articleId") Long articleId);
    
    // 统计已发布文章数量
    @Query("SELECT COUNT(a) FROM Article a WHERE a.status = 1")
    Long countPublishedArticles();
    
    // 统计作者的文章数量
    @Query("SELECT COUNT(a) FROM Article a WHERE a.authorId = :authorId AND a.status = 1")
    Long countPublishedArticlesByAuthor(@Param("authorId") Long authorId);
    
    // 获取月度文章统计
    @Query("SELECT YEAR(a.publishedAt), MONTH(a.publishedAt), COUNT(a) " +
           "FROM Article a WHERE a.status = 1 " +
           "GROUP BY YEAR(a.publishedAt), MONTH(a.publishedAt) " +
           "ORDER BY YEAR(a.publishedAt) DESC, MONTH(a.publishedAt) DESC")
    List<Object[]> getMonthlyArticleStats();
    
    // 获取作者文章统计
    @Query("SELECT a.authorId, COUNT(a) FROM Article a WHERE a.status = 1 GROUP BY a.authorId ORDER BY COUNT(a) DESC")
    List<Object[]> getAuthorArticleStats();
} 