package com.cooksys.springMock.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TrackRequestDto {
	
	private String name;
	
	private double length;

}
