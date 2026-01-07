package com.superhealthclaim.claimservice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.superhealthclaim.claimservice.dto.DocumentResponse;
import com.superhealthclaim.claimservice.service.impl.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimDocumentController {

    private final S3Service s3Service;

    @PreAuthorize("hasAuthority('SCOPE_claim.write')")
    @PostMapping("/{claimId}/documents")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long claimId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String uploadedBy
    ) throws IOException {

        var metadata = s3Service.uploadAndSave(file, claimId, uploadedBy);

        return ResponseEntity.status(201).body(
                new DocumentResponse(
                        metadata.getId(),
                        metadata.getFileName(),
                        metadata.getUploadedBy(),
                        metadata.getUploadedAt()
                )
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.read')")
    @GetMapping("/{claimId}/documents")
    public ResponseEntity<List<DocumentResponse>> listDocuments(
            @PathVariable Long claimId) {

        List<DocumentResponse> documents =
                s3Service.getDocumentsForClaim(claimId)
                        .stream()
                        .map(m -> new DocumentResponse(
                                m.getId(),
                                m.getFileName(),
                                m.getUploadedBy(),
                                m.getUploadedAt()
                        ))
                        .toList();

        return ResponseEntity.ok(documents);
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.read')")
    @GetMapping("/documents/{fileId}/download")
    public ResponseEntity<String> getPresignedDownloadUrl(
            @PathVariable Long fileId) {

        return ResponseEntity.ok(
                s3Service.getPresignedUrlById(fileId)
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_claim.write')")
    @DeleteMapping("/documents/{fileId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long fileId) {

        s3Service.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
}
