package com.cooksys.groupfinal.dtos;

import java.security.Timestamp;

import org.apache.tomcat.jni.Time;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AnnouncementRequestDto {
    private String title;
    
    private String message;
    
    private Long authorId;

    private Long teamId;

}
