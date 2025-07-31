package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Skill;
import com.zshyyds.backend.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {
    
    private final SkillRepository skillRepository;
    
    @Autowired
    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }
    
    @Override
    public List<Skill> getSkillsByUserId(Long userId) {
        return skillRepository.findByUserIdOrderBySortOrderAsc(userId);
    }
    
    @Override
    public List<Skill> getSkillsByUserIdAndCategory(Long userId, String category) {
        return skillRepository.findByUserIdAndCategory(userId, category);
    }
    
    @Override
    public String saveOrUpdateSkill(Skill skill) {
        if (skill.getUserId() == null) {
            return "用户ID不能为空";
        }
        
        if (skill.getName() == null || skill.getName().trim().isEmpty()) {
            return "技能名称不能为空";
        }
        
        if (skill.getLevel() == null || skill.getLevel() < 0 || skill.getLevel() > 100) {
            return "技能等级必须在0-100之间";
        }
        
        try {
            // 检查是否已存在相同技能
            if (skill.getId() == null) {
                Optional<Skill> existingSkill = skillRepository.findByUserIdAndName(skill.getUserId(), skill.getName());
                if (existingSkill.isPresent()) {
                    return "该技能已存在";
                }
            }
            
            skillRepository.save(skill);
            return "success";
        } catch (Exception e) {
            return "保存失败，请重试";
        }
    }
    
    @Override
    @Transactional
    public String batchSaveSkills(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return "技能列表不能为空";
        }
        
        try {
            for (Skill skill : skills) {
                String result = saveOrUpdateSkill(skill);
                if (!"success".equals(result)) {
                    return "批量保存失败：" + result;
                }
            }
            return "success";
        } catch (Exception e) {
            return "批量保存失败";
        }
    }
    
    @Override
    public String deleteSkill(Long id) {
        Optional<Skill> skillOptional = skillRepository.findById(id);
        if (!skillOptional.isPresent()) {
            return "技能不存在";
        }
        
        try {
            skillRepository.deleteById(id);
            return "success";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    @Override
    @Transactional
    public String deleteSkillsByUserId(Long userId) {
        try {
            List<Skill> userSkills = skillRepository.findByUserId(userId);
            if (!userSkills.isEmpty()) {
                skillRepository.deleteAll(userSkills);
            }
            return "success";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    @Override
    public List<String> getSkillCategoriesByUserId(Long userId) {
        return skillRepository.findCategoriesByUserId(userId);
    }
    
    @Override
    public List<String> getAllSkillCategories() {
        return skillRepository.findAllCategories();
    }
    
    @Override
    public Double getAverageSkillLevel(Long userId) {
        Double average = skillRepository.getAverageSkillLevelByUserId(userId);
        return average != null ? average : 0.0;
    }
    
    @Override
    public Integer getMaxSkillLevel(Long userId) {
        Integer maxLevel = skillRepository.getMaxSkillLevelByUserId(userId);
        return maxLevel != null ? maxLevel : 0;
    }
    
    @Override
    public Map<String, Long> getSkillCountByCategory(Long userId) {
        List<Object[]> results = skillRepository.countSkillsByCategory(userId);
        Map<String, Long> categoryCountMap = new HashMap<>();
        
        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = (Long) result[1];
            categoryCountMap.put(category, count);
        }
        
        return categoryCountMap;
    }
    
    @Override
    public boolean hasSkill(Long userId, String skillName) {
        return skillRepository.existsByUserIdAndName(userId, skillName);
    }
    
    @Override
    public Map<String, Long> getPopularSkills(int limit) {
        List<Object[]> results = skillRepository.findPopularSkills();
        Map<String, Long> popularSkillsMap = new HashMap<>();
        
        int count = 0;
        for (Object[] result : results) {
            if (count >= limit) break;
            
            String skillName = (String) result[0];
            Long skillCount = (Long) result[1];
            popularSkillsMap.put(skillName, skillCount);
            count++;
        }
        
        return popularSkillsMap;
    }
    
    @Override
    public List<Skill> getSkillsByLevelRange(Long userId, Integer minLevel, Integer maxLevel) {
        return skillRepository.findByUserIdAndLevelBetween(userId, minLevel, maxLevel);
    }
    
    @Override
    @Transactional
    public String updateSkillOrder(List<Long> skillIds) {
        try {
            for (int i = 0; i < skillIds.size(); i++) {
                Optional<Skill> skillOptional = skillRepository.findById(skillIds.get(i));
                if (skillOptional.isPresent()) {
                    Skill skill = skillOptional.get();
                    skill.setSortOrder(i + 1);
                    skillRepository.save(skill);
                }
            }
            return "success";
        } catch (Exception e) {
            return "排序更新失败";
        }
    }
    
    @Override
    public List<Skill> searchSkillsByName(String skillName) {
        return skillRepository.findByName(skillName);
    }
} 