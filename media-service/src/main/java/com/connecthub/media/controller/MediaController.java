package com.connecthub.media.controller;

import com.connecthub.media.dto.MediaUploadResponse;
import com.connecthub.media.entity.MediaFile;
import com.connecthub.media.service.MediaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/media")
public class MediaController {

	private final MediaService mediaService;

	public MediaController(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	@PostMapping("/upload")
	public MediaUploadResponse uploadFile(@RequestParam UUID roomId, @RequestParam UUID senderId,
			@RequestParam("file") MultipartFile file) {
		return mediaService.uploadFile(roomId, senderId, file);
	}

	@GetMapping("/room/{roomId}")
	public List<MediaFile> getMediaByRoom(@PathVariable UUID roomId) {
		return mediaService.getMediaByRoom(roomId);
	}
}