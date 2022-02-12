package com.project.mentoridge.modules.inquiry.service;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
// @Transactional
@SpringBootTest
class InquiryServiceIntegrationTest extends AbstractTest {

    @Autowired
    InquiryService inquiryService;
    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    UserRepository userRepository;

    @WithAccount(NAME)
    @Test
    void createInquiry() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        // When
        InquiryCreateRequest inquiryCreateRequest = InquiryCreateRequest.of(InquiryType.ETC, "제목", "내용");
        Long inquiryId = inquiryService.createInquiry(user, inquiryCreateRequest).getId();

        // Then
        assertEquals(1, inquiryRepository.count());
        assertTrue(inquiryRepository.findById(inquiryId).isPresent());

        Inquiry inquiry = inquiryRepository.findById(inquiryId).get();
        assertAll(
                () -> assertEquals(inquiryCreateRequest.getInquiryType(), inquiry.getType()),
                () -> assertEquals(inquiryCreateRequest.getTitle(), inquiry.getTitle()),
                () -> assertEquals(inquiryCreateRequest.getContent(), inquiry.getContent())
        );
    }
}