package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.MentorUpdateRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MentorLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static com.project.mentoridge.configuration.AbstractTest.careerCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.educationCreateRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @InjectMocks
    MentorService mentorService;

    @Mock
    UserRepository userRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @Mock
    LectureRepository lectureRepository;
    @Mock
    LectureService lectureService;
    @Mock
    MentorLogService mentorLogService;

    @Test
    void createMentor_alreadyMentor() {

        // given
        User user = getUserWithNameAndRole("user", RoleType.MENTOR);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // when
        // then
        assertThrows(AlreadyExistException.class,
                () -> mentorService.createMentor(user, mock(MentorSignUpRequest.class)));
    }

    @Test
    void createMentor() {
        // user, mentorSignUpRequest

        // given
        User user = getUserWithName("user");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // when
        MentorSignUpRequest mentorSignUpRequest =
                getMentorSignUpRequestWithCareersAndEducations(Arrays.asList(careerCreateRequest), Arrays.asList(educationCreateRequest));
        mentorService.createMentor(user, mentorSignUpRequest);

        // then
        verify(mentorRepository).save(mentorSignUpRequest.toEntity(user));
    }

    // TODO - 도메인 로직 테스트
    @Test
    void updateMentor() {
        // user, mentorUpdateRequest

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        CareerUpdateRequest careerUpdateRequest = mock(CareerUpdateRequest.class);
        EducationUpdateRequest educationUpdateRequest = mock(EducationUpdateRequest.class);
        MentorUpdateRequest mentorUpdateRequest = getMentorUpdateRequestWithCareersAndEducations(
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
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        when(enrollmentRepository.findAllWithLectureMentorByMentorId(mentor.getId()))
                .thenReturn(Collections.emptyList());

        // 진행중인 강의 확인 및 삭제
        when(lectureRepository.findByMentor(mentor))
                .thenReturn(Arrays.asList(mock(Lecture.class), mock(Lecture.class)));
        doNothing().when(lectureService).deleteLecture(any(Lecture.class));

        // when
        mentorService.deleteMentor(user);

        // then
        verify(lectureService, atLeast(2)).deleteLecture(any(Lecture.class));
        verify(mentor).quit();
        verify(mentorRepository).delete(mentor);
    }
}