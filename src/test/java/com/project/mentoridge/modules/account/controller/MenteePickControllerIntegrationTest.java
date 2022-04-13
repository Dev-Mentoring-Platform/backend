package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        Long pickId = pickService.createPick(user, lecture1Id, lecturePriceId).getId();

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

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        LecturePrice lecturePrice2 = lecturePriceRepository.findByLecture(lecture2).get(0);

        Long pick1Id = pickService.createPick(user, lecture1Id, lecturePrice1.getId()).getId();
        Long pick2Id = pickService.createPick(user, lecture2Id, lecturePrice2.getId()).getId();
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