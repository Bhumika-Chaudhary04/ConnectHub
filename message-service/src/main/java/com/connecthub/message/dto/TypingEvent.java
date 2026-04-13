package com.connecthub.message.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TypingEvent {
	private UUID roomId;
	private String userName;
	private boolean typing;

}
