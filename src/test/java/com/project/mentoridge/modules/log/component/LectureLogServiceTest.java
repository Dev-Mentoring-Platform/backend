package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ServiceTest
class LectureLogServiceTest {

    @Autowired
    LectureLogService lectureLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
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

        // when
        String log = lectureLogService.insert(user, lecture);
        // then
/*        assertEquals(String.format("[Lecture] 멘토 : %s, 제목 : %s, 소제목 : %s, 소개 : %s, 내용 : %s, 난이도 : %s, 이미지 : %s",
                lecture.getMentor().getUser().getUsername(), lecture.getTitle(), lecture.getSubTitle(), lecture.getIntroduce(), lecture.getContent(), lecture.getDifficulty(), lecture.getThumbnail()),
                log);*/
        assertThat(log).isEqualTo("[Lecture] 멘토 : username, 제목 : titleA, 소제목 : subTitleA, 소개 : introduceA, 내용 : contentA, 난이도 : BASIC, 이미지 : thumbnailA, 가격 : (그룹여부 : true, 멤버 수 : 5, 시간당 가격 : 10000, 1회당 강의 시간 : 3, 강의 횟수 : 5, 최종 수강료 : 150000)/(그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000), 온/오프라인 : 온라인/오프라인, 주제 : 자바/파이썬");
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
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

        Lecture before = Lecture.builder()
                .mentor(mentor)
                .title("titleA")
                .subTitle("subTitleA")
                .introduce("introduceA")
                .content("contentA")
                .difficulty(DifficultyType.BASIC)
                .thumbnail("thumbnailA")
                .systems(Arrays.asList(SystemType.ONLINE))
                .lecturePrices(Arrays.asList(lecturePrice1, lecturePrice2))
                .build();
        Lecture after = Lecture.builder()
                .mentor(mentor)
                .title("titleB")
                .subTitle("subTitleB")
                .introduce("introduceB")
                .content("contentB")
                .difficulty(DifficultyType.ADVANCED)
                .thumbnail("thumbnailB")
                .systems(Arrays.asList(SystemType.OFFLINE))
                .lecturePrices(Arrays.asList(lecturePrice1, lecturePrice2))
                .build();

        // when
        String log = lectureLogService.update(user, before, after);
        // then
        assertEquals(String.format("[Lecture] 제목 : %s → %s, 소제목 : %s → %s, 소개 : %s → %s, 내용 : %s → %s, 난이도 : %s → %s, 이미지 : %s → %s, 온/오프라인 : %s → %s",
                before.getTitle(), after.getTitle(),
                before.getSubTitle(), after.getSubTitle(),
                before.getIntroduce(), after.getIntroduce(),
                before.getContent(), after.getContent(),
                before.getDifficulty(), after.getDifficulty(),
                before.getThumbnail(), after.getThumbnail(),
                before.getSystems().get(0).getName(), after.getSystems().get(0).getName()), log);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
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

        // when
        String log = lectureLogService.delete(user, lecture);
        // then
        assertThat(log).isEqualTo("[Lecture] 멘토 : username, 제목 : titleA, 소제목 : subTitleA, 소개 : introduceA, 내용 : contentA, 난이도 : BASIC, 이미지 : thumbnailA, 가격 : (그룹여부 : true, 멤버 수 : 5, 시간당 가격 : 10000, 1회당 강의 시간 : 3, 강의 횟수 : 5, 최종 수강료 : 150000)/(그룹여부 : false, 멤버 수 : 0, 시간당 가격 : 5000, 1회당 강의 시간 : 10, 강의 횟수 : 5, 최종 수강료 : 250000), 온/오프라인 : 온라인/오프라인, 주제 : 자바/파이썬");
    }

    @Test
    void approve() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
                .build();
        Lecture lecture = Lecture.builder()
                .mentor(mentor)
                .title("titleA")
                .subTitle("subTitleA")
                .introduce("introduceA")
                .content("contentA")
                .difficulty(DifficultyType.BASIC)
                .thumbnail("thumbnailA")
                .systems(Arrays.asList(SystemType.ONLINE))
                .build();

        // when
        String log = lectureLogService.approve(lecture);
        // then
        assertThat(log).isEqualTo("[Lecture] 승인 : true → false");
    }
/*
    @Test
    void close() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
                .build();
        Lecture lecture = Lecture.builder()
                .mentor(mentor)
                .title("titleA")
                .subTitle("subTitleA")
                .introduce("introduceA")
                .content("contentA")
                .difficulty(DifficultyType.BASIC)
                .thumbnail("thumbnailA")
                .systems(Arrays.asList(SystemType.ONLINE))
                .build();
        lecture.close();

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        lectureLogService.close(user, lecture);
        // then
        System.out.println(sw.toString());
    }

    @Test
    void open() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
                .build();
        Lecture lecture = Lecture.builder()
                .mentor(mentor)
                .title("titleA")
                .subTitle("subTitleA")
                .introduce("introduceA")
                .content("contentA")
                .difficulty(DifficultyType.BASIC)
                .thumbnail("thumbnailA")
                .systems(Arrays.asList(SystemType.ONLINE))
                .build();
        lecture.open();

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        lectureLogService.open(user, lecture);
        // then
        System.out.println(sw.toString());
    }*/
}