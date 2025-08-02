package com.sunic.community.spec.community.entity;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.sunic.community.spec.community.facade.sdo.CommunityCdo;
import com.sunic.community.spec.community.facade.sdo.CommunityRdo;
import com.sunic.community.spec.community.facade.sdo.CommunityUdo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class Community {
	private Integer id;
	private CommunityType type;
	private String thumbnail;
	private String name;
	private String description;
	private String managerId;
	private String managerName;
	private String managerEmail;
	private Long memberCount;
	private Long registeredTime;
	private Integer registrant;
	private Long modifiedTime;
	private Integer modifier;
	private String secretNumber;
	private boolean allowSelfJoin;
	private List<Member> members;

	public static Community create(CommunityCdo sdo) {
		long currentTime = System.currentTimeMillis();
		return Community.builder()
			.type(sdo.getType())
			.thumbnail(sdo.getThumbnail())
			.name(sdo.getName())
			.description(sdo.getDescription())
			.managerId(sdo.getManagerId())
			.managerName(sdo.getManagerName())
			.managerEmail(sdo.getManagerEmail())
			.memberCount(0L)
			.registeredTime(currentTime)
			.registrant(sdo.getRegistrant())
			.modifiedTime(currentTime)
			.modifier(sdo.getRegistrant())
			.secretNumber(sdo.getSecretNumber())
			.allowSelfJoin(sdo.isAllowSelfJoin())
			.build();
	}

	public void modify(CommunityUdo sdo) {
		modifiedTime = System.currentTimeMillis();
		BeanUtils.copyProperties(sdo, this);
	}

	public Community addMember() {
		return this.toBuilder()
			.memberCount(this.memberCount + 1)
			.build();
	}

	public Community removeMember() {
		long newCount = Math.max(0L, this.memberCount - 1);
		return this.toBuilder()
			.memberCount(newCount)
			.build();
	}

	public CommunityRdo toRdo() {
		return CommunityRdo.builder()
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
			.registrant(registrant)
			.modifiedTime(modifiedTime)
			.modifier(modifier)
			.allowSelfJoin(allowSelfJoin)
			.secretNumber(secretNumber)
			.build();
	}
}