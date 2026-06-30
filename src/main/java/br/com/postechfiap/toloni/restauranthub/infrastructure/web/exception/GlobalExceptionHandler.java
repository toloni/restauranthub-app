package br.com.postechfiap.toloni.restauranthub.infrastructure.web.exception;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.Arrays;

/// Global exception handler for the application.
///
/// Maps domain and infrastructure exceptions to appropriate HTTP responses
/// using [ProblemDetail] as the response body.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /// Handles [DomainException] — domain rule violations.
    ///
    /// @return a [ProblemDetail] with status `422 Unprocessable Entity`
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex, HttpServletRequest request) {
        log.warn("Domain rule violated: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(422), ex.getMessage());
        problem.setTitle("Unprocessable Entity");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    /// Handles [NotFoundException] — resource not found.
    ///
    /// @return a [ProblemDetail] with status \404 Not Found``
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    /// Handles [AlreadyExistsException] — resource already exists.
    ///
    /// @return a [ProblemDetail] with status \409 Conflict``
    @ExceptionHandler(AlreadyExistsException.class)
    public ProblemDetail handleAlreadyExistsException(AlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Resource already exists: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflict");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    /// Handles [EntityInUseException] — resource is in use and cannot be deleted.
    ///
    /// @return a [ProblemDetail] with status \409 Conflict``
    @ExceptionHandler(EntityInUseException.class)
    public ProblemDetail handleEntityInUseException(EntityInUseException ex, HttpServletRequest request) {
        log.warn("Entity in use: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflict");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    /// Handles [UnauthorizedException] — requester does not have permission.
    ///
    /// @return a [ProblemDetail] with status \403 Forbidden``
    @ExceptionHandler(UnauthorizedException.class)
    public ProblemDetail handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Forbidden");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    /// Handles [HttpMessageNotReadableException] — malformed JSON or invalid field type.
    ///
    /// @return a [ProblemDetail] with status \400 Bad Request``
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Invalid request body: cause={}, message={}",
                ex.getCause() != null ? ex.getCause().getClass().getName() : "null",
                ex.getMessage());

        String detail = "Invalid or malformed request body.";

        Throwable cause = ex.getCause();
        if (cause instanceof tools.jackson.databind.exc.InvalidFormatException invalidFormat && invalidFormat.getTargetType() != null && invalidFormat.getTargetType().isEnum()) {

            String fieldName = "unknown";
            if (invalidFormat.getPath() != null && !invalidFormat.getPath().isEmpty()) {
                fieldName = invalidFormat.getPath().getFirst().getPropertyName();
            }

            detail = "Invalid value '%s' for field '%s'. Valid values are: %s".formatted(invalidFormat.getValue(), fieldName, Arrays.toString(invalidFormat.getTargetType().getEnumConstants()));
        }

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Invalid Request Body");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    /// Handles [Exception] — unexpected errors.
    ///
    /// @return a [ProblemDetail] with status \500 Internal Server Error``
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");
        problem.setTitle("Internal Server Error");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ProblemDetail handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex, HttpServletRequest request) {
        log.warn("Invalid filter field: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid filter field. Please check the field name and try again.");
        problem.setTitle("Invalid Filter");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Invalid argument: {}", ex.getMessage());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Argument");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ProblemDetail handleMissingRequestHeaderException(MissingRequestHeaderException ex, HttpServletRequest request) {
        log.warn("Missing request header: {}", ex.getHeaderName());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Required header '%s' is missing.".formatted(ex.getHeaderName()));
        problem.setTitle("Missing Request Header");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail handlePropertyReferenceException(
            PropertyReferenceException ex, HttpServletRequest request) {
        log.warn("Invalid sort field: {}", ex.getPropertyName());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid sort field '%s'.".formatted(ex.getPropertyName()));
        problem.setTitle("Invalid Sort Field");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not supported: {}", ex.getMethod());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED,
                "Method '%s' is not supported for this endpoint. Supported methods: %s"
                        .formatted(ex.getMethod(), ex.getSupportedHttpMethods()));
        problem.setTitle("Method Not Allowed");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFoundException(
            NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getResourcePath());
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "No resource found at '%s'.".formatted(ex.getResourcePath()));
        problem.setTitle("Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

}
