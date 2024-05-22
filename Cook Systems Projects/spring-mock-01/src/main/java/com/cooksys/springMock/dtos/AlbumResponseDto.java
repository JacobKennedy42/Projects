package com.cooksys.springMock.dtos;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlbumResponseDto {
	
	private Long id;
	
	private String artist;
	
	private String name;
	
	private List<TrackResponseDto> tracks;

}
