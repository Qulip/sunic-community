package com.sunic.community.aggregate.community.store.jpo;

import com.sunic.community.spec.community.entity.Member;

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
@Table(name = "member")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class MemberJpo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	private CommunityJpo community;

	private Integer userId;
	private Long joinedTime;
	private Integer registrant;

	public void setCommunity(CommunityJpo community) {
		this.community = community;
	}

	public static MemberJpo fromDomain(Member member) {
		return MemberJpo.builder()
			.id(member.getId())
			.userId(member.getUserId())
			.joinedTime(member.getJoinedTime())
			.registrant(member.getRegistrant())
			.build();
	}

	public Member toDomain() {
		return Member.builder()
			.id(id)
			.communityId(community != null ? community.getId() : null)
			.userId(userId)
			.joinedTime(joinedTime)
			.registrant(registrant)
			.build();
	}
}