package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.MentorUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MentorLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.project.mentoridge.modules.base.TestDataBuilder.getUserWithName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
    MessageRepository messageRepository;
    @Mock
    ChatroomRepository chatroomRepository;

    @Mock
    LectureRepository lectureRepository;
    @Mock
    LectureService lectureService;
    @Mock
    UserLogService userLogService;
    @Mock
    MentorLogService mentorLogService;

    @Mock
    CareerRepository careerRepository;
    @Mock
    EducationRepository educationRepository;

    @Test
    void get_MentorResponses() {

        // given
        User user = User.builder()
                .username("user@email.com")
                .password("password")
                .name("userName")
                .gender(GenderType.MALE)
                .birthYear("19941013")
                .phoneNumber("01012345678")
                .nickname("userNickname")
                .zone("서울특별시 강남구 삼성동")
                .image("image")
                .role(RoleType.MENTOR)
                .provider(null)
                .providerId(null)
                .build();
        Mentor mentor = Mentor.builder()
                .bio("bio")
                .user(user)
                .careers(Arrays.asList(Career.builder()
                        .companyName("company")
                        .job("job")
                        .license("license")
                        .others("others")
                        .build()))
                .educations(Arrays.asList(Education.builder()
                        .educationLevel(EducationLevelType.COLLEGE)
                        .major("major")
                        .schoolName("school")
                        .others("others")
                        .build()))
                .build();
        when(mentorRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(mentor)));

        // when
        Page<MentorResponse> response = mentorService.getMentorResponses(1);
        // then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getBio()).isEqualTo("bio");
    }

    @Test
    void get_MentorResponse_by_user_but_not_existed() {

        // given
        User user = mock(User.class);
        when(mentorRepository.findByUser(user)).thenReturn(null);
        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> mentorService.getMentorResponse(user));
    }

    @Test
    void get_MentorResponse_by_user() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mentorUser);
        when(mentor.getId()).thenReturn(1L);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);
        when(enrollmentRepository.countAllMenteesByMentor(1L)).thenReturn(3);

        // when
        MentorResponse response = mentorService.getMentorResponse(mentorUser);
        // then
        assertThat(response.getAccumulatedMenteeCount()).isEqualTo(3);
    }

    @Test
    void _get_MentorResponse_by_user() {

        // given
        User user = User.builder()
                .username("user@email.com")
                .password("password")
                .name("userName")
                .gender(GenderType.MALE)
                .birthYear("19941013")
                .phoneNumber("01012345678")
                .nickname("userNickname")
                .zone("서울특별시 강남구 삼성동")
                .image("image")
                .role(RoleType.MENTOR)
                .provider(null)
                .providerId(null)
                .build();
        Mentor mentor = Mentor.builder()
                .bio("bio")
                .user(user)
                .careers(Arrays.asList(Career.builder()
                                .companyName("company")
                                .job("job")
                                .license("license")
                                .others("others")
                        .build()))
                .educations(Arrays.asList(Education.builder()
                                .educationLevel(EducationLevelType.COLLEGE)
                                .major("major")
                                .schoolName("school")
                                .others("others")
                        .build()))
                .build();
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        // 누적 멘티
        when(enrollmentRepository.countAllMenteesByMentor(mentor.getId())).thenReturn(5);

        // when
        MentorResponse response = mentorService.getMentorResponse(user);
        // then
        assertAll(
                () -> assertThat(response).extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("user").hasNoNullFieldsOrPropertiesExcept("userId"),
//                () -> assertThat(response).extracting("user")
//                        .hasOnlyFields("userId", "username", "role", "name", "gender", "birthYear", "phoneNumber", "nickname", "image", "zone"),
                () -> assertThat(response).extracting("bio").isEqualTo(mentor.getBio()),

                () -> assertThat(response.getCareers()).hasSize(1),
                () -> assertThat(response.getEducations()).hasSize(1),
                // 누적 멘티
                () -> assertThat(response).extracting("accumulatedMenteeCount").isEqualTo(5)
        );
    }

    @Test
    void get_MentorResponse_by_id_but_not_existed() {

        // given
        when(mentorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        // then
        assertThrows(EntityNotFoundException.class, () -> mentorService.getMentorResponse(1L));
    }

    @Test
    void get_MentorResponse_by_id() {

        // given
        User user = getUserWithName("user");
        Mentor mentor = Mentor.builder()
                .bio("bio")
                .user(user)
                .careers(Arrays.asList(Career.builder()
                        .companyName("company")
                        .job("job")
                        .license("license")
                        .others("others")
                        .build()))
                .educations(Arrays.asList(Education.builder()
                        .educationLevel(EducationLevelType.COLLEGE)
                        .major("major")
                        .schoolName("school")
                        .others("others")
                        .build()))
                .build();
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        // 누적 멘티
        when(enrollmentRepository.countAllMenteesByMentor(1L)).thenReturn(5);

        // when
        // then
        MentorResponse response = mentorService.getMentorResponse(1L);
        assertAll(
                // () -> assertThat(response).extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("user").extracting("userId").isEqualTo(user.getId()),
                () -> assertThat(response).extracting("user").extracting("username").isEqualTo(user.getUsername()),
                () -> assertThat(response).extracting("user").extracting("role").isEqualTo(user.getRole()),
                () -> assertThat(response).extracting("user").extracting("name").isEqualTo(user.getName()),
                () -> assertThat(response).extracting("user").extracting("gender").isEqualTo(user.getGender()),
                () -> assertThat(response).extracting("user").extracting("birthYear").isEqualTo(user.getBirthYear()),
                () -> assertThat(response).extracting("user").extracting("phoneNumber").isEqualTo(user.getPhoneNumber()),
                () -> assertThat(response).extracting("user").extracting("nickname").isEqualTo(user.getNickname()),
                () -> assertThat(response).extracting("user").extracting("image").isEqualTo(user.getImage()),
                () -> assertThat(response).extracting("user").extracting("zone").isEqualTo(user.getZone().toString()),
                () -> assertThat(response).extracting("bio").isEqualTo(mentor.getBio()),

                () -> assertThat(response).extracting("careers").isOfAnyClassIn(ArrayList.class),
                () -> assertThat(response).extracting("educations").isOfAnyClassIn(ArrayList.class),
                // 누적 멘티
                () -> assertThat(response).extracting("accumulatedMenteeCount").isEqualTo(5)
        );
    }

    @Test
    void createMentor_when_user_is_already_mentor() {

        // given
        User user = mock(User.class);
        when(user.getRole()).thenReturn(RoleType.MENTOR);
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
        User user = mock(User.class);
        when(user.getRole()).thenReturn(RoleType.MENTEE);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        MentorSignUpRequest mentorSignUpRequest = mock(MentorSignUpRequest.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorSignUpRequest.toEntity(user)).thenReturn(mentor);
        Mentor saved = mock(Mentor.class);
        when(mentorRepository.save(mentor)).thenReturn(saved);

        // when
        mentorService.createMentor(user, mentorSignUpRequest);

        // then
        verify(user).joinMentor(userLogService);
        // verify(userLogService).update(eq(user), any(User.class), any(User.class));
        verify(mentorRepository).save(any(Mentor.class));
        verify(mentorLogService).insert(user, saved);
    }

    @Test
    void updateMentor() {
        // user, mentorUpdateRequest

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
//        CareerUpdateRequest careerUpdateRequest = mock(CareerUpdateRequest.class);
//        EducationUpdateRequest educationUpdateRequest = mock(EducationUpdateRequest.class);
//        MentorUpdateRequest mentorUpdateRequest = getMentorUpdateRequestWithCareersAndEducations(
//                Arrays.asList(careerUpdateRequest),
//                Arrays.asList(educationUpdateRequest)
//        );
        MentorUpdateRequest mentorUpdateRequest = mock(MentorUpdateRequest.class);
        mentorService.updateMentor(user, mentorUpdateRequest);

        // then
        verify(mentor).update(mentorUpdateRequest, user, mentorLogService);
        // verify(mentorLogService).update(eq(user), any(Mentor.class), any(Mentor.class));
    }

    @Test
    void deleteMentor_when_exist_unfinished_enrollment() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        when(enrollmentRepository.countUnfinishedEnrollmentOfMentor(mentor.getId())).thenReturn(2);

        // when
        // then
        assertThrows(RuntimeException.class,
                () -> mentorService.deleteMentor(user));
    }

    @Test
    void deleteMentor() {
        // user

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);
        when(enrollmentRepository.countUnfinishedEnrollmentOfMentor(mentor.getId())).thenReturn(0);

        List<Long> chatroomIds = Arrays.asList(1L, 2L);
        when(chatroomRepository.findIdsByMentor(mentor)).thenReturn(chatroomIds);

        // 진행중인 강의 확인 및 삭제
        Lecture lecture1 = mock(Lecture.class);
        Lecture lecture2 = mock(Lecture.class);
        when(lectureRepository.findByMentor(mentor)).thenReturn(Arrays.asList(lecture1, lecture2));

        // when
        mentorService.deleteMentor(mentorUser);

        // then
        verify(messageRepository).deleteByChatroomIds(chatroomIds);
        verify(chatroomRepository).deleteByIds(chatroomIds);
        verify(lectureService).deleteLecture(lecture1);
        verify(lectureService).deleteLecture(lecture2);
        verify(lectureService, atLeast(2)).deleteLecture(any(Lecture.class));

        verify(mentor).delete(mentorUser, mentorLogService, userLogService);
        // verify(mentorLogService).delete(user, mentor);
        // verify(user).quitMentor(userLogService);
        verify(mentorRepository).delete(mentor);
    }

    @Test
    void getCareerResponses() {

        // given
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        // when
        mentorService.getCareerResponses(1L);
        // then
        verify(careerRepository).findByMentor(mentor);
    }

    @Test
    void getEducationResponses() {

        // given
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        // when
        mentorService.getEducationResponses(1L);
        // then
        verify(educationRepository).findByMentor(mentor);
    }
}