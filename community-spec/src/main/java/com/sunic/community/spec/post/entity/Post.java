package com.sunic.community.spec.post.entity;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.sunic.community.spec.post.facade.sdo.PostCdo;
import com.sunic.community.spec.post.facade.sdo.PostRdo;
import com.sunic.community.spec.post.facade.sdo.PostUdo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class Post {
	private Integer id;
	private String title;
	private String content;
	private PostType postType;
	private Integer communityId;
	private List<Comment> comments;
	private Long registeredTime;
	private Integer registrant;
	private Long modifiedTime;
	private Integer modifier;

	public static Post create(PostCdo sdo) {
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

	public void update(PostUdo sdo) {
		BeanUtils.copyProperties(sdo, this);
		this.modifiedTime = System.currentTimeMillis();
	}

	public PostRdo toRdo() {
		return PostRdo.builder()
			.id(id)
			.title(title)
			.content(content)
			.postType(postType)
			.communityId(communityId)
			.comments(comments.stream().map(Comment::toRdo).collect(Collectors.toList()))
			.registeredTime(registeredTime)
			.registrant(registrant)
			.modifiedTime(modifiedTime)
			.modifier(modifier)
			.build();
	}
}