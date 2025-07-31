package com.sunic.community.spec.community.entity;

import com.sunic.community.spec.community.facade.sdo.CommunityModifySdo;
import com.sunic.community.spec.community.facade.sdo.CommunityRegisterSdo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder(toBuilder = true)
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

    public static Community create(CommunityRegisterSdo sdo) {
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

    public Community modify(CommunityModifySdo sdo) {
        long currentTime = System.currentTimeMillis();
        return this.toBuilder()
                .type(sdo.getType())
                .thumbnail(sdo.getThumbnail())
                .name(sdo.getName())
                .description(sdo.getDescription())
                .modifiedTime(currentTime)
                .modifier(sdo.getModifier())
                .build();
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
}