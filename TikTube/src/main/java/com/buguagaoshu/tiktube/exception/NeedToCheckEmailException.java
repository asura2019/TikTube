package com.buguagaoshu.tiktube.exception;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-05
 */
public class NeedToCheckEmailException extends RuntimeException {
    public NeedToCheckEmailException() {
        super();
    }

    public NeedToCheckEmailException(String message) {
        super(message);
    }

    public NeedToCheckEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeedToCheckEmailException(Throwable cause) {
        super(cause);
    }

    protected NeedToCheckEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
