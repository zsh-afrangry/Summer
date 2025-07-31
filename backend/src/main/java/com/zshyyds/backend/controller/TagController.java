package com.zshyyds.backend.controller;

import com.zshyyds.backend.common.ApiResponse;
import com.zshyyds.backend.entity.Tag;
import com.zshyyds.backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {
    
    private final TagService tagService;
    
    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getTagById(@PathVariable Long id) {
        try {
            Optional<Tag> tag = tagService.getTagById(id);
            if (tag.isPresent()) {
                return ApiResponse.success(tag.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/name/{name}")
    public Map<String, Object> getTagByName(@PathVariable String name) {
        try {
            Optional<Tag> tag = tagService.getTagByName(name);
            if (tag.isPresent()) {
                return ApiResponse.success(tag.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping
    public Map<String, Object> createTag(@RequestBody Tag tag) {
        try {
            String result = tagService.saveOrUpdateTag(tag);
            
            if ("success".equals(result)) {
                return ApiResponse.success("标签创建成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        try {
            tag.setId(id);
            String result = tagService.saveOrUpdateTag(tag);
            
            if ("success".equals(result)) {
                return ApiResponse.success("标签更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteTag(@PathVariable Long id) {
        try {
            String result = tagService.deleteTag(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("标签删除成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping
    public Map<String, Object> getAllTags() {
        try {
            List<Tag> tags = tagService.getAllTags();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/ordered/name")
    public Map<String, Object> getAllTagsOrderByName() {
        try {
            List<Tag> tags = tagService.getAllTagsOrderByName();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/ordered/sort")
    public Map<String, Object> getAllTagsOrderBySortOrder() {
        try {
            List<Tag> tags = tagService.getAllTagsOrderBySortOrder();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/popular")
    public Map<String, Object> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Tag> tags = tagService.getPopularTags(limit);
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/article-count/{minCount}")
    public Map<String, Object> getTagsByArticleCount(@PathVariable Integer minCount) {
        try {
            List<Tag> tags = tagService.getTagsByArticleCount(minCount);
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/search")
    public Map<String, Object> searchTags(@RequestParam String keyword) {
        try {
            List<Tag> tags = tagService.searchTags(keyword);
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/article/{articleId}")
    public Map<String, Object> getTagsByArticleId(@PathVariable Long articleId) {
        try {
            List<Tag> tags = tagService.getTagsByArticleId(articleId);
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/cloud")
    public Map<String, Object> getTagCloudData() {
        try {
            List<Map<String, Object>> tagCloud = tagService.getTagCloudData();
            return ApiResponse.success(tagCloud);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}/article-count")
    public Map<String, Object> updateTagArticleCount(@PathVariable Long id) {
        try {
            String result = tagService.updateTagArticleCount(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("文章计数更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/article-count/all")
    public Map<String, Object> updateAllTagArticleCounts() {
        try {
            String result = tagService.updateAllTagArticleCounts();
            
            if ("success".equals(result)) {
                return ApiResponse.success("批量更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/check/name")
    public Map<String, Object> checkTagName(@RequestParam String name, 
                                          @RequestParam(required = false) Long excludeId) {
        try {
            boolean available = tagService.isTagNameAvailable(name, excludeId);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count")
    public Map<String, Object> countTags() {
        try {
            Long count = tagService.countTags();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count/with-articles")
    public Map<String, Object> countTagsWithArticles() {
        try {
            Long count = tagService.countTagsWithArticles();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/unused")
    public Map<String, Object> getUnusedTags() {
        try {
            List<Tag> tags = tagService.getUnusedTags();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/unused")
    public Map<String, Object> deleteUnusedTags() {
        try {
            String result = tagService.deleteUnusedTags();
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/color/{color}")
    public Map<String, Object> getTagsByColor(@PathVariable String color) {
        try {
            List<Tag> tags = tagService.getTagsByColor(color);
            return ApiResponse.success(tags);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping("/create-if-not-exists")
    public Map<String, Object> createTagIfNotExists(@RequestParam String name,
                                                   @RequestParam(defaultValue = "#667eea") String color,
                                                   @RequestParam(required = false) String description) {
        try {
            Optional<Tag> tag = tagService.createTagIfNotExists(name, color, description);
            if (tag.isPresent()) {
                return ApiResponse.success("标签获取成功", tag.get());
            } else {
                return ApiResponse.error("标签创建失败");
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
} 