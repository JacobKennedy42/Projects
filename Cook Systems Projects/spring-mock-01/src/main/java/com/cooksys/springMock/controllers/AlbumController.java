package com.cooksys.springMock.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.springMock.dtos.AlbumRequestDto;
import com.cooksys.springMock.dtos.AlbumResponseDto;
import com.cooksys.springMock.services.AlbumService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {
	
	private final AlbumService albumService;
	
	@GetMapping()
	public List<AlbumResponseDto> getAllAlbums() {
		return albumService.getAllAlbums();
	}
	

	@PostMapping()
	public AlbumResponseDto postAlbum (@RequestBody AlbumRequestDto request) {
		return albumService.postAlbum(request);
	}
}
