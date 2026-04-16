package com.connecthub.notification.service;

import com.connecthub.notification.entity.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

	Notification createNotification(UUID userId, String type, String message);

	List<Notification> getUserNotifications(UUID userId);

	Notification markAsRead(UUID notificationId);
}