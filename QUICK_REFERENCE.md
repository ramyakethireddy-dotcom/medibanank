# Quick Reference

## Run the application

1. From project root:

```bash
./gradlew bootRun
```

For Windows (PowerShell):

```powershell
.\gradlew.bat bootRun
```

App default URL: `http://localhost:8080`

## Build and test

- Run unit tests:

```bash
./gradlew test
```

- Build the application:

```bash
./gradlew clean build
```

Windows (PowerShell):

```powershell
.\gradlew.bat test
.\gradlew.bat clean build
```

## Swagger

- UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/posts` | List posts with optional filters |
| GET | `/posts/{id}` | Get one post with comments |
| GET | `/posts/{id}/comments` | Get comments for a post |

## Query parameters for `/posts`

| Parameter | Allowed | Notes |
| --- | --- | --- |
| `userId` | yes | Positive integer |
| `id` | yes | Positive integer |
| `title` | yes | Case-insensitive match |

Unsupported params like `tite` return `400`.

## Sample endpoint calls

```bash
# Get all posts
curl "http://localhost:8080/posts"

# Filter by userId
curl "http://localhost:8080/posts?userId=1"

# Filter by title
curl "http://localhost:8080/posts?title=qui"

# Get post by id
curl "http://localhost:8080/posts/1"

# Get comments for a post
curl "http://localhost:8080/posts/1/comments"
```

## Error shape

```json
{
  "title": "Validation Error",
  "status": 400,
  "detail": "userId must be a positive integer",
  "instance": "/posts"
}
```

## Actuator

Only the configured actuator endpoints are exposed in `application.yml`.

## Notes

- Case-insensitive filtering uses `Locale.ROOT`.