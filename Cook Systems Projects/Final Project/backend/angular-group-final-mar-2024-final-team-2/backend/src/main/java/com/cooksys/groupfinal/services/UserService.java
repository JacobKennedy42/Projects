package com.cooksys.groupfinal.services;


import com.cooksys.groupfinal.dtos.BasicUserDto;
import com.cooksys.groupfinal.dtos.CredentialsDto;
import com.cooksys.groupfinal.dtos.FullUserDto;
import com.cooksys.groupfinal.dtos.UserRequestDto;

public interface UserService {

	FullUserDto login(CredentialsDto credentialsDto);
	BasicUserDto changeUserAdminStatus(Long id, boolean adminStatus);
	BasicUserDto createUser(Long companyId, UserRequestDto userRequestDto);
}
