package com.connecthub.room.repository;

import com.connecthub.room.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {

    List<RoomMember> findByUserId(UUID userId);

    List<RoomMember> findByRoomId(UUID roomId);
}