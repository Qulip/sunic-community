package com.sunic.community.spec.post.facade.sdo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommentQdo {
	private final Integer postId;
	private final Integer registrant;
	private final Integer page;
	private final Integer size;
	private final String sortBy;
	private final String sortDirection;
}