package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.audition.common.exception.SystemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Audition Validation Service Tests")
class AuditionValidationServiceTest {

    private final AuditionValidationService auditionValidationService = new AuditionValidationService();

    @Test
    @DisplayName("Should allow valid filter parameters")
    void testValidatePostFiltersSuccess() {
        assertDoesNotThrow(() -> auditionValidationService.validatePostFilters(1, 2, "post"));
    }

    @Test
    @DisplayName("Should reject negative userId")
    void testValidateNegativeUserId() {
        final SystemException exception = assertThrows(SystemException.class,
            () -> auditionValidationService.validatePostFilters(-1, null, null));

        assertEquals(400, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should reject overly long title")
    void testValidateTitleLength() {
        final String title = "a".repeat(101);
        final SystemException exception = assertThrows(SystemException.class,
            () -> auditionValidationService.validatePostFilters(null, null, title));

        assertEquals(400, exception.getStatusCode());
    }

    @Test
    @DisplayName("Should allow valid post id")
    void testValidatePostIdSuccess() {
        assertDoesNotThrow(() -> auditionValidationService.validatePostId(1));
    }

    @Test
    @DisplayName("Should reject invalid post id")
    void testValidatePostIdFailure() {
        final SystemException exception = assertThrows(SystemException.class,
            () -> auditionValidationService.validatePostId(0));

        assertEquals(400, exception.getStatusCode());
    }
}
