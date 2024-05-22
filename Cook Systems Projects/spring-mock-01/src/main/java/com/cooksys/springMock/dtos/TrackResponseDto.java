package com.cooksys.springMock.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TrackResponseDto {
	
	private Long id;
	
	private String name;
	
	private String artist;
	
	private double length;

}
