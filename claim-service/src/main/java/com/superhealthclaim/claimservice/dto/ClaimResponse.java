package com.superhealthclaim.claimservice.dto;

import java.time.LocalDate;
import java.util.List;

import com.superhealthclaim.claimservice.entity.ClaimStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {

    private Long id;

    private String policyNumber;
    private String claimantName;
    private String providerName;

    private Double claimAmount;

    private String diagnosisCode;
    private String treatmentDetails;

    private LocalDate serviceDate;

    private ClaimStatus status;

    private LocalDate submittedDate;
    private LocalDate updatedDate;
    
    private List<String> presignedUrls;

}
