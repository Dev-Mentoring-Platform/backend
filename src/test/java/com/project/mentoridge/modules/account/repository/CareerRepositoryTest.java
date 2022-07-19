package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.configuration.annotation.RepositoryTest;
import com.project.mentoridge.modules.account.vo.Career;
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

@RepositoryTest
class CareerRepositoryTest {

    @Autowired
    CareerRepository careerRepository;
    @Autowired
    MentorRepository mentorRepository;

    private Mentor mentor;

    @BeforeEach
    void init() {

        assertNotNull(careerRepository);
        assertNotNull(mentorRepository);

        mentor = mentorRepository.findAll().stream().filter(t -> t.getCareers().size() != 0).findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Test
    void findByMentorAndId() {

        // given
        assertNotNull(mentor);
        Career career = mentor.getCareers().get(0);
        Long careerId = career.getId();

        // when
        Career result = careerRepository.findByMentorAndId(mentor, careerId).orElseThrow(RuntimeException::new);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertThat(result).extracting("job").isEqualTo(career.getJob()),
                () -> assertThat(result).extracting("companyName").isEqualTo(career.getCompanyName()),
                () -> assertThat(result).extracting("others").isEqualTo(career.getOthers()),
                () -> assertThat(result).extracting("license").isEqualTo(career.getLicense())
        );
    }

    @Test
    void findByMentor() {

        // given
        assertNotNull(mentor);
        List<Career> careers = mentor.getCareers();

        // when
        List<Career> result = careerRepository.findByMentor(mentor);
        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(careers.size()),
                () -> assertThat(result.get(0)).isEqualTo(careers.get(0))
        );
    }
}