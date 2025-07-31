package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // 根据标签名查找
    Optional<Tag> findByName(String name);
    
    // 检查标签名是否存在
    boolean existsByName(String name);
    
    // 根据文章数量查找标签（热门标签）
    List<Tag> findByArticleCountGreaterThanOrderByArticleCountDesc(Integer count);
    
    // 获取所有标签按文章数量排序
    List<Tag> findAllByOrderByArticleCountDesc();
    
    // 获取所有标签按排序顺序排列
    List<Tag> findAllByOrderBySortOrderAsc();
    
    // 获取所有标签按名称排序
    List<Tag> findAllByOrderByNameAsc();
    
    // 根据颜色查找标签
    List<Tag> findByColor(String color);
    
    // 根据名称模糊查询
    List<Tag> findByNameContainingIgnoreCase(String name);
    
    // 获取热门标签（文章数量大于0）
    @Query("SELECT t FROM Tag t WHERE t.articleCount > 0 ORDER BY t.articleCount DESC")
    List<Tag> findPopularTags();
    
    // 获取指定数量的热门标签
    @Query("SELECT t FROM Tag t WHERE t.articleCount > 0 ORDER BY t.articleCount DESC")
    List<Tag> findTopPopularTags(@Param("limit") int limit);
    
    // 根据文章ID查找相关标签
    @Query("SELECT t FROM Tag t JOIN ArticleTag at ON t.id = at.tagId WHERE at.articleId = :articleId")
    List<Tag> findTagsByArticleId(@Param("articleId") Long articleId);
    
    // 获取标签云数据（名称和文章数量）
    @Query("SELECT t.name, t.articleCount, t.color FROM Tag t WHERE t.articleCount > 0 ORDER BY t.articleCount DESC")
    List<Object[]> getTagCloudData();
    
    // 更新标签的文章数量
    @Modifying
    @Transactional
    @Query("UPDATE Tag t SET t.articleCount = (SELECT COUNT(at) FROM ArticleTag at WHERE at.tagId = t.id) WHERE t.id = :tagId")
    void updateArticleCount(@Param("tagId") Long tagId);
    
    // 批量更新所有标签的文章数量
    @Modifying
    @Transactional
    @Query("UPDATE Tag t SET t.articleCount = (SELECT COUNT(at) FROM ArticleTag at WHERE at.tagId = t.id)")
    void updateAllArticleCounts();
    
    // 统计标签总数
    @Query("SELECT COUNT(t) FROM Tag t")
    Long countTags();
    
    // 统计有文章的标签数量
    @Query("SELECT COUNT(t) FROM Tag t WHERE t.articleCount > 0")
    Long countTagsWithArticles();
    
    // 查找未使用的标签
    @Query("SELECT t FROM Tag t WHERE t.articleCount = 0")
    List<Tag> findUnusedTags();
} 