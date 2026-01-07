package com.superhealthclaim.claimservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.superhealthclaim.claimservice.entity.FileMetadata;
import com.superhealthclaim.claimservice.entity.FileStatus;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findByClaimId(Long claimId);

	List<FileMetadata> findByClaimIdAndStatus(Long claimId, FileStatus active);

	boolean existsByClaimIdAndFileNameAndStatus(Long claimId, String originalName, FileStatus active);
}

