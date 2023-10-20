package nice.mailViolation.exception;

public class ExelUploadException extends RuntimeException{


    public ExelUploadException() {
        super();
    }

    public ExelUploadException(String message) {
        super(message);
    }

    public ExelUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExelUploadException(Throwable cause) {
        super(cause);
    }

    protected ExelUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
