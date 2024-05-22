package com.cooksys.groupfinal.mappers;

import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.TeamRequestDto;
import com.cooksys.groupfinal.entities.Team;

@Mapper(componentModel = "spring", uses = { BasicUserMapper.class })
public interface TeamMapper {
	
	TeamDto entityToDto(Team team);

  Set<TeamDto> entitiesToDtos(Set<Team> teams);

  @AfterMapping
  default void setNumberOfProjects(Team team, @MappingTarget TeamDto teamDto) {
    teamDto.setNumberOfProjects(team.getProjects().size());
  }

  Team requestDtoToEntity(TeamRequestDto teamRequestDto);

}