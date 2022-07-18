package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserPasswordUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.UserResponse;
import com.project.mentoridge.modules.account.service.UserService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class UserControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/users";

    @MockBean
    UserService userService;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
//        mockMvc = MockMvcBuilders.standaloneSetup(userController)
//                .addFilter(jwtRequestFilter)
//                .addInterceptors(authInterceptor)
//                .setControllerAdvice(RestControllerExceptionAdvice.class)
//                .build();
    }

    @Test
    void get_users() throws Exception {

        // given
//        User user = getUserWithName("user");
//        UserResponse response = new UserResponse(user);
//        Page<UserResponse> users = new PageImpl<>(Arrays.asList(response), Pageable.ofSize(20), 1);
//        doReturn(users)
//                .when(userService).getUserResponses(anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).getUserResponses(eq(1));

    }

    @Test
    void get_user() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{user_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).getUserResponse(eq(1L));
    }

    @Test
    void _get_user() throws Exception {

        // given
        User user = getUserWithName("user");
        UserResponse response = new UserResponse(user);
        doReturn(response)
                .when(userService).getUserResponse(anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{user_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").hasJsonPath())
                .andExpect(jsonPath("$.username").hasJsonPath())
                .andExpect(jsonPath("$.role").hasJsonPath())
                .andExpect(jsonPath("$.name").hasJsonPath())
                .andExpect(jsonPath("$.gender").hasJsonPath())
                .andExpect(jsonPath("$.birthYear").hasJsonPath())
                .andExpect(jsonPath("$.phoneNumber").hasJsonPath())
                .andExpect(jsonPath("$.nickname").hasJsonPath())
                .andExpect(jsonPath("$.image").hasJsonPath())
                .andExpect(jsonPath("$.zone").hasJsonPath());
    }

    @Test
    void get_myInfo() throws Exception {

        // given
//        User user = getUserWithName("user");
//        PrincipalDetails principal = new PrincipalDetails(user);
//        SecurityContext context = SecurityContextHolder.getContext();
//        // principal.getAuthorities().stream().forEach(a -> System.out.println(a.getAuthority()));
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
//
//        UserResponse response = new UserResponse(user);
//        doReturn(response)
//                .when(userService).getUserResponse(any(User.class));
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).getUserResponse(any(User.class));
    }

    @Test
    void get_myInfo_without_auth() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/my-info"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(userService);
    }

    @Test
    void edit_user() throws Exception {

        // given
        // when
        // then
        UserUpdateRequest userUpdateRequest = getUserUpdateRequestWithNickname("user");
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).updateUser(any(User.class), eq(userUpdateRequest));
    }

    @DisplayName("프로필 이미지 수정")
    @Test
    void change_image() throws Exception {

        // given
        // when
        // then
        UserImageUpdateRequest userImageUpdateRequest = getUserImageUpdateRequestWithImage("path");
        mockMvc.perform(put(BASE_URL + "/my-info/image")
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userImageUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).updateUserImage(any(User.class), eq(userImageUpdateRequest));
    }

    @DisplayName("탈퇴 이유 번호를 잘못 입력한 경우")
    @Test
    void quit_user_invalidReasonId() throws Exception {

        // given
        // when
        // then
        UserQuitRequest userQuitRequest = getUserQuitRequestWithReasonIdAndReasonAndPassword(7, null, "password");
        mockMvc.perform(delete(BASE_URL).header(AUTHORIZATION, accessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userQuitRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @DisplayName("기타인데 이유를 입력하지 않은 경우")
    @Test
    void quit_user_noReason() throws Exception {

        // given
        // when
        // then
        UserQuitRequest userQuitRequest = getUserQuitRequestWithReasonIdAndReasonAndPassword(6, null, "password");
        mockMvc.perform(delete(BASE_URL).header(AUTHORIZATION, accessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userQuitRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void quit_user() throws Exception {

        // given
        // when
        // then
        UserQuitRequest userQuitRequest = getUserQuitRequestWithReasonIdAndReasonAndPassword(1, null, "password");
        mockMvc.perform(delete(BASE_URL).header(AUTHORIZATION, accessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userQuitRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).deleteUser(any(User.class), eq(userQuitRequest));
    }

    @DisplayName("변경하려는 비밀번호가 기존 비밀번호와 동일한 경우")
    @Test
    void change_userPassword_same_with_currentPassword() throws Exception {

        // given
        // when
        // then
        UserPasswordUpdateRequest userPasswordUpdateRequest = getUserPasswordUpdateRequestWithPasswordAndNewPasswordAndNewPasswordConfirm("password", "password", "password");
        mockMvc.perform(put(BASE_URL + "/my-password").header(AUTHORIZATION, accessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(userService).updateUserPassword(any(User.class), eq(userPasswordUpdateRequest));
    }

    @DisplayName("비밀번호 확인과 일치하지 않은 경우")
    @Test
    void change_userPassword_different_from_confirmInput() throws Exception {

        // given
        // when
        // then
        UserPasswordUpdateRequest userPasswordUpdateRequest = getUserPasswordUpdateRequestWithPasswordAndNewPasswordAndNewPasswordConfirm("password", "password", "password_");
        mockMvc.perform(put(BASE_URL + "/my-password").header(AUTHORIZATION, accessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    @Test
    void get_quitReasons() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/quit-reasons"))
                .andDo(print())
                .andExpect(content().string(UserQuitRequest.reasons.toString()));
    }
}