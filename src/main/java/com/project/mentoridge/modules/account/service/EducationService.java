package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.EDUCATION;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Transactional
@RequiredArgsConstructor
@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final MentorRepository mentorRepository;
    // TODO - CHECK : user deleted/verified

    private Education getEducation(User user, Long educationId) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        return educationRepository.findByMentorAndId(mentor, educationId)
                .orElseThrow(() -> new EntityNotFoundException(EDUCATION));
    }

    @Transactional(readOnly = true)
    public EducationResponse getEducationResponse(User user, Long educationId) {
        return new EducationResponse(getEducation(user, educationId));
    }

    public Education createEducation(User user, EducationCreateRequest educationCreateRequest) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        Education education = Education.of(
                mentor,
                educationCreateRequest.getEducationLevel(),
                educationCreateRequest.getSchoolName(),
                educationCreateRequest.getMajor(),
                educationCreateRequest.getOthers()
        );
        mentor.addEducation(education);
        return educationRepository.save(education);
    }

    public void updateEducation(User user, Long educationId, EducationUpdateRequest educationUpdateRequest) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        Education education = educationRepository.findByMentorAndId(mentor, educationId)
                .orElseThrow(() -> new EntityNotFoundException(EDUCATION));

        education.update(educationUpdateRequest);
    }

    public void deleteEducation(User user, Long educationId) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        Education education = educationRepository.findByMentorAndId(mentor, educationId)
                .orElseThrow(() -> new EntityNotFoundException(EDUCATION));

        education.delete();
        // TODO - CHECK
        educationRepository.delete(education);
    }

}
