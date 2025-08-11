package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Article;
import com.zshyyds.backend.entity.ArticleStat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleStatRepository extends JpaRepository<ArticleStat, Long> {
    
    /**
     * 根据文章ID查找统计信息
     */
    Optional<ArticleStat> findByArticleId(Long articleId);
    
    /**
     * 根据文章对象查找统计信息
     */
    Optional<ArticleStat> findByArticle(Article article);
    
    /**
     * 增加文章浏览量
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.viewCount = s.viewCount + 1, s.lastViewAt = :viewTime WHERE s.article.id = :articleId")
    void incrementViewCount(@Param("articleId") Long articleId, @Param("viewTime") Instant viewTime);
    
    /**
     * 增加文章点赞数
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.likeCount = s.likeCount + 1 WHERE s.article.id = :articleId")
    void incrementLikeCount(@Param("articleId") Long articleId);
    
    /**
     * 减少文章点赞数
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.likeCount = s.likeCount - 1 WHERE s.article.id = :articleId AND s.likeCount > 0")
    void decrementLikeCount(@Param("articleId") Long articleId);
    
    /**
     * 增加评论数
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.commentCount = s.commentCount + 1 WHERE s.article.id = :articleId")
    void incrementCommentCount(@Param("articleId") Long articleId);
    
    /**
     * 减少评论数
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.commentCount = s.commentCount - 1 WHERE s.article.id = :articleId AND s.commentCount > 0")
    void decrementCommentCount(@Param("articleId") Long articleId);
    
    /**
     * 增加分享数
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.shareCount = s.shareCount + 1 WHERE s.article.id = :articleId")
    void incrementShareCount(@Param("articleId") Long articleId);
    
    /**
     * 增加收藏数
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.collectCount = s.collectCount + 1 WHERE s.article.id = :articleId")
    void incrementCollectCount(@Param("articleId") Long articleId);
    
    /**
     * 减少收藏数
     */
    @Modifying
    @Query("UPDATE ArticleStat s SET s.collectCount = s.collectCount - 1 WHERE s.article.id = :articleId AND s.collectCount > 0")
    void decrementCollectCount(@Param("articleId") Long articleId);
    
    /**
     * 获取浏览量最高的文章
     */
    @Query("SELECT s FROM ArticleStat s ORDER BY s.viewCount DESC")
    List<ArticleStat> findTopByViewCount(Pageable pageable);
    
    /**
     * 获取点赞数最高的文章
     */
    @Query("SELECT s FROM ArticleStat s ORDER BY s.likeCount DESC")
    List<ArticleStat> findTopByLikeCount(Pageable pageable);
    
    /**
     * 获取评论数最高的文章
     */
    @Query("SELECT s FROM ArticleStat s ORDER BY s.commentCount DESC")
    List<ArticleStat> findTopByCommentCount(Pageable pageable);
    
    /**
     * 根据浏览量范围查找
     */
    List<ArticleStat> findByViewCountBetweenOrderByViewCountDesc(Integer minViews, Integer maxViews);
    
    /**
     * 根据点赞数范围查找
     */
    List<ArticleStat> findByLikeCountBetweenOrderByLikeCountDesc(Integer minLikes, Integer maxLikes);
    
    /**
     * 获取总浏览量
     */
    @Query("SELECT SUM(s.viewCount) FROM ArticleStat s")
    Long getTotalViewCount();
    
    /**
     * 获取总点赞数
     */
    @Query("SELECT SUM(s.likeCount) FROM ArticleStat s")
    Long getTotalLikeCount();
    
    /**
     * 获取总评论数
     */
    @Query("SELECT SUM(s.commentCount) FROM ArticleStat s")
    Long getTotalCommentCount();
    
    /**
     * 根据作者获取统计汇总
     */
    @Query("SELECT SUM(s.viewCount), SUM(s.likeCount), SUM(s.commentCount) FROM ArticleStat s WHERE s.article.author.id = :authorId")
    List<Object[]> getStatsSummaryByAuthor(@Param("authorId") Long authorId);
    
    /**
     * 获取最近更新的统计
     */
    @Query("SELECT s FROM ArticleStat s WHERE s.lastViewAt >= :since ORDER BY s.lastViewAt DESC")
    List<ArticleStat> findRecentlyViewed(@Param("since") Instant since);
    
    /**
     * 获取平均浏览量
     */
    @Query("SELECT AVG(s.viewCount) FROM ArticleStat s")
    Double getAverageViewCount();
    
    /**
     * 获取平均点赞数
     */
    @Query("SELECT AVG(s.likeCount) FROM ArticleStat s")
    Double getAverageLikeCount();
    
    /**
     * 删除文章统计
     */
    void deleteByArticleId(Long articleId);
}