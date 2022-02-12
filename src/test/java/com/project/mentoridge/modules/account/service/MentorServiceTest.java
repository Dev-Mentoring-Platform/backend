package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @InjectMocks
    MentorService mentorService;

    @Mock
    UserRepository userRepository;
    @Mock
    MentorRepository mentorRepository;

    @Spy
    EnrollmentRepository enrollmentRepository;
    @Spy
    LectureRepository lectureRepository;
    @Spy
    LectureService lectureService;

    @Test
    void createMentor_alreadyMentor() {

        // given
        String email = "user@email.com";
        User user = User.of(email, null, null, null, null, null,
                email, null, null, null, null, RoleType.MENTOR, null, null);
        when(userRepository.findByUsername(email)).thenReturn(Optional.of(user));

        // when
        // then
        assertThrows(AlreadyExistException.class,
                () -> mentorService.createMentor(user, any(MentorSignUpRequest.class)));
    }

    @Test
    void createMentor() {
        // user, mentorSignUpRequest

        // given
        String email = "user@email.com";
        User user = User.of(email, null, null, null, null, null,
                email, null, null, null, null, RoleType.MENTEE, null, null);
        when(userRepository.findByUsername(email)).thenReturn(Optional.of(user));

        // when
        CareerCreateRequest careerCreateRequest1 = Mockito.mock(CareerCreateRequest.class);
        CareerCreateRequest careerCreateRequest2 = Mockito.mock(CareerCreateRequest.class);
        EducationCreateRequest educationCreateRequest1 = Mockito.mock(EducationCreateRequest.class);
        EducationCreateRequest educationCreateRequest2 = Mockito.mock(EducationCreateRequest.class);
        MentorSignUpRequest mentorSignUpRequest = MentorSignUpRequest.of(
                Arrays.asList(careerCreateRequest1, careerCreateRequest2),
                Arrays.asList(educationCreateRequest1, educationCreateRequest2)
        );
        mentorService.createMentor(user, mentorSignUpRequest);

        // then
        verify(careerCreateRequest1).getCompanyName();
        verify(careerCreateRequest2).getCompanyName();
        verify(educationCreateRequest1).getSchoolName();
        verify(educationCreateRequest2).getSchoolName();

        verify(mentorRepository).save(any(Mentor.class));
    }

    // TODO - 도메인 로직 테스트
    @Test
    void updateMentor() {
        // user, mentorUpdateRequest

        // given
        User user = Mockito.mock(User.class);
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        CareerUpdateRequest careerUpdateRequest = Mockito.mock(CareerUpdateRequest.class);
        EducationUpdateRequest educationUpdateRequest = Mockito.mock(EducationUpdateRequest.class);
        MentorUpdateRequest mentorUpdateRequest = MentorUpdateRequest.of(
                Arrays.asList(careerUpdateRequest),
                Arrays.asList(educationUpdateRequest)
        );
        mentorService.updateMentor(user, mentorUpdateRequest);

        // then
        verify(mentor).updateCareers(mentorUpdateRequest.getCareers());
        verify(mentor).updateEducations(mentorUpdateRequest.getEducations());

    }

    @Test
    void deleteMentor() {
        // user

        // given
        User user = Mockito.mock(User.class);
        Mentor mentor = Mockito.mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // TODO - 수강 중인 강의 확인 및 삭제 TEST
        // when
        mentorService.deleteMentor(user);

        // then
        verify(mentor).quit();
        verify(mentorRepository).delete(mentor);
    }
}