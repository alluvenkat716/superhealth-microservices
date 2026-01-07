package com.superhealthclaim.claimservice.dto;

import lombok.Data;

@Data
public class UploadUrlResponse {
	  private String uploadUrl;
	    private String key;
	    private long expiresIn;
}
