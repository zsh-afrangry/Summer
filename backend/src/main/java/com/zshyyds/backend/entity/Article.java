package com.zshyyds.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "article", schema = "my_blog")
public class Article {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "summary", length = 500)
    private String summary;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ColumnDefault("1")
    @Column(name = "status")
    private Byte status;

    @ColumnDefault("0")
    @Column(name = "read_time")
    private Integer readTime;

    @Column(name = "cover_image")
    private String coverImage;

    @ColumnDefault("0")
    @Column(name = "is_top")
    private Byte isTop;

    @ColumnDefault("1")
    @Column(name = "allow_comment")
    private Byte allowComment;

    @Column(name = "published_at")
    private Instant publishedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}