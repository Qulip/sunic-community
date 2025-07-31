package com.sunic.community.spec.post.entity;

import com.sunic.community.spec.post.facade.sdo.CommentCreateSdo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class Comment {
    private final Integer id;
    private final String content;
    private final Integer postId;
    private final Long registeredTime;
    private final Integer registrant;
    private final Long modifiedTime;
    private final Integer modifier;

    public static Comment create(CommentCreateSdo sdo) {
        long currentTime = System.currentTimeMillis();
        return Comment.builder()
                .content(sdo.getContent())
                .postId(sdo.getPostId())
                .registeredTime(currentTime)
                .registrant(sdo.getRegistrant())
                .modifiedTime(currentTime)
                .modifier(sdo.getRegistrant())
                .build();
    }

    public Comment updateContent(String newContent, Integer modifier) {
        long currentTime = System.currentTimeMillis();
        return this.toBuilder()
                .content(newContent)
                .modifiedTime(currentTime)
                .modifier(modifier)
                .build();
    }
}