package com.connecthub.message.controller;

import com.connecthub.message.dto.ChatMessage;
import com.connecthub.message.dto.CreateMessageRequest;
import com.connecthub.message.entity.Message;
import com.connecthub.message.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class ChatWebSocketController {

	private final SimpMessagingTemplate messagingTemplate;
	private final MessageService messageService;

	public ChatWebSocketController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
		this.messagingTemplate = messagingTemplate;
		this.messageService = messageService;
	}

	@MessageMapping("/chat.send")
	public void sendMessage(ChatMessage chatMessage) {
		System.out.println("Message received: " + chatMessage.getContent());

		CreateMessageRequest request = new CreateMessageRequest();
		request.setContent(chatMessage.getContent());
		request.setRoomId(UUID.fromString(chatMessage.getRoomId()));

		Message savedMessage = messageService.sendMessage(UUID.fromString(chatMessage.getSenderId()), request);

		messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoomId(), savedMessage);
	}
}