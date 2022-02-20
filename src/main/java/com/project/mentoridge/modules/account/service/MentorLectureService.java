package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.MENTOR;

@Transactional
@Service
@RequiredArgsConstructor
public class MentorLectureService extends AbstractService {

    private final MentorRepository mentorRepository;
    private final LectureRepository lectureRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;

    private Page<Lecture> getLectures(User user, Integer page) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        return getLectures(mentor, page);
    }

    @Transactional(readOnly = true)
    public Page<LectureResponse> getLectureResponses(User user, Integer page) {
        // return getLectures(user, page).map(lectureMapstructUtil::getLectureResponse);
        return getLectures(user, page).map(LectureResponse::new);
    }

    private Page<Lecture> getLectures(Long mentorId, Integer page) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(MENTOR));

        return getLectures(mentor, page);
    }

    private Page<Lecture> getLectures(Mentor mentor, Integer page) {
        return lectureRepository.findByMentor(mentor, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public Page<LectureResponse> getLectureResponses(Long mentorId, Integer page) {
        return getLectures(mentorId, page).map(LectureResponse::new);
    }

    private Page<Enrollment> getEnrollmentsOfLecture(User user, Long lectureId, Integer page) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        return enrollmentRepository.findByLecture(lecture, getPageRequest(page));
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollmentResponsesOfLecture(User user, Long lectureId, Integer page) {
        return getEnrollmentsOfLecture(user, lectureId, page).map(EnrollmentResponse::new);
    }

    private Page<Mentee> getMenteesOfLecture(User user, Long lectureId, Integer page) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        // TODO - fetch join
        return enrollmentRepository.findByLecture(lecture, getPageRequest(page))
                .map(Enrollment::getMentee);
    }

    @Transactional(readOnly = true)
    public Page<MenteeResponse> getMenteeResponsesOfLecture(User user, Long lectureId, Integer page) {
        return getMenteesOfLecture(user, lectureId, page).map(MenteeResponse::new);
    }

//    @Transactional(readOnly = true)
//    public Page<MenteeResponse> getMenteeResponsesOfLecture(User user, Long lectureId, Integer page) {
//
//        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
//                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));
//
//        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
//                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
//        // TODO - fetch join
//        return enrollmentRepository.findByLecture(lecture, PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").ascending()))
//                .map(enrollment -> new MenteeResponse(enrollment.getMentee()));
//    }

}
