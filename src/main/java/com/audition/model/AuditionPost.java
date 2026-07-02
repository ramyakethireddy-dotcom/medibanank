package com.audition.model;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Audition post returned by the API")
public class AuditionPost {

    @Schema(description = "Post owner identifier", example = "1")
    private Integer userId;
    @Schema(description = "Post identifier", example = "10")
    private Integer id;
    @Schema(description = "Post title", example = "A sample post")
    private String title;
    @Schema(description = "Post body", example = "Lorem ipsum dolor sit amet.")
    private String body;
    @Schema(description = "Comments attached to the post")
    private List<Comment> comments;
}
