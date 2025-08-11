package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Article;
import com.zshyyds.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * 根据状态查找文章（分页）
     * @param status 文章状态：1-发布，0-草稿，-1-删除
     */
    Page<Article> findByStatusOrderByPublishedAtDesc(Byte status, Pageable pageable);
    
    /**
     * 根据作者和状态查找文章
     */
    Page<Article> findByAuthorAndStatusOrderByCreatedAtDesc(User author, Byte status, Pageable pageable);
    
    /**
     * 查找置顶文章
     */
    List<Article> findByIsTopAndStatusOrderByPublishedAtDesc(Byte isTop, Byte status);
    
    /**
     * 获取最新发布的文章
     */
    @Query("SELECT a FROM Article a WHERE a.status = 1 ORDER BY a.publishedAt DESC")
    List<Article> findLatestPublishedArticles(Pageable pageable);
    
    /**
     * 根据标题搜索文章（模糊查询）
     */
    @Query("SELECT a FROM Article a WHERE a.status = 1 AND a.title LIKE %:keyword%")
    Page<Article> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据标题或内容搜索文章（全文搜索）
     */
    @Query("SELECT a FROM Article a WHERE a.status = 1 AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%)")
    Page<Article> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据作者ID查找文章
     */
    List<Article> findByAuthorIdAndStatusOrderByCreatedAtDesc(Long authorId, Byte status);
    
    /**
     * 统计作者的文章数量
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.author.id = :authorId AND a.status = 1")
    long countByAuthorIdAndStatus(@Param("authorId") Long authorId);
    
    /**
     * 获取指定时间段内的文章
     */
    @Query("SELECT a FROM Article a WHERE a.status = 1 AND a.publishedAt BETWEEN :startTime AND :endTime")
    List<Article> findByPublishedAtBetween(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    /**
     * 根据阅读时间范围查找文章
     */
    List<Article> findByStatusAndReadTimeBetweenOrderByPublishedAtDesc(Byte status, Integer minReadTime, Integer maxReadTime);
    
    /**
     * 查找相关文章（基于标题相似度）
     */
    @Query("SELECT a FROM Article a WHERE a.status = 1 AND a.id != :excludeId AND a.title LIKE %:keyword% ORDER BY a.publishedAt DESC")
    List<Article> findRelatedArticles(@Param("excludeId") Long excludeId, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取热门文章（基于浏览量）
     */
    @Query("SELECT a FROM Article a JOIN ArticleStat s ON a.id = s.article.id WHERE a.status = 1 ORDER BY s.viewCount DESC")
    List<Article> findPopularArticles(Pageable pageable);
    
    /**
     * 统计总发布文章数
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.status = 1")
    long countPublishedArticles();
    
    /**
     * 根据标签查找文章
     */
    @Query("SELECT DISTINCT a FROM Article a JOIN ArticleTag at ON a.id = at.article.id WHERE at.tag.id = :tagId AND a.status = 1")
    Page<Article> findByTagId(@Param("tagId") Long tagId, Pageable pageable);
    
    /**
     * 根据标签名查找文章
     */
    @Query("SELECT DISTINCT a FROM Article a JOIN ArticleTag at ON a.id = at.article.id WHERE at.tag.name = :tagName AND a.status = 1")
    Page<Article> findByTagName(@Param("tagName") String tagName, Pageable pageable);
}