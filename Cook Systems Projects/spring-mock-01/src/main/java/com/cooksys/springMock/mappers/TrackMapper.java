package com.cooksys.springMock.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.springMock.dtos.TrackRequestDto;
import com.cooksys.springMock.dtos.TrackResponseDto;
import com.cooksys.springMock.entities.Track;

@Mapper(componentModel = "spring")
public interface TrackMapper {
	
	TrackResponseDto entityToDto(Track track);
	
	List<TrackResponseDto> entitiesToDtos(List<Track> tracks);
	
	Track dtoToEntity(TrackRequestDto track);
	
	List<Track> dtosToEntities(List<TrackRequestDto> tracks);

}
