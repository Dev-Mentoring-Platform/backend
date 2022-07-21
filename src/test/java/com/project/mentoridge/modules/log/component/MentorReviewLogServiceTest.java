package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

@ServiceTest
class MentorReviewLogServiceTest {

    @Autowired
    MentorReviewLogService mentorReviewLogService;

    User userA = User.builder()
            .username("usernameA")
            .name("nameA")
            .gender(GenderType.MALE)
            .birthYear("20220318")
            .phoneNumber("01012345678")
            .nickname("nicknameA")
            .image(null)
            .zone("서울특별시 강남구 청담동")
            .build();
    Mentor mentor = Mentor.builder()
            .user(userA)
            .bio("bio")
            .build();
    User userB = User.builder()
            .username("usernameB")
            .name("nameB")
            .gender(GenderType.FEMALE)
            .birthYear("20220319")
            .phoneNumber("01012345679")
            .nickname("nicknameB")
            .image(null)
            .zone("서울특별시 강남구 압구정동")
            .build();
    Mentee mentee = Mentee.builder()
            .user(userB)
            .subjects("subjects")
            .build();
    LecturePrice lecturePrice1 = LecturePrice.builder()
            .isGroup(true)
            .numberOfMembers(5)
            .pricePerHour(10000L)
            .timePerLecture(3)
            .numberOfLectures(5)
            .totalPrice(150000L)
            .build();
    LecturePrice lecturePrice2 = LecturePrice.builder()
            .isGroup(false)
            .numberOfMembers(0)
            .pricePerHour(5000L)
            .timePerLecture(10)
            .numberOfLectures(5)
            .totalPrice(250000L)
            .build();
    LectureSubject lectureSubject1 = LectureSubject.builder()
            .subject(Subject.builder()
                    .subjectId(1L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("자바")
                    .build())
            .build();
    LectureSubject lectureSubject2 = LectureSubject.builder()
            .subject(Subject.builder()
                    .subjectId(2L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("파이썬")
                    .build())
            .build();
    Lecture lecture = Lecture.builder()
            .mentor(mentor)
            .title("titleA")
            .subTitle("subTitleA")
            .introduce("introduceA")
            .content("contentA")
            .difficulty(DifficultyType.BASIC)
            .thumbnail("thumbnailA")
            .systems(Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE))
            .lecturePrices(Arrays.asList(lecturePrice1, lecturePrice2))
            .lectureSubjects(Arrays.asList(lectureSubject1, lectureSubject2))
            .build();
    Enrollment enrollment = Enrollment.builder()
            .mentee(mentee)
            .lecture(lecture)
            .lecturePrice(lecturePrice2)
            .build();

    MenteeReview parent = MenteeReview.builder()
            .score(5)
            .content("Good!")
            .mentee(mentee)
            .enrollment(enrollment)
            .lecture(lecture)
            .build();

    MentorReview review = MentorReview.builder()
            .content("Thank You!")
            .mentor(mentor)
            .parent(parent)
            .build();

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // then
        mentorReviewLogService.insert(pw, review);
        System.out.println(sw.toString());
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        MentorReview after = MentorReview.builder()
                .content("Sorry")
                .mentor(mentor)
                .parent(parent)
                .build();

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        mentorReviewLogService.update(pw, review, after);
        // then
        System.out.println(sw.toString());
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        mentorReviewLogService.delete(pw, review);
        // then
        System.out.println(sw.toString());
    }
}