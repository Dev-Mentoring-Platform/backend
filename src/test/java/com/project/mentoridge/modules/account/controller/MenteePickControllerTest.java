package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.purchase.service.PickServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MenteePickController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MenteePickControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentees/my-picks";

    @MockBean
    PickServiceImpl pickService;


    @Test
    void get_paged_picks() throws Exception {

        // given
        // when
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(pickService).getPickWithSimpleEachLectureResponses(user, 1);
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
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(pickService).deleteAllPicks(user);
    }
}