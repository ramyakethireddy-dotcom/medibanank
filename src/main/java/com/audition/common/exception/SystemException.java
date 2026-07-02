package com.audition.common.exception;

import lombok.Getter;

/**
 * Represents an application-specific runtime exception with HTTP response metadata.
 */
@Getter
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = -5876728854007114881L;

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private Integer statusCode;
    private String title;
    private String detail;

    /**
     * Creates an empty system exception.
     */
    public SystemException() {
        super();
    }

    /**
     * Creates a system exception with a message and default title.
     *
     * @param message the exception message
     */
    public SystemException(final String message) {
        super(message);
        this.title = DEFAULT_TITLE;
    }

    /**
     * Creates a system exception with a message and HTTP status code.
     *
     * @param message the exception message
     * @param errorCode the HTTP status code
     */
    public SystemException(final String message, final Integer errorCode) {
        super(message);
        this.title = DEFAULT_TITLE;
        this.statusCode = errorCode;
    }

    /**
     * Creates a system exception with a message and cause.
     *
     * @param message the exception message
     * @param exception the underlying cause
     */
    public SystemException(final String message, final Throwable exception) {
        super(message, exception);
        this.title = DEFAULT_TITLE;
    }

    /**
     * Creates a system exception with custom title and status code.
     *
     * @param detail the exception detail
     * @param title the response title
     * @param errorCode the HTTP status code
     */
    public SystemException(final String detail, final String title, final Integer errorCode) {
        super(detail);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }

    /**
     * Creates a system exception with custom title and cause.
     *
     * @param detail the exception detail
     * @param title the response title
     * @param exception the underlying cause
     */
    public SystemException(final String detail, final String title, final Throwable exception) {
        super(detail, exception);
        this.title = title;
        this.statusCode = 500;
        this.detail = detail;
    }

    /**
     * Creates a system exception with HTTP status code and cause.
     *
     * @param detail the exception detail
     * @param errorCode the HTTP status code
     * @param exception the underlying cause
     */
    public SystemException(final String detail, final Integer errorCode, final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = DEFAULT_TITLE;
        this.detail = detail;
    }

    /**
     * Creates a system exception with custom title, HTTP status code, and cause.
     *
     * @param detail the exception detail
     * @param title the response title
     * @param errorCode the HTTP status code
     * @param exception the underlying cause
     */
    public SystemException(final String detail, final String title, final Integer errorCode, final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }
}
