package com.project.mentoridge.modules.notice.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NoticeControllerTest {

    private final static String BASE_URL = "/api/notices";

    @InjectMocks
    NoticeController noticeController;
    @Mock
    NoticeService noticeService;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(noticeController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void get_paged_notices() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        verify(noticeService).getNoticeResponses(eq(1));
    }

    @Test
    void get_notice() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{notice_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(noticeService).getNoticeResponse(eq(1L));
    }
}