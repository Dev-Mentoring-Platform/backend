package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserPasswordUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.UserResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.UserService;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private final static String BASE_URL = "/api/users";

    @InjectMocks
    UserController userController;
    @Mock
    UserService userService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getUsers() throws Exception {

        // given
        User user = User.of(
                "user@email.com",
                "password",
                "user", null, null, null, "user@email.com",
                "user", null, null, null, RoleType.MENTEE,
                null, null
        );
        UserResponse response = new UserResponse(user);
        Page<UserResponse> users =
                new PageImpl<>(Arrays.asList(response), Pageable.ofSize(20), 1);
        doReturn(users)
                .when(userService).getUserResponses(anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void getUser() throws Exception {

        // given
        User user = User.of(
                "user@email.com",
                "password",
                "user", null, null, null, "user@email.com",
                "user", null, null, null, RoleType.MENTEE,
                null, null
        );
        UserResponse response = new UserResponse(user);
        doReturn(response)
                .when(userService).getUserResponse(anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{user_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }


    // @WithMockUser
    // @WithAnonymousUser
    @Test
    void getMyInfo() throws Exception {

        // given
        User user = User.of(
                "user@email.com",
                "password",
                "user", null, null, null, "user@email.com",
                "user", null, null, null, RoleType.MENTEE,
                null, null
        );
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        // principal.getAuthorities().stream().forEach(a -> System.out.println(a.getAuthority()));
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        UserResponse response = new UserResponse(user);
        doReturn(response)
                .when(userService).getUserResponse(any(User.class));
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/my-info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void editUser() throws Exception {

        // given
        doNothing()
                .when(userService).updateUser(any(User.class), any(UserUpdateRequest.class));
        // when
        // then
        UserUpdateRequest userUpdateRequest = AbstractTest.getUserUpdateRequest("user@email.com", "user");
        mockMvc.perform(put(BASE_URL + "/my-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("프로필 이미지 수정")
    @Test
    void changeImage() throws Exception {

        // given
        doNothing()
                .when(userService).updateUserImage(any(User.class), any(UserImageUpdateRequest.class));
        // when
        // then
        UserImageUpdateRequest userImageUpdateRequest = UserImageUpdateRequest.of("path");
        mockMvc.perform(put(BASE_URL + "/my-info/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userImageUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
                //.andExpect(content().string("path"));
    }

    @DisplayName("탈퇴 이유 번호를 잘못 입력한 경우")
    @Test
    void quitUser_invalidReasonId() throws Exception {

        // given
//        doNothing()
//                .when(userService).deleteUser(any(User.class), any(UserQuitRequest.class));
        // when
        // then
        UserQuitRequest userQuitRequest = UserQuitRequest.of(7, null, "password");
        mockMvc.perform(delete(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userQuitRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @DisplayName("기타인데 이유를 입력하지 않은 경우")
    @Test
    void quitUser_noReason() throws Exception {

        // given
//        doNothing()
//                .when(userService).deleteUser(any(User.class), any(UserQuitRequest.class));
        // when
        // then
        UserQuitRequest userQuitRequest = UserQuitRequest.of(6, null, "password");
        mockMvc.perform(delete(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userQuitRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void quitUser() throws Exception {

        // given
        doNothing()
                .when(userService).deleteUser(any(User.class), any(UserQuitRequest.class));
        // when
        // then
        UserQuitRequest userQuitRequest = UserQuitRequest.of(1, null, "password");
        mockMvc.perform(delete(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userQuitRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("변경하려는 비밀번호가 기존 비밀번호와 동일한 경우")
    @Test
    void changeUserPassword_sameWithCurrentPassword() throws Exception {

        // given
//        doNothing()
//                .when(userService).updateUserPassword(any(User.class), any(UserPasswordUpdateRequest.class));
        // when
        // then
        UserPasswordUpdateRequest userPasswordUpdateRequest = UserPasswordUpdateRequest.of("password", "password", "password");
        mockMvc.perform(put(BASE_URL + "/my-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @DisplayName("비밀번호 확인과 일치하지 않은 경우")
    @Test
    void changeUserPassword_differentFromConfirmInput() throws Exception {

        // given
//        doNothing()
//                .when(userService).updateUserPassword(any(User.class), any(UserPasswordUpdateRequest.class));
        // when
        // then
        UserPasswordUpdateRequest userPasswordUpdateRequest = UserPasswordUpdateRequest.of("password", "password", "password_");
        mockMvc.perform(put(BASE_URL + "/my-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void getQuitReasons() throws Exception {
        System.out.println(UserQuitRequest.reasons);
    }
}