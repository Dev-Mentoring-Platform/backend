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
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.log.component.CareerLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CAREER;

@Transactional
@RequiredArgsConstructor
@Service
public class CareerService extends AbstractService {

    private final CareerRepository careerRepository;
    private final MentorRepository mentorRepository;
    private final CareerLogService careerLogService;

        private Career getCareer(User mentorUser, Long careerId) {
            Mentor mentor = getMentor(mentorRepository, mentorUser);
            return careerRepository.findByMentorAndId(mentor, careerId)
                    .orElseThrow(() -> new EntityNotFoundException(CAREER));
        }

    @Transactional(readOnly = true)
    public CareerResponse getCareerResponse(User user, Long careerId) {
        return new CareerResponse(getCareer(user, careerId));
    }

    public Career createCareer(User mentorUser, CareerCreateRequest careerCreateRequest) {

        Mentor mentor = getMentor(mentorRepository, mentorUser);
        Career career = careerCreateRequest.toEntity(mentor);
        mentor.addCareer(career);

        Career saved = careerRepository.save(career);
        careerLogService.insert(mentorUser, saved);
        return saved;
    }

    public void updateCareer(User mentorUser, Long careerId, CareerUpdateRequest careerUpdateRequest) {

        Career career = getCareer(mentorUser, careerId);
        career.update(careerUpdateRequest, mentorUser, careerLogService);
    }

    public void deleteCareer(User mentorUser, Long careerId) {

        Career career = getCareer(mentorUser, careerId);
        career.delete(mentorUser, careerLogService);
        careerRepository.delete(career);
    }
}
