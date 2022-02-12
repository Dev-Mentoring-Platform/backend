package com.project.mentoridge.modules.lecture.mapstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class _LectureMapstructUtil {

//    private final _LectureMapstruct lectureMapstruct;
//
//    public List<LectureResponse> getLectureResponses(List<Lecture> lectures) {
//        return lectureMapstruct.lectureListToLectureResponseList(lectures);
//    }
//
//    public LectureResponse getLectureResponse(Lecture lecture) {
//
//        List<LectureResponse.LecturePriceResponse> prices = lectureMapstruct.lecturePriceListToLecturePriceResponseList(lecture.getLecturePrices());
//        List<LectureResponse.LectureSubjectResponse> subjects = lectureMapstruct.lectureSubjectListToLectureSubjectResponseList(lecture.getLectureSubjects());
//        List<LectureResponse.SystemTypeResponse> systemTypes = lectureMapstruct.systemTypeListToSystemTypeResponseList(lecture.getSystemTypes().stream().map(systemType -> SystemType.find(systemType.getType())).collect(Collectors.toList()));
//
//        return lectureMapstruct.lectureToLectureResponse(lecture, prices, systemTypes, subjects);
//    }
}
