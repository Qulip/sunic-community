package com.sunic.community.spec.community.facade.sdo;

import com.sunic.community.spec.community.entity.CommunityType;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommunityUdo {
	private final Integer id;
	private final CommunityType type;
	private final String thumbnail;
	private final String name;
	private final String description;
	private final Integer modifier;
}