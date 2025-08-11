package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    /**
     * 根据标签名查找标签
     */
    Optional<Tag> findByName(String name);
    
    /**
     * 检查标签名是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据标签名模糊查询
     */
    List<Tag> findByNameContainingIgnoreCase(String name);
    
    /**
     * 按文章数量排序获取热门标签
     */
    List<Tag> findByOrderByArticleCountDesc();
    
    /**
     * 获取热门标签（限制数量）
     */
    @Query("SELECT t FROM Tag t ORDER BY t.articleCount DESC")
    List<Tag> findPopularTags(Pageable pageable);
    
    /**
     * 按排序顺序获取标签
     */
    List<Tag> findByOrderBySortOrderAsc();
    
    /**
     * 获取有文章的标签
     */
    @Query("SELECT t FROM Tag t WHERE t.articleCount > 0 ORDER BY t.articleCount DESC")
    List<Tag> findTagsWithArticles();
    
    /**
     * 获取指定文章数量范围的标签
     */
    List<Tag> findByArticleCountBetweenOrderByArticleCountDesc(Integer minCount, Integer maxCount);
    
    /**
     * 更新标签的文章数量
     */
    @Modifying
    @Query("UPDATE Tag t SET t.articleCount = (SELECT COUNT(at) FROM ArticleTag at WHERE at.tag.id = t.id)")
    void updateAllArticleCounts();
    
    /**
     * 更新指定标签的文章数量
     */
    @Modifying
    @Query("UPDATE Tag t SET t.articleCount = (SELECT COUNT(at) FROM ArticleTag at WHERE at.tag.id = :tagId) WHERE t.id = :tagId")
    void updateArticleCount(@Param("tagId") Long tagId);
    
    /**
     * 获取标签总数
     */
    @Query("SELECT COUNT(t) FROM Tag t")
    long countAllTags();
    
    /**
     * 获取有文章的标签总数
     */
    @Query("SELECT COUNT(t) FROM Tag t WHERE t.articleCount > 0")
    long countTagsWithArticles();
    
    /**
     * 根据颜色查找标签
     */
    List<Tag> findByColor(String color);
    
    /**
     * 获取标签云数据（名称和文章数量）
     */
    @Query("SELECT t.name, t.articleCount FROM Tag t WHERE t.articleCount > 0 ORDER BY t.articleCount DESC")
    List<Object[]> getTagCloudData();
}