package com.audition.web.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import io.micrometer.common.util.StringUtils;
import java.net.URI;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private static final String ERROR_MESSAGE = " Error Code from Exception could not be mapped to a valid HttpStatus Code - ";
    private static final String DEFAULT_MESSAGE = "API Error occurred. Please contact support or administrator.";
    private static final String PROBLEM_TYPE_PREFIX = "urn:audition:problem:";
    private static final String METHOD_NOT_ALLOWED_TYPE = "method-not-allowed";
    private static final String INTERNAL_SERVER_ERROR_TYPE = "internal-server-error";

    private final AuditionLogger logger;

    /**
     * Handles downstream client exceptions using the remote status code.
     *
     * @param e the exception raised by the downstream call
     * @return the mapped problem detail
     */
    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(final HttpClientErrorException e) {
        return createProblemDetail(e, e.getStatusCode());

    }


    /**
     * Handles unexpected exceptions and maps them to a server error response.
     *
     * @param e the exception that was raised
     * @return the mapped problem detail
     */
    @ExceptionHandler(Exception.class)
    ProblemDetail handleMainException(final Exception e) {
        logger.logErrorWithException(log, "Unexpected exception occurred", e);
        final HttpStatusCode status = getHttpStatusCodeFromException(e);
        return createProblemDetail(e, status);
    }

    /**
     * Handles application exceptions using the status code stored on the exception.
     *
     * @param e the exception that was raised
     * @return the mapped problem detail
     */
    @ExceptionHandler(SystemException.class)
    ProblemDetail handleSystemException(final SystemException e) {
        logger.logErrorWithException(log, "System exception occurred: " + e.getTitle(), e);
        final HttpStatusCode status = getHttpStatusCodeFromSystemException(e);
        return createProblemDetail(e, status);
    }


    /**
     * Creates a problem detail payload for the supplied exception and status code.
     *
     * @param exception the exception being rendered
     * @param statusCode the HTTP status to return
     * @return the populated problem detail
     */
    private ProblemDetail createProblemDetail(final Exception exception,
        final HttpStatusCode statusCode) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(statusCode);
        problemDetail.setDetail(getMessageFromException(exception));
        if (exception instanceof SystemException) {
            final SystemException systemException = (SystemException) exception;
            problemDetail.setTitle(systemException.getTitle());
        } else {
            problemDetail.setTitle(DEFAULT_TITLE);
        }
        problemDetail.setType(URI.create(PROBLEM_TYPE_PREFIX + resolveProblemTypeSlug(exception, statusCode)));
        return problemDetail;
    }

    private String resolveProblemTypeSlug(final Exception exception, final HttpStatusCode statusCode) {
        if (exception instanceof SystemException) {
            final SystemException systemException = (SystemException) exception;
            if (StringUtils.isNotBlank(systemException.getTitle())) {
                return slugify(systemException.getTitle());
            }
        }
        if (exception instanceof HttpClientErrorException) {
            final HttpClientErrorException clientErrorException = (HttpClientErrorException) exception;
            if (StringUtils.isNotBlank(clientErrorException.getStatusText())) {
                return slugify(clientErrorException.getStatusText());
            }
        }
        if (statusCode.value() == METHOD_NOT_ALLOWED.value()) {
            return METHOD_NOT_ALLOWED_TYPE;
        }
        if (statusCode.value() == INTERNAL_SERVER_ERROR.value()) {
            return INTERNAL_SERVER_ERROR_TYPE;
        }
        return "http-" + statusCode.value();
    }

    private String slugify(final String value) {
        return value.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    }

    /**
     * Returns a safe error message for the response body.
     *
     * @param exception the exception to inspect
     * @return the exception message or a default fallback
     */
    private String getMessageFromException(final Exception exception) {
        if (StringUtils.isNotBlank(exception.getMessage())) {
            return exception.getMessage();
        }
        return DEFAULT_MESSAGE;
    }

    /**
     * Resolves the HTTP status code from a system exception.
     *
     * @param exception the system exception to inspect
     * @return the mapped status code
     */
    private HttpStatusCode getHttpStatusCodeFromSystemException(final SystemException exception) {
        try {
            return HttpStatusCode.valueOf(exception.getStatusCode());
        } catch (final IllegalArgumentException iae) {
            logger.info(log, ERROR_MESSAGE + exception.getStatusCode());
            return INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Resolves the HTTP status code for non-system exceptions.
     *
     * @param exception the exception to inspect
     * @return the mapped status code
     */
    private HttpStatusCode getHttpStatusCodeFromException(final Exception exception) {
        if (exception instanceof HttpClientErrorException) {
            return ((HttpClientErrorException) exception).getStatusCode();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return METHOD_NOT_ALLOWED;
        }
        return INTERNAL_SERVER_ERROR;
    }
}
