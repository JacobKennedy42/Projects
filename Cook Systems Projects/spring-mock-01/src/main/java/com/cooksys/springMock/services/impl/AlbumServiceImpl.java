package com.cooksys.springMock.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.cooksys.springMock.dtos.AlbumRequestDto;
import com.cooksys.springMock.dtos.AlbumResponseDto;
import com.cooksys.springMock.entities.Album;
import com.cooksys.springMock.entities.Track;
import com.cooksys.springMock.mappers.AlbumMapper;
import com.cooksys.springMock.repositories.AlbumRepository;
import com.cooksys.springMock.repositories.TrackRepository;
import com.cooksys.springMock.services.AlbumService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
	
	private final AlbumRepository albumRepository;
	private final AlbumMapper albumMapper;
	private final TrackRepository trackRepository;

	private Album saveAlbum (Album album) {
		Album savedAlbum = albumRepository.saveAndFlush(album);
		for (Track track : album.getTracks())
			saveTrack(track, album);
		return savedAlbum;
	}

	private Track saveTrack (Track track, Album parentAlbum) {
		track.setAlbum(parentAlbum);
		track.setArtist(parentAlbum.getArtist());
		return trackRepository.saveAndFlush(track);
	}

	@Override
	public List<AlbumResponseDto> getAllAlbums() {
		return albumMapper.entitiesToDtos(albumRepository.findAll());
	}

	@Override
	public AlbumResponseDto postAlbum (AlbumRequestDto request) {
		Album album = albumMapper.dtoToEntity(request);
		return albumMapper.entityToDto(saveAlbum(album));
	}

}
