package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.SystemConfig;
import com.zshyyds.backend.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    
    private final SystemConfigRepository systemConfigRepository;
    
    @Autowired
    public SystemConfigServiceImpl(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }
    
    @Override
    public Optional<SystemConfig> getConfigByKey(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey);
    }
    
    @Override
    public String getConfigValue(String configKey) {
        Optional<SystemConfig> config = systemConfigRepository.findByConfigKey(configKey);
        return config.map(SystemConfig::getConfigValue).orElse(null);
    }
    
    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public String getStringValue(String configKey) {
        Optional<String> value = systemConfigRepository.getStringConfigValue(configKey);
        return value.orElse("");
    }
    
    @Override
    public Integer getIntValue(String configKey) {
        Optional<String> value = systemConfigRepository.getNumberConfigValue(configKey);
        if (value.isPresent()) {
            try {
                return Integer.parseInt(value.get());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    @Override
    public Boolean getBooleanValue(String configKey) {
        return systemConfigRepository.isBooleanConfigEnabled(configKey);
    }
    
    @Override
    public String saveOrUpdateConfig(SystemConfig config) {
        if (config.getConfigKey() == null || config.getConfigKey().trim().isEmpty()) {
            return "配置键不能为空";
        }
        
        if (config.getConfigKey().length() > 100) {
            return "配置键长度不能超过100字符";
        }
        
        // 检查配置键唯一性（新增时）
        if (config.getId() == null && systemConfigRepository.existsByConfigKey(config.getConfigKey())) {
            return "配置键已存在";
        }
        
        try {
            // 设置默认值
            if (config.getConfigType() == null) {
                config.setConfigType("string");
            }
            if (config.getCategory() == null) {
                config.setCategory("system");
            }
            if (config.getIsEditable() == null) {
                config.setIsEditable(1);
            }
            
            systemConfigRepository.save(config);
            return "success";
        } catch (Exception e) {
            return "保存失败，请重试";
        }
    }
    
    @Override
    @Transactional
    public String updateConfigValue(String configKey, String configValue) {
        if (!systemConfigRepository.existsByConfigKey(configKey)) {
            return "配置不存在";
        }
        
        try {
            systemConfigRepository.updateConfigValue(configKey, configValue);
            return "success";
        } catch (Exception e) {
            return "更新失败";
        }
    }
    
    @Override
    @Transactional
    public String batchUpdateConfigs(Map<String, String> configs) {
        try {
            for (Map.Entry<String, String> entry : configs.entrySet()) {
                String result = updateConfigValue(entry.getKey(), entry.getValue());
                if (!"success".equals(result)) {
                    return "批量更新失败：" + result;
                }
            }
            return "success";
        } catch (Exception e) {
            return "批量更新失败";
        }
    }
    
    @Override
    public String deleteConfig(String configKey) {
        Optional<SystemConfig> configOptional = systemConfigRepository.findByConfigKey(configKey);
        if (!configOptional.isPresent()) {
            return "配置不存在";
        }
        
        SystemConfig config = configOptional.get();
        if (config.getIsEditable() == 0) {
            return "该配置不允许删除";
        }
        
        try {
            systemConfigRepository.delete(config);
            return "success";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    @Override
    public List<SystemConfig> getConfigsByCategory(String category) {
        return systemConfigRepository.findByCategory(category);
    }
    
    @Override
    public List<SystemConfig> getConfigsByType(String configType) {
        return systemConfigRepository.findByConfigType(configType);
    }
    
    @Override
    public List<SystemConfig> getEditableConfigs() {
        return systemConfigRepository.findByIsEditable(1);
    }
    
    @Override
    public List<SystemConfig> getSiteConfigs() {
        return systemConfigRepository.findSiteConfigs();
    }
    
    @Override
    public List<SystemConfig> getFeatureConfigs() {
        return systemConfigRepository.findFeatureConfigs();
    }
    
    @Override
    public List<SystemConfig> getAdminConfigs() {
        return systemConfigRepository.findAdminConfigs();
    }
    
    @Override
    public List<String> getAllCategories() {
        return systemConfigRepository.findAllCategories();
    }
    
    @Override
    public List<String> getAllConfigTypes() {
        return systemConfigRepository.findAllConfigTypes();
    }
    
    @Override
    public List<SystemConfig> searchConfigs(String keyword) {
        List<SystemConfig> results = systemConfigRepository.findByConfigKeyContainingIgnoreCase(keyword);
        results.addAll(systemConfigRepository.findByDescriptionContainingIgnoreCase(keyword));
        return results;
    }
    
    @Override
    public boolean configExists(String configKey) {
        return systemConfigRepository.existsByConfigKey(configKey);
    }
    
    @Override
    public Long countConfigs() {
        return systemConfigRepository.countConfigs();
    }
    
    @Override
    public Long countEditableConfigs() {
        return systemConfigRepository.countEditableConfigs();
    }
    
    @Override
    public Map<String, Long> countConfigsByCategory() {
        List<Object[]> results = systemConfigRepository.countByCategory();
        Map<String, Long> categoryCountMap = new HashMap<>();
        
        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = (Long) result[1];
            categoryCountMap.put(category, count);
        }
        
        return categoryCountMap;
    }
    
    @Override
    public Map<String, Long> countConfigsByType() {
        List<Object[]> results = systemConfigRepository.countByConfigType();
        Map<String, Long> typeCountMap = new HashMap<>();
        
        for (Object[] result : results) {
            String type = (String) result[0];
            Long count = (Long) result[1];
            typeCountMap.put(type, count);
        }
        
        return typeCountMap;
    }
    
    @Override
    @Transactional
    public String initializeDefaultConfigs() {
        try {
            // 检查是否已经初始化
            if (systemConfigRepository.existsByConfigKey("site_title")) {
                return "配置已经初始化";
            }
            
            // 创建默认配置
            SystemConfig[] defaultConfigs = {
                new SystemConfig("site_title", "我的个人博客", "string", "网站标题", "site"),
                new SystemConfig("site_description", "用心记录技术成长之路", "string", "网站描述", "site"),
                new SystemConfig("site_keywords", "Vue,Spring Boot,全栈开发,技术博客", "string", "网站关键词", "site"),
                new SystemConfig("admin_email", "admin@example.com", "string", "管理员邮箱", "admin"),
                new SystemConfig("articles_per_page", "10", "number", "每页文章数", "pagination"),
                new SystemConfig("comment_enabled", "1", "boolean", "是否启用评论", "feature"),
                new SystemConfig("registration_enabled", "1", "boolean", "是否允许注册", "feature")
            };
            
            for (SystemConfig config : defaultConfigs) {
                systemConfigRepository.save(config);
            }
            
            return "success";
        } catch (Exception e) {
            return "初始化失败";
        }
    }
    
    @Override
    public boolean isBooleanConfigEnabled(String configKey) {
        return systemConfigRepository.isBooleanConfigEnabled(configKey);
    }
} 