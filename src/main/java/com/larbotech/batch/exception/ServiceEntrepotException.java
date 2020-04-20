package com.larbotech.batch.exception;

public class ServiceEntrepotException extends BatchAgregatException {

  public ServiceEntrepotException(String message) {
    super(message);
  }

  public ServiceEntrepotException(Throwable cause) {
    super(cause);
  }

}
