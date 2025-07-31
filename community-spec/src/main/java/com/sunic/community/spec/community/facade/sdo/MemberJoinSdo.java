package com.sunic.community.spec.community.facade.sdo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MemberJoinSdo {
    private final Integer communityId;
    private final Integer userId;
    private final Integer registrant;
    private final String secretNumber;
}