package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.configuration.annotation.RepositoryTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RepositoryTest
class LectureQueryRepositoryTest {

    private LectureQueryRepository lectureQueryRepository;
    private Mentor mentor;

    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    LecturePriceRepository lecturePriceRepository;

    @BeforeEach
    void init() {

        assertNotNull(em);
        lectureQueryRepository = new LectureQueryRepository(em);

        User user = userRepository.findAll().stream()
                .filter(u -> u.getRole().equals(RoleType.MENTOR)).findFirst()
                .orElseThrow(RuntimeException::new);
        mentor = mentorRepository.findByUser(user);
    }

    @Test
    void findLectureReviewQueryDtoMap() {

        // given
        List<Long> lectureIds = lectureRepository.findAll().stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<Long> lecturePriceIds = lecturePriceRepository.findAll().stream().map(BaseEntity::getId).collect(Collectors.toList());

        // when, then
        Map<Long, LectureReviewQueryDto> lectureReviewQueryDtoMap = lectureQueryRepository.findLectureReviewQueryDtoMap(lectureIds, lecturePriceIds);
        lectureReviewQueryDtoMap.values().forEach(System.out::println);
    }

    @Test
    void findLectureMentorQueryDtoMap() {

        // given
        List<Long> lectureIds = lectureRepository.findAll().stream().map(BaseEntity::getId).collect(Collectors.toList());

        // when, then
        Map<Long, LectureMentorQueryDto> lectureMentorQueryDtoMap
                = lectureQueryRepository.findLectureMentorQueryDtoMap(lectureIds);
        lectureMentorQueryDtoMap.values().forEach(System.out::println);
    }

//    @Test
//    void test() {
//
//        Lecture lecture = lectureRepository.findByLectureIdAndLecturePriceId(4L, 4L);
//        System.out.println(lecture);
//    }
}