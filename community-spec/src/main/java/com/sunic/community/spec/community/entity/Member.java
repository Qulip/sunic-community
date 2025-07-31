package com.sunic.community.spec.community.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Member {
    private final Integer id;
    private final Integer communityId;
    private final Integer userId;
    private final Long joinedTime;
    private final Integer registrant;
}