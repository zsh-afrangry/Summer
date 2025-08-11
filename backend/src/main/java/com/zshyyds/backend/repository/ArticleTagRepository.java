package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Article;
import com.zshyyds.backend.entity.ArticleTag;
import com.zshyyds.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    
    /**
     * 根据文章ID查找所有标签关联
     */
    List<ArticleTag> findByArticleId(Long articleId);
    
    /**
     * 根据文章对象查找所有标签关联
     */
    List<ArticleTag> findByArticle(Article article);
    
    /**
     * 根据标签ID查找所有文章关联
     */
    List<ArticleTag> findByTagId(Long tagId);
    
    /**
     * 根据标签对象查找所有文章关联
     */
    List<ArticleTag> findByTag(Tag tag);
    
    /**
     * 查找特定文章和标签的关联
     */
    Optional<ArticleTag> findByArticleAndTag(Article article, Tag tag);
    
    /**
     * 根据文章ID和标签ID查找关联
     */
    Optional<ArticleTag> findByArticleIdAndTagId(Long articleId, Long tagId);
    
    /**
     * 检查文章和标签是否已关联
     */
    boolean existsByArticleAndTag(Article article, Tag tag);
    
    /**
     * 根据文章ID和标签ID检查关联
     */
    boolean existsByArticleIdAndTagId(Long articleId, Long tagId);
    
    /**
     * 删除文章的所有标签关联
     */
    void deleteByArticleId(Long articleId);
    
    /**
     * 删除文章对象的所有标签关联
     */
    void deleteByArticle(Article article);
    
    /**
     * 删除标签的所有文章关联
     */
    void deleteByTagId(Long tagId);
    
    /**
     * 删除标签对象的所有文章关联
     */
    void deleteByTag(Tag tag);
    
    /**
     * 删除特定的文章标签关联
     */
    void deleteByArticleIdAndTagId(Long articleId, Long tagId);
    
    /**
     * 统计文章的标签数量
     */
    @Query("SELECT COUNT(at) FROM ArticleTag at WHERE at.article.id = :articleId")
    long countByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 统计标签的文章数量
     */
    @Query("SELECT COUNT(at) FROM ArticleTag at WHERE at.tag.id = :tagId")
    long countByTagId(@Param("tagId") Long tagId);
    
    /**
     * 获取文章的所有标签（只返回标签对象）
     */
    @Query("SELECT at.tag FROM ArticleTag at WHERE at.article.id = :articleId")
    List<Tag> findTagsByArticleId(@Param("articleId") Long articleId);
    
    /**
     * 获取标签的所有文章（只返回文章对象）
     */
    @Query("SELECT at.article FROM ArticleTag at WHERE at.tag.id = :tagId")
    List<Article> findArticlesByTagId(@Param("tagId") Long tagId);
    
    /**
     * 获取文章标签关联统计
     */
    @Query("SELECT at.tag.name, COUNT(at) FROM ArticleTag at GROUP BY at.tag.id, at.tag.name ORDER BY COUNT(at) DESC")
    List<Object[]> getArticleTagStats();
    
    /**
     * 获取最常用的标签（根据关联文章数）
     */
    @Query("SELECT at.tag FROM ArticleTag at GROUP BY at.tag HAVING COUNT(at) >= :minCount ORDER BY COUNT(at) DESC")
    List<Tag> findPopularTags(@Param("minCount") Long minCount);
    
    /**
     * 获取文章的相关标签（通过其他文章的共同标签）
     */
    @Query("SELECT DISTINCT at2.tag FROM ArticleTag at1 " +
           "JOIN ArticleTag at2 ON at1.tag = at2.tag " +
           "WHERE at1.article.id = :articleId AND at2.article.id != :articleId")
    List<Tag> findRelatedTagsByArticle(@Param("articleId") Long articleId);
    
    /**
     * 获取标签的相关文章（通过共同标签）
     */
    @Query("SELECT DISTINCT at2.article FROM ArticleTag at1 " +
           "JOIN ArticleTag at2 ON at1.tag = at2.tag " +
           "WHERE at1.tag.id = :tagId AND at2.article.id != at1.article.id")
    List<Article> findRelatedArticlesByTag(@Param("tagId") Long tagId);
    
    /**
     * 批量创建文章标签关联
     */
    @Modifying
    @Query("INSERT INTO ArticleTag (article, tag) VALUES (:article, :tag)")
    void createArticleTagAssociation(@Param("article") Article article, @Param("tag") Tag tag);
}