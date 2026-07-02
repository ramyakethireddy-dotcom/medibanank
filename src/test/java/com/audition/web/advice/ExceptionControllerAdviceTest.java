package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;

@DisplayName("Exception Controller Advice Tests")
class ExceptionControllerAdviceTest {

    private static final URI VALIDATION_ERROR_TYPE = URI.create("urn:audition:problem:validation-error");
    private static final URI RESOURCE_NOT_FOUND_TYPE = URI.create("urn:audition:problem:resource-not-found");
    private static final URI EXTERNAL_SERVICE_ERROR_TYPE = URI.create("urn:audition:problem:external-service-error");
    private static final URI HTTP_ERROR_TYPE = URI.create("urn:audition:problem:bad-request");
    private static final URI METHOD_NOT_ALLOWED_TYPE = URI.create("urn:audition:problem:method-not-allowed");
    private static final URI INTERNAL_SERVER_ERROR_TYPE = URI.create("urn:audition:problem:internal-server-error");

    private final ExceptionControllerAdvice exceptionControllerAdvice =
        new ExceptionControllerAdvice(mock(AuditionLogger.class));

    @Test
    @DisplayName("Should set custom validation error type")
    void testValidationErrorType() {
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleSystemException(
            new SystemException("userId must be a positive integer", "Validation Error", 400));

        assertProblemDetail(problemDetail, 400, "Validation Error", "userId must be a positive integer",
            VALIDATION_ERROR_TYPE);
    }

    @Test
    @DisplayName("Should set resource not found error type")
    void testResourceNotFoundErrorType() {
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleSystemException(
            new SystemException("Cannot find a Post with id 1", "Resource Not Found", 404));

        assertProblemDetail(problemDetail, 404, "Resource Not Found", "Cannot find a Post with id 1",
            RESOURCE_NOT_FOUND_TYPE);
    }

    @Test
    @DisplayName("Should set external service error type")
    void testExternalServiceErrorType() {
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleSystemException(
            new SystemException("Failed to fetch posts from external service", "External Service Error", 500));

        assertProblemDetail(problemDetail, 500, "External Service Error",
            "Failed to fetch posts from external service", EXTERNAL_SERVICE_ERROR_TYPE);
    }

    @Test
    @DisplayName("Should set downstream HTTP error type")
    void testHttpClientErrorType() {
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleHttpClientException(
            new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertProblemDetail(problemDetail, 400, "API Error Occurred", null, HTTP_ERROR_TYPE);
    }

    @Test
    @DisplayName("Should set method not allowed error type")
    void testMethodNotAllowedErrorType() {
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleMainException(
            new HttpRequestMethodNotSupportedException("POST"));

        assertProblemDetail(problemDetail, 405, "API Error Occurred",
            "Request method 'POST' is not supported", METHOD_NOT_ALLOWED_TYPE);
    }

    @Test
    @DisplayName("Should set generic server error type")
    void testGenericServerErrorType() {
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleMainException(
            new RuntimeException("Unexpected failure"));

        assertProblemDetail(problemDetail, 500, "API Error Occurred", "Unexpected failure",
            INTERNAL_SERVER_ERROR_TYPE);
    }

    private void assertProblemDetail(final ProblemDetail problemDetail, final int expectedStatus,
        final String expectedTitle, final String expectedDetail, final URI expectedType) {
        assertAll(
            () -> assertEquals(expectedStatus, problemDetail.getStatus()),
            () -> assertEquals(expectedTitle, problemDetail.getTitle()),
            () -> {
                if (expectedDetail == null) {
                    assertNotNull(problemDetail.getDetail());
                } else {
                    assertEquals(expectedDetail, problemDetail.getDetail());
                }
            },
            () -> assertEquals(expectedType, problemDetail.getType())
        );
    }
}
