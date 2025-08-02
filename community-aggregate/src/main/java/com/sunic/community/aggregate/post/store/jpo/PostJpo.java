package com.sunic.community.aggregate.post.store.jpo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import com.sunic.community.spec.post.entity.Post;
import com.sunic.community.spec.post.entity.PostType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class PostJpo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String title;
	private String content;

	@Enumerated(EnumType.STRING)
	private PostTypeJpo postType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	private CommunityJpo community;

	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Builder.Default
	private List<CommentJpo> comments = new ArrayList<>();

	private Long registeredTime;
	private Integer registrant;
	private Long modifiedTime;
	private Integer modifier;

	public void updateContent(String title, String content, PostTypeJpo postType, Integer modifier) {
		this.title = title;
		this.content = content;
		this.postType = postType;
		this.modifier = modifier;
		this.modifiedTime = System.currentTimeMillis();
	}

	public void updateFromDomain(Post post) {
		this.title = post.getTitle();
		this.content = post.getContent();
		this.postType = convertToPostTypeJpo(post.getPostType());
		this.modifier = post.getModifier();
		this.modifiedTime = System.currentTimeMillis();
	}

	public void setCommunity(CommunityJpo community) {
		this.community = community;
	}

	public static PostJpo fromDomain(Post post) {
		return PostJpo.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.postType(convertToPostTypeJpo(post.getPostType()))
			.registeredTime(post.getRegisteredTime())
			.registrant(post.getRegistrant())
			.modifiedTime(post.getModifiedTime())
			.modifier(post.getModifier())
			.build();
	}

	public Post toDomain() {
		return Post.builder()
			.id(id)
			.title(title)
			.content(content)
			.postType(convertToPostType(postType))
			.communityId(community != null ? community.getId() : null)
			.comments(comments != null && !comments.isEmpty() ?
				comments.stream().map(CommentJpo::toDomain).collect(Collectors.toList()) : new ArrayList<>())
			.registeredTime(registeredTime)
			.registrant(registrant)
			.modifiedTime(modifiedTime)
			.modifier(modifier)
			.build();
	}

	private static PostTypeJpo convertToPostTypeJpo(PostType postType) {
		return PostTypeJpo.valueOf(postType.name());
	}

	private static PostType convertToPostType(PostTypeJpo postTypeJpo) {
		return PostType.valueOf(postTypeJpo.name());
	}
}