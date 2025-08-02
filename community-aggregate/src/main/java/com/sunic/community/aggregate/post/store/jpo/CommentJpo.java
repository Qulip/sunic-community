package com.sunic.community.aggregate.post.store.jpo;

import com.sunic.community.spec.post.entity.Comment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CommentJpo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private PostJpo post;

	private Long registeredTime;
	private Integer registrant;
	private Long modifiedTime;
	private Integer modifier;

	public void setPost(PostJpo post) {
		this.post = post;
	}

	public static CommentJpo fromDomain(Comment comment) {
		return CommentJpo.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.registeredTime(comment.getRegisteredTime())
			.registrant(comment.getRegistrant())
			.modifiedTime(comment.getModifiedTime())
			.modifier(comment.getModifier())
			.build();
	}

	public Comment toDomain() {
		return Comment.builder()
			.id(id)
			.content(content)
			.postId(post != null ? post.getId() : null)
			.registeredTime(registeredTime)
			.registrant(registrant)
			.modifiedTime(modifiedTime)
			.modifier(modifier)
			.build();
	}
}