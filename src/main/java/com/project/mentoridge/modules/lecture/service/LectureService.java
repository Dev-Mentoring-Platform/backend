package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import org.springframework.data.domain.Page;

public interface LectureService {

    LectureResponse getLectureResponse(User user, Long lectureId);
    EachLectureResponse getEachLectureResponse(User user, Long lectureId, Long lecturePriceId);

    Page<EachLectureResponse> getEachLectureResponses(User user, String zone, LectureListRequest LectureListRequest, Integer page);

    Lecture createLecture(User user, LectureCreateRequest lectureCreateRequest);

    void updateLecture(User user, Long lectureId, LectureUpdateRequest lectureUpdateRequest);

    void deleteLecture(Lecture lecture);
    void deleteLecture(User user, Long lectureId);

    void approve(User user, Long lectureId);
    void open(User user, Long lectureId, Long lecturePriceId);
    void close(User user, Long lectureId, Long lecturePriceId);

}
