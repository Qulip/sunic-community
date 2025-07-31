package com.sunic.community.spec.post.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
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
}