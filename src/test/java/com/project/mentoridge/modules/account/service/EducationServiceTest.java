package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.EducationLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @InjectMocks
    EducationService educationService;
    @Mock
    EducationRepository educationRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    EducationLogService educationLogService;


    @Test
    void getEducationResponse() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        Education education = Education.builder()
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("school")
                .mentor(mentor)
                .build();
        when(educationRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(education));
        // when
        EducationResponse response = educationService.getEducationResponse(user, 1L);
        // then
        assertThat(response.getEducationLevel()).isEqualTo(EducationLevelType.UNIVERSITY);
        assertThat(response.getSchoolName()).isEqualTo("school");
    }

    @Test
    void createEducation() {
        // user, educationCreateRequest

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        EducationCreateRequest educationCreateRequest  = mock(EducationCreateRequest.class);
        Education education = mock(Education.class);
        when(educationCreateRequest.toEntity(mentor)).thenReturn(education);
        Education saved = mock(Education.class);
        when(educationRepository.save(education)).thenReturn(saved);

        // when
        educationService.createEducation(user, educationCreateRequest);

        // then
        verify(educationRepository).save(educationCreateRequest.toEntity(mentor));
        verify(educationLogService).insert(eq(user), eq(saved));
    }

    @Test
    void updateEducation() {
        // user, educationId, educationUpdateRequest

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        Education education = mock(Education.class);
        when(educationRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(education));

        // when
        EducationUpdateRequest educationUpdateRequest = mock(EducationUpdateRequest.class);
        educationService.updateEducation(user, 1L, educationUpdateRequest);

        // then
        verify(education).update(educationUpdateRequest, user, educationLogService);
        // verify(education, atLeastOnce()).setEducationLevel(educationUpdateRequest.getEducationLevel());
    }

    @Test
    void deleteEducation() {
        // user, educationId

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        Education education = mock(Education.class);
        when(educationRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(education));

        // when
        educationService.deleteEducation(user, 1L);

        // then
        verify(education).delete(user, educationLogService);
        verify(educationRepository, atLeastOnce()).delete(education);
    }
}