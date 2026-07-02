package com.audition.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import com.audition.service.AuditionValidationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Audition Controller Tests")
class AuditionControllerTest {

    @Mock
    private AuditionService auditionService;

    @Mock
    private AuditionValidationService auditionValidationService;

    @InjectMocks
    private AuditionController auditionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return all posts without filter")
    void testGetPostsWithoutFilter() {
        final List<AuditionPost> mockPosts = createMockPosts(2);
        when(auditionService.getPostsWithFilterCriteria(null, null, null)).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionController.getPosts(null, null, null);

        assertEquals(2, result.size());
        verify(auditionService, times(1)).getPostsWithFilterCriteria(null, null, null);
    }

    @Test
    @DisplayName("Should return filtered posts when filters are provided")
    void testGetPostsWithFilters() {
        final List<AuditionPost> mockPosts = createMockPosts(1);
        when(auditionService.getPostsWithFilterCriteria(1, 1, "post")).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionController.getPosts(1, 1, "post");

        assertEquals(1, result.size());
        verify(auditionService, times(1)).getPostsWithFilterCriteria(1, 1, "post");
    }

    @Test
    @DisplayName("Should return post by valid ID")
    void testGetPostByValidId() {
        final AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setTitle("Test Post");
        when(auditionService.getPostById(1)).thenReturn(mockPost);

        final AuditionPost result = auditionController.getPostById(1);

        assertEquals(1, result.getId());
        verify(auditionService, times(1)).getPostById(1);
    }

    @Test
    @DisplayName("Should throw SystemException for invalid post ID")
    void testGetPostWithInvalidId() {
        doThrow(new SystemException("id must be a positive integer", "Validation Error", 400))
            .when(auditionValidationService).validatePostId(0);

        assertThrows(SystemException.class, () -> auditionController.getPostById(0));
    }

    @Test
    @DisplayName("Should return comments for a valid post")
    void testGetPostComments() {
        final AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setComments(createMockComments(2));

        when(auditionService.getPostById(1)).thenReturn(mockPost);

        final List<Comment> result = auditionController.getCommentsByPostId(1);

        assertEquals(2, result.size());
        verify(auditionService, times(1)).getPostById(1);
    }

    @Test
    @DisplayName("Should return empty list when post has no comments")
    void testGetPostCommentsEmpty() {
        final AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setComments(new ArrayList<>());

        when(auditionService.getPostById(1)).thenReturn(mockPost);

        final List<Comment> result = auditionController.getCommentsByPostId(1);

        assertEquals(0, result.size());
        verify(auditionService, times(1)).getPostById(1);
    }

    @Test
    @DisplayName("Should throw SystemException for invalid comment request ID")
    void testGetPostCommentsWithInvalidId() {
        doThrow(new SystemException("id must be a positive integer", "Validation Error", 400))
            .when(auditionValidationService).validatePostId(-1);

        assertThrows(SystemException.class, () -> auditionController.getCommentsByPostId(-1));
    }

    private List<AuditionPost> createMockPosts(final int count) {
        final List<AuditionPost> posts = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            final AuditionPost post = new AuditionPost();
            post.setId(i);
            post.setUserId(1);
            post.setTitle("Post " + i);
            post.setBody("Body of post " + i);
            posts.add(post);
        }
        return posts;
    }

    private List<Comment> createMockComments(final int count) {
        final List<Comment> comments = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            final Comment comment = new Comment();
            comment.setId(i);
            comment.setPostId(1);
            comment.setName("Comment " + i);
            comment.setEmail("test@example.com");
            comment.setBody("Comment body " + i);
            comments.add(comment);
        }
        return comments;
    }
}
