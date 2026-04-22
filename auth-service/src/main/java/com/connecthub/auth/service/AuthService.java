package com.connecthub.auth.service;

import com.connecthub.auth.dto.*;

import java.util.List;
import java.util.UUID;

public interface AuthService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	boolean validateToken(String token);

	UserProfileResponse getCurrentUser(String email);

	UserProfileResponse updateProfile(String email, UpdateProfileRequest request);

	void changePassword(String email, ChangePasswordRequest request);

	List<UserSearchResponse> searchUsers(String keyword);

	UserProfileResponse getUserById(UUID userId);

	List<UserProfileResponse> getUsersByIds(List<UUID> userIds);

	UserProfileResponse updateStatus(String email, UpdateStatusRequest request);

	void recordLastSeen(String email);
}
