package com.connecthub.message.service;

import com.connecthub.message.dto.CreateMessageRequest;
import com.connecthub.message.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
	Message sendMessage(UUID userId, CreateMessageRequest request);

	List<Message> getMessagesByRoom(UUID roomId);
}