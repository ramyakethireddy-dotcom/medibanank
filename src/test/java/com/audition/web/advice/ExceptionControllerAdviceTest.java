package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

@DisplayName("Exception Controller Advice Tests")
class ExceptionControllerAdviceTest {

    private final ExceptionControllerAdvice exceptionControllerAdvice =
        new ExceptionControllerAdvice(mock(AuditionLogger.class));

    @Test
    @DisplayName("Should set custom validation error type")
    void testValidationErrorType() {
        final ProblemDetail problemDetail = exceptionControllerAdvice.handleSystemException(
            new SystemException("userId must be a positive integer", "Validation Error", 400));

        assertEquals(URI.create("about:blank"), problemDetail.getType());
    }
}
