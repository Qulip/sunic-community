package com.sunic.community.aggregate.community.store.jpo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class MemberJpo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private CommunityJpo community;
    
    private Integer userId;
    private Long joinedTime;
    private Integer registrant;
}