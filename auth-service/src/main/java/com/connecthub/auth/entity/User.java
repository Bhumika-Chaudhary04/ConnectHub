package com.connecthub.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = { @Index(name = "idx_user_email", columnList = "email"),
		@Index(name = "idx_user_username", columnList = "username"),
		@Index(name = "idx_user_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(updatable = false, nullable = false)
	private UUID userId;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = true)
	private String passwordHash;

	@Column(nullable = false, length = 100)
	private String fullName;

	@Column(length = 500)
	private String avatarUrl;

	@Column(length = 250)
	private String bio;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private AuthProvider provider;

	@Column(nullable = false)
	private Boolean isActive;

	private LocalDateTime lastSeenAt;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		if (this.status == null) {
			this.status = UserStatus.ONLINE;
		}
		if (this.provider == null) {
			this.provider = AuthProvider.LOCAL;
		}
		if (this.isActive == null) {
			this.isActive = true;
		}
	}
}