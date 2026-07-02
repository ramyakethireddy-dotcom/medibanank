package com.audition.service;

import com.audition.common.exception.SystemException;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Provides post retrieval and filtering operations for the web layer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditionService {

    private final AuditionIntegrationClient auditionIntegrationClient;

    /**
     * Returns all posts from the downstream integration client.
     *
     * @return all available posts
     */
    public List<AuditionPost> getPosts() {
        return auditionIntegrationClient.getPosts();
    }

    /**
     * Returns posts filtered by the supplied optional query parameters.
     *
     * @param userId the optional user id filter
     * @param id the optional post id filter
     * @param title the optional title filter
     * @return posts matching the supplied filters
     */
    public List<AuditionPost> getPostsWithFilterCriteria(final Integer userId, final Integer id, final String title) {
        final List<AuditionPost> posts = auditionIntegrationClient.getPosts();
        //Return all posts if no filters are provided
        if (userId == null && id == null && (title == null || title.isBlank())) {
            return posts;
        }

        log.info("Filtering posts by userId: {}, id: {}, title: {}", userId, id, title);
        return posts.stream()
            .filter(post -> matchesUserId(post, userId))
            .filter(post -> matchesId(post, id))
            .filter(post -> matchesTitle(post, title))
            .toList();
    }

    /**
     * Checks whether the post matches the requested user id.
     *
     * @param post the post to evaluate
     * @param userId the requested user id
     * @return {@code true} when the post matches or the filter is absent
     */
    private boolean matchesUserId(final AuditionPost post, final Integer userId) {
        //Assuming every existing post has userId, otherwise we can use Objects.equals(post.getUserId(), userId) to handle nulls
        return userId == null || (post.getUserId() != null && post.getUserId().equals(userId));
    }

    /**
     * Checks whether the post matches the requested id.
     *
     * @param post the post to evaluate
     * @param id the requested post id
     * @return {@code true} when the post matches or the filter is absent
     */
    private boolean matchesId(final AuditionPost post, final Integer id) {
        //Assuming every existing post has id, otherwise we can use Objects.equals(post.getId(), id) to handle nulls
        return id == null || (post.getId() != null && post.getId().equals(id));
    }

    /**
     * Checks whether the post title contains the supplied text.
     *
     * @param post the post to evaluate
     * @param title the requested title text
     * @return {@code true} when the post matches or the filter is absent
     */
    private boolean matchesTitle(final AuditionPost post, final String title) {
      // Using case-insensitive comparison to check if the post title contains the requested text
        return title == null || title.isBlank()
            || (post.getTitle() != null
            && post.getTitle().toLowerCase(Locale.ROOT).contains(title.toLowerCase(Locale.ROOT)));
    }

    /**
     * Returns a post by id and enriches it with comments from the downstream service.
     *
     * @param postId the post identifier
     * @return the post with comments attached
     */
    public AuditionPost getPostById(final int postId) {
        final AuditionPost post = auditionIntegrationClient.getPostById(postId);
        attachComments(post, postId);
        return post;
    }

    /**
     * Attempts to attach comments to the supplied post.
     *
     * @param post the post to enrich
     * @param postId the post identifier used to fetch comments
     */
    private void attachComments(final AuditionPost post, final int postId) {
        try {
            post.setComments(auditionIntegrationClient.getCommentsByPostIdFromEndpoint(postId));
        } catch (final SystemException e) {
            log.warn("Failed to fetch comments for post {}: {}", postId, e.getMessage());
            post.setComments(List.of());
        }
    }

}
