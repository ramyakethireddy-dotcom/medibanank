package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@DisplayName("Response Header Injector Tests")
class ResponseHeaderInjectorTest {

    private final ResponseHeaderInjector responseHeaderInjector = new ResponseHeaderInjector();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should copy trace identifiers into response headers")
    void shouldCopyTraceIdentifiersIntoResponseHeaders() {
        MDC.put("traceId", "trace-123");
        MDC.put("spanId", "span-456");
        MDC.put("parentId", "parent-789");

        final MockHttpServletResponse response = new MockHttpServletResponse();

        responseHeaderInjector.injectResponseHeaders(response);

        assertEquals("trace-123", response.getHeader("X-B3-TraceId"));
        assertEquals("span-456", response.getHeader("X-B3-SpanId"));
        assertEquals("parent-789", response.getHeader("X-B3-ParentSpanId"));
    }

    @Test
    @DisplayName("Should inject headers during pre handle")
    void shouldInjectHeadersDuringPreHandle() {
        MDC.put("traceId", "trace-abc");

        final HttpServletRequest request = new MockHttpServletRequest();
        final HttpServletResponse response = new MockHttpServletResponse();

        responseHeaderInjector.preHandle(request, response, new Object());

        assertEquals("trace-abc", response.getHeader("X-B3-TraceId"));
    }
}
