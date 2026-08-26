package com.tosan.otpgenerator.service;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@FunctionalInterface
public interface SecretKeyProvider {

    byte[] getSecretKey(String userId);
}
