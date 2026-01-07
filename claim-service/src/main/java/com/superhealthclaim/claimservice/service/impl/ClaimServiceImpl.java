package com.superhealthclaim.claimservice.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.superhealthclaim.claimservice.dto.ClaimRequest;
import com.superhealthclaim.claimservice.dto.ClaimResponse;
import com.superhealthclaim.claimservice.entity.Claim;
import com.superhealthclaim.claimservice.entity.ClaimStatus;
import com.superhealthclaim.claimservice.entity.FileStatus;
import com.superhealthclaim.claimservice.exception.ClaimNotFoundException;
import com.superhealthclaim.claimservice.repository.ClaimRepository;
import com.superhealthclaim.claimservice.repository.FileMetadataRepository;
import com.superhealthclaim.claimservice.service.ClaimService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepo;
    private final FileMetadataRepository metadataRepo;
    private final S3Service s3Service;

    @Override
    public ClaimResponse createClaim(ClaimRequest request) {

        Claim claim = Claim.builder()
                .policyNumber(request.getPolicyNumber())
                .claimantName(request.getClaimantName())
                .providerName(request.getProviderName())
                .claimAmount(request.getClaimAmount())
                .diagnosisCode(request.getDiagnosisCode())
                .treatmentDetails(request.getTreatmentDetails())
                .serviceDate(request.getServiceDate())
                .status(ClaimStatus.SUBMITTED)
                .submittedDate(LocalDate.now())
                .updatedDate(LocalDate.now())
                .build();

        return mapToResponse(claimRepo.save(claim));
    }

    @Override
    public ClaimResponse getClaimById(Long id) {

        Claim claim = claimRepo.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + id));

        return mapToResponse(claim);
    }

    @Override
    public List<ClaimResponse> getAllClaims() {

        return claimRepo.findByStatusNot(ClaimStatus.ARCHIVED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ClaimResponse updateClaim(Long id, ClaimRequest request) {

        Claim claim = claimRepo.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + id));

        claim.setPolicyNumber(request.getPolicyNumber());
        claim.setClaimantName(request.getClaimantName());
        claim.setProviderName(request.getProviderName());
        claim.setClaimAmount(request.getClaimAmount());
        claim.setDiagnosisCode(request.getDiagnosisCode());
        claim.setTreatmentDetails(request.getTreatmentDetails());
        claim.setServiceDate(request.getServiceDate());
        claim.setUpdatedDate(LocalDate.now());

        return mapToResponse(claimRepo.save(claim));
    }

    @Override
    public ClaimResponse updateClaimStatus(Long id, ClaimStatus status) {

        Claim claim = claimRepo.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + id));

        claim.setStatus(status);
        claim.setUpdatedDate(LocalDate.now());

        return mapToResponse(claimRepo.save(claim));
    }

    @Override
    public void deleteClaim(Long id) {

        Claim claim = claimRepo.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + id));

        claim.setStatus(ClaimStatus.ARCHIVED);
        claim.setUpdatedDate(LocalDate.now());

        claimRepo.save(claim);
    }

    private ClaimResponse mapToResponse(Claim claim) {

        List<String> urls = metadataRepo
                .findByClaimIdAndStatus(claim.getId(), FileStatus.ACTIVE)
                .stream()
                .map(f -> s3Service.getPresignedUrlById(f.getId()))
                .toList();

        return ClaimResponse.builder()
                .id(claim.getId())
                .policyNumber(claim.getPolicyNumber())
                .claimantName(claim.getClaimantName())
                .providerName(claim.getProviderName())
                .claimAmount(claim.getClaimAmount())
                .diagnosisCode(claim.getDiagnosisCode())
                .treatmentDetails(claim.getTreatmentDetails())
                .serviceDate(claim.getServiceDate())
                .status(claim.getStatus())
                .submittedDate(claim.getSubmittedDate())
                .updatedDate(claim.getUpdatedDate())
                .presignedUrls(urls)
                .build();
    }
}
