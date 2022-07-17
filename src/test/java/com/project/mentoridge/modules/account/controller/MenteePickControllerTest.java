package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.purchase.service.PickServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenteePickControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentees/my-picks";

    @InjectMocks
    MenteePickController menteePickController;
    @Mock
    PickServiceImpl pickService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        mockMvc = MockMvcBuilders.standaloneSetup(menteePickController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void get_paged_picks() throws Exception {

        // given
        // when
        mockMvc.perform(get(BASE_URL, 1)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(pickService).getPickWithSimpleEachLectureResponses(any(User.class), eq(1));
    }
//
//    @Test
//    void subtractPick() throws Exception {
//
//        // given
//        doNothing()
//                .when(pickService).deletePick(any(User.class), anyLong());
//        // when
//        // then
//        mockMvc.perform(delete(BASE_URL + "/{pick_id}", 1))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }

    @Test
    void clear() throws Exception {

        // given
        // when
        mockMvc.perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(pickService).deleteAllPicks(any(User.class));
    }
}