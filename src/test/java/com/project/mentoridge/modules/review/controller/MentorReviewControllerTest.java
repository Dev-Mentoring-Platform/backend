package com.project.mentoridge.modules.review.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MentorReviewControllerTest extends AbstractControllerTest {

    private final String BASE_URL = "/api/mentors/my-reviews";

    @InjectMocks
    MentorReviewController mentorReviewController;
    @Mock
    MentorReviewService mentorReviewService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
/*
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(RoleType.MENTOR::getType);
        when(principalDetails.getAuthorities()).thenReturn(authorities);*/
        mockMvc = MockMvcBuilders.standaloneSetup(mentorReviewController)
                //.apply(springSecurity(springSecurityFilterChain))
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

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