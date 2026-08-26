package com.tosan.otpgeneratorspringsample.controller;


import com.tosan.otpgenerator.model.TransactionDataDto;
import com.tosan.otpgenerator.service.OtpGenerationService;
import com.tosan.otpgeneratorspringsample.model.DefaultOtpRequestDto;
import com.tosan.otpgeneratorspringsample.model.DefaultOtpValidationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpGenerationService otpGenerationService;


    @PostMapping("/generate")
    public String generateOtp(@RequestBody DefaultOtpRequestDto request) {

        return otpGenerationService.generate(request);
    }

    @PostMapping("/validate")
    public void validateOtp(@RequestBody DefaultOtpValidationRequestDto request) {

        otpGenerationService.validate(request);
    }

    @GetMapping("/{id}")
    public List<TransactionDataDto> getTransactionData(@PathVariable String id) {

        return otpGenerationService.getUserTransactions(id);
    }
}