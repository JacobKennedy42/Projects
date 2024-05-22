package com.cooksys.springMock.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.springMock.dtos.AlbumRequestDto;
import com.cooksys.springMock.dtos.AlbumResponseDto;
import com.cooksys.springMock.entities.Album;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

	AlbumResponseDto entityToDto(Album album);
	
	List<AlbumResponseDto> entitiesToDtos(List<Album> albums);
	
	Album dtoToEntity(AlbumRequestDto album);
	
}
