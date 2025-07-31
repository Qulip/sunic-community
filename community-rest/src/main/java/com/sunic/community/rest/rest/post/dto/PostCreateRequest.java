package com.sunic.community.rest.rest.post.dto;

import com.sunic.community.spec.post.entity.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Post type is required")
    private PostType postType;
    
    @NotNull(message = "Community ID is required")
    private Integer communityId;
    
    @NotNull(message = "Registrant is required")
    private Integer registrant;
}