package com.cooksys.groupfinal.services.impl;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;
import com.cooksys.groupfinal.entities.Project;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.ProjectMapper;
import com.cooksys.groupfinal.repositories.ProjectRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.services.ProjectService;

import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TeamRepository teamRepository;

    private Team findTeamById(Long id) {
        Optional<Team> teamOptional = teamRepository.findById(id);
        if (teamOptional.isEmpty()) {
            throw new NotFoundException("A team with the given id could not be found");
        }
        return teamOptional.get();
    }

    @Override
    public List<ProjectDto> getProjectsByTeamId(Long teamId) {
        return new ArrayList<>(projectMapper.entitiesToDtos(new HashSet<>(projectRepository.findByTeamId(teamId))));
    }

	@Override
	public ProjectDto createProject(Long id, ProjectRequestDto projectRequestDto) {
		Team team = findTeamById(id);
        Project project = projectMapper.requestDtoToEntity(projectRequestDto);
        project.setTeam(team);

        if ( projectRequestDto.getActive() != null ) {
            project.setActive( projectRequestDto.getActive() );
        }
        else {
            project.setActive(false);
        }

        project = projectRepository.saveAndFlush(project);
        return projectMapper.entityToDto(project);
	}

    @Override
    public ProjectDto updateProject(Long teamId, Long projectId, ProjectRequestDto projectRequestDto) {

        Team team = findTeamById(teamId);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project with the given id could not be found"));

        project.setName(projectRequestDto.getName());
        project.setDescription(projectRequestDto.getDescription());
        project.setActive(projectRequestDto.getActive());
        project.setTeam(team);

        return projectMapper.entityToDto(projectRepository.saveAndFlush(project));
    }
}
