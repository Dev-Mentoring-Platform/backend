package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class EnrollmentWithLecturePriceResponse {

    private Long enrollmentId;
    private boolean checked;
    private boolean finished;

    private Long lectureId;
    private String title;
    private String subTitle;
    private String introduce;
    private String content;
    private DifficultyType difficulty;
    // private String difficultyName;
    private List<LecturePriceWithLectureResponse.SystemTypeResponse> systems;
    private LecturePriceWithLectureResponse.LecturePriceResponse lecturePrice;
    private Long lecturePriceId;
    private List<LecturePriceWithLectureResponse.LectureSubjectResponse> lectureSubjects;
    private String thumbnail;
    private boolean approved = false;
    private boolean closed = false;

    private LecturePriceWithLectureResponse.LectureMentorResponse lectureMentor;

    public EnrollmentWithLecturePriceResponse(Enrollment enrollment) {

        this.enrollmentId = enrollment.getId();
        this.checked = enrollment.isChecked();
        this.finished = enrollment.isFinished();

        LecturePrice lecturePrice = enrollment.getLecturePrice();
        Lecture lecture = enrollment.getLecture();
        this.lectureId = lecture.getId();
        this.title = lecture.getTitle();
        this.subTitle = lecture.getSubTitle();
        this.introduce = lecture.getIntroduce();
        this.content = lecture.getContent();
        this.difficulty = lecture.getDifficulty();
        this.systems = lecture.getSystems().stream()
                .map(LecturePriceWithLectureResponse.SystemTypeResponse::new).collect(Collectors.toList());
        this.lectureSubjects = lecture.getLectureSubjects().stream()
                .map(LecturePriceWithLectureResponse.LectureSubjectResponse::new).collect(Collectors.toList());
        this.thumbnail = lecture.getThumbnail();
        this.approved = lecture.isApproved();
        this.lectureMentor = new LecturePriceWithLectureResponse.LectureMentorResponse(lecture.getMentor());

        this.lecturePrice = new LecturePriceWithLectureResponse.LecturePriceResponse(lecturePrice);
        this.lecturePriceId = lecturePrice.getId();
        this.closed = lecturePrice.isClosed();

    }
}
