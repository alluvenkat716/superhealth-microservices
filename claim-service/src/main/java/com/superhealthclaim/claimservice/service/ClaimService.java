package com.superhealthclaim.claimservice.service;

import com.superhealthclaim.claimservice.dto.ClaimRequest;
import com.superhealthclaim.claimservice.dto.ClaimResponse;
import com.superhealthclaim.claimservice.entity.ClaimStatus;

import java.util.List;

public interface ClaimService {

    ClaimResponse createClaim(ClaimRequest request);

    ClaimResponse getClaimById(Long id);

    List<ClaimResponse> getAllClaims();

    ClaimResponse updateClaim(Long id, ClaimRequest request);

    ClaimResponse updateClaimStatus(Long id, ClaimStatus status);

    void deleteClaim(Long id);
}
