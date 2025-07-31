package com.sunic.community.rest.rest.post.dto;

import com.sunic.community.spec.post.entity.PostType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequest {
    
    private String title;
    private String content;
    private PostType postType;
    
    @NotNull(message = "Modifier is required")
    private Integer modifier;
}