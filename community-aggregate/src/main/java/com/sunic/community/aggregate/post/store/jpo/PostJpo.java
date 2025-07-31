package com.sunic.community.aggregate.post.store.jpo;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class PostJpo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String content;
    
    @Enumerated(EnumType.STRING)
    private PostTypeJpo postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private CommunityJpo community;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<CommentJpo> comments = new ArrayList<>();

    private Long registeredTime;
    private Integer registrant;
    private Long modifiedTime;
    private Integer modifier;

    public void updateContent(String title, String content, PostTypeJpo postType, Integer modifier) {
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.modifier = modifier;
        this.modifiedTime = System.currentTimeMillis();
    }
}