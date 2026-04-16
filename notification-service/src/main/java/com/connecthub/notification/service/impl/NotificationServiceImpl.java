package com.connecthub.notification.service.impl;

import com.connecthub.notification.entity.Notification;
import com.connecthub.notification.repository.NotificationRepository;
import com.connecthub.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository repository;

	public NotificationServiceImpl(NotificationRepository repository) {
		this.repository = repository;
	}

	@Override
	public Notification createNotification(UUID userId, String type, String message) {

		Notification notification = new Notification();
		notification.setUserId(userId);
		notification.setType(type);
		notification.setMessage(message);
		notification.setRead(false);
		notification.setCreatedAt(LocalDateTime.now());

		return repository.save(notification);
	}

	@Override
	public List<Notification> getUserNotifications(UUID userId) {
		return repository.findByUserIdOrderByCreatedAtDesc(userId);
	}

	@Override
	public Notification markAsRead(UUID notificationId) {
		Notification notification = repository.findById(notificationId).orElseThrow();
		notification.setRead(true);
		return repository.save(notification);
	}
}