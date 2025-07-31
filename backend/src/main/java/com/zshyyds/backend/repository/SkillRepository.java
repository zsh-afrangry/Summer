package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    // 根据用户ID查找技能
    List<Skill> findByUserId(Long userId);
    
    // 根据用户ID和分类查找技能
    List<Skill> findByUserIdAndCategory(Long userId, String category);
    
    // 根据用户ID查找技能并按排序顺序排列
    List<Skill> findByUserIdOrderBySortOrderAsc(Long userId);
    
    // 根据分类查找技能
    List<Skill> findByCategory(String category);
    
    // 根据技能名称查找
    List<Skill> findByName(String name);
    
    // 根据用户ID和技能名称查找
    Optional<Skill> findByUserIdAndName(Long userId, String name);
    
    // 根据技能等级范围查找
    List<Skill> findByLevelBetween(Integer minLevel, Integer maxLevel);
    
    // 根据用户ID和技能等级范围查找
    List<Skill> findByUserIdAndLevelBetween(Long userId, Integer minLevel, Integer maxLevel);
    
    // 获取用户的所有技能分类
    @Query("SELECT DISTINCT s.category FROM Skill s WHERE s.userId = :userId AND s.category IS NOT NULL")
    List<String> findCategoriesByUserId(@Param("userId") Long userId);
    
    // 获取所有技能分类
    @Query("SELECT DISTINCT s.category FROM Skill s WHERE s.category IS NOT NULL")
    List<String> findAllCategories();
    
    // 获取用户技能平均等级
    @Query("SELECT AVG(s.level) FROM Skill s WHERE s.userId = :userId")
    Double getAverageSkillLevelByUserId(@Param("userId") Long userId);
    
    // 获取用户最高技能等级
    @Query("SELECT MAX(s.level) FROM Skill s WHERE s.userId = :userId")
    Integer getMaxSkillLevelByUserId(@Param("userId") Long userId);
    
    // 根据分类统计技能数量
    @Query("SELECT s.category, COUNT(s) FROM Skill s WHERE s.userId = :userId GROUP BY s.category")
    List<Object[]> countSkillsByCategory(@Param("userId") Long userId);
    
    // 检查用户是否已有该技能
    boolean existsByUserIdAndName(Long userId, String name);
    
    // 获取热门技能（按出现次数排序）
    @Query("SELECT s.name, COUNT(s) as count FROM Skill s GROUP BY s.name ORDER BY count DESC")
    List<Object[]> findPopularSkills();
} 