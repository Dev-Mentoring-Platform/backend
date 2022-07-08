package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
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
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.log.component.MentorLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final ChatroomRepository chatroomRepository;

    private final UserLogService userLogService;
    private final MentorLogService mentorLogService;

        private Page<Mentor> getMentors(Integer page) {
            return mentorRepository.findAll(getPageRequest(page));
        }

    @Transactional(readOnly = true)
    public Page<MentorResponse> getMentorResponses(Integer page) {
        return getMentors(page).map(MentorResponse::new);
        // TODO - 누적 멘티 수
    }

    @Transactional(readOnly = true)
    public MentorResponse getMentorResponse(User mentorUser) {
        Mentor mentor = getMentor(mentorRepository, mentorUser);
        MentorResponse response = new MentorResponse(mentor);
        response.setAccumulatedMenteeCount(enrollmentRepository.countAllMenteesByMentor(mentor.getId()));
        return response;
    }

    @Transactional(readOnly = true)
    public MentorResponse getMentorResponse(Long mentorId) {
        MentorResponse response = new MentorResponse(getMentor(mentorRepository, mentorId));
        response.setAccumulatedMenteeCount(enrollmentRepository.countAllMenteesByMentor(mentorId));
        return response;
    }

    public Mentor createMentor(User user, MentorSignUpRequest mentorSignUpRequest) {
        // menteeUser
        user = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(USER));
        if (user.getRole() == RoleType.MENTOR) {
            throw new AlreadyExistException(AlreadyExistException.MENTOR);
        }
        user.joinMentor(userLogService);
        Mentor saved = mentorRepository.save(mentorSignUpRequest.toEntity(user));
        mentorLogService.insert(user, saved);
        return saved;
    }

    public void updateMentor(User mentorUser, MentorUpdateRequest mentorUpdateRequest) {
        Mentor mentor = getMentor(mentorRepository, mentorUser);
        mentor.update(mentorUpdateRequest, mentorUser, mentorLogService);
    }

    // 멘토 탈퇴 시
    public void deleteMentor(User mentorUser) {

        Mentor mentor = getMentor(mentorRepository, mentorUser);

        // 진행중인 강의 없는지 확인
        if (enrollmentRepository.countUnfinishedEnrollmentOfMentor(mentor.getId()) > 0) {
            throw new RuntimeException("진행중인 강의가 존재합니다.");
        }
        chatroomRepository.deleteByMentor(mentor);
        lectureRepository.findByMentor(mentor).forEach(lecture -> {
            lectureService.deleteLecture(lecture);
        });
        mentor.delete(mentorUser, mentorLogService, userLogService);
        mentorRepository.delete(mentor);
    }

        private List<Career> getCareers(Long mentorId) {
            Mentor mentor = getMentor(mentorRepository, mentorId);
            return careerRepository.findByMentor(mentor);
        }

    @Transactional(readOnly = true)
    public List<CareerResponse> getCareerResponses(Long mentorId) {
        return getCareers(mentorId).stream()
                .map(CareerResponse::new).collect(Collectors.toList());
    }

        private List<Education> getEducations(Long mentorId) {
            Mentor mentor = getMentor(mentorRepository, mentorId);
            return educationRepository.findByMentor(mentor);
        }

    @Transactional(readOnly = true)
    public List<EducationResponse> getEducationResponses(Long mentorId) {
        return getEducations(mentorId).stream()
                .map(EducationResponse::new).collect(Collectors.toList());
    }

}
