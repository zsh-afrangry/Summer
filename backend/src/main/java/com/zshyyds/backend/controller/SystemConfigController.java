package com.zshyyds.backend.controller;

import com.zshyyds.backend.common.ApiResponse;
import com.zshyyds.backend.entity.SystemConfig;
import com.zshyyds.backend.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/system-config")
@CrossOrigin(origins = "*")
public class SystemConfigController {
    
    private final SystemConfigService systemConfigService;
    
    @Autowired
    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }
    
    @GetMapping("/{configKey}")
    public Map<String, Object> getConfigByKey(@PathVariable String configKey) {
        try {
            Optional<SystemConfig> config = systemConfigService.getConfigByKey(configKey);
            if (config.isPresent()) {
                return ApiResponse.success(config.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/{configKey}/value")
    public Map<String, Object> getConfigValue(@PathVariable String configKey,
                                             @RequestParam(required = false) String defaultValue) {
        try {
            String value;
            if (defaultValue != null) {
                value = systemConfigService.getConfigValue(configKey, defaultValue);
            } else {
                value = systemConfigService.getConfigValue(configKey);
            }
            return ApiResponse.success(value);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/{configKey}/string")
    public Map<String, Object> getStringValue(@PathVariable String configKey) {
        try {
            String value = systemConfigService.getStringValue(configKey);
            return ApiResponse.success(value);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/{configKey}/int")
    public Map<String, Object> getIntValue(@PathVariable String configKey) {
        try {
            Integer value = systemConfigService.getIntValue(configKey);
            return ApiResponse.success(value);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/{configKey}/boolean")
    public Map<String, Object> getBooleanValue(@PathVariable String configKey) {
        try {
            Boolean value = systemConfigService.getBooleanValue(configKey);
            return ApiResponse.success(value);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping
    public Map<String, Object> createConfig(@RequestBody SystemConfig config) {
        try {
            String result = systemConfigService.saveOrUpdateConfig(config);
            
            if ("success".equals(result)) {
                return ApiResponse.success("配置创建成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping
    public Map<String, Object> updateConfig(@RequestBody SystemConfig config) {
        try {
            String result = systemConfigService.saveOrUpdateConfig(config);
            
            if ("success".equals(result)) {
                return ApiResponse.success("配置更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{configKey}")
    public Map<String, Object> updateConfigValue(@PathVariable String configKey,
                                                @RequestParam String configValue) {
        try {
            String result = systemConfigService.updateConfigValue(configKey, configValue);
            
            if ("success".equals(result)) {
                return ApiResponse.success("配置值更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/batch")
    public Map<String, Object> batchUpdateConfigs(@RequestBody Map<String, String> configs) {
        try {
            String result = systemConfigService.batchUpdateConfigs(configs);
            
            if ("success".equals(result)) {
                return ApiResponse.success("批量更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/{configKey}")
    public Map<String, Object> deleteConfig(@PathVariable String configKey) {
        try {
            String result = systemConfigService.deleteConfig(configKey);
            
            if ("success".equals(result)) {
                return ApiResponse.success("配置删除成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/category/{category}")
    public Map<String, Object> getConfigsByCategory(@PathVariable String category) {
        try {
            List<SystemConfig> configs = systemConfigService.getConfigsByCategory(category);
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/type/{configType}")
    public Map<String, Object> getConfigsByType(@PathVariable String configType) {
        try {
            List<SystemConfig> configs = systemConfigService.getConfigsByType(configType);
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/editable")
    public Map<String, Object> getEditableConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getEditableConfigs();
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/site")
    public Map<String, Object> getSiteConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getSiteConfigs();
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/feature")
    public Map<String, Object> getFeatureConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getFeatureConfigs();
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/admin")
    public Map<String, Object> getAdminConfigs() {
        try {
            List<SystemConfig> configs = systemConfigService.getAdminConfigs();
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/categories")
    public Map<String, Object> getAllCategories() {
        try {
            List<String> categories = systemConfigService.getAllCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/types")
    public Map<String, Object> getAllConfigTypes() {
        try {
            List<String> types = systemConfigService.getAllConfigTypes();
            return ApiResponse.success(types);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/search")
    public Map<String, Object> searchConfigs(@RequestParam String keyword) {
        try {
            List<SystemConfig> configs = systemConfigService.searchConfigs(keyword);
            return ApiResponse.success(configs);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/exists/{configKey}")
    public Map<String, Object> configExists(@PathVariable String configKey) {
        try {
            boolean exists = systemConfigService.configExists(configKey);
            return ApiResponse.success("检查完成", exists);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count")
    public Map<String, Object> countConfigs() {
        try {
            Long count = systemConfigService.countConfigs();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count/editable")
    public Map<String, Object> countEditableConfigs() {
        try {
            Long count = systemConfigService.countEditableConfigs();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/stats/category")
    public Map<String, Object> countConfigsByCategory() {
        try {
            Map<String, Long> stats = systemConfigService.countConfigsByCategory();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/stats/type")
    public Map<String, Object> countConfigsByType() {
        try {
            Map<String, Long> stats = systemConfigService.countConfigsByType();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping("/initialize")
    public Map<String, Object> initializeDefaultConfigs() {
        try {
            String result = systemConfigService.initializeDefaultConfigs();
            
            if ("success".equals(result) || result.contains("已经初始化")) {
                return ApiResponse.success(result);
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/{configKey}/enabled")
    public Map<String, Object> isBooleanConfigEnabled(@PathVariable String configKey) {
        try {
            boolean enabled = systemConfigService.isBooleanConfigEnabled(configKey);
            return ApiResponse.success("检查完成", enabled);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
} 