package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MenteePickControllerIntegrationTest extends AbstractTest {

    private final static String BASE_URL = "/api/mentees/my-picks";

    @Autowired
    MockMvc mockMvc;

    @WithAccount(NAME)
    @Test
    void subtractPick() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        Long pickId = pickService.createPick(user, lecture1Id).getId();

        // When
        mockMvc.perform(delete(BASE_URL + "/{pick_id}", pickId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertNull(pick);
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }

    @WithAccount(NAME)
    @Test
    void clear() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        Long pick1Id = pickService.createPick(user, lecture1Id).getId();
        Long pick2Id = pickService.createPick(user, lecture2Id).getId();
        assertEquals(2, pickRepository.findByMentee(mentee).size());

        // When
        mockMvc.perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertFalse(pickRepository.findById(pick1Id).isPresent());
        assertFalse(pickRepository.findById(pick2Id).isPresent());
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }
}