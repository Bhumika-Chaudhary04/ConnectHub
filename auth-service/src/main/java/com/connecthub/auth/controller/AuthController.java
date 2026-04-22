package com.connecthub.auth.controller;

import com.connecthub.auth.dto.AuthResponse;
import com.connecthub.auth.dto.ChangePasswordRequest;
import com.connecthub.auth.dto.LoginRequest;
import com.connecthub.auth.dto.RegisterRequest;
import com.connecthub.auth.dto.UpdateProfileRequest;
import com.connecthub.auth.dto.UpdateStatusRequest;
import com.connecthub.auth.dto.UserProfileResponse;
import com.connecthub.auth.dto.UserSearchResponse;
import com.connecthub.auth.security.CustomUserDetails;
import com.connecthub.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@GetMapping("/validate")
	public ResponseEntity<Map<String, Boolean>> validateToken(@RequestParam String token) {
		return ResponseEntity.ok(Map.of("valid", authService.validateToken(token)));
	}

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(authService.getCurrentUser(userDetails.getUsername()));
	}

	@PutMapping("/profile")
	public ResponseEntity<UserProfileResponse> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody UpdateProfileRequest request) {
		return ResponseEntity.ok(authService.updateProfile(userDetails.getUsername(), request));
	}

	@PutMapping("/password")
	public ResponseEntity<Map<String, String>> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody ChangePasswordRequest request) {
		authService.changePassword(userDetails.getUsername(), request);
		return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
	}

	@GetMapping("/search")
	public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam String keyword) {
		return ResponseEntity.ok(authService.searchUsers(keyword));
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<UserProfileResponse> getUserById(@PathVariable UUID userId) {
		return ResponseEntity.ok(authService.getUserById(userId));
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserProfileResponse>> getUsersByIds(@RequestParam List<UUID> ids) {
		return ResponseEntity.ok(authService.getUsersByIds(ids));
	}

	@PutMapping("/status")
	public ResponseEntity<UserProfileResponse> updateStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody UpdateStatusRequest request) {
		return ResponseEntity.ok(authService.updateStatus(userDetails.getUsername(), request));
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
		authService.recordLastSeen(userDetails.getUsername());
		return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
	}
}
