package com.cooksys.springMock.services;

import java.util.List;

import com.cooksys.springMock.dtos.AlbumRequestDto;
import com.cooksys.springMock.dtos.AlbumResponseDto;

public interface AlbumService {

	List<AlbumResponseDto> getAllAlbums();
	AlbumResponseDto postAlbum(AlbumRequestDto request);

}
