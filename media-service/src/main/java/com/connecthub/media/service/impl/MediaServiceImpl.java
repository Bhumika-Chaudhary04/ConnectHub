package com.connecthub.media.service.impl;

import com.connecthub.media.dto.MediaUploadResponse;
import com.connecthub.media.entity.MediaFile;
import com.connecthub.media.repository.MediaFileRepository;
import com.connecthub.media.service.MediaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

	private final MediaFileRepository mediaFileRepository;

	@Value("${file.upload-dir}")
	private String uploadDir;

	public MediaServiceImpl(MediaFileRepository mediaFileRepository) {
		this.mediaFileRepository = mediaFileRepository;
	}

	@Override
	public MediaUploadResponse uploadFile(UUID roomId, UUID senderId, MultipartFile file) {
		try {
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
			Path filePath = uploadPath.resolve(storedFileName);
			Files.copy(file.getInputStream(), filePath);

			MediaFile mediaFile = new MediaFile();
			mediaFile.setRoomId(roomId);
			mediaFile.setSenderId(senderId);
			mediaFile.setFileName(file.getOriginalFilename());
			mediaFile.setFileType(file.getContentType());
			mediaFile.setFilePath(filePath.toString());
			mediaFile.setUploadedAt(LocalDateTime.now());

			MediaFile saved = mediaFileRepository.save(mediaFile);

			return new MediaUploadResponse(saved.getId(), saved.getRoomId(), saved.getSenderId(), saved.getFileName(),
					saved.getFileType(), saved.getFilePath(), saved.getUploadedAt());
		} catch (IOException e) {
			throw new RuntimeException("File upload failed", e);
		}
	}

	@Override
	public List<MediaFile> getMediaByRoom(UUID roomId) {
		return mediaFileRepository.findByRoomIdOrderByUploadedAtDesc(roomId);
	}
}