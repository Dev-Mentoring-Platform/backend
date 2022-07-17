package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.purchase.service.PickService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PickControllerTest extends AbstractControllerTest {

    @InjectMocks
    PickController pickController;
    @Mock
    PickService pickService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        mockMvc = MockMvcBuilders.standaloneSetup(pickController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void add_pick() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/picks", 1L, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(pickService).createPick(any(User.class), eq(1L), eq(1L));
    }

    @Test
    void add_pick_and_get_response() throws Exception {

        // given
        doReturn(1L)
                .when(pickService).createPick(any(User.class), eq(1L), eq(1L));
        // when
        // then
        MockHttpServletResponse response = mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/picks", 1L, 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentAsString()).isEqualTo("1L");
    }

    @Test
    void remove_pick() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/picks", 1L, 2L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();
        verify(pickService).createPick(any(User.class), eq(1L), eq(2L));
    }

    @Test
    void remove_pick_and_get_response() throws Exception {

        // given
        doReturn(null)
                .when(pickService).createPick(any(User.class), eq(1L), eq(2L));
        // when
        // then
        MockHttpServletResponse response = mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/picks", 1L, 2L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentLength()).isEqualTo(0);
    }
}