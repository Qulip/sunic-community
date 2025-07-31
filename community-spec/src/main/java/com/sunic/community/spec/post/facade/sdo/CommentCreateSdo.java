package com.sunic.community.spec.post.facade.sdo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommentCreateSdo {
    private final String content;
    private final Integer postId;
    private final Integer registrant;
}