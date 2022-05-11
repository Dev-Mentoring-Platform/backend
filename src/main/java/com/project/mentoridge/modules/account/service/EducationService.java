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
    // TODO - CHECK : user deleted/verified

        private Education getEducation(User user, Long educationId) {
            Mentor mentor = getMentor(mentorRepository, user);
            return educationRepository.findByMentorAndId(mentor, educationId)
                    .orElseThrow(() -> new EntityNotFoundException(EDUCATION));
        }

    @Transactional(readOnly = true)
    public EducationResponse getEducationResponse(User user, Long educationId) {
        return new EducationResponse(getEducation(user, educationId));
    }

    public Education createEducation(User user, EducationCreateRequest educationCreateRequest) {

        Mentor mentor = getMentor(mentorRepository, user);
        Education education = educationCreateRequest.toEntity(mentor);
        mentor.addEducation(education);

        Education saved = educationRepository.save(education);
        educationLogService.insert(user, saved);
        return saved;
    }

    public void updateEducation(User user, Long educationId, EducationUpdateRequest educationUpdateRequest) {

        Education education = getEducation(user, educationId);

        Education before = education.copy();
        education.update(educationUpdateRequest);
        educationLogService.update(user, before, education);
    }

    public void deleteEducation(User user, Long educationId) {

        Education education = getEducation(user, educationId);
        education.delete();
        // TODO - CHECK
        educationRepository.delete(education);
        educationLogService.delete(user, education);
    }

}
