package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class EducationRepositoryTest {

    @Autowired
    EducationRepository educationRepository;
    @Autowired
    MentorRepository mentorRepository;

    private Mentor mentor;

    @BeforeEach
    void setup() {

        assertNotNull(educationRepository);
        assertNotNull(mentorRepository);

        mentor = mentorRepository.findAll().stream().filter(t -> !t.getEducations().isEmpty()).findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Test
    void findByMentorAndId() {

        // given
        assertNotNull(mentor);
        Education education = mentor.getEducations().get(0);
        Long educationId = education.getId();

        // when
        Education result = educationRepository.findByMentorAndId(mentor, educationId)
                .orElseThrow(RuntimeException::new);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertThat(result).extracting("educationLevel").isEqualTo(education.getEducationLevel()),
                () -> assertThat(result).extracting("schoolName").isEqualTo(education.getSchoolName()),
                () -> assertThat(result).extracting("major").isEqualTo(education.getMajor()),
                () -> assertThat(result).extracting("others").isEqualTo(education.getOthers())
        );
    }

    @Test
    void findByMentor() {

        // given
        assertNotNull(mentor);
        List<Education> educations = mentor.getEducations();

        // when
        List<Education> result = educationRepository.findByMentor(mentor);
        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(educations.size()),
                () -> assertThat(result.get(0)).isEqualTo(educations.get(0))
        );
    }
}