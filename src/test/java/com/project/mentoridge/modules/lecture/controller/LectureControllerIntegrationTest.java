package com.project.mentoridge.modules.lecture.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class LectureControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/lectures";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    LecturePriceRepository lecturePriceRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    PickService pickService;
    @Autowired
    PickRepository pickRepository;
    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    MentorReviewRepository mentorReviewRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessToken;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

    @BeforeEach
    void init() {

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);
    }

    /*

    // TODO - CHECK / 파라미터 테스트
    @Test
    void getEachLectures() throws Exception {

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
    void getEachLectures_authenticated() throws Exception {

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
    */

    @Test
    void get_each_lectures() throws Exception {

        // given
        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);

        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$..title").value(lecture.getTitle()))
                .andExpect(jsonPath("$..subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$..introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$..content").value(lecture.getContent()))
                .andExpect(jsonPath("$..difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$..systems").exists())
                .andExpect(jsonPath("$..lectureSubjects").exists())
                .andExpect(jsonPath("$..thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$..approved").value(lecture.isApproved()))
                // lecturePrice
                .andExpect(jsonPath("$..lecturePrice").exists())
                .andExpect(jsonPath("$..lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$..lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$..lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$..lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$..lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$..lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$..lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$..lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$..lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$..lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$..lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$..closed").value(lecturePrice.isClosed()))
                // lectureMentor
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..lectureMentor.lectureCount").value(1L))
                .andExpect(jsonPath("$..lectureMentor.reviewCount").value(0L))

                .andExpect(jsonPath("$..reviewCount").value(0L))
                .andExpect(jsonPath("$..scoreAverage").value(0.0))
                .andExpect(jsonPath("$..enrollmentCount").value(0L))
                .andExpect(jsonPath("$..picked").value(false))
                .andExpect(jsonPath("$..pickCount").value(0L));
    }

    @Test
    void get_each_lecture() throws Exception {

        // given
        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}", lecture.getId(), lecturePrice.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.systems").exists())
                .andExpect(jsonPath("$.lectureSubjects").exists())
                .andExpect(jsonPath("$.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.approved").value(lecture.isApproved()))
                // lecturePrice
                .andExpect(jsonPath("$.lecturePrice").exists())
                .andExpect(jsonPath("$.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.closed").value(lecturePrice.isClosed()))
                // lectureMentor
                .andExpect(jsonPath("$.lectureMentor").exists())
                .andExpect(jsonPath("$.lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.lectureMentor.lectureCount").value(1L))
                .andExpect(jsonPath("$.lectureMentor.reviewCount").value(0L))

                .andExpect(jsonPath("$.reviewCount").value(0L))
                .andExpect(jsonPath("$.scoreAverage").value(0.0))
                .andExpect(jsonPath("$.enrollmentCount").value(0L))
                .andExpect(jsonPath("$.picked").value(false))
                .andExpect(jsonPath("$.pickCount").value(0L));
    }


    @Test
    void new_lecture() throws Exception {

        // Given
        // When
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessToken)
                        .content(objectMapper.writeValueAsString(lectureCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        // Then
        List<Lecture> lectures = lectureRepository.findByMentor(mentor);
        assertThat(lectures.size()).isEqualTo(1L);
        Lecture lecture = lectures.get(0);
        Lecture created = lectureRepository.findById(lecture.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertThat(created.getMentor()).isEqualTo(mentor),
                () -> assertThat(created.getTitle()).isEqualTo(lectureCreateRequest.getTitle()),
                () -> assertThat(created.getSubTitle()).isEqualTo(lectureCreateRequest.getSubTitle()),
                () -> assertThat(created.getIntroduce()).isEqualTo(lectureCreateRequest.getIntroduce()),
                () -> assertThat(created.getContent()).isEqualTo(lectureCreateRequest.getContent()),
                () -> assertThat(created.getDifficulty()).isEqualTo(lectureCreateRequest.getDifficulty()),
                () -> assertThat(created.getSystems().size()).isEqualTo(lectureCreateRequest.getSystems().size()),
                () -> assertThat(created.getThumbnail()).isEqualTo(lectureCreateRequest.getThumbnail()),

                () -> assertThat(created.getLecturePrices().get(0).isGroup()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).isGroup()),
                () -> assertThat(created.getLecturePrices().get(0).getNumberOfMembers()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getNumberOfMembers()),
                () -> assertThat(created.getLecturePrices().get(0).getPricePerHour()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getPricePerHour()),
                () -> assertThat(created.getLecturePrices().get(0).getTimePerLecture()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getTimePerLecture()),
                () -> assertThat(created.getLecturePrices().get(0).getNumberOfLectures()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getNumberOfLectures()),
                () -> assertThat(created.getLecturePrices().get(0).getTotalPrice()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getTotalPrice()),

                () -> assertThat(created.getLectureSubjects().size()).isEqualTo(lectureCreateRequest.getLectureSubjects().size()),
                () -> assertThat(created.isApproved()).isEqualTo(false)
        );
    }

    @Test
    void edit_lecture() throws Exception {

        // Given
        Lecture lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        // 강의 승인
        lecture.approve(lectureLogService);

        // When
        mockMvc.perform(post(BASE_URL + "/{lecture_id}", lecture.getId())
                        .header(AUTHORIZATION, mentorAccessToken)
                        .content(objectMapper.writeValueAsString(lectureUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        Lecture updated = lectureRepository.findById(lecture.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertThat(updated.getMentor()).isEqualTo(mentor),
                () -> assertThat(updated.getTitle()).isEqualTo(lectureUpdateRequest.getTitle()),
                () -> assertThat(updated.getSubTitle()).isEqualTo(lectureUpdateRequest.getSubTitle()),
                () -> assertThat(updated.getIntroduce()).isEqualTo(lectureUpdateRequest.getIntroduce()),
                () -> assertThat(updated.getContent()).isEqualTo(lectureUpdateRequest.getContent()),
                () -> assertThat(updated.getDifficulty()).isEqualTo(lectureUpdateRequest.getDifficulty()),
                () -> assertThat(updated.getSystems().size()).isEqualTo(lectureUpdateRequest.getSystems().size()),
                () -> assertThat(updated.getThumbnail()).isEqualTo(lectureUpdateRequest.getThumbnail()),

                () -> assertThat(updated.getLecturePrices().get(0).isGroup()).isEqualTo(lectureUpdateRequest.getLecturePrices().get(0).isGroup()),
                () -> assertThat(updated.getLecturePrices().get(0).getNumberOfMembers()).isEqualTo(lectureUpdateRequest.getLecturePrices().get(0).getNumberOfMembers()),
                () -> assertThat(updated.getLecturePrices().get(0).getPricePerHour()).isEqualTo(lectureUpdateRequest.getLecturePrices().get(0).getPricePerHour()),
                () -> assertThat(updated.getLecturePrices().get(0).getTimePerLecture()).isEqualTo(lectureUpdateRequest.getLecturePrices().get(0).getTimePerLecture()),
                () -> assertThat(updated.getLecturePrices().get(0).getNumberOfLectures()).isEqualTo(lectureUpdateRequest.getLecturePrices().get(0).getNumberOfLectures()),
                () -> assertThat(updated.getLecturePrices().get(0).getTotalPrice()).isEqualTo(lectureUpdateRequest.getLecturePrices().get(0).getTotalPrice()),

                () -> assertThat(updated.getLectureSubjects().size()).isEqualTo(lectureUpdateRequest.getLectureSubjects().size()),
                () -> assertThat(updated.isApproved()).isEqualTo(false)
        );
    }

    @Test
    void edit_lecture_when_already_enrolled() throws Exception {

        // Given
        Lecture lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        mockMvc.perform(post(BASE_URL + "/{lecture_id}", lecture.getId())
                        .header(AUTHORIZATION, mentorAccessToken)
                        .content(objectMapper.writeValueAsString(lectureUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
        // Then
        Lecture _lecture = lectureRepository.findById(lecture.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertThat(_lecture.getMentor()).isEqualTo(mentor),
                () -> assertThat(_lecture.getTitle()).isEqualTo(lectureCreateRequest.getTitle()),
                () -> assertThat(_lecture.getSubTitle()).isEqualTo(lectureCreateRequest.getSubTitle()),
                () -> assertThat(_lecture.getIntroduce()).isEqualTo(lectureCreateRequest.getIntroduce()),
                () -> assertThat(_lecture.getContent()).isEqualTo(lectureCreateRequest.getContent()),
                () -> assertThat(_lecture.getDifficulty()).isEqualTo(lectureCreateRequest.getDifficulty()),
                () -> assertThat(_lecture.getSystems().size()).isEqualTo(lectureCreateRequest.getSystems().size()),
                () -> assertThat(_lecture.getThumbnail()).isEqualTo(lectureCreateRequest.getThumbnail()),

                () -> assertThat(_lecture.getLecturePrices().get(0).isGroup()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).isGroup()),
                () -> assertThat(_lecture.getLecturePrices().get(0).getNumberOfMembers()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getNumberOfMembers()),
                () -> assertThat(_lecture.getLecturePrices().get(0).getPricePerHour()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getPricePerHour()),
                () -> assertThat(_lecture.getLecturePrices().get(0).getTimePerLecture()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getTimePerLecture()),
                () -> assertThat(_lecture.getLecturePrices().get(0).getNumberOfLectures()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getNumberOfLectures()),
                () -> assertThat(_lecture.getLecturePrices().get(0).getTotalPrice()).isEqualTo(lectureCreateRequest.getLecturePrices().get(0).getTotalPrice()),

                () -> assertThat(_lecture.getLectureSubjects().size()).isEqualTo(lectureCreateRequest.getLectureSubjects().size()),
                () -> assertThat(_lecture.isApproved()).isEqualTo(false)
        );
    }

    @Test
    void delete_lecture() throws Exception {

        // Given
        Lecture lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        // 강의 승인
        lecture.approve(lectureLogService);
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice.getId());
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}", lecture.getId())
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        assertThat(lectureRepository.findById(lecture.getId()).isPresent()).isFalse();
        assertThat(enrollmentRepository.findById(enrollment.getId()).isPresent()).isFalse();
        assertThat(pickRepository.findById(pickId).isPresent()).isFalse();
    }

    @Test
    void get_reviews_of_each_lecture() throws Exception {

        // given
        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        MenteeReview menteeReview = menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);
        MentorReview mentorReview = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview.getId(), mentorReviewCreateRequest);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/reviews", lecture.getId(), lecturePrice.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..menteeReviewId").value(menteeReview.getId()))
                .andExpect(jsonPath("$..enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$..score").value(menteeReview.getScore()))
                .andExpect(jsonPath("$..content").value(menteeReview.getContent()))
                .andExpect(jsonPath("$..username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$..userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$..userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$..createdAt").isNotEmpty())
                // child
                .andExpect(jsonPath("$..child").exists())
                .andExpect(jsonPath("$..child.mentorReviewId").value(mentorReview.getId()))
                .andExpect(jsonPath("$..child.content").value(mentorReview.getContent()))
                .andExpect(jsonPath("$..child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$..child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..child.createdAt").isNotEmpty());
    }

    @Test
    void get_review_of_each_lecture() throws Exception {

        // given
        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        MenteeReview menteeReview = menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);
        MentorReview mentorReview = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview.getId(), mentorReviewCreateRequest);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/reviews/{mentee_review_id}", lecture.getId(), lecturePrice.getId(), menteeReview.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").value(menteeReview.getId()))
                .andExpect(jsonPath("$.enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$.score").value(menteeReview.getScore()))
                .andExpect(jsonPath("$.content").value(menteeReview.getContent()))
                .andExpect(jsonPath("$.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                // child
                .andExpect(jsonPath("$.child").exists())
                .andExpect(jsonPath("$.child.mentorReviewId").value(mentorReview.getId()))
                .andExpect(jsonPath("$.child.content").value(mentorReview.getContent()))
                .andExpect(jsonPath("$.child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.child.createdAt").isNotEmpty());
    }

}