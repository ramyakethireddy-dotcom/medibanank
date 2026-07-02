package com.audition.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Comment attached to a post")
public class Comment {
    @Schema(description = "Identifier of the parent post", example = "1")
    private int postId;
    @Schema(description = "Comment identifier", example = "5")
    private int id;
    @Schema(description = "Commenter name", example = "Jane Doe")
    private String name;
    @Schema(description = "Commenter email", example = "jane@example.com")
    private String email;
    @Schema(description = "Comment body", example = "Great post!")
    private String body;

}
