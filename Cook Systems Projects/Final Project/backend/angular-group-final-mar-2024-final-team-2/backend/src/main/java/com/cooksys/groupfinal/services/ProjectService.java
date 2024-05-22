package com.cooksys.groupfinal.services;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;

import java.util.List;

public interface ProjectService {

    List<ProjectDto> getProjectsByTeamId(Long teamId);

    ProjectDto createProject(Long id, ProjectRequestDto projectRequestDto);

    ProjectDto updateProject(Long teamId, Long projectId, ProjectRequestDto projectRequestDto);
}
