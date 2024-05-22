package com.cooksys.groupfinal.services.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.TeamDto;
import com.cooksys.groupfinal.dtos.TeamRequestDto;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Team;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.TeamMapper;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.TeamRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.TeamService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final TeamRepository teamRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    Company findCompanyById(Long id) {
        Optional<Company> companyOptional = companyRepository.findById(id);
        if (companyOptional.isEmpty()) {
            throw new NotFoundException("Company with the given id could not be found");
        }
        return companyOptional.get();
    }

    public void checkUserExists(Set<Long> ids) {
        for (Long id : ids) {
            if (userRepository.findById(id).isEmpty()) {
                throw new NotFoundException("User with " + id + " does not exist");
            }
            
        } 
    }

    public void checkCompanyUsers(Set<Long> ids, Company company) {
        for (Long id : ids) {
            User user = userRepository.findById(id).get();
            if (!company.getEmployees().contains(user)) {
                throw new NotFoundException("User with " + id + " does not exist in this company: " + company.getId());
            }
        } 
    }

    @Override
    public TeamDto createTeam(Long companyId, TeamRequestDto teamRequestDto) {
        Team team = teamMapper.requestDtoToEntity(teamRequestDto);
        Set<User> teammates = Set.copyOf(userRepository.findAllById(teamRequestDto.getTeammateIds()));
        Company company = findCompanyById(companyId);

        checkUserExists(teamRequestDto.getTeammateIds());
        checkCompanyUsers(teamRequestDto.getTeammateIds(), company);
        team.setName(teamRequestDto.getName());
        team.setDescription(teamRequestDto.getDescription());
        team.setCompany(company);
        team.setTeammates(teammates);
        team = teamRepository.saveAndFlush(team);

        // add this team to each teammates' teams field
        for (User teammate : teammates) {
            Set<Team> teammateTeams = teammate.getTeams();
            teammateTeams.add(team);
            teammate.setTeams(teammateTeams);
        }
        userRepository.saveAllAndFlush(teammates);

        // add this team to the company's teams
        Set<Team> companyTeams = company.getTeams();
        companyTeams.add(team);
        company.setTeams(companyTeams);
        companyRepository.saveAndFlush(company);

        return teamMapper.entityToDto(team);
    }

}
