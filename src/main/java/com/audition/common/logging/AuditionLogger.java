package com.audition.common.logging;

import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
public class AuditionLogger {

    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    public void debug(final Logger logger, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public void warn(final Logger logger, final String message) {
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
    }

    public void error(final Logger logger, final String message) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {
        if (logger.isErrorEnabled()) {
            logger.error(createBasicErrorResponseMessage(errorCode, message) + "\n");
        }
    }

    private String createStandardProblemDetailMessage(final ProblemDetail standardProblemDetail) {
        if (standardProblemDetail == null) {
            return StringUtils.EMPTY;
        }

        final StringJoiner joiner = new StringJoiner(" | ", "API Error - ", "");

        if (standardProblemDetail.getTitle() != null) {
            joiner.add("Title: " + standardProblemDetail.getTitle());
        }

        joiner.add("Status: " + standardProblemDetail.getStatus());

        if (standardProblemDetail.getDetail() != null) {
            joiner.add("Detail: " + standardProblemDetail.getDetail());
        }

        return joiner.toString();
    }

    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        if (errorCode == null) {
            return "Error: " + (StringUtils.isNotBlank(message) ? message : "Unknown error");
        }
        return "Error [" + errorCode + "]: " + (StringUtils.isNotBlank(message) ? message : "An error occurred");
    }
}
