package com.caerus.identity.exception;

public class FeignException extends RuntimeException {
  private final int status;

  public FeignException(String message, int status) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
