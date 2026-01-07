package com.superhealthclaim.claimservice.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.superhealthclaim.claimservice.entity.Claim;
import com.superhealthclaim.claimservice.entity.ClaimStatus;
import com.superhealthclaim.claimservice.entity.FileMetadata;
import com.superhealthclaim.claimservice.entity.FileStatus;
import com.superhealthclaim.claimservice.exception.ClaimNotFoundException;
import com.superhealthclaim.claimservice.repository.ClaimRepository;
import com.superhealthclaim.claimservice.repository.FileMetadataRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final FileMetadataRepository metadataRepo;
    private final ClaimRepository claimRepo;

    public FileMetadata uploadAndSave(MultipartFile file, Long claimId, String uploadedBy)
            throws IOException {

        Claim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found"));

        if (claim.getStatus() == ClaimStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot upload document to archived claim");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        List<String> allowedTypes = List.of(
                "application/pdf",
                "image/jpeg",
                "image/png"
        );

        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        String originalName = file.getOriginalFilename();
        String s3Key = "claims/" + claimId + "/" + System.currentTimeMillis() + "-" + originalName;

        boolean exists = metadataRepo
                .existsByClaimIdAndFileNameAndStatus(claimId, originalName, FileStatus.ACTIVE);

        if (exists) {
            throw new IllegalArgumentException("File already exists for this claim");
        }

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

        FileMetadata meta = new FileMetadata();
        meta.setFileName(originalName);
        meta.setS3Key(s3Key);
        meta.setMimeType(file.getContentType());
        meta.setFileSize(file.getSize());
        meta.setBucketName(bucketName);
        meta.setClaimId(claimId);
        meta.setUploadedBy(uploadedBy);
        meta.setUploadedAt(LocalDateTime.now());
        meta.setStatus(FileStatus.ACTIVE);

        return metadataRepo.save(meta);
    }

    public List<FileMetadata> getDocumentsForClaim(Long claimId) {
        return metadataRepo.findByClaimIdAndStatus(claimId, FileStatus.ACTIVE);
    }

    public byte[] downloadFileById(Long fileId) {

        FileMetadata meta = metadataRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(meta.getS3Key())
                .build();

        ResponseBytes<GetObjectResponse> bytes =
                s3Client.getObjectAsBytes(getRequest);

        return bytes.asByteArray();
    }

    public String getPresignedUrlById(Long fileId) {

        FileMetadata meta = metadataRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .getObjectRequest(
                                GetObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(meta.getS3Key())
                                        .build()
                        )
                        .signatureDuration(Duration.ofMinutes(15))
                        .build();

        PresignedGetObjectRequest presigned =
                presigner.presignGetObject(presignRequest);

        return presigned.url().toString();
    }

    @Transactional
    public void deleteFile(Long fileId) {

        FileMetadata meta = metadataRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(meta.getS3Key())
                        .build()
        );

        meta.setStatus(FileStatus.DELETED);
        metadataRepo.save(meta);
    }
}
