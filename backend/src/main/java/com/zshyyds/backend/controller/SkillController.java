package com.zshyyds.backend.controller;

import com.zshyyds.backend.common.ApiResponse;
import com.zshyyds.backend.entity.Skill;
import com.zshyyds.backend.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillController {
    
    private final SkillService skillService;
    
    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }
    
    @GetMapping("/user/{userId}")
    public Map<String, Object> getSkillsByUserId(@PathVariable Long userId) {
        try {
            List<Skill> skills = skillService.getSkillsByUserId(userId);
            return ApiResponse.success(skills);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/user/{userId}/category/{category}")
    public Map<String, Object> getSkillsByUserIdAndCategory(@PathVariable Long userId, 
                                                           @PathVariable String category) {
        try {
            List<Skill> skills = skillService.getSkillsByUserIdAndCategory(userId, category);
            return ApiResponse.success(skills);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping
    public Map<String, Object> createSkill(@RequestBody Skill skill) {
        try {
            String result = skillService.saveOrUpdateSkill(skill);
            
            if ("success".equals(result)) {
                return ApiResponse.success("技能创建成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        try {
            skill.setId(id);
            String result = skillService.saveOrUpdateSkill(skill);
            
            if ("success".equals(result)) {
                return ApiResponse.success("技能更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping("/batch")
    public Map<String, Object> batchSaveSkills(@RequestBody List<Skill> skills) {
        try {
            String result = skillService.batchSaveSkills(skills);
            
            if ("success".equals(result)) {
                return ApiResponse.success("批量保存成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteSkill(@PathVariable Long id) {
        try {
            String result = skillService.deleteSkill(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("技能删除成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/user/{userId}")
    public Map<String, Object> deleteSkillsByUserId(@PathVariable Long userId) {
        try {
            String result = skillService.deleteSkillsByUserId(userId);
            
            if ("success".equals(result)) {
                return ApiResponse.success("用户技能删除成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/user/{userId}/categories")
    public Map<String, Object> getSkillCategoriesByUserId(@PathVariable Long userId) {
        try {
            List<String> categories = skillService.getSkillCategoriesByUserId(userId);
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/categories")
    public Map<String, Object> getAllSkillCategories() {
        try {
            List<String> categories = skillService.getAllSkillCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/user/{userId}/average-level")
    public Map<String, Object> getAverageSkillLevel(@PathVariable Long userId) {
        try {
            Double averageLevel = skillService.getAverageSkillLevel(userId);
            return ApiResponse.success(averageLevel);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/user/{userId}/max-level")
    public Map<String, Object> getMaxSkillLevel(@PathVariable Long userId) {
        try {
            Integer maxLevel = skillService.getMaxSkillLevel(userId);
            return ApiResponse.success(maxLevel);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/user/{userId}/count-by-category")
    public Map<String, Object> getSkillCountByCategory(@PathVariable Long userId) {
        try {
            Map<String, Long> categoryCount = skillService.getSkillCountByCategory(userId);
            return ApiResponse.success(categoryCount);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/user/{userId}/has-skill")
    public Map<String, Object> hasSkill(@PathVariable Long userId, @RequestParam String skillName) {
        try {
            boolean hasSkill = skillService.hasSkill(userId, skillName);
            return ApiResponse.success("检查完成", hasSkill);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/popular")
    public Map<String, Object> getPopularSkills(@RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Long> popularSkills = skillService.getPopularSkills(limit);
            return ApiResponse.success(popularSkills);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/user/{userId}/level-range")
    public Map<String, Object> getSkillsByLevelRange(@PathVariable Long userId,
                                                    @RequestParam Integer minLevel,
                                                    @RequestParam Integer maxLevel) {
        try {
            List<Skill> skills = skillService.getSkillsByLevelRange(userId, minLevel, maxLevel);
            return ApiResponse.success(skills);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/order")
    public Map<String, Object> updateSkillOrder(@RequestBody List<Long> skillIds) {
        try {
            String result = skillService.updateSkillOrder(skillIds);
            
            if ("success".equals(result)) {
                return ApiResponse.success("排序更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/search")
    public Map<String, Object> searchSkillsByName(@RequestParam String skillName) {
        try {
            List<Skill> skills = skillService.searchSkillsByName(skillName);
            return ApiResponse.success(skills);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
} 