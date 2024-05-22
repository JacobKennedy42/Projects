package com.cooksys.springMock.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Album {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String artist;
	
	private String name;
	
	@OneToMany(mappedBy = "album")
	private List<Track> tracks;

}
