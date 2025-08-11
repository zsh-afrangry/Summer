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
@Table(name = "article_stats", schema = "my_blog")
public class ArticleStat {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ColumnDefault("0")
    @Column(name = "view_count")
    private Integer viewCount;

    @ColumnDefault("0")
    @Column(name = "like_count")
    private Integer likeCount;

    @ColumnDefault("0")
    @Column(name = "comment_count")
    private Integer commentCount;

    @ColumnDefault("0")
    @Column(name = "share_count")
    private Integer shareCount;

    @ColumnDefault("0")
    @Column(name = "collect_count")
    private Integer collectCount;

    @Column(name = "last_view_at")
    private Instant lastViewAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}