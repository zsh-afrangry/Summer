package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    /**
     * 根据配置键查找配置
     */
    Optional<SystemConfig> findByConfigKey(String configKey);
    
    /**
     * 根据配置分类查找配置
     */
    List<SystemConfig> findByCategory(String category);
    
    /**
     * 根据配置分类排序查找
     */
    List<SystemConfig> findByCategoryOrderByConfigKey(String category);
    
    /**
     * 根据配置类型查找
     */
    List<SystemConfig> findByConfigType(String configType);
    
    /**
     * 查找可编辑的配置
     */
    List<SystemConfig> findByIsEditable(Byte isEditable);
    
    /**
     * 根据分类查找可编辑的配置
     */
    List<SystemConfig> findByCategoryAndIsEditableOrderByConfigKey(String category, Byte isEditable);
    
    /**
     * 检查配置键是否存在
     */
    boolean existsByConfigKey(String configKey);
    
    /**
     * 根据配置键模糊查询
     */
    List<SystemConfig> findByConfigKeyContainingIgnoreCase(String configKey);
    
    /**
     * 根据描述模糊查询
     */
    List<SystemConfig> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * 获取所有配置分类
     */
    @Query("SELECT DISTINCT s.category FROM SystemConfig s ORDER BY s.category")
    List<String> findAllCategories();
    
    /**
     * 获取所有配置类型
     */
    @Query("SELECT DISTINCT s.configType FROM SystemConfig s ORDER BY s.configType")
    List<String> findAllConfigTypes();
    
    /**
     * 根据配置键更新配置值
     */
    @Modifying
    @Query("UPDATE SystemConfig s SET s.configValue = :configValue WHERE s.configKey = :configKey")
    void updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);
    
    /**
     * 批量更新配置值
     */
    @Modifying
    @Query("UPDATE SystemConfig s SET s.configValue = :configValue WHERE s.configKey IN :configKeys")
    void updateConfigValues(@Param("configKeys") List<String> configKeys, @Param("configValue") String configValue);
    
    /**
     * 根据配置键获取配置值
     */
    @Query("SELECT s.configValue FROM SystemConfig s WHERE s.configKey = :configKey")
    Optional<String> getConfigValue(@Param("configKey") String configKey);
    
    /**
     * 根据配置键获取配置值，如果不存在返回默认值
     */
    @Query("SELECT COALESCE(s.configValue, :defaultValue) FROM SystemConfig s WHERE s.configKey = :configKey")
    String getConfigValueOrDefault(@Param("configKey") String configKey, @Param("defaultValue") String defaultValue);
    
    /**
     * 获取布尔类型配置值
     */
    @Query("SELECT CASE WHEN s.configValue = '1' OR s.configValue = 'true' THEN true ELSE false END FROM SystemConfig s WHERE s.configKey = :configKey")
    Optional<Boolean> getBooleanConfigValue(@Param("configKey") String configKey);
    
    /**
     * 获取整数类型配置值
     */
    @Query("SELECT CAST(s.configValue AS int) FROM SystemConfig s WHERE s.configKey = :configKey AND s.configType = 'number'")
    Optional<Integer> getIntegerConfigValue(@Param("configKey") String configKey);
    
    /**
     * 统计各分类的配置数量
     */
    @Query("SELECT s.category, COUNT(s) FROM SystemConfig s GROUP BY s.category ORDER BY s.category")
    List<Object[]> countConfigsByCategory();
    
    /**
     * 统计各类型的配置数量
     */
    @Query("SELECT s.configType, COUNT(s) FROM SystemConfig s GROUP BY s.configType ORDER BY s.configType")
    List<Object[]> countConfigsByType();
    
    /**
     * 统计可编辑配置数量
     */
    @Query("SELECT COUNT(s) FROM SystemConfig s WHERE s.isEditable = 1")
    long countEditableConfigs();
    
    /**
     * 获取网站基本配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.category = 'site' ORDER BY s.configKey")
    List<SystemConfig> getSiteConfigs();
    
    /**
     * 获取功能开关配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.category = 'feature' ORDER BY s.configKey")
    List<SystemConfig> getFeatureConfigs();
    
    /**
     * 删除配置
     */
    void deleteByConfigKey(String configKey);
}