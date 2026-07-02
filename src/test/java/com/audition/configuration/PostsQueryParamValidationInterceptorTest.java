package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.audition.common.exception.SystemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@DisplayName("Posts Query Param Validation Interceptor Tests")
class PostsQueryParamValidationInterceptorTest {

    private final PostsQueryParamValidationInterceptor interceptor =
        new PostsQueryParamValidationInterceptor("userId,id,title");

    @Test
    @DisplayName("Should allow whitelisted query params")
    void testAllowedQueryParams() {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts");
        request.addParameter("userId", "2");
        request.addParameter("title", "test");

        assertDoesNotThrow(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    @DisplayName("Should reject unsupported query params")
    void testUnsupportedQueryParams() {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/posts");
        request.addParameter("tite", "test");

        assertThrows(SystemException.class,
            () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }
}
