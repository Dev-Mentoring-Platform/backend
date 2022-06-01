package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LogService.Property;
import com.project.mentoridge.modules.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.project.mentoridge.modules.log.vo.Log.buildUpdateLog;

@RequiredArgsConstructor
@Service
public class LecturePriceLogService extends MappingItemsLogService<LecturePrice> {

    private final LogRepository logRepository;

    @PostConstruct
    void init() {
        properties.add(new Property("isGroup", "그룹여부"));
        properties.add(new Property("numberOfMembers", "멤버 수"));
        properties.add(new Property("pricePerHour", "시간당 가격"));
        properties.add(new Property("timePerLecture", "1회당 강의 시간"));
        properties.add(new Property("numberOfLectures", "강의 횟수"));
        properties.add(new Property("totalPrice", "최종 수강료"));
    }

    public void close(User user, Lecture lecture, LecturePrice lecturePrice) {
        logRepository.saveLog(buildUpdateLog(user.getUsername(), String.format("[Lecture] 강의 : %s/%s, 모집 종료", lecture.getId(), lecturePrice.getId())));
    }

    public void open(User user, Lecture lecture, LecturePrice lecturePrice) {
        logRepository.saveLog(buildUpdateLog(user.getUsername(), String.format("[Lecture] 강의 : %s/%s, 모집 개시", lecture.getId(), lecturePrice.getId())));
    }
}
