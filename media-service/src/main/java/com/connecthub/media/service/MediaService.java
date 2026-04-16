package com.connecthub.media.service;

import com.connecthub.media.dto.MediaUploadResponse;
import com.connecthub.media.entity.MediaFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaService {
	MediaUploadResponse uploadFile(UUID roomId, UUID senderId, MultipartFile file);

	List<MediaFile> getMediaByRoom(UUID roomId);
}