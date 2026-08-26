package com.tosan.otpgeneratorspringsample.service;

import com.tosan.otpgenerator.service.SecretKeyProvider;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MySecretKeyProvider implements SecretKeyProvider {

    @Override
    public byte[] getSecretKey(String userId) {

        return ("my-secret-key-from-db-or-vault-" + userId).getBytes(StandardCharsets.UTF_8);
    }
}