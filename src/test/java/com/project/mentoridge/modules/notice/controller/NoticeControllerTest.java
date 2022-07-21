package com.project.mentoridge.modules.notice.controller;

import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.notice.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NoticeController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class NoticeControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/notices";

    @MockBean
    NoticeService noticeService;

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