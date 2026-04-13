package com.connecthub.message.service.impl;

import com.connecthub.message.dto.CreateMessageRequest;
import com.connecthub.message.entity.Message;
import com.connecthub.message.repository.MessageRepository;
import com.connecthub.message.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;

	public MessageServiceImpl(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	@Override
	public Message sendMessage(UUID userId, CreateMessageRequest request) {
		Message message = new Message();
		message.setSenderId(userId);
		message.setRoomId(request.getRoomId());
		message.setContent(request.getContent());
		message.setCreatedAt(LocalDateTime.now());
		return messageRepository.save(message);
	}

	@Override
	public List<Message> getMessagesByRoom(UUID roomId) {
		return messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
	}
}