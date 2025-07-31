package com.sunic.community.rest.rest.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreateRequest {
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Registrant is required")
    private Integer registrant;
}