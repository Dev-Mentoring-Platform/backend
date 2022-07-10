package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@MockMvcTest
public class MentorMenteeControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentors/my-mentees";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("멘토의 멘티 리스트 - 모집 종료 강의")
    @Test
    void get_my_mentees_of_closed_lecture() throws Exception {

    }

    @DisplayName("멘토의 멘티 리스트")
    @Test
    void get_my_mentees_of_not_closed_lecture() throws Exception {

    }

    @DisplayName("멘토의 멘티-강의 정보")
    @Test
    void get_my_paged_MenteeEnrollmentInfoResponses() throws Exception {

    }


}
