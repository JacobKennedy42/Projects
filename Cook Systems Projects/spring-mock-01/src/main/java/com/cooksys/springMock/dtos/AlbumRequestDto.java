package com.cooksys.springMock.dtos;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlbumRequestDto {
	
	private String name;
	
	private String artist;
	
	private List<TrackRequestDto> tracks;
}
