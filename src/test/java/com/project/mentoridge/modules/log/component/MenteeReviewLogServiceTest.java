package com.project.mentoridge.modules.log.component;

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
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

@Transactional
@SpringBootTest
class MenteeReviewLogServiceTest {

    @Autowired
    MenteeReviewLogService menteeReviewLogService;

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

    MenteeReview review = MenteeReview.builder()
            .score(5)
            .content("Good!")
            .mentee(mentee)
            .enrollment(enrollment)
            .lecture(lecture)
            .build();

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = mock(User.class);
        // when
        // then
        String log = menteeReviewLogService.insert(user, review);
        fail();
        assertThat(log).isEqualTo("[Mentee] 평점 : 5, 내용 : Good!, 멘티 : nicknameB, " +
                "수강 내역 : (멘티 : usernameB, " +
                "강의 : (멘토 : usernameA, 제목 : titleA, 소제목 : subTitleA, 소개 : introduceA, 내용 : contentA, 난이도 : BASIC, 이미지 : thumbnailA, " +
                "가격 : (그룹여부 : true, 멤버 수 : 5, 시간당 가격 : 10000, 1회당 강의 시간 : 3, 강의 횟수 : 5, 최종 수강료 : 150000)/(그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000), 온/오프라인 : 온라인/오프라인, 주제 : 자바/파이썬), " +
                "옵션 : (그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000)), " +

                "강의 : (멘토 : usernameA, 제목 : titleA, 소제목 : subTitleA, 소개 : introduceA, 내용 : contentA, 난이도 : BASIC, 이미지 : thumbnailA, " +
                "가격 : (그룹여부 : true, 멤버 수 : 5, 시간당 가격 : 10000, 1회당 강의 시간 : 3, 강의 횟수 : 5, 최종 수강료 : 150000)/(그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000), 온/오프라인 : 온라인/오프라인, 주제 : 자바/파이썬)");
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = mock(User.class);
        MenteeReview after = MenteeReview.builder()
                .score(1)
                .content("Bad")
                .mentee(mentee)
                .enrollment(enrollment)
                .lecture(lecture)
                .build();

        // when
        String log = menteeReviewLogService.update(user, review, after);
        // then
        assertThat(log).isEqualTo("[Mentee] 평점 : 5 → 1, 내용 : Good! → Bad");
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = mock(User.class);
        // when
        String log = menteeReviewLogService.delete(user, review);
        // then
        assertThat(log).isEqualTo("[Mentee] 평점 : 5, 내용 : Good!, 멘티 : nicknameB, " +
                "수강 내역 : (멘티 : usernameB, " +
                    "강의 : (멘토 : usernameA, 제목 : titleA, 소제목 : subTitleA, 소개 : introduceA, 내용 : contentA, 난이도 : BASIC, 이미지 : thumbnailA, 가격 : (그룹여부 : true, 멤버 수 : 5, 시간당 가격 : 10000, 1회당 강의 시간 : 3, 강의 횟수 : 5, 최종 수강료 : 150000)/(그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000), 온/오프라인 : 온라인/오프라인, 주제 : 자바/파이썬), 옵션 : (그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000)), 강의 : (멘토 : usernameA, 제목 : titleA, 소제목 : subTitleA, 소개 : introduceA, 내용 : contentA, 난이도 : BASIC, 이미지 : thumbnailA, 가격 : (그룹여부 : true, 멤버 수 : 5, 시간당 가격 : 10000, 1회당 강의 시간 : 3, 강의 횟수 : 5, 최종 수강료 : 150000)/(그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000), 온/오프라인 : 온라인/오프라인, 주제 : 자바/파이썬)");
    }
}