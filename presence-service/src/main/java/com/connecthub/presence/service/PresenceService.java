package com.connecthub.presence.service;

import com.connecthub.presence.dto.PresenceResponse;

import java.util.UUID;

public interface PresenceService {
    PresenceResponse markOnline(UUID userId);
    PresenceResponse markOffline(UUID userId);
    PresenceResponse getPresence(UUID userId);
}