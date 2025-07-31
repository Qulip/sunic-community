package com.sunic.community.spec.community.entity;

import com.sunic.community.spec.community.facade.sdo.MemberJoinSdo;
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

    public static Member create(MemberJoinSdo sdo) {
        long currentTime = System.currentTimeMillis();
        return Member.builder()
                .communityId(sdo.getCommunityId())
                .userId(sdo.getUserId())
                .joinedTime(currentTime)
                .registrant(sdo.getRegistrant())
                .build();
    }
}