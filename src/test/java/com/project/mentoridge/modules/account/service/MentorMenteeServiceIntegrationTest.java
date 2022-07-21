package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.AbstractIntegrationTest;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.SimpleMenteeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

@ServiceTest
class MentorMenteeServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MentorMenteeService mentorMenteeService;

    @Test
    void get_SimpleMenteeResponses_of_unclosed_lecture() {
        // Given
        // When
        List<SimpleMenteeResponse> responses = mentorMenteeService.getSimpleMenteeResponses(mentorUser, false, true);
        // Then
        assertThat(responses).hasSize(3);
    }

    @Test
    void get_SimpleMenteeResponses_of_closed_lecture() {
        // Given
        // When
        List<SimpleMenteeResponse> responses = mentorMenteeService.getSimpleMenteeResponses(mentorUser, true, true);
        // Then
        assertThat(responses).hasSize(1);
        SimpleMenteeResponse response = responses.get(0);
        assertAll(
                () -> assertThat(response.getMenteeId()).isEqualTo(mentee1.getId()),
                () -> assertThat(response.getUserId()).isEqualTo(menteeUser1.getId()),
                () -> assertThat(response.getName()).isEqualTo(menteeUser1.getName()),
                () -> assertThat(response.getNickname()).isEqualTo(menteeUser1.getNickname()),
                () -> assertThat(response.getEnrollmentId()).isEqualTo(enrollment2.getId())
        );
    }

    @Test
    void get_paged_MenteeEnrollmentInfoResponses() {
        // Given
        // When
        Page<MenteeEnrollmentInfoResponse> responses = mentorMenteeService.getMenteeLectureResponses(mentorUser, mentee1.getId(), 1);
        // Then
        assertThat(responses).hasSize(2);
        for (MenteeEnrollmentInfoResponse response : responses) {
            if (response.getEnrollmentId().equals(enrollment1.getId())) {

                assertThat(response.getMenteeId()).isEqualTo(mentee1.getId());
                assertThat(response.getEnrollmentId()).isEqualTo(enrollment1.getId());

                assertThat(response.getLecture().getLectureId()).isEqualTo(lecture1.getId());
                assertThat(response.getLecture().getThumbnail()).isEqualTo(lecture1.getThumbnail());
                assertThat(response.getLecture().getTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getLecture().getSubTitle()).isEqualTo(lecture1.getSubTitle());
                assertThat(response.getLecture().getIntroduce()).isEqualTo(lecture1.getIntroduce());
                assertThat(response.getLecture().getContent()).isEqualTo(lecture1.getContent());
                assertThat(response.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId());
                assertThat(response.getLecture().getSystemTypes()).hasSize(2);

                assertThat(response.getReviewId()).isEqualTo(menteeReview1.getId());
                assertThat(response.getChatroomId()).isEqualTo(chatroom.getId());

            } else if (response.getEnrollmentId().equals(enrollment2.getId())) {

                assertThat(response.getMenteeId()).isEqualTo(mentee1.getId());
                assertThat(response.getEnrollmentId()).isEqualTo(enrollment2.getId());

                assertThat(response.getLecture().getLectureId()).isEqualTo(lecture1.getId());
                assertThat(response.getLecture().getThumbnail()).isEqualTo(lecture1.getThumbnail());
                assertThat(response.getLecture().getTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getLecture().getSubTitle()).isEqualTo(lecture1.getSubTitle());
                assertThat(response.getLecture().getIntroduce()).isEqualTo(lecture1.getIntroduce());
                assertThat(response.getLecture().getContent()).isEqualTo(lecture1.getContent());
                assertThat(response.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId());
                assertThat(response.getLecture().getSystemTypes()).hasSize(2);

                assertThat(response.getReviewId()).isEqualTo(menteeReview2.getId());
                assertThat(response.getChatroomId()).isEqualTo(chatroom.getId());

            } else {
                fail();
            }
        }
    }

    @Test
    void get_MenteeEnrollmentInfoResponse() {
        // Given
        // When
        MenteeEnrollmentInfoResponse response = mentorMenteeService.getMenteeLectureResponse(mentorUser, mentee1.getId(), enrollment1.getId());
        // Then
        assertAll(
                () -> assertThat(response.getMenteeId()).isEqualTo(mentee1.getId()),
                () -> assertThat(response.getEnrollmentId()).isEqualTo(enrollment1.getId()),

                () -> assertThat(response.getLecture().getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response.getLecture().getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response.getLecture().getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response.getLecture().getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response.getLecture().getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response.getLecture().getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response.getLecture().getSystemTypes()).hasSize(2),

                () -> assertThat(response.getReviewId()).isEqualTo(menteeReview1.getId()),
                () -> assertThat(response.getChatroomId()).isEqualTo(chatroom.getId())
        );
    }

}