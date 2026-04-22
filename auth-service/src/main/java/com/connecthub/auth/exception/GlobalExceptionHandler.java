package com.connecthub.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
		log.warn("UserNotFoundException: " + ex.getMessage());
		return buildResponse(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex,
			HttpServletRequest request) {
		log.warn("UserAlreadyExistsException: " + ex.getMessage());
		return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
			HttpServletRequest request) {
		log.warn("InvalidCredentialsException: " + ex.getMessage());
		return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
		log.warn("InvalidTokenException: " + ex.getMessage());
		return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid Token", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(AccountDisabledException.class)
	public ResponseEntity<ErrorResponse> handleAccountDisabled(AccountDisabledException ex,
			HttpServletRequest request) {
		log.warn("AccountDisabledException: " + ex.getMessage());
		return buildResponse(HttpStatus.FORBIDDEN, "Account Disabled", ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, String> validationErrors = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		log.warn("Validation failed: " + validationErrors);

		ErrorResponse error = ErrorResponse.builder().status(HttpStatus.BAD_REQUEST.value()).error("Validation Failed")
				.message("One or more fields are invalid").path(request.getRequestURI()).timestamp(LocalDateTime.now())
				.validationErrors(validationErrors).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		log.warn("Missing request parameter: " + ex.getParameterName());
		return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request",
				"Required parameter '" + ex.getParameterName() + "' is missing", request.getRequestURI());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
		log.warn("AuthenticationException: " + ex.getMessage());
		return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication required",
				request.getRequestURI());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		log.warn("AccessDeniedException: " + ex.getMessage());
		return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to access this resource",
				request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception at [" + request.getRequestURI() + "]: " + ex.getMessage(), ex);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
				"An unexpected error occurred. Please try again later.", request.getRequestURI());
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, String path) {
		ErrorResponse response = ErrorResponse.builder().status(status.value()).error(error).message(message).path(path)
				.timestamp(LocalDateTime.now()).build();
		return ResponseEntity.status(status).body(response);
	}
}