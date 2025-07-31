package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    // 根据配置键查找配置
    Optional<SystemConfig> findByConfigKey(String configKey);
    
    // 根据分类查找配置
    List<SystemConfig> findByCategory(String category);
    
    // 根据配置类型查找配置
    List<SystemConfig> findByConfigType(String configType);
    
    // 查找可编辑的配置
    List<SystemConfig> findByIsEditable(Integer isEditable);
    
    // 根据分类查找可编辑的配置
    List<SystemConfig> findByCategoryAndIsEditable(String category, Integer isEditable);
    
    // 检查配置键是否存在
    boolean existsByConfigKey(String configKey);
    
    // 根据配置键模糊查询
    List<SystemConfig> findByConfigKeyContainingIgnoreCase(String configKey);
    
    // 根据描述模糊查询
    List<SystemConfig> findByDescriptionContainingIgnoreCase(String description);
    
    // 获取所有分类
    @Query("SELECT DISTINCT s.category FROM SystemConfig s ORDER BY s.category")
    List<String> findAllCategories();
    
    // 获取所有配置类型
    @Query("SELECT DISTINCT s.configType FROM SystemConfig s ORDER BY s.configType")
    List<String> findAllConfigTypes();
    
    // 更新配置值
    @Modifying
    @Transactional
    @Query("UPDATE SystemConfig s SET s.configValue = :configValue WHERE s.configKey = :configKey")
    void updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);
    
    // 批量更新配置值
    @Modifying
    @Transactional
    @Query("UPDATE SystemConfig s SET s.configValue = :configValue WHERE s.configKey IN :configKeys")
    void updateConfigValues(@Param("configKeys") List<String> configKeys, @Param("configValue") String configValue);
    
    // 根据分类统计配置数量
    @Query("SELECT s.category, COUNT(s) FROM SystemConfig s GROUP BY s.category ORDER BY COUNT(s) DESC")
    List<Object[]> countByCategory();
    
    // 根据类型统计配置数量
    @Query("SELECT s.configType, COUNT(s) FROM SystemConfig s GROUP BY s.configType ORDER BY COUNT(s) DESC")
    List<Object[]> countByConfigType();
    
    // 获取网站配置（常用配置项）
    @Query("SELECT s FROM SystemConfig s WHERE s.category = 'site' ORDER BY s.configKey")
    List<SystemConfig> findSiteConfigs();
    
    // 获取功能配置
    @Query("SELECT s FROM SystemConfig s WHERE s.category = 'feature' ORDER BY s.configKey")
    List<SystemConfig> findFeatureConfigs();
    
    // 获取管理员配置
    @Query("SELECT s FROM SystemConfig s WHERE s.category = 'admin' ORDER BY s.configKey")
    List<SystemConfig> findAdminConfigs();
    
    // 检查配置是否为布尔类型且值为true
    @Query("SELECT COUNT(s) > 0 FROM SystemConfig s WHERE s.configKey = :configKey AND s.configType = 'boolean' AND (s.configValue = '1' OR s.configValue = 'true')")
    boolean isBooleanConfigEnabled(@Param("configKey") String configKey);
    
    // 获取整数类型配置值
    @Query("SELECT s.configValue FROM SystemConfig s WHERE s.configKey = :configKey AND s.configType = 'number'")
    Optional<String> getNumberConfigValue(@Param("configKey") String configKey);
    
    // 获取字符串类型配置值
    @Query("SELECT s.configValue FROM SystemConfig s WHERE s.configKey = :configKey AND s.configType = 'string'")
    Optional<String> getStringConfigValue(@Param("configKey") String configKey);
    
    // 统计配置总数
    @Query("SELECT COUNT(s) FROM SystemConfig s")
    Long countConfigs();
    
    // 统计可编辑配置数量
    @Query("SELECT COUNT(s) FROM SystemConfig s WHERE s.isEditable = 1")
    Long countEditableConfigs();
} 