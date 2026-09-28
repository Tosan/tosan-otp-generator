package com.tosan.otpgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpData {

    /**
     * Map of transaction data used in OTP generation and validation.
     */
    private Map<String, Object> otpMapData;

    /**
     * OTP length (number of digits).
     */
    private Integer otpLength;

    /**
     * Server-managed issuance counter for the same client transaction data.
     * Equals the number of already-consumed issuances plus one, so each
     * re-issuance produces a different OTP payload. Excluded from the
     * transaction identity string and from client requests.
     */
    private Long issuanceSequence;
}
