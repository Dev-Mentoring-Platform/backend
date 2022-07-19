package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.modules.account.controller.CareerController;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.purchase.service.PickService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PickController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class PickControllerTest extends AbstractControllerTest {

    @MockBean
    PickService pickService;


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