package com.superhealthclaim.claimservice.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class ClaimRequest {

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @NotBlank(message = "Claimant name is required")
    private String claimantName;

    @NotBlank(message = "Provider name is required")
    private String providerName;

    @NotNull(message = "Claim amount is required")
    @Positive(message = "Claim amount must be positive")
    private Double claimAmount;

    private String diagnosisCode;
    private String treatmentDetails;

    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;
}
