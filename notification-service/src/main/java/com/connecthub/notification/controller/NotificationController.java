package com.connecthub.notification.controller;

import com.connecthub.notification.dto.NotificationRequest;
import com.connecthub.notification.entity.Notification;
import com.connecthub.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService service;

	public NotificationController(NotificationService service) {
		this.service = service;
	}

	@PostMapping
	public Notification create(@RequestBody NotificationRequest request) {
		return service.createNotification(request.getUserId(), request.getType(), request.getMessage());
	}

	@GetMapping("/{userId}")
	public List<Notification> getUserNotifications(@PathVariable UUID userId) {
		return service.getUserNotifications(userId);
	}

	@PutMapping("/{id}/read")
	public Notification markAsRead(@PathVariable UUID id) {
		return service.markAsRead(id);
	}
}