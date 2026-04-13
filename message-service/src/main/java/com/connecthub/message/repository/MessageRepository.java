package com.connecthub.message.repository;

import com.connecthub.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
	List<Message> findByRoomIdOrderByCreatedAtAsc(UUID roomId);
}