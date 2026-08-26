package com.tosan.otpgenerator.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@Validated
@ConfigurationProperties(prefix = "otp")
@Getter
@Setter
public class OtpProperties {

    public static final String HMAC_SHA_1_256_512 = "HmacSHA(1|256|512)";
    @NotBlank
    private String suite = "OCRA-1:HOTP-SHA256-6:TX-T30S";

    @Min(1)
    @Max(3600)
    private long timeStepSeconds = 30;

    @NotBlank
    @Pattern(regexp = HMAC_SHA_1_256_512, message = "cryptoAlgorithm must be HmacSHA1, HmacSHA256, or HmacSHA512")
    private String cryptoAlgorithm = "HmacSHA1";

    @Min(0)
    @Max(10)
    private int allowedClockSkew = 1;
}
