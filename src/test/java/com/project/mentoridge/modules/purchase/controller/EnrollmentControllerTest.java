package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EnrollmentController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
public class EnrollmentControllerTest extends AbstractControllerTest {

    @MockBean
    EnrollmentService enrollmentService;


    @Test
    void enroll() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/enrollments", 1L, 1L)
                .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(enrollmentService).createEnrollment(any(User.class), eq(1L), eq(1L));
    }

    @Test
    void check() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/check", 1L)
                .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(enrollmentService).check(any(User.class), eq(1L));
    }

    @Test
    void check_exception() throws Exception {

        // given
        doThrow(RuntimeException.class)
                .when(enrollmentService).check(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/check", 1L)
                .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void check_without_auth() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/check", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(enrollmentService);
    }

    @Test
    void finish() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/finish", 1L)
                .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(enrollmentService).finish(any(User.class), 1L);
    }

    @Test
    void finish_exception() throws Exception {

        // given
        doThrow(RuntimeException.class)
                .when(enrollmentService).finish(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/finish", 1L)
                .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void finish_without_auth() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put("/api/enrollments/{enrollment_id}/finish", 1L))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(enrollmentService);
    }
}
