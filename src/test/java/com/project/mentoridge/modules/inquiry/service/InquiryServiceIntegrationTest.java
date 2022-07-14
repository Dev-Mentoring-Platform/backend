package com.project.mentoridge.modules.inquiry.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.getInquiryCreateRequestWithInquiryType;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class InquiryServiceIntegrationTest {

    @Autowired
    InquiryService inquiryService;
    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    UserRepository userRepository;

    @WithAccount("user")
    @Test
    void createInquiry() {

        // Given
        User user = userRepository.findByUsername("user@email.com").orElseThrow(RuntimeException::new);

        // When
        InquiryCreateRequest inquiryCreateRequest = getInquiryCreateRequestWithInquiryType(InquiryType.ETC);
        Long inquiryId = inquiryService.createInquiry(user, inquiryCreateRequest).getId();

        // Then
        assertEquals(1, inquiryRepository.count());
        assertTrue(inquiryRepository.findById(inquiryId).isPresent());
        Inquiry inquiry = inquiryRepository.findById(inquiryId).get();
        assertAll(
                () -> assertEquals(inquiryCreateRequest.getType(), inquiry.getType()),
                () -> assertEquals(inquiryCreateRequest.getTitle(), inquiry.getTitle()),
                () -> assertEquals(inquiryCreateRequest.getContent(), inquiry.getContent())
        );
    }
}