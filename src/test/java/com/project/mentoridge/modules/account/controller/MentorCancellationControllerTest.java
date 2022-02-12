package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.service.MentorCancellationService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.CancellationResponse;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MentorCancellationControllerTest {

    private final static String BASE_URL = "/api/mentors/my-cancellations";

    @InjectMocks
    MentorCancellationController mentorCancellationController;
    @Mock
    MentorCancellationService mentorCancellationService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
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
                .chatroomId(1L)
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
    }
}