package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SkillService {
    
    /**
     * 根据用户ID获取技能列表
     * @param userId 用户ID
     * @return 技能列表
     */
    List<Skill> getSkillsByUserId(Long userId);
    
    /**
     * 根据用户ID和分类获取技能
     * @param userId 用户ID
     * @param category 分类
     * @return 技能列表
     */
    List<Skill> getSkillsByUserIdAndCategory(Long userId, String category);
    
    /**
     * 保存或更新技能
     * @param skill 技能信息
     * @return 操作结果
     */
    String saveOrUpdateSkill(Skill skill);
    
    /**
     * 批量保存技能
     * @param skills 技能列表
     * @return 操作结果
     */
    String batchSaveSkills(List<Skill> skills);
    
    /**
     * 根据ID删除技能
     * @param id 技能ID
     * @return 删除结果
     */
    String deleteSkill(Long id);
    
    /**
     * 根据用户ID删除所有技能
     * @param userId 用户ID
     * @return 删除结果
     */
    String deleteSkillsByUserId(Long userId);
    
    /**
     * 获取用户的技能分类列表
     * @param userId 用户ID
     * @return 分类列表
     */
    List<String> getSkillCategoriesByUserId(Long userId);
    
    /**
     * 获取所有技能分类
     * @return 分类列表
     */
    List<String> getAllSkillCategories();
    
    /**
     * 获取用户技能平均等级
     * @param userId 用户ID
     * @return 平均等级
     */
    Double getAverageSkillLevel(Long userId);
    
    /**
     * 获取用户最高技能等级
     * @param userId 用户ID
     * @return 最高等级
     */
    Integer getMaxSkillLevel(Long userId);
    
    /**
     * 根据分类统计用户技能数量
     * @param userId 用户ID
     * @return 分类统计Map
     */
    Map<String, Long> getSkillCountByCategory(Long userId);
    
    /**
     * 检查用户是否已有该技能
     * @param userId 用户ID
     * @param skillName 技能名称
     * @return 是否存在
     */
    boolean hasSkill(Long userId, String skillName);
    
    /**
     * 获取热门技能统计
     * @param limit 限制数量
     * @return 热门技能列表
     */
    Map<String, Long> getPopularSkills(int limit);
    
    /**
     * 根据技能等级范围查找技能
     * @param userId 用户ID
     * @param minLevel 最小等级
     * @param maxLevel 最大等级
     * @return 技能列表
     */
    List<Skill> getSkillsByLevelRange(Long userId, Integer minLevel, Integer maxLevel);
    
    /**
     * 更新技能排序
     * @param skillIds 技能ID列表（按新顺序排列）
     * @return 更新结果
     */
    String updateSkillOrder(List<Long> skillIds);
    
    /**
     * 根据技能名称搜索
     * @param skillName 技能名称
     * @return 技能列表
     */
    List<Skill> searchSkillsByName(String skillName);
} 