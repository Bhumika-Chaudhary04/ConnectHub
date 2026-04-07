package com.connecthub.room.service;

import com.connecthub.room.dto.CreateRoomRequest;
import com.connecthub.room.entity.Room;

import java.util.List;
import java.util.UUID;

public interface RoomService {

	Room createRoom(UUID creatorId, CreateRoomRequest request);

	List<Room> getUserRooms(UUID userId);
}