package com.tosan.otpgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author T.Sadeh
 * @since 28-06-2026
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDataDto {

    /**
     * Transaction OTP data.
     */
    private OtpData otpData;

    /**
     * Transaction signature string (unique identifier based on OTP data).
     */
    private String transactionSignData;

    /**
     * Remaining transaction expiration time (in seconds).
     */
    private Long remainingTtl;
}
