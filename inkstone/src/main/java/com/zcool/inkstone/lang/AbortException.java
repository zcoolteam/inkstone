package com.zcool.inkstone.lang;

public class AbortException extends RuntimeException {

    public AbortException() {
    }

    public AbortException(String detailMessage) {
        super(detailMessage);
    }

    public AbortException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AbortException(Throwable throwable) {
        super(throwable);
    }
}
