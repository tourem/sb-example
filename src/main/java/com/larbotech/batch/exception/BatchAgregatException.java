package com.larbotech.batch.exception;

class BatchAgregatException extends RuntimeException {
  BatchAgregatException(String message) {
    super(message);
  }
  BatchAgregatException(Throwable cause) {
    super(cause);
  }

}
