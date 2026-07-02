# Audition API

The purpose of this Spring Boot application is to test general knowledge of SpringBoot, Java, Gradle etc. It is created for hiring needs of our company but can be used for other purposes.

## Overarching expectations & Assessment areas

<pre>
This is not a university test. 
This is meant to be used for job applications and MUST showcase your full skillset. 
<b>As such, PRODUCTION-READY code must be written and submitted. </b> 
</pre>

- clean, easy to understand code
- good code structures
- Proper code encapsulation
- unit tests with minimum 80% coverage.
- A Working application to be submitted.
- Observability. Does the application contain Logging, Tracing and Metrics instrumentation?
- Input validation.
- Proper error handling.
- Ability to use and configure rest template. We allow for half-setup object mapper and rest template
- Not all information in the Application is perfect. It is expected that a person would figure these out and correct.
  
## Getting Started

### Prerequisite tooling

- Any Springboot/Java IDE. Ideally IntelliJIdea.
- Java 17
- Gradle 8
  
### Prerequisite knowledge

- Java
- SpringBoot
- Gradle
- Junit

### Importing Google Java codestyle into INtelliJ

```
- Go to IntelliJ Settings
- Search for "Code Style"
- Click on the "Settings" icon next to the Scheme dropdown
- Choose "Import -> IntelliJ Idea code style XML
- Pick the file "google_java_code_style.xml" from root directory of the application
__Optional__
- Search for "Actions on Save"
    - Check "Reformat Code" and "Organise Imports"
```

---
**NOTE** -
It is  highly recommended that the application be loaded and started up to avoid any issues.

---

## Audition Application information

This section provides information on the application and what the needs to be completed as part of the audition application.

The audition consists of multiple TODO statements scattered throughout the codebase. The applicants are expected to:

- Complete all the TODO statements.
- Add unit tests where applicants believe it to be necessary.
- Make sure that all code quality check are completed.
- Gradle build completes sucessfully.
- Make sure the application if functional.

## Submission process
Applicants need to do the following to submit their work: 
- Clone this repository
- Complete their work and zip up the working application. 
- Applicants then need to send the ZIP archive to the email of the recruiting manager. This email be communicated to the applicant during the recruitment process. 

  
---
## Additional Information based on the implementation

This section MUST be completed by applicants. It allows applicants to showcase their view on how an application can/should be documented. 
Applicants can choose to do this in a separate markdown file that needs to be included when the code is committed. 

Implementation notes and a quick reference are provided in:

- `IMPLEMENTATION_NOTES.md`
- `QUICK_REFERENCE.md`

## API endpoints

Swagger UI is available at:

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

| Method | Path | Description |
| --- | --- | --- |
| GET | `/posts` | Returns all posts. Optional filters: `userId`, `id`, `title` |
| GET | `/posts/{id}` | Returns a single post by id and includes its comments |
| GET | `/posts/{id}/comments` | Returns the comments for a single post |

### Query parameters for `GET /posts`

| Parameter | Type | Description |
| --- | --- | --- |
| `userId` | Integer | Filters posts by user id |
| `id` | Integer | Filters posts by post id |
| `title` | String | Filters posts by partial title match, case-insensitive |

### Response notes

- `GET /posts/{id}` returns the post with `comments` populated.
- `GET /posts/{id}/comments` returns only the comments list.
- Invalid post ids return a `400` validation error.
- Unsupported query parameters on `GET /posts` are rejected before controller logic runs.

### Validation error model

Validation failures return a plain `ProblemDetail` response with `title`, `status`, and `detail`:

```json
{
  "title": "Validation Error",
  "status": 400,
  "detail": "userId must be a positive integer",
  "instance": "/posts"
}
```
