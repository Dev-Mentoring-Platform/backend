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
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static com.project.mentoridge.modules.purchase.vo.Pick.buildPick;

@Transactional
@SpringBootTest
class PickLogServiceTest {

    @Autowired
    PickLogService pickLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
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

        Pick pick = buildPick(mentee, lecture, lecturePrice1);

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // then
        pickLogService.insert(pw, pick);
        System.out.println(sw.toString());
    }
}