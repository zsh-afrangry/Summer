package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Skill;
import com.zshyyds.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    
    /**
     * 根据用户ID查找技能
     */
    List<Skill> findByUserIdOrderBySortOrderAsc(Long userId);
    
    /**
     * 根据用户对象查找技能
     */
    List<Skill> findByUserOrderBySortOrderAsc(User user);
    
    /**
     * 根据用户和技能名查找
     */
    Optional<Skill> findByUserAndName(User user, String name);
    
    /**
     * 根据用户ID和技能名查找
     */
    Optional<Skill> findByUserIdAndName(Long userId, String name);
    
    /**
     * 根据技能分类查找
     */
    List<Skill> findByCategoryOrderByLevelDesc(String category);
    
    /**
     * 根据用户ID和分类查找技能
     */
    List<Skill> findByUserIdAndCategoryOrderBySortOrderAsc(Long userId, String category);
    
    /**
     * 根据技能等级范围查找
     */
    List<Skill> findByLevelBetweenOrderByLevelDesc(Integer minLevel, Integer maxLevel);
    
    /**
     * 根据用户和等级范围查找技能
     */
    List<Skill> findByUserIdAndLevelBetweenOrderByLevelDesc(Long userId, Integer minLevel, Integer maxLevel);
    
    /**
     * 获取用户的顶级技能（等级大于某个值）
     */
    @Query("SELECT s FROM Skill s WHERE s.user.id = :userId AND s.level >= :minLevel ORDER BY s.level DESC")
    List<Skill> findTopSkillsByUser(@Param("userId") Long userId, @Param("minLevel") Integer minLevel);
    
    /**
     * 根据技能名模糊查询
     */
    List<Skill> findByNameContainingIgnoreCase(String name);
    
    /**
     * 获取所有技能分类
     */
    @Query("SELECT DISTINCT s.category FROM Skill s WHERE s.category IS NOT NULL ORDER BY s.category")
    List<String> findAllCategories();
    
    /**
     * 获取用户的技能分类
     */
    @Query("SELECT DISTINCT s.category FROM Skill s WHERE s.user.id = :userId AND s.category IS NOT NULL ORDER BY s.category")
    List<String> findCategoriesByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的技能数量
     */
    @Query("SELECT COUNT(s) FROM Skill s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计分类下的技能数量
     */
    @Query("SELECT COUNT(s) FROM Skill s WHERE s.category = :category")
    long countByCategory(@Param("category") String category);
    
    /**
     * 获取技能统计（分类和数量）
     */
    @Query("SELECT s.category, COUNT(s) FROM Skill s WHERE s.category IS NOT NULL GROUP BY s.category ORDER BY COUNT(s) DESC")
    List<Object[]> getSkillStatsByCategory();
    
    /**
     * 获取用户技能统计（分类和数量）
     */
    @Query("SELECT s.category, COUNT(s) FROM Skill s WHERE s.user.id = :userId AND s.category IS NOT NULL GROUP BY s.category ORDER BY COUNT(s) DESC")
    List<Object[]> getSkillStatsByUserAndCategory(@Param("userId") Long userId);
    
    /**
     * 获取用户技能的平均等级
     */
    @Query("SELECT AVG(s.level) FROM Skill s WHERE s.user.id = :userId")
    Double getAverageSkillLevel(@Param("userId") Long userId);
    
    /**
     * 根据技能名查找所有用户的该技能
     */
    List<Skill> findByNameOrderByLevelDesc(String name);
    
    /**
     * 删除用户的所有技能
     */
    void deleteByUserId(Long userId);
    
    /**
     * 检查用户是否已有该技能
     */
    boolean existsByUserIdAndName(Long userId, String name);
}