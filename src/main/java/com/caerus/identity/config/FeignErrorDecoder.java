package com.caerus.identity.config;

import com.caerus.identity.exception.EmailAlreadyExistsException;
import com.caerus.identity.exception.UserNotFoundException;
import com.caerus.identity.exception.ValidationException;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultErrorDecoder = new Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    return switch (response.status()) {
      case 400 -> new ValidationException("Validation failed when calling User Service");
      case 404 -> new UserNotFoundException("User not found in User Service");
      case 409 -> new EmailAlreadyExistsException("User already exists in User Service");
      default -> FeignException.errorStatus(methodKey, response);
    };
  }
}
