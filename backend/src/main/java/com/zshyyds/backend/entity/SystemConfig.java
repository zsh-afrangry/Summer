package com.zshyyds.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "system_config")
public class SystemConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "config_key", unique = true, nullable = false, length = 100)
    private String configKey;
    
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;
    
    @Column(name = "config_type", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'string'")
    private String configType = "string";
    
    @Column(length = 255)
    private String description;
    
    @Column(length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'system'")
    private String category = "system";
    
    @Column(name = "is_editable", columnDefinition = "TINYINT DEFAULT 1")
    private Integer isEditable = 1; // 是否可编辑：1-是，0-否
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public SystemConfig() {}
    
    public SystemConfig(String configKey, String configValue, String configType, String description, String category) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.description = description;
        this.category = category;
    }
    
    // 便捷方法：获取配置值并转换为指定类型
    public String getStringValue() {
        return configValue;
    }
    
    public Integer getIntValue() {
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public Boolean getBooleanValue() {
        return "1".equals(configValue) || "true".equalsIgnoreCase(configValue);
    }
} 