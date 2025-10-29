package com.caerus.identity.exception;

import com.caerus.identity.dto.ApiResponse;
import feign.FeignException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ApiResponse<String>> handleEmailExists(EmailAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiResponse<String>> handleInvalidCreds(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(InvalidToken.class)
  public ResponseEntity<ApiResponse<String>> handleInvalidToken(InvalidToken ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<ApiResponse<String>> handleFeignException(FeignException ex) {
    int status = (ex.status() > 0 ? ex.status() : HttpStatus.BAD_GATEWAY.value());
    return ResponseEntity.status(ex.status()).body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(RetryableException.class)
  public ResponseEntity<ApiResponse<String>> handleRetryableException(RetryableException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ApiResponse.failure("User service is unreachable. Please try again later."));
  }

  @ExceptionHandler(UserServiceUnavailableException.class)
  public ResponseEntity<ApiResponse<String>> handleUserServiceUnavailable(
      UserServiceUnavailableException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
    log.error("Exception occurred: ", ex);
    return ResponseEntity.internalServerError().body(ApiResponse.failure("Something went wrong"));
  }

  @ExceptionHandler(BadRequestException.class)
  public final ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ex.getMessage()));
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiResponse<String>> handleValidationException(ValidationException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(ex.getMessage()));
  }
}
