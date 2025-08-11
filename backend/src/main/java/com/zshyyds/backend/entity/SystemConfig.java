package com.zshyyds.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "system_config", schema = "my_blog")
public class SystemConfig {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "config_key", nullable = false, length = 100)
    private String configKey;

    @Lob
    @Column(name = "config_value")
    private String configValue;

    @ColumnDefault("'string'")
    @Column(name = "config_type", length = 20)
    private String configType;

    @Column(name = "description")
    private String description;

    @ColumnDefault("'system'")
    @Column(name = "category", length = 50)
    private String category;

    @ColumnDefault("1")
    @Column(name = "is_editable")
    private Byte isEditable;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}