package com.sunic.community.aggregate.post.store.jpo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CommentJpo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostJpo post;

    private Long registeredTime;
    private Integer registrant;
    private Long modifiedTime;
    private Integer modifier;

    public void setPost(PostJpo post) {
        this.post = post;
    }
}