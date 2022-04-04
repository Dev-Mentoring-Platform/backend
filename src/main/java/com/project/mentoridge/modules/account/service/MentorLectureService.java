package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MentorLectureService extends AbstractService {

    private final MentorRepository mentorRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final LectureRepository lectureRepository;
    private final LectureSearchRepository lectureSearchRepository;

        private Mentor getMentor(User user) {
            return Optional.ofNullable(mentorRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTOR));
        }

        private Mentor getMentor(Long mentorId) {
            return mentorRepository.findById(mentorId)
                    .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));
        }

        private Lecture getLecture(Mentor mentor, Long lectureId) {
            return lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

    public Page<LectureResponse> getLectureResponses(User user, Integer page) {
        Mentor mentor = getMentor(user);
        return lectureSearchRepository.findLecturesWithEnrollmentCountByMentor(mentor, getPageRequest(page));
    }

    public Page<LectureResponse> getLectureResponses(Long mentorId, Integer page) {
        Mentor mentor = getMentor(mentorId);
        return lectureRepository.findByMentor(mentor, getPageRequest(page)).map(LectureResponse::new);
    }

    public Page<LectureResponse> getLectureResponsesPerLecturePrice(Long mentorId, Integer page) {
        Mentor mentor = getMentor(mentorId);
        return lectureSearchRepository.findLecturesPerLecturePriceByMentor(mentor, getPageRequest(page)).map(LectureResponse::new);
    }

        private Page<Enrollment> getEnrollmentsOfLecture(User user, Long lectureId, Integer page) {
            Mentor mentor = getMentor(user);
            Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
            return enrollmentRepository.findByLecture(lecture, getPageRequest(page));
        }

    public Page<EnrollmentResponse> getEnrollmentResponsesOfLecture(User user, Long lectureId, Integer page) {
        return getEnrollmentsOfLecture(user, lectureId, page).map(EnrollmentResponse::new);
    }

        private Page<Mentee> getMenteesOfLecture(User user, Long lectureId, Integer page) {
            Mentor mentor = getMentor(user);
            Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
            // TODO - fetch join
            return enrollmentRepository.findByLecture(lecture, getPageRequest(page))
                    .map(Enrollment::getMentee);
        }

    public Page<MenteeResponse> getMenteeResponsesOfLecture(User user, Long lectureId, Integer page) {
        return getMenteesOfLecture(user, lectureId, page).map(MenteeResponse::new);
    }

    public LectureResponse getLectureResponsePerLecturePrice(Long mentorId, Long lectureId, Long lecturePriceId) {
        Mentor mentor = getMentor(mentorId);
        return new LectureResponse(lectureSearchRepository.findLecturePerLecturePriceByMentor(mentor, lectureId, lecturePriceId));
    }

}
