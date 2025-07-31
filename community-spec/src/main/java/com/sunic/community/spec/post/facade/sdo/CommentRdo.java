package com.sunic.community.spec.post.facade.sdo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommentRdo {
    private final Integer id;
    private final String content;
    private final Integer postId;
    private final Long registeredTime;
    private final Integer registrant;
    private final Long modifiedTime;
    private final Integer modifier;
}