package com.sunic.community.aggregate.community.store.jpo;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.BeanUtils;

import com.sunic.community.spec.community.entity.Community;
import com.sunic.community.spec.community.entity.CommunityType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "community")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CommunityJpo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private CommunityType type;

	private String thumbnail;
	private String name;
	private String description;
	private String managerId;
	private String managerName;
	private String managerEmail;

	@ColumnDefault("0")
	private Long memberCount;

	private Long registeredTime;
	private Integer registrant;
	private Long modifiedTime;
	private Integer modifier;
	private String secretNumber;
	private boolean allowSelfJoin;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id")
	private List<MemberJpo> members;

	public void update(Community community) {
		BeanUtils.copyProperties(community, this);
	}

	public Community toDomain() {
		return Community.builder()
			.id(id)
			.type(type)
			.thumbnail(thumbnail)
			.name(name)
			.description(description)
			.managerId(managerId)
			.managerName(managerName)
			.managerEmail(managerEmail)
			.memberCount(memberCount)
			.registeredTime(registeredTime)
			.modifiedTime(modifiedTime)
			.modifier(modifier)
			.secretNumber(secretNumber)
			.allowSelfJoin(allowSelfJoin)
			.build();
	}

	public static CommunityJpo fromDomain(Community community) {
		return CommunityJpo.builder()
			.type(community.getType())
			.thumbnail(community.getThumbnail())
			.name(community.getName())
			.description(community.getDescription())
			.managerId(community.getManagerId())
			.managerName(community.getManagerName())
			.managerEmail(community.getManagerEmail())
			.memberCount(community.getMemberCount())
			.registeredTime(community.getRegisteredTime())
			.registrant(community.getRegistrant())
			.modifiedTime(community.getModifiedTime())
			.modifier(community.getModifier())
			.secretNumber(community.getSecretNumber())
			.allowSelfJoin(community.isAllowSelfJoin())
			.build();
	}
}