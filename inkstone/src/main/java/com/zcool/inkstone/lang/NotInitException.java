package com.zcool.inkstone.lang;

public class NotInitException extends RuntimeException {

    public NotInitException() {
    }

    public NotInitException(String message) {
        super(message);
    }

    public NotInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInitException(Throwable cause) {
        super(cause);
    }

}
