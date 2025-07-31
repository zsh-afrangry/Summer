package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.SystemConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SystemConfigService {
    
    /**
     * 根据配置键获取配置
     * @param configKey 配置键
     * @return 配置信息
     */
    Optional<SystemConfig> getConfigByKey(String configKey);
    
    /**
     * 获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);
    
    /**
     * 获取配置值（带默认值）
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);
    
    /**
     * 获取字符串配置值
     * @param configKey 配置键
     * @return 字符串值
     */
    String getStringValue(String configKey);
    
    /**
     * 获取整数配置值
     * @param configKey 配置键
     * @return 整数值
     */
    Integer getIntValue(String configKey);
    
    /**
     * 获取布尔配置值
     * @param configKey 配置键
     * @return 布尔值
     */
    Boolean getBooleanValue(String configKey);
    
    /**
     * 保存或更新配置
     * @param config 配置信息
     * @return 操作结果
     */
    String saveOrUpdateConfig(SystemConfig config);
    
    /**
     * 更新配置值
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 更新结果
     */
    String updateConfigValue(String configKey, String configValue);
    
    /**
     * 批量更新配置
     * @param configs 配置Map
     * @return 更新结果
     */
    String batchUpdateConfigs(Map<String, String> configs);
    
    /**
     * 删除配置
     * @param configKey 配置键
     * @return 删除结果
     */
    String deleteConfig(String configKey);
    
    /**
     * 根据分类获取配置
     * @param category 分类
     * @return 配置列表
     */
    List<SystemConfig> getConfigsByCategory(String category);
    
    /**
     * 根据类型获取配置
     * @param configType 配置类型
     * @return 配置列表
     */
    List<SystemConfig> getConfigsByType(String configType);
    
    /**
     * 获取可编辑的配置
     * @return 配置列表
     */
    List<SystemConfig> getEditableConfigs();
    
    /**
     * 获取网站配置
     * @return 网站配置列表
     */
    List<SystemConfig> getSiteConfigs();
    
    /**
     * 获取功能配置
     * @return 功能配置列表
     */
    List<SystemConfig> getFeatureConfigs();
    
    /**
     * 获取管理员配置
     * @return 管理员配置列表
     */
    List<SystemConfig> getAdminConfigs();
    
    /**
     * 获取所有配置分类
     * @return 分类列表
     */
    List<String> getAllCategories();
    
    /**
     * 获取所有配置类型
     * @return 类型列表
     */
    List<String> getAllConfigTypes();
    
    /**
     * 搜索配置
     * @param keyword 关键词
     * @return 搜索结果
     */
    List<SystemConfig> searchConfigs(String keyword);
    
    /**
     * 检查配置键是否存在
     * @param configKey 配置键
     * @return 是否存在
     */
    boolean configExists(String configKey);
    
    /**
     * 统计配置总数
     * @return 配置数量
     */
    Long countConfigs();
    
    /**
     * 统计可编辑配置数量
     * @return 配置数量
     */
    Long countEditableConfigs();
    
    /**
     * 根据分类统计配置数量
     * @return 统计结果
     */
    Map<String, Long> countConfigsByCategory();
    
    /**
     * 根据类型统计配置数量
     * @return 统计结果
     */
    Map<String, Long> countConfigsByType();
    
    /**
     * 初始化默认配置
     * @return 初始化结果
     */
    String initializeDefaultConfigs();
    
    /**
     * 检查布尔配置是否启用
     * @param configKey 配置键
     * @return 是否启用
     */
    boolean isBooleanConfigEnabled(String configKey);
} 