package com.engagetech.expenses;


public class FxException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FxException() {
    }

    public FxException(String message) {
        super(message);
    }

    public FxException(String message, Throwable cause) {
        super(message, cause);
    }

    public FxException(Throwable cause) {
        super(cause);
    }

    public FxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
