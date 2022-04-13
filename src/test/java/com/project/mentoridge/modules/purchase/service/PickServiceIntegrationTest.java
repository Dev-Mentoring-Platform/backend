package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PickServiceIntegrationTest extends AbstractTest {

    @WithAccount(NAME)
    @Test
    void createPick() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        // When
        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long pickId = pickService.createPick(user, lecture1Id, lecturePrice1.getId()).getId();

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertAll(
                () -> assertNotNull(pick),
                () -> assertEquals(mentee, pick.getMentee()),
                () -> assertEquals(lecture1, pick.getLecture()),
                () -> assertEquals(lecturePrice1, pick.getLecturePrice())
        );
    }

    @WithAccount(NAME)
    @Test
    void deletePick() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long pickId = pickService.createPick(user, lecture1Id, lecturePrice1.getId()).getId();

        // When
        pickService.deletePick(user, pickId);

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertNull(pick);
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }

    @WithAccount(NAME)
    @Test
    void deleteAllPicks() {

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
        pickService.deleteAllPicks(user);

        // Then
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }
}