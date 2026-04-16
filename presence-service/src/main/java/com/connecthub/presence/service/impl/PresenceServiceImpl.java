package com.connecthub.presence.service.impl;

import com.connecthub.presence.dto.PresenceResponse;
import com.connecthub.presence.entity.PresenceStatus;
import com.connecthub.presence.entity.UserPresence;
import com.connecthub.presence.repository.UserPresenceRepository;
import com.connecthub.presence.service.PresenceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PresenceServiceImpl implements PresenceService {

	private final UserPresenceRepository userPresenceRepository;

	public PresenceServiceImpl(UserPresenceRepository userPresenceRepository) {
		this.userPresenceRepository = userPresenceRepository;
	}

	@Override
	public PresenceResponse markOnline(UUID userId) {
		UserPresence presence = userPresenceRepository.findById(userId).orElseGet(() -> {
			UserPresence userPresence = new UserPresence();
			userPresence.setUserId(userId);
			return userPresence;
		});

		presence.setStatus(PresenceStatus.ONLINE);
		presence.setLastSeen(LocalDateTime.now());

		UserPresence saved = userPresenceRepository.save(presence);

		return new PresenceResponse(saved.getUserId(), saved.getStatus(), saved.getLastSeen());
	}

	@Override
	public PresenceResponse markOffline(UUID userId) {
		UserPresence presence = userPresenceRepository.findById(userId).orElseGet(() -> {
			UserPresence userPresence = new UserPresence();
			userPresence.setUserId(userId);
			return userPresence;
		});

		presence.setStatus(PresenceStatus.OFFLINE);
		presence.setLastSeen(LocalDateTime.now());

		UserPresence saved = userPresenceRepository.save(presence);

		return new PresenceResponse(saved.getUserId(), saved.getStatus(), saved.getLastSeen());
	}

	@Override
	public PresenceResponse getPresence(UUID userId) {
		UserPresence presence = userPresenceRepository.findById(userId).orElseGet(() -> {
			UserPresence userPresence = new UserPresence();
			userPresence.setUserId(userId);
			userPresence.setStatus(PresenceStatus.OFFLINE);
			userPresence.setLastSeen(null);
			return userPresence;
		});

		return new PresenceResponse(presence.getUserId(), presence.getStatus(), presence.getLastSeen());
	}
}