package com.caerus.identity.exception;

public class InvalidToken extends RuntimeException {
  public InvalidToken(String message) {
    super(message);
  }
}
