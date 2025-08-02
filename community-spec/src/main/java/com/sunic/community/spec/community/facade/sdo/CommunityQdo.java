package com.sunic.community.spec.community.facade.sdo;

import com.sunic.community.spec.community.entity.CommunityType;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommunityQdo {
	private final String name;
	private final CommunityType type;
	private final String managerId;
	private final Integer page;
	private final Integer size;
	private final String sortBy;
	private final String sortDirection;
}