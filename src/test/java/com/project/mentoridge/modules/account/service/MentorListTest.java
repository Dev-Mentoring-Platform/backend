package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
@ServiceTest
public class MentorListTest {

    @Autowired
    MentorService mentorService;

    @Test
    void getMentorResponses() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> START");
        mentorService.getMentorResponses(1);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> END");
    }
}
