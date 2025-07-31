package com.sunic.community.spec.community.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class Community {
    private final Integer id;
    private final CommunityType type;
    private final String thumbnail;
    private final String name;
    private final String description;
    private final String managerId;
    private final String managerName;
    private final String managerEmail;
    private final Long memberCount;
    private final Long registeredTime;
    private final Integer registrant;
    private final Long modifiedTime;
    private final Integer modifier;
    private final String secretNumber;
    private final boolean allowSelfJoin;
    private final List<Member> members;
}