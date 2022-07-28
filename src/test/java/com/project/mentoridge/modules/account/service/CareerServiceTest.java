package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.request.CareerCreateRequest;
import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.CareerLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareerServiceTest {

    @Mock
    CareerRepository careerRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    CareerLogService careerLogService;
    @InjectMocks
    CareerService careerService;

    @Test
    void getCareerResponse() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        CareerResponse careerResponse = careerService.getCareerResponse(user, 1L);
        // then
        verify(careerRepository).findByMentorAndId(mentor, 1L);
    }

    @DisplayName("존재하지 않는 User")
    @Test
    void getCareerResponse_withNotExistUser() {

        // given
        User user = mock(User.class);
        when(mentorRepository.findByUser(user)).thenReturn(null);
        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> careerService.getCareerResponse(user, 1L));
    }

    @DisplayName("존재하지 않는 CareerId")
    @Test
    void getCareerResponse_withNotExistCareerId() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
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
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        CareerCreateRequest careerCreateRequest = mock(CareerCreateRequest.class);
        careerService.createCareer(user, careerCreateRequest);

        // then
        Career career = mock(Career.class);
        when(careerCreateRequest.toEntity(mentor)).thenReturn(career);

        verify(careerRepository).save(career);
        verify(careerLogService).insert(eq(user), any(Career.class));
    }

    @Test
    void createCareer_withNotExistUser() {

        // given
        User user = mock(User.class);
        when(mentorRepository.findByUser(user)).thenReturn(null);
        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> careerService.createCareer(eq(user), any(CareerCreateRequest.class)));
    }

    @Test
    void updateCareer() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        Career career = mock(Career.class);
        when(careerRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(career));

        // when
        CareerUpdateRequest careerUpdateRequest = mock(CareerUpdateRequest.class);
        careerService.updateCareer(user,1L, careerUpdateRequest);

        // then
        verify(career).update(eq(careerUpdateRequest), eq(user), eq(careerLogService));
//        verify(career, atMost(0)).setMentor(mentor);
//        verify(career, atLeastOnce()).setJob(careerUpdateRequest.getJob());
//        verify(career, atLeastOnce()).setCompanyName(careerUpdateRequest.getCompanyName());
//        verify(career, atLeastOnce()).setOthers(careerUpdateRequest.getOthers());
//        verify(career, atLeastOnce()).setLicense(careerUpdateRequest.getLicense());
    }

    @Test
    void deleteCareer() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        when(mentorRepository.findByUser(user)).thenReturn(mentor);
        Career career = mock(Career.class);
        when(careerRepository.findByMentorAndId(mentor, 1L)).thenReturn(Optional.of(career));

        // when
        careerService.deleteCareer(user, 1L);

        // then
        verify(career).delete(user, careerLogService);
        verify(careerRepository, atLeastOnce()).delete(career);
    }
}