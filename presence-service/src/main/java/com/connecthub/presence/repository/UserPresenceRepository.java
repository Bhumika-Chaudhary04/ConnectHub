package com.connecthub.presence.repository;

import com.connecthub.presence.entity.UserPresence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPresenceRepository extends JpaRepository<UserPresence, UUID> {
}