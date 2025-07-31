package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Tag;
import com.zshyyds.backend.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {
    
    private final TagRepository tagRepository;
    
    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    
    @Override
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }
    
    @Override
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }
    
    @Override
    public String saveOrUpdateTag(Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            return "标签名称不能为空";
        }
        
        if (tag.getName().length() > 50) {
            return "标签名称长度不能超过50字符";
        }
        
        // 检查标签名唯一性
        if (!isTagNameAvailable(tag.getName(), tag.getId())) {
            return "标签名称已存在";
        }
        
        try {
            // 设置默认颜色
            if (tag.getColor() == null || tag.getColor().trim().isEmpty()) {
                tag.setColor("#667eea");
            }
            
            tagRepository.save(tag);
            return "success";
        } catch (Exception e) {
            return "保存失败，请重试";
        }
    }
    
    @Override
    public String deleteTag(Long id) {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (!tagOptional.isPresent()) {
            return "标签不存在";
        }
        
        Tag tag = tagOptional.get();
        if (tag.getArticleCount() > 0) {
            return "该标签下还有文章，无法删除";
        }
        
        try {
            tagRepository.deleteById(id);
            return "success";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
    
    @Override
    public List<Tag> getAllTagsOrderByName() {
        return tagRepository.findAllByOrderByNameAsc();
    }
    
    @Override
    public List<Tag> getAllTagsOrderBySortOrder() {
        return tagRepository.findAllByOrderBySortOrderAsc();
    }
    
    @Override
    public List<Tag> getPopularTags(int limit) {
        List<Tag> tags = tagRepository.findPopularTags();
        return tags.size() > limit ? tags.subList(0, limit) : tags;
    }
    
    @Override
    public List<Tag> getTagsByArticleCount(Integer minCount) {
        return tagRepository.findByArticleCountGreaterThanOrderByArticleCountDesc(minCount);
    }
    
    @Override
    public List<Tag> searchTags(String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        return tagRepository.findTagsByArticleId(articleId);
    }
    
    @Override
    public List<Map<String, Object>> getTagCloudData() {
        List<Object[]> results = tagRepository.getTagCloudData();
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", result[0]);
                    map.put("count", result[1]);
                    map.put("color", result[2]);
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    @Transactional
    public String updateTagArticleCount(Long tagId) {
        try {
            tagRepository.updateArticleCount(tagId);
            return "success";
        } catch (Exception e) {
            return "更新失败";
        }
    }
    
    @Override
    @Transactional
    public String updateAllTagArticleCounts() {
        try {
            tagRepository.updateAllArticleCounts();
            return "success";
        } catch (Exception e) {
            return "批量更新失败";
        }
    }
    
    @Override
    public boolean isTagNameAvailable(String name, Long excludeId) {
        Optional<Tag> existingTag = tagRepository.findByName(name);
        if (!existingTag.isPresent()) {
            return true;
        }
        
        // 如果是编辑模式，排除自己
        return excludeId != null && existingTag.get().getId().equals(excludeId);
    }
    
    @Override
    public Long countTags() {
        return tagRepository.countTags();
    }
    
    @Override
    public Long countTagsWithArticles() {
        return tagRepository.countTagsWithArticles();
    }
    
    @Override
    public List<Tag> getUnusedTags() {
        return tagRepository.findUnusedTags();
    }
    
    @Override
    @Transactional
    public String deleteUnusedTags() {
        try {
            List<Tag> unusedTags = getUnusedTags();
            if (!unusedTags.isEmpty()) {
                tagRepository.deleteAll(unusedTags);
                return "已删除 " + unusedTags.size() + " 个未使用的标签";
            }
            return "没有未使用的标签";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    @Override
    public List<Tag> getTagsByColor(String color) {
        return tagRepository.findByColor(color);
    }
    
    @Override
    public Optional<Tag> createTagIfNotExists(String name, String color, String description) {
        Optional<Tag> existingTag = tagRepository.findByName(name);
        if (existingTag.isPresent()) {
            return existingTag;
        }
        
        try {
            Tag newTag = new Tag(name, color, description);
            Tag savedTag = tagRepository.save(newTag);
            return Optional.of(savedTag);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 