package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@DisplayName("Audition Integration Client Tests")
class AuditionIntegrationClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully fetch all posts")
    void testGetPostsSuccess() {
        AuditionPost[] mockPosts = createMockPostArray(2);
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class))
            .thenReturn(mockPosts);

        List<AuditionPost> result = auditionIntegrationClient.getPosts();

        assertEquals(2, result.size());
        verify(restTemplate, times(1))
            .getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class);
    }

    @Test
    @DisplayName("Should return empty list when API returns null")
    void testGetPostsNull() {
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class))
            .thenReturn(null);

        List<AuditionPost> result = auditionIntegrationClient.getPosts();

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should throw SystemException when API call fails")
    void testGetPostsError() {
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts", AuditionPost[].class))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPosts());

        assertEquals(500, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should successfully fetch post by ID")
    void testGetPostByIdSuccess() {
        AuditionPost mockPost = new AuditionPost();
        mockPost.setId(1);
        mockPost.setTitle("Test Post");

        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenReturn(mockPost);

        AuditionPost result = auditionIntegrationClient.getPostById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Post", result.getTitle());
    }

    @Test
    @DisplayName("Should throw SystemException when post not found")
    void testGetPostByIdNotFound() {
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/999", AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById(999));

        assertEquals(404, exception.getStatusCode());
        assertEquals("Resource Not Found", exception.getTitle());
    }

    @Test
    @DisplayName("Should throw SystemException when post API returns null")
    void testGetPostByIdNull() {
        when(restTemplate.getForObject("https://jsonplaceholder.typicode.com/posts/1", AuditionPost.class))
            .thenReturn(null);

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById(1));

        assertEquals(404, exception.getStatusCode());
    }

    private AuditionPost[] createMockPostArray(int count) {
        AuditionPost[] posts = new AuditionPost[count];
        for (int i = 0; i < count; i++) {
            posts[i] = new AuditionPost();
            posts[i].setId(i + 1);
            posts[i].setUserId(1);
            posts[i].setTitle("Post " + (i + 1));
            posts[i].setBody("Body of post " + (i + 1));
        }
        return posts;
    }
}
