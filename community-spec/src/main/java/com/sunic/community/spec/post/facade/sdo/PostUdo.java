package com.sunic.community.spec.post.facade.sdo;

import com.sunic.community.spec.post.entity.PostType;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PostUdo {
	private final Integer id;
	private final String title;
	private final String content;
	private final PostType postType;
	private final Integer modifier;
}