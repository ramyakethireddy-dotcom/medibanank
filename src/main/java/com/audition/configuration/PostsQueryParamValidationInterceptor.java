package com.audition.configuration;

import com.audition.common.exception.SystemException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Validates allowed query parameters for the /posts endpoint.
 */
@Component
public class PostsQueryParamValidationInterceptor implements HandlerInterceptor {

    private static final String VALIDATION_TITLE = "Validation Error";
    private static final int BAD_REQUEST = 400;

    private final Set<String> allowedQueryParameters;

    public PostsQueryParamValidationInterceptor(
        @Value("${audition.posts.allowed-query-parameters:userId,id,title}") final String allowedQueryParameters) {
        this.allowedQueryParameters = new HashSet<>(Arrays.asList(allowedQueryParameters.split(",")));
    }

    /**
     * Rejects unsupported query parameters before controller execution.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param handler the selected handler for the request
     * @return always {@code true} when validation succeeds
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        for (final String parameterName : request.getParameterMap().keySet()) {
            if (!allowedQueryParameters.contains(parameterName)) {
                throw new SystemException("Unsupported query parameter: " + parameterName, VALIDATION_TITLE, BAD_REQUEST);
            }
        }
        return true;
    }
}
