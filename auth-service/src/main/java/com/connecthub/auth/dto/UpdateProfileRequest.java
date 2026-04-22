package com.connecthub.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

	@NotBlank(message = "Full name is required")
	@Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
	private String fullName;

	@Size(max = 500, message = "Avatar URL must not exceed 500 characters")
	private String avatarUrl;

	@Size(max = 250, message = "Bio must not exceed 250 characters")
	private String bio;
}