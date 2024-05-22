package com.cooksys.groupfinal.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.groupfinal.dtos.AnnouncementDto;
import com.cooksys.groupfinal.dtos.AnnouncementRequestDto;
import com.cooksys.groupfinal.dtos.BasicUserDto;
import com.cooksys.groupfinal.entities.Announcement;
import com.cooksys.groupfinal.entities.Company;
import com.cooksys.groupfinal.exceptions.BadRequestException;
import com.cooksys.groupfinal.exceptions.NotFoundException;
import com.cooksys.groupfinal.mappers.AnnouncementMapper;
import com.cooksys.groupfinal.mappers.BasicUserMapper;
import com.cooksys.groupfinal.repositories.AnnouncementRepository;
import com.cooksys.groupfinal.repositories.UserRepository;
import com.cooksys.groupfinal.repositories.CompanyRepository;
import com.cooksys.groupfinal.services.AnnouncementService;
import com.cooksys.groupfinal.entities.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor


public class AnnouncementServiceImpl implements AnnouncementService {
    
    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;
    private final CompanyRepository companyRepository;
    private final AnnouncementMapper announcementMapper;
    
    private Company findCompanyById(Long id) {
        Optional<Company> companyOptional = companyRepository.findById(id);
        if (companyOptional.isEmpty()) {
            throw new NotFoundException("A company with the given id could not be found");
        }
        return companyOptional.get();
    }

    private User findUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("A user with the given id could not be found");
        }
        return userOptional.get();
    }

    private void checkEmpty(String title, String message) {
        if (title == ""){
            throw new BadRequestException("Title cannot be empty");
        }
        if (message == "") {
            throw new BadRequestException("Message cannot be empty");
        }
    }

    
    @Override
    public AnnouncementDto createAnnouncement(Long companyId, Long userId, AnnouncementRequestDto announcementRequestDto) {

        String title = announcementRequestDto.getTitle();
        String message = announcementRequestDto.getMessage();
        checkEmpty(title, message);

        Company company = findCompanyById(companyId);
        User author = findUserById(userId);

        Announcement announcement = announcementMapper.requestDtoToEntity(announcementRequestDto);

        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setAuthor(author);
        announcement.setCompany(company);

        announcementRepository.saveAndFlush(announcement);

        return announcementMapper.entityToDto(announcement);
    }

}