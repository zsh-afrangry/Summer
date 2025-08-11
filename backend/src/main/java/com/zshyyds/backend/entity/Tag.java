package com.zshyyds.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tag", schema = "my_blog")
public class Tag {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ColumnDefault("'#667eea'")
    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "description")
    private String description;

    @ColumnDefault("0")
    @Column(name = "article_count")
    private Integer articleCount;

    @ColumnDefault("0")
    @Column(name = "sort_order")
    private Integer sortOrder;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}