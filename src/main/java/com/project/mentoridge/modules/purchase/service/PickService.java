package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.controller.response.PickWithSimpleEachLectureResponse;
import org.springframework.data.domain.Page;

public interface PickService {

    Page<PickWithSimpleEachLectureResponse> getPickWithSimpleEachLectureResponses(User user, Integer page);

    Long createPick(User user, Long lectureId, Long lecturePriceId);
    // void deletePick(User user, Long pickId);
    void deleteAllPicks(User user);

}
