package com.sunic.community.spec.post.entity;

import com.sunic.community.spec.post.facade.sdo.CommentCdo;
import com.sunic.community.spec.post.facade.sdo.CommentRdo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class Comment {
	private Integer id;
	private String content;
	private Integer postId;
	private Long registeredTime;
	private Integer registrant;
	private Long modifiedTime;
	private Integer modifier;

	public static Comment create(CommentCdo sdo) {
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

	public void updateContent(String newContent, Integer modifier) {
		long currentTime = System.currentTimeMillis();
		this.content = newContent;
		this.modifiedTime = currentTime;
		this.modifier = modifier;
	}

	public CommentRdo toRdo() {
		return CommentRdo.builder()
			.id(id)
			.content(content)
			.postId(postId)
			.registeredTime(registeredTime)
			.registrant(registrant)
			.modifiedTime(modifiedTime)
			.modifier(modifier)
			.build();
	}
}