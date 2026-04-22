package com.connecthub.auth.service.impl;

import com.connecthub.auth.dto.AuthResponse;
import com.connecthub.auth.dto.ChangePasswordRequest;
import com.connecthub.auth.dto.LoginRequest;
import com.connecthub.auth.dto.RegisterRequest;
import com.connecthub.auth.dto.UpdateProfileRequest;
import com.connecthub.auth.dto.UpdateStatusRequest;
import com.connecthub.auth.dto.UserProfileResponse;
import com.connecthub.auth.dto.UserSearchResponse;
import com.connecthub.auth.entity.AuthProvider;
import com.connecthub.auth.entity.User;
import com.connecthub.auth.entity.UserStatus;
import com.connecthub.auth.exception.AccountDisabledException;
import com.connecthub.auth.exception.InvalidCredentialsException;
import com.connecthub.auth.exception.UserAlreadyExistsException;
import com.connecthub.auth.exception.UserNotFoundException;
import com.connecthub.auth.repository.UserRepository;
import com.connecthub.auth.security.JwtService;
import com.connecthub.auth.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException("Email is already registered: " + request.getEmail());
		}

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UserAlreadyExistsException("Username is already taken: " + request.getUsername());
		}

		User user = User.builder().fullName(request.getFullName()).username(request.getUsername())
				.email(request.getEmail()).passwordHash(passwordEncoder.encode(request.getPassword()))
				.status(UserStatus.ONLINE).provider(AuthProvider.LOCAL).isActive(true).build();

		User savedUser = userRepository.save(user);
		return buildAuthResponse(savedUser);
	}

	@Override
	@Transactional
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!Boolean.TRUE.equals(user.getIsActive())) {
			throw new AccountDisabledException("Your account has been disabled. Please contact support.");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}

		user.setStatus(UserStatus.ONLINE);
		User updatedUser = userRepository.save(user);

		return buildAuthResponse(updatedUser);
	}

	@Override
	public boolean validateToken(String token) {
		return jwtService.isTokenValid(token);
	}

	@Override
	public UserProfileResponse getCurrentUser(String email) {
		User user = findUserByEmail(email);
		return mapToUserProfileResponse(user);
	}

	@Override
	@Transactional
	public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
		User user = findUserByEmail(email);

		if (request.getFullName() != null && !request.getFullName().isBlank()) {
			user.setFullName(request.getFullName());
		}

		if (request.getAvatarUrl() != null) {
			user.setAvatarUrl(request.getAvatarUrl());
		}

		if (request.getBio() != null) {
			user.setBio(request.getBio());
		}

		User updatedUser = userRepository.save(user);
		return mapToUserProfileResponse(updatedUser);
	}

	@Override
	@Transactional
	public void changePassword(String email, ChangePasswordRequest request) {
		User user = findUserByEmail(email);

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
			throw new InvalidCredentialsException("Current password is incorrect");
		}

		if (request.getCurrentPassword().equals(request.getNewPassword())) {
			throw new InvalidCredentialsException("New password must be different from the current password");
		}

		user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	@Override
	public List<UserSearchResponse> searchUsers(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return List.of();
		}

		return userRepository.findByUsernameContainingIgnoreCase(keyword.trim()).stream()
				.filter(user -> Boolean.TRUE.equals(user.getIsActive())).map(this::mapToUserSearchResponse).toList();
	}

	@Override
	public UserProfileResponse getUserById(UUID userId) {
		User user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
		return mapToUserProfileResponse(user);
	}

	@Override
	public List<UserProfileResponse> getUsersByIds(List<UUID> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return List.of();
		}
		return userRepository.findAllById(userIds).stream().map(this::mapToUserProfileResponse).toList();
	}

	@Override
	@Transactional
	public UserProfileResponse updateStatus(String email, UpdateStatusRequest request) {
		User user = findUserByEmail(email);
		user.setStatus(request.getStatus());

		User updatedUser = userRepository.save(user);
		return mapToUserProfileResponse(updatedUser);
	}

	@Override
	@Transactional
	public void recordLastSeen(String email) {
		userRepository.findByEmail(email).ifPresent(user -> {
			user.setLastSeenAt(LocalDateTime.now());
			user.setStatus(UserStatus.AWAY);
			userRepository.save(user);
		});
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
	}

	private AuthResponse buildAuthResponse(User user) {
		String token = jwtService.generateToken(user.getUserId(), user.getEmail());

		return AuthResponse.builder().token(token).userId(user.getUserId()).username(user.getUsername())
				.email(user.getEmail()).fullName(user.getFullName()).build();
	}

	private UserSearchResponse mapToUserSearchResponse(User user) {
		return UserSearchResponse.builder().userId(user.getUserId()).username(user.getUsername())
				.fullName(user.getFullName()).avatarUrl(user.getAvatarUrl()).status(user.getStatus()).build();
	}

	private UserProfileResponse mapToUserProfileResponse(User user) {
		return UserProfileResponse.builder().userId(user.getUserId()).username(user.getUsername())
				.email(user.getEmail()).fullName(user.getFullName()).avatarUrl(user.getAvatarUrl()).bio(user.getBio())
				.status(user.getStatus()).provider(user.getProvider()).isActive(user.getIsActive())
				.lastSeenAt(user.getLastSeenAt()).createdAt(user.getCreatedAt()).build();
	}
}
