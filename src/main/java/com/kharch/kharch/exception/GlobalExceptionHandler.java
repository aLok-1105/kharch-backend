//package com.kharch.kharch.exception;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.security.SignatureException;
//import jakarta.validation.ConstraintViolationException;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//import org.springframework.web.HttpRequestMethodNotAllowedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//import org.springframework.web.servlet.NoHandlerFoundException;
//
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    // ─────────────────────────────────────────────
//    // Error Response Shape
//    // ─────────────────────────────────────────────
//
//    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
//        return ResponseEntity.status(status)
//                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, Instant.now()));
//    }
//
//    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, Map<String, String> errors) {
//        return ResponseEntity.status(status)
//                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, Instant.now(), errors));
//    }
//
//    public record ErrorResponse(
//            int status,
//            String error,
//            String message,
//            Instant timestamp,
//            Map<String, String> fieldErrors
//    ) {
//        // Constructor without fieldErrors
//        public ErrorResponse(int status, String error, String message, Instant timestamp) {
//            this(status, error, message, timestamp, null);
//        }
//    }
//
//    // ─────────────────────────────────────────────
//    // 400 — Bad Request
//    // ─────────────────────────────────────────────
//
//    // @Valid / @Validated failed on a request body
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
//        Map<String, String> fieldErrors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors()
//                .forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));
//        return build(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
//    }
//
//    // @Validated failed on path/query params (e.g. @Min, @NotBlank on @RequestParam)
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
//        Map<String, String> fieldErrors = new HashMap<>();
//        ex.getConstraintViolations()
//                .forEach(v -> fieldErrors.put(v.getPropertyPath().toString(), v.getMessage()));
//        return build(HttpStatus.BAD_REQUEST, "Constraint violation", fieldErrors);
//    }
//
//    // Malformed JSON body or unreadable request (e.g. wrong enum value, bad date format)
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
//        String message = "Malformed request body";
//        // Surface the root cause for enum/type issues
//        if (ex.getCause() != null) {
//            message = message + ": " + ex.getCause().getMessage();
//        }
//        return build(HttpStatus.BAD_REQUEST, message);
//    }
//
//    // Wrong type for path variable or request param (e.g. passing "abc" for a Long id)
//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
//        String message = String.format(
//                "Parameter '%s' should be of type '%s'",
//                ex.getName(),
//                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
//        );
//        return build(HttpStatus.BAD_REQUEST, message);
//    }
//
//    // Required @RequestParam is missing
//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
//        return build(HttpStatus.BAD_REQUEST,
//                String.format("Required parameter '%s' is missing", ex.getParameterName()));
//    }
//
//    // Custom bad request (you throw this yourself)
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
//        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
//    }
//
//    // ─────────────────────────────────────────────
//    // 401 — Unauthorized
//    // ─────────────────────────────────────────────
//
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
//        return build(HttpStatus.UNAUTHORIZED, "Invalid email or password");
//        // ↑ Never echo ex.getMessage() here — it might say "user not found" vs
//        //   "wrong password" which lets attackers enumerate valid emails
//    }
//
//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
//        return build(HttpStatus.UNAUTHORIZED, "Token has expired — please log in again");
//    }
//
//    @ExceptionHandler(MalformedJwtException.class)
//    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException ex) {
//        return build(HttpStatus.UNAUTHORIZED, "Invalid token");
//    }
//
//    @ExceptionHandler(SignatureException.class)
//    public ResponseEntity<ErrorResponse> handleSignature(SignatureException ex) {
//        return build(HttpStatus.UNAUTHORIZED, "Invalid token signature");
//    }
//
//    // ─────────────────────────────────────────────
//    // 403 — Forbidden
//    // ─────────────────────────────────────────────
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
//        return build(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
//    }
//
//    @ExceptionHandler(DisabledException.class)
//    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ex) {
//        return build(HttpStatus.FORBIDDEN, "Account is disabled");
//    }
//
//    @ExceptionHandler(LockedException.class)
//    public ResponseEntity<ErrorResponse> handleLocked(LockedException ex) {
//        return build(HttpStatus.FORBIDDEN, "Account is locked");
//    }
//
//    // ─────────────────────────────────────────────
//    // 404 — Not Found
//    // ─────────────────────────────────────────────
//
//    @ExceptionHandler(NotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
//        return build(HttpStatus.NOT_FOUND, ex.getMessage());
//    }
//
//    // Route doesn't exist at all
//    // Requires: spring.mvc.throw-exception-if-no-handler-found=true
//    //           spring.web.resources.add-mappings=false
//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex) {
//        return build(HttpStatus.NOT_FOUND,
//                String.format("Route '%s %s' not found", ex.getHttpMethod(), ex.getRequestURL()));
//    }
//
//    // ─────────────────────────────────────────────
//    // 405 — Method Not Allowed
//    // ─────────────────────────────────────────────
//
//    @ExceptionHandler(HttpRequestMethodNotAllowedException.class)
//    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotAllowedException ex) {
//        return build(HttpStatus.METHOD_NOT_ALLOWED,
//                String.format("Method '%s' is not allowed on this endpoint. Allowed: %s",
//                        ex.getMethod(), ex.getSupportedHttpMethods()));
//    }
//
//    // ─────────────────────────────────────────────
//    // 415 — Unsupported Media Type
//    // ─────────────────────────────────────────────
//
//    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
//    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
//        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
//                String.format("Content type '%s' is not supported. Use 'application/json'",
//                        ex.getContentType()));
//    }
//
//    // ─────────────────────────────────────────────
//    // 409 — Conflict
//    // ─────────────────────────────────────────────
//
//    // Unique constraint violation, FK violation, etc.
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
//        String message = "Data integrity violation";
//        // Dig into root cause for a more useful message
//        Throwable root = ex.getRootCause();
//        if (root != null && root.getMessage() != null) {
//            String rootMsg = root.getMessage().toLowerCase();
//            if (rootMsg.contains("unique") || rootMsg.contains("duplicate")) {
//                message = "A record with this value already exists";
//            } else if (rootMsg.contains("foreign key") || rootMsg.contains("fk_")) {
//                message = "Referenced record does not exist";
//            } else if (rootMsg.contains("not null") || rootMsg.contains("null value")) {
//                message = "A required field is missing";
//            }
//        }
//        return build(HttpStatus.CONFLICT, message);
//    }
//
//    // Custom conflict (you throw this yourself)
//    @ExceptionHandler(ConflictException.class)
//    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
//        return build(HttpStatus.CONFLICT, ex.getMessage());
//    }
//
//    // ─────────────────────────────────────────────
//    // 500 — Internal Server Error (catch-all)
//    // ─────────────────────────────────────────────
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
//        // Log the real error internally — never send stack traces to clients
//        ex.printStackTrace(); // replace with: log.error("Unhandled exception", ex);
//        return build(HttpStatus.INTERNAL_SERVER_ERROR,
//                "Something went wrong. Please try again later.");
//    }
//}