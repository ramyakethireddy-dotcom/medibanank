package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionPost;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Audition Service Tests")
class AuditionServiceTest {

    @Mock
    private AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private AuditionService auditionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return all posts without filter")
    void testGetPostsWithoutFilter() {
        final List<AuditionPost> mockPosts = createMockPosts(3);
        when(auditionIntegrationClient.getPosts()).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionService.getPostsWithFilterCriteria(null, null, null);

        assertEquals(3, result.size());
        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    @DisplayName("Should filter posts by userId")
    void testFilterPostsByUserId() {
        final List<AuditionPost> mockPosts = createMockPostsWithMultipleUsers();
        when(auditionIntegrationClient.getPosts()).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionService.getPostsWithFilterCriteria(1, null, null);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getUserId());
    }

    @Test
    @DisplayName("Should filter posts by id")
    void testFilterPostsById() {
        final List<AuditionPost> mockPosts = createMockPosts(3);
        when(auditionIntegrationClient.getPosts()).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionService.getPostsWithFilterCriteria(null, 2, null);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
    }

    @Test
    @DisplayName("Should filter posts by title ignoring case")
    void testFilterPostsByTitle() {
        final List<AuditionPost> mockPosts = createMockPosts(3);
        mockPosts.get(1).setTitle("Spring Boot testing");
        when(auditionIntegrationClient.getPosts()).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionService.getPostsWithFilterCriteria(null, null, "TESTING");

        assertEquals(1, result.size());
        assertEquals("Spring Boot testing", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Should apply all filters together")
    void testFilterPostsByAllCriteria() {
        final List<AuditionPost> mockPosts = createMockPosts(3);
        mockPosts.get(0).setTitle("Alpha Post");
        mockPosts.get(1).setUserId(2);
        when(auditionIntegrationClient.getPosts()).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionService.getPostsWithFilterCriteria(1, 1, "alpha");

        assertEquals(1, result.size());
        assertEquals("Alpha Post", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Should return no posts when filters do not match")
    void testFilterPostsNoMatch() {
        final List<AuditionPost> mockPosts = createMockPosts(2);
        when(auditionIntegrationClient.getPosts()).thenReturn(mockPosts);

        final List<AuditionPost> result = auditionService.getPostsWithFilterCriteria(99, 99, "missing");

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should successfully fetch post by ID")
    void testGetPostByIdSuccess() {
        final AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setTitle("Test Post");

        when(auditionIntegrationClient.getPostById(1)).thenReturn(mockPost);

        final AuditionPost result = auditionService.getPostById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(auditionIntegrationClient, times(1)).getPostById(1);
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

    private List<AuditionPost> createMockPostsWithMultipleUsers() {
        final List<AuditionPost> posts = new ArrayList<>();

        final AuditionPost firstPost = new AuditionPost();
        firstPost.setId(1);
        firstPost.setUserId(1);
        firstPost.setTitle("Post 1");
        firstPost.setBody("Body of post 1");
        posts.add(firstPost);

        final AuditionPost secondPost = new AuditionPost();
        secondPost.setId(2);
        secondPost.setUserId(2);
        secondPost.setTitle("Post 2");
        secondPost.setBody("Body of post 2");
        posts.add(secondPost);

        final AuditionPost thirdPost = new AuditionPost();
        thirdPost.setId(3);
        thirdPost.setUserId(1);
        thirdPost.setTitle("Post 3");
        thirdPost.setBody("Body of post 3");
        posts.add(thirdPost);

        return posts;
    }
}
