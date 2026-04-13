package com.connecthub.message.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatMessage {
	private String content;
	private String senderId;
	private String roomId;
}