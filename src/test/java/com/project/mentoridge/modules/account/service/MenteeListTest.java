package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@ServiceTest
public class MenteeListTest {

    @Autowired
    MenteeService menteeService;

    // 쿼리 확인
    @Test
    void getMenteeResponses() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> START");
        Page<MenteeResponse> menteeResponses = menteeService.getMenteeResponses(1);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> END");
    }
}
