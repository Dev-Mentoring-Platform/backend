package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@Transactional
@SpringBootTest
public class MentorListTest {

    @Autowired
    MentorService mentorService;

    @Test
    void getMentorResponses() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> START");
        Page<MentorResponse> mentorResponses = mentorService.getMentorResponses(1);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> END");
    }
}
