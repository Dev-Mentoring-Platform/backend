package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CareerControllerApiTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;

//    @Test
//    @WithAccount(NAME)
//    void Career_등록() throws Exception {
//
//        // Given
//        User user = userRepository.findByUsername(USERNAME).orElse(null);
//        mentorService.createMentor(user, mentorSignUpRequest);
//
//        // When
//        mockMvc.perform(post("/careers")
//                .content(objectMapper.writeValueAsString(careerCreateRequest))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        // Then
//        user = userRepository.findByUsername(USERNAME).orElse(null);
//        Mentor mentor = mentorRepository.findByUser(user);
//
//        Assertions.assertEquals(1, careerRepository.findByMentor(mentor).size());
//        Career createdCareer = careerRepository.findByMentor(mentor).get(0);
//        assertAll(
//                () -> assertEquals(careerCreateRequest.getCompanyName(), createdCareer.getCompanyName())
//        );
//    }

}