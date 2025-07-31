package com.sunic.community.aggregate.community.store.jpo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Table(name = "community")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class CommunityJpo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private CommunityTypeJpo type;
    
    private String thumbnail;
    private String name;
    private String description;
    private String managerId;
    private String managerName;
    private String managerEmail;

    @ColumnDefault("0")
    private Long memberCount;

    private Long registeredTime;
    private Integer registrant;
    private Long modifiedTime;
    private Integer modifier;
    private String secretNumber;
    private boolean allowSelfJoin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private List<MemberJpo> members;

    public void updateDetails(CommunityTypeJpo type, String thumbnail, String name, String description, Integer modifier) {
        this.type = type;
        this.thumbnail = thumbnail;
        this.name = name;
        this.description = description;
        this.modifier = modifier;
        this.modifiedTime = System.currentTimeMillis();
    }

    public void incrementMemberCount() {
        this.memberCount = (this.memberCount == null ? 0 : this.memberCount) + 1;
    }

    public void decrementMemberCount() {
        this.memberCount = (this.memberCount == null || this.memberCount <= 0) ? 0 : this.memberCount - 1;
    }
}