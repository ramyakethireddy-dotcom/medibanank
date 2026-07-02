package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import com.audition.service.AuditionValidationService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes post and comment endpoints for the audition API.
 */
@Tag(name = "Audition Posts", description = "Endpoints for posts and comments")
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuditionController {

    public final AuditionService auditionService;
    public final AuditionValidationService auditionValidationService;

    /**
     * Returns posts matching the optional query parameters. Here it won't return the comments of the posts, only the post data itself.
     * The comments can be retrieved separately using the /posts/{id}/comments or /posts/{id} endpoint.
     *
     * @param userId the optional user id filter
     * @param id the optional post id filter
     * @param title the optional title filter
     * @return the matching posts
     */
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get posts", description = "Returns posts filtered by optional userId, id, and title query parameters")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Posts returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AuditionPost.class)))),
        @ApiResponse(responseCode = "400", description = "Invalid query parameters",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public @ResponseBody List<AuditionPost> getPosts(
        @Parameter(description = "Filter by owner user id", example = "1")
        @RequestParam(value = "userId", required = false) final Integer userId,
        @Parameter(description = "Filter by post id", example = "10")
        @RequestParam(value = "id", required = false) final Integer id,
        @Parameter(description = "Filter by title text", example = "post")
        @RequestParam(value = "title", required = false) final String title) {
        auditionValidationService.validatePostFilters(userId, id, title);
        log.info("Fetching posts with userId filter: {}", userId);
        return auditionService.getPostsWithFilterCriteria(userId, id, title);
    }

    /**
     * Returns a post by id. It will include comments also if they exist.
     * If you want to get only the comments of a post, you can use the /posts/{id}/comments endpoint.
     *
     * @param postId the post id path parameter
     * @return the matching post
     */
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get post by id", description = "Returns a single post and its comments")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Post returned successfully",
            content = @Content(schema = @Schema(implementation = AuditionPost.class))),
        @ApiResponse(responseCode = "400", description = "Invalid post id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Post not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public @ResponseBody AuditionPost getPostById(
        @Parameter(description = "Post id", example = "1")
        @PathVariable("id") final Integer postId) {
        auditionValidationService.validatePostId(postId);
        log.info("Fetching post with id: {}", postId);

        return auditionService.getPostById(postId);
    }

    /**
     * Returns the comments for a post.
     *
     * @param postId the post id path parameter
     * @return the comments associated with the post
     */
    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get comments by post id", description = "Returns the comments for a specific post")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comments returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Comment.class)))),
        @ApiResponse(responseCode = "400", description = "Invalid post id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Post not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public @ResponseBody List<Comment> getCommentsByPostId(
        @Parameter(description = "Post id", example = "1")
        @PathVariable("id") final Integer postId) {
        auditionValidationService.validatePostId(postId);
        log.info("Fetching comments for post with id: {}", postId);
        final AuditionPost post = auditionService.getPostById(postId);
        return post.getComments() != null ? post.getComments() : List.of();
    }

}
