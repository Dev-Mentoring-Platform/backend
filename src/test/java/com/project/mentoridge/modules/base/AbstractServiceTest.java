package com.project.mentoridge.modules.base;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;

import java.util.Arrays;

import static com.project.mentoridge.modules.base.TestDataBuilder.getSubjectWithSubjectIdAndKrSubject;
import static com.project.mentoridge.modules.base.TestDataBuilder.getUserWithName;

public abstract class AbstractServiceTest {

    protected static User mentorUser = getUserWithName("mentorUser");
    protected static Mentor mentor = Mentor.builder()
            .user(mentorUser)
            .build();
    protected static User menteeUser = getUserWithName("menteeUser");
    protected static Mentee mentee = Mentee.builder()
            .user(menteeUser)
            .build();

    private static LecturePrice lecturePrice = LecturePrice.builder()
            .lecture(null)
            .isGroup(true)
            .numberOfMembers(10)
            .pricePerHour(10000L)
            .timePerLecture(3)
            .numberOfLectures(5)
            .build();
    private static LectureSubject lectureSubject = LectureSubject.builder()
            .lecture(null)
            .subject(getSubjectWithSubjectIdAndKrSubject(1L, "백엔드"))
            .build();
    private static Lecture lecture = Lecture.builder()
            .mentor(mentor)
            .title("title")
            .subTitle("subTitle")
            .introduce("introduce")
            .content("content")
            .difficulty(DifficultyType.ADVANCED)
            .systems(Arrays.asList(SystemType.OFFLINE, SystemType.ONLINE))
            .lecturePrices(Arrays.asList(lecturePrice))
            .lectureSubjects(Arrays.asList(lectureSubject))
            .thumbnail("thumbnail")
            .build();

}
