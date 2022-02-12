package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.MentorUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.USER;

@Transactional
@Service
@RequiredArgsConstructor
public class MentorService extends AbstractService {

    private final MentorRepository mentorRepository;

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;

    private final LectureService lectureService;
    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;

    private Page<Mentor> getMentors(Integer page) {
        return mentorRepository.findAll(getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public Page<MentorResponse> getMentorResponses(Integer page) {
        return getMentors(page).map(MentorResponse::new);
    }

    private Mentor getMentor(Long mentorId) {
        return mentorRepository.findById(mentorId).orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));
    }

    @Transactional(readOnly = true)
    public MentorResponse getMentorResponse(User user) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));
        return new MentorResponse(mentor);
    }

    @Transactional(readOnly = true)
    public MentorResponse getMentorResponse(Long mentorId) {
        return new MentorResponse(getMentor(mentorId));
    }

    public Mentor createMentor(User user, MentorSignUpRequest mentorSignUpRequest) {

        user = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(USER));
//        if (!user.isEmailVerified()) {
//            // TODO - throw
//        }

        if (user.getRole() == RoleType.MENTOR) {
            throw new AlreadyExistException(AlreadyExistException.MENTOR);
        }
        user.setRole(RoleType.MENTOR);

        Mentor mentor = Mentor.of(user);
        mentor.addCareers(mentorSignUpRequest.getCareers());
        mentor.addEducations(mentorSignUpRequest.getEducations());
        mentorRepository.save(mentor);

        return mentor;
    }

    // TODO - TEST
    public void updateMentor(User user, MentorUpdateRequest mentorUpdateRequest) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
            .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        mentor.updateCareers(mentorUpdateRequest.getCareers());
        mentor.updateEducations(mentorUpdateRequest.getEducations());
    }

    // TODO - CHECK
    // 튜터 탈퇴 시
    public void deleteMentor(User user) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        // TODO - CHECK
        // 수강 중인 강의 없는지 확인
        if (enrollmentRepository.findAllWithLectureMentorByMentorId(mentor.getId()).size() > 0) {
            // TODO - Error Message
            throw new RuntimeException("수강 중인 강의가 존재합니다.");
        }

        lectureRepository.findByMentor(mentor).forEach(lecture -> {
            lectureService.deleteLecture(lecture);
        });

        mentor.quit();
        mentorRepository.delete(mentor);
    }

    private List<Career> getCareers(Long mentorId) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));
        return careerRepository.findByMentor(mentor);
    }

    @Transactional(readOnly = true)
    public List<CareerResponse> getCareerResponses(Long mentorId) {
        return getCareers(mentorId).stream()
                .map(CareerResponse::new).collect(Collectors.toList());
    }

    private List<Education> getEducations(Long mentorId) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));
        return educationRepository.findByMentor(mentor);
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> getEducationResponses(Long mentorId) {
        return getEducations(mentorId).stream()
                .map(EducationResponse::new).collect(Collectors.toList());
    }

}
