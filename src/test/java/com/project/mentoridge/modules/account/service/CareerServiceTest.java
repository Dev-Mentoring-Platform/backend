package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.request.CareerCreateRequest;
import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class CareerServiceTest {

    @Mock
    CareerRepository careerRepository;
    @Mock
    MentorRepository mentorRepository;
    @InjectMocks
    CareerService careerService;

    private User user;
    private Mentor mentor;
    private Career career;

    @BeforeEach
    void setup() {

        assertNotNull(careerRepository);
        assertNotNull(mentorRepository);
        assertNotNull(careerService);

        user = Mockito.mock(User.class);
        // mentor = Mockito.mock(Mentor.class);
        mentor = Mentor.of(user);
    }

    @Test
    void getCareerResponse() {

        // given
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        career = Career.of(mentor, "job", "companyName", "others", "license");
        mentor.addCareer(career);
        when(careerRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(career));

        // when
        CareerResponse careerResponse = careerService.getCareerResponse(user, 1L);
        // then
        assertAll(
                () -> assertThat(careerResponse).extracting("job").isEqualTo(career.getJob()),
                () -> assertThat(careerResponse).extracting("companyName").isEqualTo(career.getCompanyName()),
                () -> assertThat(careerResponse).extracting("others").isEqualTo(career.getOthers()),
                () -> assertThat(careerResponse).extracting("license").isEqualTo(career.getLicense())
        );
    }

    @DisplayName("존재하지 않는 User")
    @Test
    void getCareerResponse_withNotExistUser() {

        // given
        when(mentorRepository.findByUser(user)).thenReturn(null);
        // when
        // then
        assertThrows(UnauthorizedException.class,
                () -> careerService.getCareerResponse(user, 1L));
    }

    @DisplayName("존재하지 않는 CareerId")
    @Test
    void getCareerResponse_withNotExistCareerId() {

        // given
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        when(careerRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> careerService.getCareerResponse(user, 1L));
    }

    @Test
    void createCareer() {

        // given
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        when(careerRepository.save(any(Career.class))).then(AdditionalAnswers.returnsFirstArg());

        // when
        CareerCreateRequest careerCreateRequest
                = CareerCreateRequest.of("job", "companyName", "others", "license");
        Career response = careerService.createCareer(user, careerCreateRequest);

        // then
        assertThat(response.getMentor()).isEqualTo(mentor);
        assertThat(mentor.getCareers().contains(response)).isTrue();
    }

    @Test
    void createCareer_withNotExistUser() {

        // given
        when(mentorRepository.findByUser(user)).thenReturn(null);
        // when
        // then
        assertThrows(UnauthorizedException.class,
                () -> careerService.createCareer(user, Mockito.mock(CareerCreateRequest.class)));
    }

    @Test
    void updateCareer() {

        // given
        career = Mockito.mock(Career.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        when(careerRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(career));

        // when
        CareerUpdateRequest careerUpdateRequest
                = CareerUpdateRequest.of("job2", "companyName2", "others2", "license2");
        careerService.updateCareer(user,1L, careerUpdateRequest);

        // then
        verify(career).update(careerUpdateRequest);
//        verify(career, atMost(0)).setMentor(mentor);
//        verify(career, atLeastOnce()).setJob(careerUpdateRequest.getJob());
//        verify(career, atLeastOnce()).setCompanyName(careerUpdateRequest.getCompanyName());
//        verify(career, atLeastOnce()).setOthers(careerUpdateRequest.getOthers());
//        verify(career, atLeastOnce()).setLicense(careerUpdateRequest.getLicense());
    }

    @Test
    void deleteCareer() {

        // given
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        career = Career.of(mentor, "job", "companyName", "others", "license");
        mentor.addCareer(career);
        when(careerRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(career));

        // when
        careerService.deleteCareer(user, 1L);

        // then
        assertThat(career).extracting("mentor").isNull();
        assertThat(mentor.getCareers().contains(career)).isFalse();
        verify(careerRepository, atLeastOnce()).delete(career);

    }
}