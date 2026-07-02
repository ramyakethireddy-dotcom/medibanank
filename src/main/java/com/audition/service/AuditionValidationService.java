package com.audition.service;

import com.audition.common.exception.SystemException;
import org.springframework.stereotype.Service;

/**
 * Validates query and path parameters for audition endpoints.
 */
@Service
public class AuditionValidationService {

    private static final String VALIDATION_TITLE = "Validation Error";
    private static final int BAD_REQUEST = 400;

    /**
     * Validates the post filter parameters accepted by the API.
     *
     * @param userId the optional user id filter
     * @param id the optional post id filter
     * @param title the optional title filter
     */
    public void validatePostFilters(final Integer userId, final Integer id, final String title) {
        validatePositiveInteger(userId, "userId");
        validatePositiveInteger(id, "id");

        if (title != null && title.length() > 100) {
            throw new SystemException("title cannot exceed 100 characters", VALIDATION_TITLE, BAD_REQUEST);
        }
    }

    /**
     * Validates a post id path parameter.
     *
     * @param postId the post id to validate
     */
    public void validatePostId(final Integer postId) {
        validatePositiveInteger(postId, "id");
    }

    /**
     * Ensures a numeric value is positive when it is present.
     *
     * @param value the value to validate
     * @param fieldName the field name used in the exception message
     */
    private void validatePositiveInteger(final Integer value, final String fieldName) {
        if (value != null && value <= 0) {
            throw new SystemException(fieldName + " must be a positive integer", VALIDATION_TITLE, BAD_REQUEST);
        }
    }
}
