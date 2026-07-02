package com.audition.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Copies trace identifiers from MDC into inbound HTTP responses.
 */
@Component
public class ResponseHeaderInjector implements HandlerInterceptor {

    private static final String TRACE_ID_HEADER = "X-B3-TraceId";
    private static final String SPAN_ID_HEADER = "X-B3-SpanId";
    private static final String PARENT_SPAN_ID_HEADER = "X-B3-ParentSpanId";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String PARENT_SPAN_ID_KEY = "parentId";

    /**
     * Copies trace values into response headers before controller execution.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param handler the selected handler for the request
     * @return always {@code true}
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
        final Object handler) {
        injectResponseHeaders(response);
        return true;
    }

    /**
     * Re-applies trace values to the response after handler execution.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @param handler the selected handler for the request
     * @param modelAndView the model and view returned from the handler
     */
    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response,
        final Object handler, final ModelAndView modelAndView) {
        injectResponseHeaders(response);
    }

    /**
     * Adds the current trace values to the response when they are present in MDC.
     *
     * @param response the response to enrich
     */
    public void injectResponseHeaders(final HttpServletResponse response) {
        if (response.isCommitted()) {
            return;
        }

        addHeaderIfPresent(response, TRACE_ID_HEADER, firstNonBlank(MDC.get(TRACE_ID_KEY), MDC.get(TRACE_ID_HEADER)));
        addHeaderIfPresent(response, SPAN_ID_HEADER, firstNonBlank(MDC.get(SPAN_ID_KEY), MDC.get(SPAN_ID_HEADER)));
        addHeaderIfPresent(response, PARENT_SPAN_ID_HEADER,
            firstNonBlank(MDC.get(PARENT_SPAN_ID_KEY), MDC.get(PARENT_SPAN_ID_HEADER)));
    }

    /**
     * Sets a response header when the value contains text.
     *
     * @param response the response to update
     * @param headerName the response header name
     * @param headerValue the header value
     */
    private void addHeaderIfPresent(final HttpServletResponse response, final String headerName,
        final String headerValue) {
        if (StringUtils.hasText(headerValue)) {
            response.setHeader(headerName, headerValue);
        }
    }

    /**
     * Returns the first non-blank value from the provided candidates.
     *
     * @param first the first candidate value
     * @param second the fallback candidate value
     * @return the first non-blank value, or {@code null} if both are blank
     */
    private String firstNonBlank(final String first, final String second) {
        if (StringUtils.hasText(first)) {
            return first;
        }
        if (StringUtils.hasText(second)) {
            return second;
        }
        return null;
    }
}
