package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.ArticleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleStatsRepository extends JpaRepository<ArticleStats, Long> {
    
    // 根据文章ID查找统计数据
    Optional<ArticleStats> findByArticleId(Long articleId);
    
    // 检查文章统计是否存在
    boolean existsByArticleId(Long articleId);
    
    // 按浏览量排序获取统计数据
    List<ArticleStats> findAllByOrderByViewCountDesc();
    
    // 按点赞数排序获取统计数据
    List<ArticleStats> findAllByOrderByLikeCountDesc();
    
    // 按评论数排序获取统计数据
    List<ArticleStats> findAllByOrderByCommentCountDesc();
    
    // 获取浏览量大于指定值的文章统计
    List<ArticleStats> findByViewCountGreaterThan(Integer viewCount);
    
    // 获取点赞数大于指定值的文章统计
    List<ArticleStats> findByLikeCountGreaterThan(Integer likeCount);
    
    // 增加浏览量
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.viewCount = s.viewCount + 1, s.lastViewAt = :viewTime WHERE s.articleId = :articleId")
    void incrementViewCount(@Param("articleId") Long articleId, @Param("viewTime") LocalDateTime viewTime);
    
    // 增加点赞数
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.likeCount = s.likeCount + 1 WHERE s.articleId = :articleId")
    void incrementLikeCount(@Param("articleId") Long articleId);
    
    // 减少点赞数
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.likeCount = s.likeCount - 1 WHERE s.articleId = :articleId AND s.likeCount > 0")
    void decrementLikeCount(@Param("articleId") Long articleId);
    
    // 增加评论数
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.commentCount = s.commentCount + 1 WHERE s.articleId = :articleId")
    void incrementCommentCount(@Param("articleId") Long articleId);
    
    // 减少评论数
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.commentCount = s.commentCount - 1 WHERE s.articleId = :articleId AND s.commentCount > 0")
    void decrementCommentCount(@Param("articleId") Long articleId);
    
    // 增加分享数
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.shareCount = s.shareCount + 1 WHERE s.articleId = :articleId")
    void incrementShareCount(@Param("articleId") Long articleId);
    
    // 增加收藏数
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.collectCount = s.collectCount + 1 WHERE s.articleId = :articleId")
    void incrementCollectCount(@Param("articleId") Long articleId);
    
    // 减少收藏数
    @Modifying
    @Transactional
    @Query("UPDATE ArticleStats s SET s.collectCount = s.collectCount - 1 WHERE s.articleId = :articleId AND s.collectCount > 0")
    void decrementCollectCount(@Param("articleId") Long articleId);
    
    // 获取总浏览量
    @Query("SELECT SUM(s.viewCount) FROM ArticleStats s")
    Long getTotalViewCount();
    
    // 获取总点赞数
    @Query("SELECT SUM(s.likeCount) FROM ArticleStats s")
    Long getTotalLikeCount();
    
    // 获取总评论数
    @Query("SELECT SUM(s.commentCount) FROM ArticleStats s")
    Long getTotalCommentCount();
    
    // 获取最受欢迎的文章（综合统计）
    @Query("SELECT s.articleId, (s.viewCount + s.likeCount * 5 + s.commentCount * 3 + s.shareCount * 2 + s.collectCount * 4) as score " +
           "FROM ArticleStats s ORDER BY score DESC")
    List<Object[]> getMostPopularArticles();
    
    // 获取最近活跃的文章（基于最后浏览时间）
    List<ArticleStats> findAllByOrderByLastViewAtDesc();
    
    // 根据时间范围获取浏览统计
    @Query("SELECT s.articleId, s.viewCount FROM ArticleStats s WHERE s.lastViewAt BETWEEN :startDate AND :endDate")
    List<Object[]> getViewStatsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 获取文章统计概览
    @Query("SELECT " +
           "COUNT(s) as totalArticles, " +
           "SUM(s.viewCount) as totalViews, " +
           "SUM(s.likeCount) as totalLikes, " +
           "SUM(s.commentCount) as totalComments, " +
           "AVG(s.viewCount) as avgViews " +
           "FROM ArticleStats s")
    Object[] getStatsOverview();
} 