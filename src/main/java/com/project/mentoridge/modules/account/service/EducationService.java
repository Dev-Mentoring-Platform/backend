package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.log.component.EducationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.EDUCATION;

@Transactional
@RequiredArgsConstructor
@Service
public class EducationService extends AbstractService {

    private final EducationRepository educationRepository;
    private final MentorRepository mentorRepository;
    private final EducationLogService educationLogService;

        private Education getEducation(User mentorUser, Long educationId) {
            Mentor mentor = getMentor(mentorRepository, mentorUser);
            return educationRepository.findByMentorAndId(mentor, educationId)
                    .orElseThrow(() -> new EntityNotFoundException(EDUCATION));
        }

    @Transactional(readOnly = true)
    public EducationResponse getEducationResponse(User user, Long educationId) {
        return new EducationResponse(getEducation(user, educationId));
    }

    public Education createEducation(User mentorUser, EducationCreateRequest educationCreateRequest) {

        Mentor mentor = getMentor(mentorRepository, mentorUser);
        Education education = educationCreateRequest.toEntity(mentor);
        mentor.addEducation(education);

        Education saved = educationRepository.save(education);
        educationLogService.insert(mentorUser, saved);
        return saved;
    }

    public void updateEducation(User mentorUser, Long educationId, EducationUpdateRequest educationUpdateRequest) {

        Education education = getEducation(mentorUser, educationId);
        education.update(educationUpdateRequest, mentorUser, educationLogService);
    }

    public void deleteEducation(User mentorUser, Long educationId) {

        Education education = getEducation(mentorUser, educationId);
        education.delete(mentorUser, educationLogService);
        educationRepository.delete(education);
    }

}
