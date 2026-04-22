package com.connecthub.auth.dto;

import com.connecthub.auth.entity.AuthProvider;
import com.connecthub.auth.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
	private UUID userId;
	private String username;
	private String email;
	private String fullName;
	private String avatarUrl;
	private String bio;
	private UserStatus status;
	private AuthProvider provider;
	private Boolean isActive;
	private LocalDateTime lastSeenAt;
	private LocalDateTime createdAt;
}