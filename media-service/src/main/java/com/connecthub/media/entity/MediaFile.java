package com.connecthub.media.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "media_files")
public class MediaFile {

	@Id
	@GeneratedValue
	private UUID id;

	private UUID roomId;
	private UUID senderId;
	private String fileName;
	private String fileType;
	private String filePath;
	private LocalDateTime uploadedAt;
}