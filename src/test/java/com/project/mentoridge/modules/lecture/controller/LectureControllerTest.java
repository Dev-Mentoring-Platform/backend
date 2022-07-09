package com.project.mentoridge.modules.lecture.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.service.LectureServiceImpl;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getSubjectWithSubjectIdAndKrSubject;
import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithNameAndRole;
import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.lectureUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LectureControllerTest {

    private final static String BASE_URL = "/api/lectures";

    @InjectMocks
    LectureController lectureController;
    @Mock
    LectureServiceImpl lectureService;
    @Mock
    MenteeReviewService menteeReviewService;
    @Mock
    PickRepository pickRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Mentor mentor;

    private LecturePrice lecturePrice1;
    private LectureSubject lectureSubject1;
    private Lecture lecture1;

    private LecturePrice lecturePrice2;
    private LectureSubject lectureSubject2;
    private Lecture lecture2;

    @BeforeEach
    void init() {

        mockMvc = MockMvcBuilders.standaloneSetup(lectureController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();

        user = getUserWithNameAndRole("user", RoleType.MENTOR);
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

    // TODO - CHECK / 파라미터 테스트
    @Test
    void getLectures() throws Exception {

        // given
        Page<LectureResponse> lectures =
                new PageImpl<>(Arrays.asList(new LectureResponse(lecture1), new LectureResponse(lecture2)), Pageable.ofSize(20), 2);
        doReturn(lectures)
                .when(lectureService).getEachLectureResponses(any(User.class), anyString(), any(LectureListRequest.class), anyInt());
        // when
        // then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("_zone", "서울특별시 강남구 청담동");
        params.add("title", "title1");
        params.add("subjects", "java,python");
        params.add("systemType", "OFFLINE");
        params.add("isGroup", "true");
        params.add("difficultyTypes", "BASIC,ADVANCED");
        params.add("page", "1");
        String response = mockMvc.perform(get(BASE_URL)
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(lectures)))
                .andReturn().getResponse().getContentAsString();

        _Page _response = objectMapper.readValue(response, _Page.class);
        String content = objectMapper.writeValueAsString(_response.getContent());
        Field[] fields = LectureResponse.class.getDeclaredFields();
        LectureResponse[] lectureResponses = objectMapper.readValue(content, LectureResponse[].class);
//        for(LectureResponse lectureResponse : lectureResponses) {
//            for(Field field : fields) {
//                String _field = field.getName();
//                if (_field.equals("id")) {
//                    continue;
//                }
//                // assertThat(lectureResponse).extracting(_field).isNotNull();
//            }
//        }
    }

    @Data
    @NoArgsConstructor
    private static class _Page<T>{
        List<T> content;
        Object pageable;
        boolean last;
        int totalPages;
        int totalElements;
        Object sort;
        boolean first;
        int numberOfElements;
        int number;
        int size;
        boolean empty;
    }

    @Test
    void getLectures_authenticated() throws Exception {

        // given
        Page<LectureResponse> lectures =
                new PageImpl<>(Arrays.asList(new LectureResponse(lecture1), new LectureResponse(lecture2)), Pageable.ofSize(20), 2);
        //doCallRealMethod()
        doReturn(lectures)
                .when(lectureService).getEachLectureResponses(any(), anyString(), any(LectureListRequest.class), anyInt());
        // when
        // then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("_zone", "서울특별시 강남구 청담동");
        params.add("title", "title1");
        params.add("subjects", "java,python");
        params.add("systemType", "OFFLINE");
        params.add("isGroup", "true");
        params.add("difficultyTypes", "BASIC,ADVANCED");
        params.add("page", "1");

        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
        mockMvc.perform(get(BASE_URL).with(securityContext(securityContext))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(lectures)));

        // verify(pickRepository).findByMenteeAndLectureId(any(Mentee.class), anyLong());
    }

//    @Test
//    void getLectures_with_noZone() throws Exception {
//
//        // given
//        Page<LectureResponse> lectures =
//                new PageImpl<>(Arrays.asList(new LectureResponse(lecture1), new LectureResponse(lecture2)), Pageable.ofSize(20), 2);
//
////        LectureListRequest lectureListRequest =
////                LectureListRequest.of("title1", Arrays.asList("java", "python"), SystemType.OFFLINE, true, Arrays.asList(DifficultyType.BASIC, DifficultyType.ADVANCED));
////        doReturn(lectures)
////                .when(lectureService).getLectureResponses(null, lectureListRequest, 1);
//        doReturn(lectures)
//                .when(lectureService).getLectureResponses(any(), any(LectureListRequest.class), anyInt());
//        // when
//        // then
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("title", "title1");
//        params.add("subjects", "java,python");
//        params.add("systemType", "OFFLINE");
//        params.add("isGroup", "true");
//        params.add("difficultyTypes", "BASIC,ADVANCED");
//        params.add("page", "1");
//        mockMvc.perform(get(BASE_URL)
//                .params(params))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(lectures)));
//    }

    @Test
    void get_EachLectureResponse() throws Exception {

        // given
        EachLectureResponse response = new EachLectureResponse(lecture1.getLecturePrices().get(0), lecture1);
        doReturn(response)
                .when(lectureService).getEachLectureResponse(any(User.class), anyLong(), anyLong());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    // TODO - validation 테스트
    @Test
    void newLecture() throws Exception {

        // given
        doReturn(lecture1)
                .when(lectureService).createLecture(any(User.class), any(LectureCreateRequest.class));

        // when
        // then
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lectureCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void editLecture() throws Exception {

        // given
        doNothing()
                .when(lectureService).updateLecture(any(User.class), anyLong(), any(LectureUpdateRequest.class));

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lectureUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteLecture() throws Exception {

        // given
        doNothing()
                .when(lectureService).deleteLecture(any(User.class), anyLong());

        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void getReviewsOfLecture() throws Exception {

        // given
        Page<ReviewResponse> reviews = Page.empty();
        doReturn(reviews)
                .when(menteeReviewService).getReviewResponsesOfLecture(anyLong(), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviews)));
    }

}