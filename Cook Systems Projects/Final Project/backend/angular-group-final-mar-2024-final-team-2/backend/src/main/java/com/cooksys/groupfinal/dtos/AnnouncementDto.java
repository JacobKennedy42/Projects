package com.cooksys.groupfinal.dtos;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class AnnouncementDto {
	
    @NonNull
	private Long id;
    
    @NonNull
    private Timestamp date;
    
    private String title;
    
    private String message;

    private String authorFirstName;

    private Long authorId;

    private Long companyId;

}
