package com.sunic.community.rest.rest.community.dto;

import com.sunic.community.spec.community.entity.CommunityType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityModifyRequest {
    
    private CommunityType type;
    private String thumbnail;
    private String name;
    private String description;
    
    @NotNull(message = "Modifier is required")
    private Integer modifier;
}