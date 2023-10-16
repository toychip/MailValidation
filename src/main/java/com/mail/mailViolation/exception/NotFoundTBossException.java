package com.mail.mailViolation.exception;

public class NotFoundTBossException extends RuntimeException{


    public NotFoundTBossException() {
        super();
    }

    public NotFoundTBossException(String message) {
        super(message);
    }

    public NotFoundTBossException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundTBossException(Throwable cause) {
        super(cause);
    }

    protected NotFoundTBossException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
