package com.connecthub.auth.dto;

import com.connecthub.auth.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponse {
	private UUID userId;
	private String username;
	private String fullName;
	private String avatarUrl;
	private UserStatus status;
}