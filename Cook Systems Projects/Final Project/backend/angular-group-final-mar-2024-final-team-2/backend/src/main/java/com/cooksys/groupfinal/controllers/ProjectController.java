package com.cooksys.groupfinal.controllers;

import com.cooksys.groupfinal.dtos.ProjectDto;
import com.cooksys.groupfinal.dtos.ProjectRequestDto;

import org.springframework.web.bind.annotation.*;

import com.cooksys.groupfinal.services.ProjectService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
	
	private final ProjectService projectService;

	@GetMapping("/team/{teamId}")
	public List<ProjectDto> getProjectsByTeamId(
			@PathVariable Long teamId) {
		return projectService.getProjectsByTeamId(teamId);
	}

	@PostMapping("/team/{teamId}")
	public ProjectDto createProject(@PathVariable Long teamId, @RequestBody ProjectRequestDto projectRequestDto) {
		return projectService.createProject(teamId, projectRequestDto);
	}

	@PatchMapping("/team/{teamId}/project/{projectId}")
	public ProjectDto updateProject(@PathVariable("teamId") Long teamId, @PathVariable("projectId") Long projectId, @RequestBody ProjectRequestDto projectRequestDto) {
		return projectService.updateProject(teamId, projectId, projectRequestDto);
	}
}
