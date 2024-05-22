package com.cooksys.springMock.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Track {
	
	@Id
	@GeneratedValue
	private Long id;

	private String name;
	
	private String artist;
	
	@ManyToOne
	private Album album;
	
	private double length;
}
