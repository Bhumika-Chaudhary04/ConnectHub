package com.connecthub.room.controller;

import com.connecthub.room.dto.CreateRoomRequest;
import com.connecthub.room.entity.Room;
import com.connecthub.room.security.JwtService;
import com.connecthub.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;
	private final JwtService jwtService;

	@PostMapping
	public Room createRoom(@RequestHeader("Authorization") String authHeader,
			@Valid @RequestBody CreateRoomRequest request) {

		String token = authHeader.substring(7);
		String userId = jwtService.extractUserId(token);

		return roomService.createRoom(UUID.fromString(userId), request);
	}

	@GetMapping
	public List<Room> getUserRooms(@RequestHeader("Authorization") String authHeader) {

		String token = authHeader.substring(7);
		String userId = jwtService.extractUserId(token);

		return roomService.getUserRooms(UUID.fromString(userId));
	}
}