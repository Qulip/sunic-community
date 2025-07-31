package com.sunic.community.spec.post.facade.sdo;

import com.sunic.community.spec.post.entity.PostType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class PostRdo {
    private final Integer id;
    private final String title;
    private final String content;
    private final PostType postType;
    private final Integer communityId;
    private final Long registeredTime;
    private final Integer registrant;
    private final Long modifiedTime;
    private final Integer modifier;
    private final List<CommentRdo> comments;
}