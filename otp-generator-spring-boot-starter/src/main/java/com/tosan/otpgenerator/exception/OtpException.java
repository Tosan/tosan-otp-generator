package com.tosan.otpgenerator.exception;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
public class OtpException extends RuntimeException {

    public OtpException(String message) {
        super(message);
    }

    public OtpException(String message, Throwable cause) {
        super(message, cause);
    }
}
