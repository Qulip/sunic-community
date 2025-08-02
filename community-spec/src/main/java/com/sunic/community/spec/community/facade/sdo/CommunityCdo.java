package com.sunic.community.spec.community.facade.sdo;

import com.sunic.community.spec.community.entity.CommunityType;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommunityCdo {
	private final CommunityType type;
	private final String thumbnail;
	private final String name;
	private final String description;
	private final String managerId;
	private final String managerName;
	private final String managerEmail;
	private final Integer registrant;
	private final boolean allowSelfJoin;
	private final String secretNumber;
}