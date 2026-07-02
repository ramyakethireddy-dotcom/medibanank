# Implementation Notes

## Overview

This API exposes JSONPlaceholder-backed post and comment endpoints using Spring Boot, RestTemplate, and Spring MVC.

## Main design choices

- **Controller**: keeps request mapping and delegates to service/validation components.
- **Validation interceptor**: rejects unsupported `/posts` query parameters before controller execution.
- **Validation service**: centralizes input checks for positive IDs and allowed title length.
- **Global exception advice**: converts errors into `ProblemDetail` responses.
- **Service layer**: handles post filtering and comment enrichment.
- **AOP logging**: removes repeated entry/exit logging from controller/service methods.
- **Swagger/OpenAPI**: documents all public endpoints and models.

## Request flow

1. Incoming request hits Spring MVC.
2. `/posts` requests pass through the query-parameter interceptor.
3. Controller validates known input values.
4. Service fetches or filters data.
5. Exceptions are mapped by `ExceptionControllerAdvice` into JSON error responses.

## Error handling

- Validation errors return `400`.
- Unsupported query params return `400`.
- External service failures are mapped to the appropriate status.
- Error payloads use Spring `ProblemDetail` with a simple `title`, `status`, and `detail`.

## Cross-cutting concerns
- **Interceptor**: blocks unsupported query parameters.
- **Logging**: uses Spring Boot Logback defaults plus project-specific formatting.

## Notes

- `Locale.ROOT` is used for stable case-insensitive filtering.
- Swagger UI is available at `/swagger-ui/index.html`.
