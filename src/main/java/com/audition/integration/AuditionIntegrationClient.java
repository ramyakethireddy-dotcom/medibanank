package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Performs outbound HTTP calls to the upstream JSONPlaceholder API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditionIntegrationClient {

    private static final String JSONPLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final String POSTS_ENDPOINT = JSONPLACEHOLDER_BASE_URL + "/posts";
    private static final String EXTERNAL_SERVICE_ERROR = "External Service Error";
    private static final String FAILED_FETCH_POSTS = "Failed to fetch posts from external service";
    private static final String COMMENTS_ENDPOINT = JSONPLACEHOLDER_BASE_URL + "/comments";
    private static final String FAILED_FETCH_COMMENTS = "Failed to fetch comments for post ";

    private final RestTemplate restTemplate;

    /**
     * Fetches all posts from the upstream service.
     *
     * @return the posts returned by the upstream service
     */
    public List<AuditionPost> getPosts() {
        try {
            log.info("Fetching all posts from {}", POSTS_ENDPOINT);
            final AuditionPost[] posts = restTemplate.getForObject(POSTS_ENDPOINT, AuditionPost[].class);
            if (posts == null) {
                log.warn("No posts returned from API");
                return new ArrayList<>();
            }
            return Arrays.asList(posts);
        } catch (final HttpClientErrorException e) {
            log.error("HTTP error while fetching posts: {} {}", e.getStatusCode(), e.getMessage(), e);
            throw new SystemException(FAILED_FETCH_POSTS, EXTERNAL_SERVICE_ERROR, e.getStatusCode().value(), e);
        } catch (final RestClientException e) {
            log.error("Error fetching posts", e);
            throw new SystemException(FAILED_FETCH_POSTS, EXTERNAL_SERVICE_ERROR, 500, e);
        }
    }

    /**
     * Fetches a single post by id from the upstream service.
     *
     * @param id the post id
     * @return the matching post
     */
    public AuditionPost getPostById(final int id) {
        try {
            log.info("Fetching post with id: {}", id);

            final String url = POSTS_ENDPOINT + "/" + id;
            final AuditionPost post = restTemplate.getForObject(url, AuditionPost.class);
            if (post == null) {
                log.warn("Post with id {} not found", id);
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found", 404);
            }
            return post;
        } catch (final HttpClientErrorException e) {
            throw handleHttpClientError(id, e);
        } catch (final RestClientException e) {
            log.error("Unexpected error fetching post {}", id, e);
            throw new SystemException("Failed to fetch post with id " + id, EXTERNAL_SERVICE_ERROR, 500, e);
        }
    }

    /**
     * Maps downstream client errors into application exceptions.
     *
     * @param id the post id being requested
     * @param e the downstream exception
     * @return the mapped system exception
     */
    private SystemException handleHttpClientError(final int id, final HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.warn("Post with id {} not found", id);
            return new SystemException("Cannot find a Post with id " + id, "Resource Not Found", 404, e);
        }

        log.error("HTTP error while fetching post {}: {} - {}", id, e.getStatusCode(), e.getMessage(), e);
        return new SystemException("Failed to fetch post with id " + id, EXTERNAL_SERVICE_ERROR,
            e.getStatusCode().value(), e);
    }

    /**
     * Fetches comments for a post using the upstream comments endpoint.
     *
     * @param postId the post id
     * @return the comments returned by the upstream service
     */
    public List<Comment> getCommentsByPostId(final int postId) {
        return fetchComments(postId, COMMENTS_ENDPOINT + "?postId=" + postId, "from comments query");
    }

    /**
     * Fetches comments for a post using the upstream post comments endpoint.
     *
     * @param postId the post id
     * @return the comments returned by the upstream service
     */
    public List<Comment> getCommentsByPostIdFromEndpoint(final int postId) {
        return fetchComments(postId, POSTS_ENDPOINT + "/" + postId + "/comments", "from post endpoint");
    }

    /**
     * Executes the downstream request for comments and normalizes common failures.
     *
     * @param postId the post id
     * @param url the downstream URL to call
     * @param source human-readable description of the request source
     * @return the comments returned by the upstream service
     */
    private List<Comment> fetchComments(final int postId, final String url, final String source) {
        try {
            log.info("Fetching comments {} for post id: {}", source, postId);
            final Comment[] comments = restTemplate.getForObject(url, Comment[].class);
            if (comments == null) {
                log.warn("No comments returned {} for post {}", source, postId);
                return new ArrayList<>();
            }
            return Arrays.asList(comments);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Post {} not found", postId);
                throw new SystemException("Cannot find a Post with id " + postId, "Resource Not Found", 404, e);
            }
            log.error("HTTP error while fetching comments {} for post {}: {} {}", source, postId,
                e.getStatusCode(), e.getMessage(), e);
            throw new SystemException(FAILED_FETCH_COMMENTS + postId, EXTERNAL_SERVICE_ERROR,
                e.getStatusCode().value(), e);
        } catch (final RestClientException e) {
            log.error("Error fetching comments {} for post {}", source, postId, e);
            throw new SystemException(FAILED_FETCH_COMMENTS + postId, EXTERNAL_SERVICE_ERROR, 500, e);
        }
    }

}
