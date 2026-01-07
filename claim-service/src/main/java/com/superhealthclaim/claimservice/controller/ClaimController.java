package com.superhealthclaim.claimservice.controller;

import com.superhealthclaim.claimservice.dto.ClaimRequest;
import com.superhealthclaim.claimservice.dto.ClaimResponse;
import com.superhealthclaim.claimservice.entity.ClaimStatus;
import com.superhealthclaim.claimservice.service.ClaimService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PreAuthorize("hasAuthority('SCOPE_claim.write')")
    @PostMapping
    public ClaimResponse createClaim(@RequestBody ClaimRequest request) {
        return claimService.createClaim(request);
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.read')")
    @GetMapping
    public List<ClaimResponse> getAllClaims() {
        return claimService.getAllClaims();
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.read')")
    @GetMapping("/{id}")
    public ClaimResponse getClaim(@PathVariable Long id) {
        return claimService.getClaimById(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.write')")
    @PutMapping("/{id}")
    public ClaimResponse updateClaim(
            @PathVariable Long id,
            @RequestBody ClaimRequest request
    ) {
        return claimService.updateClaim(id, request);
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.write')")
    @PutMapping("/{id}/status")
    public ClaimResponse updateClaimStatus(
            @PathVariable Long id,
            @RequestParam ClaimStatus status
    ) {
        return claimService.updateClaimStatus(id, status);
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.write')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        claimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}
