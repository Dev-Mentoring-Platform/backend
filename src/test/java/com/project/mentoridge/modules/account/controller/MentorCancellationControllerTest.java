package com.project.mentoridge.modules.account.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorCancellationControllerTest {
/*
    private final static String BASE_URL = "/api/mentors/my-cancellations";

    @InjectMocks
    MentorCancellationController mentorCancellationController;
    @Mock
    MentorCancellationService mentorCancellationService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorCancellationController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getMyCancellations() throws Exception {

        // given
        CancellationResponse cancellationResponse = CancellationResponse.builder()
                .cancellation(mock(Cancellation.class))
                .lecture(mock(Lecture.class))
                .lecturePrice(mock(LecturePrice.class))
                .menteeId(1L)
                .menteeName("user")
                .build();
        Page<CancellationResponse> cancellations = new PageImpl<>(Arrays.asList(cancellationResponse), Pageable.ofSize(20), 1);
        doReturn(cancellations)
                .when(mentorCancellationService).getCancellationResponses(any(User.class), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cancellations)));
    }

    @Test
    void approveCancellation() throws Exception {

        // given
        doNothing()
                .when(mentorCancellationService).approve(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{cancellation_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }*/
}