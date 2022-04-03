package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MentorLectureService extends AbstractService {

    private final MentorService mentorService;
    private final EnrollmentService enrollmentService;
    private final LectureService lectureService;

    public Page<LectureResponse> getLectureResponses(User user, Integer page) {
        Mentor mentor = mentorService.getMentor(user);
        return lectureService.getLectureResponsesWithEnrollmentCountByMentor(mentor, page);
    }

    public Page<LectureResponse> getLectureResponses(Long mentorId, Integer page) {
        Mentor mentor = mentorService.getMentor(mentorId);
        return lectureService.getLectureResponsesByMentor(mentor, page);
    }

    public Page<EnrollmentResponse> getEnrollmentResponsesOfLecture(User user, Long lectureId, Integer page) {
        Mentor mentor = mentorService.getMentor(user);
        Lecture lecture = lectureService.getLecture(mentor, lectureId);
        return enrollmentService.getEnrollmentResponses(lecture, page);
    }

    public Page<MenteeResponse> getMenteeResponsesOfLecture(User user, Long lectureId, Integer page) {

        Mentor mentor = mentorService.getMentor(user);
        Lecture lecture = lectureService.getLecture(mentor, lectureId);
        // TODO - fetch join
        return enrollmentService.getEnrollments(lecture, page).map(Enrollment::getMentee)
                .map(MenteeResponse::new);
    }

    public Page<LectureResponse> getLectureResponsesPerLecturePrice(Long mentorId, Integer page) {
        Mentor mentor = mentorService.getMentor(mentorId);
        return lectureService.getLectureResponsesPerLecturePriceByMentor(mentor, page);
    }

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
