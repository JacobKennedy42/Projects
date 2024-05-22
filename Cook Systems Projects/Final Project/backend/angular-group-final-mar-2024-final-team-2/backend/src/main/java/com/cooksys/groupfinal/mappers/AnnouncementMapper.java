package com.cooksys.groupfinal.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.entities.Announcement;

@Mapper(componentModel = "spring", uses = { BasicUserMapper.class })
public interface AnnouncementMapper {
  @Mapping(source = "author.profile.firstName", target = "authorFirstName")
  @Mapping(source = "author.id", target = "authorId")
  @Mapping(source = "company.id", target = "companyId") 
	AnnouncementDto entityToDto(Announcement announcement);

  Set<AnnouncementDto> entitiesToDtos(Set<Announcement> announcement);

  @Mapping(target = "author", ignore = true)
  @Mapping(target = "company", ignore = true) 
  Announcement requestDtoToEntity(AnnouncementRequestDto announcementRequestDto);
    
}
