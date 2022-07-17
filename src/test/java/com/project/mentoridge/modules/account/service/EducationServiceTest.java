package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    EducationRepository educationRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    EducationLogService educationLogService;
    @InjectMocks
    EducationService educationService;
/*
    @BeforeEach
    void init() {
        assertNotNull(educationRepository);
        assertNotNull(mentorRepository);
        assertNotNull(educationService);
    }*/

    @Test
    void getEducationResponse() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        EducationResponse response = educationService.getEducationResponse(user, 1L);
        // then
        verify(educationRepository).findByMentorAndId(mentor, 1L);
    }

    @Test
    void createEducation() {
        // user, educationCreateRequest

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        EducationCreateRequest educationCreateRequest  = mock(EducationCreateRequest.class);
        educationService.createEducation(user, educationCreateRequest);

        // then
        verify(educationRepository).save(any(Education.class));
        verify(educationLogService).insert(eq(user), any(Education.class));
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