package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LectureService {

    Lecture getLecture(Long lectureId);
    Lecture getLecture(Mentor mentor, Long lectureId);

    LecturePrice getLecturePrice(Lecture lecture, Long lecturePriceId);

    LectureResponse getLectureResponse(User user, Long lectureId);

    List<Lecture> getLectures(Mentor mentor);

    // TODO - CHECK
    // List<LectureResponse> getLectureResponses(LectureListRequest lectureListRequest);
    Page<LectureResponse> getLectureResponsesPerLecturePrice(User user, String zone, LectureListRequest LectureListRequest, Integer page);

    Lecture createLecture(User user, LectureCreateRequest lectureCreateRequest);

    void updateLecture(User user, Long lectureId, LectureUpdateRequest lectureUpdateRequest);

    void deleteLecture(Lecture lecture);
    void deleteLecture(User user, Long lectureId);

    /**
     * 강의 승인
     * @param user
     * @param lectureId
     */
    void approve(User user, Long lectureId);

    void open(User user, Long lectureId);
    void close(User user, Long lectureId);

    LectureResponse getLectureResponsePerLecturePrice(User user, Long lectureId, Long lecturePriceId);

    // MentorLectureService
    Page<LectureResponse> getLectureResponsesByMentor(Mentor mentor, Integer page);
    Page<LectureResponse> getLectureResponsesWithEnrollmentCountByMentor(Mentor mentor, Integer page);

    Page<LectureResponse> getLectureResponsesPerLecturePriceByMentor(Mentor mentor, Integer page);
}
