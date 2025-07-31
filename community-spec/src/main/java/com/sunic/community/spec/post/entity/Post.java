package com.sunic.community.spec.post.entity;

import com.sunic.community.spec.post.facade.sdo.PostCreateSdo;
import com.sunic.community.spec.post.facade.sdo.PostUpdateSdo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder(toBuilder = true)
@ToString
public class Post {
    private final Integer id;
    private final String title;
    private final String content;
    private final PostType postType;
    private final Integer communityId;
    private final List<Comment> comments;
    private final Long registeredTime;
    private final Integer registrant;
    private final Long modifiedTime;
    private final Integer modifier;

    public static Post create(PostCreateSdo sdo) {
        long currentTime = System.currentTimeMillis();
        return Post.builder()
                .title(sdo.getTitle())
                .content(sdo.getContent())
                .postType(sdo.getPostType())
                .communityId(sdo.getCommunityId())
                .registeredTime(currentTime)
                .registrant(sdo.getRegistrant())
                .modifiedTime(currentTime)
                .modifier(sdo.getRegistrant())
                .build();
    }

    public Post update(PostUpdateSdo sdo) {
        long currentTime = System.currentTimeMillis();
        return this.toBuilder()
                .title(sdo.getTitle())
                .content(sdo.getContent())
                .postType(sdo.getPostType())
                .modifiedTime(currentTime)
                .modifier(sdo.getModifier())
                .build();
    }
}