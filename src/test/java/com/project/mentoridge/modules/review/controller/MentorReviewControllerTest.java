package com.project.mentoridge.modules.review.controller;

import com.project.mentoridge.modules.account.controller.CareerController;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MentorReviewController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MentorReviewControllerTest extends AbstractControllerTest {

    private final String BASE_URL = "/api/mentors/my-reviews";

    @MockBean
    MentorReviewService mentorReviewService;


    @Test
    void get_paged_my_reviews_written_by_my_mentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/by-mentees").param("page", "2")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorReviewService).getReviewWithSimpleEachLectureResponsesOfMentorByMentees(any(User.class), eq(2));
    }
}