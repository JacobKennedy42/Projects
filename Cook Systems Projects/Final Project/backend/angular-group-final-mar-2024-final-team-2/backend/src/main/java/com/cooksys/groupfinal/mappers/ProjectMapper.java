package com.cooksys.groupfinal.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.entities.Project;

@Mapper(componentModel = "spring", uses = { TeamMapper.class })
public interface ProjectMapper {
	
	ProjectDto entityToDto(Project project);

    Set<ProjectDto> entitiesToDtos(Set<Project> projects);

    // TODO: figure out how to add the team as a param to the mapper without it complaining. Currently set to ignore
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true) 
    Project requestDtoToEntity(ProjectRequestDto projectRequestDto);
}
