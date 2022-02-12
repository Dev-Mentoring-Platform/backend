package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class EnrollmentRepositoryTest {

    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    MentorRepository mentorRepository;

    @Test
    void findAllWithLectureMentorByMentorId() {

        // given
        Mentor mentor = mentorRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);
        Long mentorId = mentor.getId();

        // when
        List<Enrollment> enrollments = enrollmentRepository.findAllWithLectureMentorByMentorId(mentorId);
        // then
        for (Enrollment enrollment : enrollments) {
            // System.out.println(enrollment);
            assertNotNull(enrollment.getLecture().getMentor());
            assertNotNull(enrollment.getLecture().getMentor().getUser());
        }
    }
}