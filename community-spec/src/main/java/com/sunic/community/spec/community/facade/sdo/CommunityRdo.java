package com.sunic.community.spec.community.facade.sdo;

import com.sunic.community.spec.community.entity.CommunityType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CommunityRdo {
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
    private final boolean allowSelfJoin;
}