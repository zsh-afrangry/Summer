package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TagService {
    
    /**
     * 根据ID获取标签
     * @param id 标签ID
     * @return 标签信息
     */
    Optional<Tag> getTagById(Long id);
    
    /**
     * 根据名称获取标签
     * @param name 标签名称
     * @return 标签信息
     */
    Optional<Tag> getTagByName(String name);
    
    /**
     * 保存或更新标签
     * @param tag 标签信息
     * @return 操作结果
     */
    String saveOrUpdateTag(Tag tag);
    
    /**
     * 删除标签
     * @param id 标签ID
     * @return 删除结果
     */
    String deleteTag(Long id);
    
    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<Tag> getAllTags();
    
    /**
     * 获取所有标签（按名称排序）
     * @return 标签列表
     */
    List<Tag> getAllTagsOrderByName();
    
    /**
     * 获取所有标签（按排序顺序）
     * @return 标签列表
     */
    List<Tag> getAllTagsOrderBySortOrder();
    
    /**
     * 获取热门标签
     * @param limit 限制数量
     * @return 热门标签列表
     */
    List<Tag> getPopularTags(int limit);
    
    /**
     * 根据文章数量获取标签
     * @param minCount 最小文章数
     * @return 标签列表
     */
    List<Tag> getTagsByArticleCount(Integer minCount);
    
    /**
     * 搜索标签
     * @param keyword 关键词
     * @return 搜索结果
     */
    List<Tag> searchTags(String keyword);
    
    /**
     * 根据文章ID获取标签
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> getTagsByArticleId(Long articleId);
    
    /**
     * 获取标签云数据
     * @return 标签云数据
     */
    List<Map<String, Object>> getTagCloudData();
    
    /**
     * 更新标签的文章数量
     * @param tagId 标签ID
     * @return 更新结果
     */
    String updateTagArticleCount(Long tagId);
    
    /**
     * 批量更新所有标签的文章数量
     * @return 更新结果
     */
    String updateAllTagArticleCounts();
    
    /**
     * 检查标签名是否可用
     * @param name 标签名
     * @param excludeId 排除的标签ID（用于编辑时）
     * @return 是否可用
     */
    boolean isTagNameAvailable(String name, Long excludeId);
    
    /**
     * 统计标签总数
     * @return 标签数量
     */
    Long countTags();
    
    /**
     * 统计有文章的标签数量
     * @return 标签数量
     */
    Long countTagsWithArticles();
    
    /**
     * 获取未使用的标签
     * @return 未使用的标签列表
     */
    List<Tag> getUnusedTags();
    
    /**
     * 批量删除未使用的标签
     * @return 删除结果
     */
    String deleteUnusedTags();
    
    /**
     * 根据颜色获取标签
     * @param color 颜色
     * @return 标签列表
     */
    List<Tag> getTagsByColor(String color);
    
    /**
     * 创建标签（如果不存在）
     * @param name 标签名
     * @param color 标签颜色
     * @param description 标签描述
     * @return 标签信息或错误消息
     */
    Optional<Tag> createTagIfNotExists(String name, String color, String description);
} 