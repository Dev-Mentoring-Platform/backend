package com.project.mentoridge.modules.notice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.notice.repository.NoticeRepository;
import com.project.mentoridge.modules.notice.vo.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class NoticeControllerIntegrationTest {

    private final static String BASE_URL = "/api/notices";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NoticeRepository noticeRepository;

    private Notice notice1;
    private Notice notice2;

    @BeforeEach
    void init() {

        notice1 = noticeRepository.save(Notice.builder()
                        .title("title1")
                        .content("content1")
                .build());
        notice2 = noticeRepository.save(Notice.builder()
                        .title("title2")
                        .content("content2")
                .build());
    }

    @DisplayName("공지사항 리스트")
    @Test
    void get_notices() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..noticeId[0]").value(notice1.getId()))
                .andExpect(jsonPath("$..title[0]").value(notice1.getTitle()))
                .andExpect(jsonPath("$..content[0]").value(notice1.getContent()))
                .andExpect(jsonPath("$..createdAt[0]").exists())
                .andExpect(jsonPath("$..noticeId[1]").value(notice2.getId()))
                .andExpect(jsonPath("$..title[1]").value(notice2.getTitle()))
                .andExpect(jsonPath("$..content[1]").value(notice2.getContent()))
                .andExpect(jsonPath("$..createdAt[1]").exists());
    }

    @DisplayName("공지사항 조회")
    @Test
    void get_notice() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{notice_id}", notice1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noticeId").value(notice1.getId()))
                .andExpect(jsonPath("$.title").value(notice1.getTitle()))
                .andExpect(jsonPath("$.content").value(notice1.getContent()))
                .andExpect(jsonPath("$.createdAt").exists());
    }
}
