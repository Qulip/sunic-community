package com.sunic.community.rest.rest.community.dto;

import com.sunic.community.spec.community.entity.CommunityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityRegisterRequest {
    
    @NotNull(message = "Community type is required")
    private CommunityType type;
    
    private String thumbnail;
    
    @NotBlank(message = "Community name is required")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Manager ID is required")
    private String managerId;
    
    @NotBlank(message = "Manager name is required")
    private String managerName;
    
    @NotBlank(message = "Manager email is required")
    private String managerEmail;
    
    @NotNull(message = "Registrant is required")
    private Integer registrant;
    
    private boolean allowSelfJoin;
    
    private String secretNumber;
}