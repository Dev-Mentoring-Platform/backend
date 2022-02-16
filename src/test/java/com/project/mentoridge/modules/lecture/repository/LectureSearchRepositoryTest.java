package com.project.mentoridge.modules.lecture.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class LectureSearchRepositoryTest {
    // TODO - 테스트 세분화
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    EntityManager em;

    private LectureSearchRepository lectureSearchRepository;

    @BeforeEach
    void setup() {

        assertNotNull(addressRepository);

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(em);
        lectureSearchRepository = new LectureSearchRepository(jpaQueryFactory);
    }

//    @WithAccount(NAME)
//    @Test
//    void findLecturesByZone() {
//
//        // Given
//        User user = userRepository.findByUsername(USERNAME).orElse(null);
//        Address zone = user.getZone();
//        assertAll(
//                () -> assertEquals("서울특별시", zone.getState()),
//                () -> assertEquals("강남구", zone.getSiGunGu()),
//                () -> assertEquals("삼성동", zone.getDongMyunLi())
//        );
//        mentorService.createMentor(user, mentorSignUpRequest);
//        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);
//
//        // When
//        // Then
//        Page<Lecture> lectures = lectureSearchRepository.findLecturesByZone(zone, PageRequest.of(0, 20));
//        assertEquals(1, lectures.getTotalElements());
//        lectures = lectureSearchRepository.findLecturesByZone(Address.of("서울특별시", "광진구", "능동"), PageRequest.of(0, 20));
//        assertEquals(0, lectures.getTotalElements());
//    }

    @Test
    void findLecturesByZoneAndSearch() {

        // given
        Mentor mentor = mentorRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);

        Address zone = mentor.getUser().getZone();

        Lecture _lecture = lectureRepository.findAll().stream()
                .filter(l -> l.getMentor().equals(mentor)).findFirst()
                .orElseThrow(RuntimeException::new);
        LectureListRequest listRequest = LectureListRequest.of(
                _lecture.getTitle(),
                Arrays.asList(_lecture.getLectureSubjects().get(0).getKrSubject()),
                _lecture.getSystems().get(0),
                _lecture.getLecturePrices().get(0).getIsGroup(),
                Arrays.asList(_lecture.getDifficulty())
        );

        // when
        Page<Lecture> lectures = lectureSearchRepository.findLecturesByZoneAndSearch(zone, listRequest, PageRequest.ofSize(20));
        // then
        assertThat(lectures.getTotalElements()).isGreaterThanOrEqualTo(1);
//        for (Lecture lecture : lectures) {
//            System.out.println(lecture);
//        }
    }

}
