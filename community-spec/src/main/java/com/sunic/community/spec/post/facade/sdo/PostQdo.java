package com.sunic.community.spec.post.facade.sdo;

import com.sunic.community.spec.post.entity.PostType;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PostQdo {
	private final Integer communityId;
	private final String title;
	private final PostType postType;
	private final Integer registrant;
	private final Integer page;
	private final Integer size;
	private final String sortBy;
	private final String sortDirection;
}