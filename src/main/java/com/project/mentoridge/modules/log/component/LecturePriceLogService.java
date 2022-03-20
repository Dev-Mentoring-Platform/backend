package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LogService.Property;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class LecturePriceLogService extends MappingItemsLogService<LecturePrice> {

    @PostConstruct
    void init() {
        properties.add(new Property("isGroup", "그룹여부"));
        properties.add(new Property("numberOfMembers", "멤버 수"));
        properties.add(new Property("pricePerHour", "시간당 가격"));
        properties.add(new Property("timePerLecture", "1회당 강의 시간"));
        properties.add(new Property("numberOfLectures", "강의 횟수"));
        properties.add(new Property("totalPrice", "최종 수강료"));
    }
}
