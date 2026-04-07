package com.connecthub.room.service.impl;

import com.connecthub.room.dto.CreateRoomRequest;
import com.connecthub.room.entity.Room;
import com.connecthub.room.entity.RoomMember;
import com.connecthub.room.repository.RoomMemberRepository;
import com.connecthub.room.repository.RoomRepository;
import com.connecthub.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;
	private final RoomMemberRepository roomMemberRepository;

	@Override
	public Room createRoom(UUID creatorId, CreateRoomRequest request) {

		Room room = Room.builder().name(request.getName()).type(request.getType()).createdBy(creatorId).build();

		Room savedRoom = roomRepository.save(room);

		// Add creator as ADMIN
		RoomMember creator = RoomMember.builder().roomId(savedRoom.getRoomId()).userId(creatorId).role("ADMIN").build();

		roomMemberRepository.save(creator);

		// Add other members
		if (request.getMemberIds() != null) {
			for (UUID memberId : request.getMemberIds()) {
				RoomMember member = RoomMember.builder().roomId(savedRoom.getRoomId()).userId(memberId).role("MEMBER")
						.build();

				roomMemberRepository.save(member);
			}
		}

		return savedRoom;
	}

	@Override
	public List<Room> getUserRooms(UUID userId) {
		List<RoomMember> memberships = roomMemberRepository.findByUserId(userId);

		return memberships.stream().map(m -> roomRepository.findById(m.getRoomId()).orElse(null)).toList();
	}
}