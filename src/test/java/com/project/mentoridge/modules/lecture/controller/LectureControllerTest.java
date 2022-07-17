package com.project.mentoridge.modules.lecture.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.service.LectureServiceImpl;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static com.project.mentoridge.config.init.TestDataBuilder.getSubjectWithSubjectIdAndKrSubject;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.lectureUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LectureControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/lectures";

    @InjectMocks
    LectureController lectureController;
    @Mock
    LectureServiceImpl lectureService;
    @Mock
    MenteeReviewService menteeReviewService;

//    private User user;
    private Mentor mentor;

    private LecturePrice lecturePrice1;
    private LectureSubject lectureSubject1;
    private Lecture lecture1;

    private LecturePrice lecturePrice2;
    private LectureSubject lectureSubject2;
    private Lecture lecture2;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        mockMvc = MockMvcBuilders.standaloneSetup(lectureController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();

        // user = getUserWithNameAndRole("user", RoleType.MENTOR);
        mentor = Mentor.builder()
                .user(user)
                .build();
        lecturePrice1 = LecturePrice.builder()
                .lecture(null)
                .isGroup(true)
                .numberOfMembers(10)
                .pricePerHour(10000L)
                .timePerLecture(3)
                .numberOfLectures(5)
                .build();
        lectureSubject1 = LectureSubject.builder()
                .lecture(null)
                .subject(getSubjectWithSubjectIdAndKrSubject(1L, "백엔드"))
                .build();
        lecture1 = Lecture.builder()
                .mentor(mentor)
                .title("title1")
                .subTitle("subTitle1")
                .introduce("introduce1")
                .content("content1")
                .difficulty(DifficultyType.ADVANCED)
                .systems(Arrays.asList(SystemType.OFFLINE, SystemType.ONLINE))
                .lecturePrices(Arrays.asList(lecturePrice1))
                .lectureSubjects(Arrays.asList(lectureSubject1))
                .thumbnail("thumbnail1")
                .build();

        lecturePrice2 = LecturePrice.builder()
                .lecture(null)
                .isGroup(false)
                .pricePerHour(20000L)
                .timePerLecture(5)
                .numberOfLectures(10)
                .build();
        lectureSubject2 = LectureSubject.builder()
                .lecture(null)
                .subject(getSubjectWithSubjectIdAndKrSubject(2L, "프론트엔드"))
                .build();
        lecture2 = Lecture.builder()
                .mentor(mentor)
                .title("title2")
                .subTitle("subTitle2")
                .introduce("introduce2")
                .content("content2")
                .difficulty(DifficultyType.BEGINNER)
                .systems(Arrays.asList(SystemType.ONLINE))
                .systems(Arrays.asList(SystemType.OFFLINE, SystemType.ONLINE))
                .lecturePrices(Arrays.asList(lecturePrice2))
                .lectureSubjects(Arrays.asList(lectureSubject2))
                .thumbnail("thumbnail2")
                .build();
    }

    @Test
    void get_eachLectures() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .param("zone", "zone")
                        .param("title", "title")
                        .param("subjects", "sub1", "sub2")
                        .param("systemType", SystemType.ONLINE.name())
                        .param("isGroup", "false")
                        .param("difficultyTypes", DifficultyType.BASIC.name(), DifficultyType.BEGINNER.name()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(lectureService).getEachLectureResponses(any(User.class), eq("zone"), any(LectureListRequest.class), eq(1));
    }

    @Test
    void get_eachLectures_without_auth() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .param("zone", "zone")
                        .param("title", "title")
                        .param("subjects", "sub1", "sub2")
                        .param("systemType", SystemType.ONLINE.name())
                        .param("isGroup", "false")
                        .param("difficultyTypes", DifficultyType.BASIC.name(), DifficultyType.BEGINNER.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(lectureService);
    }

    @Test
    void get_eachLectures_with_wrong_inputs() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .param("zone", "zone")
                        .param("title", "title1", "title2")
                        .param("subjects", "sub1", "sub2")
                        .param("systemType", SystemType.ONLINE.name(), SystemType.OFFLINE.name())
                        .param("isGroup", "false")
                        .param("difficultyTypes", DifficultyType.BASIC.name(), DifficultyType.BEGINNER.name()))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(lectureService);
    }

    @Test
    void get_eachLecture() throws Exception {

        // given
//        EachLectureResponse response = new EachLectureResponse(lecture1.getLecturePrices().get(0), lecture1);
//        doReturn(response)
//                .when(lectureService).getEachLectureResponse(any(User.class), anyLong(), anyLong());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}", 1L, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(lectureService).getEachLectureResponse(any(User.class), eq(1L), eq(1L));
    }

    // TODO - validation 테스트
    @Test
    void new_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lectureCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(lectureService).createLecture(any(User.class), eq(lectureCreateRequest));
    }

    @Test
    void edit_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lectureUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(lectureService).updateLecture(any(User.class), eq(1L), eq(lectureUpdateRequest));
    }

    @Test
    void delete_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(lectureService).deleteLecture(any(User.class), eq(1L));
    }

    @Test
    void get_paged_reviews_of_each_lecture() throws Exception {

        // given
//        Page<ReviewResponse> reviews = Page.empty();
//        doReturn(reviews)
//                .when(menteeReviewService).getReviewResponsesOfEachLecture(anyLong(), anyLong(), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/reviews", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).getReviewResponsesOfEachLecture(eq(1L), eq(1L), eq(1));
    }

    @Test
    void get_review_of_each_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/reviews/{mentee_review_id}", 1L, 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).getReviewResponseOfEachLecture(eq(1L), eq(1L), eq(1L));
    }
}