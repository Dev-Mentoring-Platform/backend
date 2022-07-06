package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public abstract class AbstractLectureResponse {

    protected Long lectureId;
    protected String title;
    protected String subTitle;
    protected String introduce;
    protected String content;
    protected DifficultyType difficulty;
    protected List<SystemTypeResponse> systems;

    // protected List<LecturePriceResponse> lecturePrices;

    protected List<LectureSubjectResponse> lectureSubjects;
    protected String thumbnail;

    protected boolean approved = false;

}
