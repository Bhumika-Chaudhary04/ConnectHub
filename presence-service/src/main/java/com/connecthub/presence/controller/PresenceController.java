package com.connecthub.presence.controller;

import com.connecthub.presence.dto.PresenceResponse;
import com.connecthub.presence.service.PresenceService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/presence")
public class PresenceController {

	private final PresenceService presenceService;

	public PresenceController(PresenceService presenceService) {
		this.presenceService = presenceService;
	}

	@PostMapping("/online/{userId}")
	public PresenceResponse markOnline(@PathVariable UUID userId) {
		return presenceService.markOnline(userId);
	}

	@PostMapping("/offline/{userId}")
	public PresenceResponse markOffline(@PathVariable UUID userId) {
		return presenceService.markOffline(userId);
	}

	@GetMapping("/{userId}")
	public PresenceResponse getPresence(@PathVariable UUID userId) {
		return presenceService.getPresence(userId);
	}
}