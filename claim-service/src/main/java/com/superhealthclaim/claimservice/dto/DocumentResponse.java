package com.superhealthclaim.claimservice.dto;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String fileName,
        String uploadedBy,
        LocalDateTime uploadedAt
) {}
