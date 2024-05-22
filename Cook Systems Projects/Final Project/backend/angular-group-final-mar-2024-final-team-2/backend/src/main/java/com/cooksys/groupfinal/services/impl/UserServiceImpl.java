package com.cooksys.groupfinal.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.BasicUserDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.entities.Credentials;
import com.cooksys.groupfinal.entities.User;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotAuthorizedException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.BasicUserMapper;
import com.cooksys.groupfinal.mappers.CredentialsMapper;
import com.cooksys.groupfinal.mappers.FullUserMapper;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final FullUserMapper fullUserMapper;
    private final BasicUserMapper basicUserMapper;
	private final CredentialsMapper credentialsMapper;
	
	private User findUser(String username) {
        Optional<User> user = userRepository.findByCredentialsUsernameAndActiveTrue(username);
        if (user.isEmpty()) {
            throw new NotFoundException("The username provided does not belong to an active user.");
        }
        return user.get();
    }

    private User findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("The id provided does not belong to an active user.");
        }
        return user.get();
    }

    private Company findCompanyById(Long id) {
        Optional<Company> companyOptional = companyRepository.findById(id);
        if (companyOptional.isEmpty()) {
            throw new NotFoundException("Company with given id could not found");
        }
        return companyOptional.get();
    }
	
	@Override
	public FullUserDto login(CredentialsDto credentialsDto) {
		if (credentialsDto == null || credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
            throw new BadRequestException("A username and password are required.");
        }
        Credentials credentialsToValidate = credentialsMapper.dtoToEntity(credentialsDto);
        User userToValidate = findUser(credentialsDto.getUsername());
        if (!userToValidate.getCredentials().equals(credentialsToValidate)) {
            throw new NotAuthorizedException("The provided credentials are invalid.");
        }
        if (userToValidate.getStatus().equals("PENDING")) {
        	userToValidate.setStatus("JOINED");
        	userRepository.saveAndFlush(userToValidate);
        }
        return fullUserMapper.entityToFullUserDto(userToValidate);
	}

    @Override
    public BasicUserDto changeUserAdminStatus(Long id, boolean adminStatus) {
        User user = findUserById(id);
        user.setAdmin(adminStatus);
        return basicUserMapper.entityToBasicUserDto(userRepository.saveAndFlush(user));
    }
	
	
    @Override
    public BasicUserDto createUser(Long companyId, UserRequestDto userRequestDto) {
    	
    	if (!userRepository.findByCredentialsUsername(userRequestDto.getCredentials().getUsername()).isEmpty()){
    		throw new BadRequestException("Username already exists");
    	}
    	
        User userToSave = basicUserMapper.requestDtoToEntity(userRequestDto);
        Company company = findCompanyById(companyId);
        userToSave.getCompanies().add(company);
        company.getEmployees().add(userToSave);
        
        userToSave = userRepository.saveAndFlush(userToSave);
        companyRepository.saveAndFlush(company);
        return basicUserMapper.entityToBasicUserDto(userToSave);
    }
	
	
	
	

}
