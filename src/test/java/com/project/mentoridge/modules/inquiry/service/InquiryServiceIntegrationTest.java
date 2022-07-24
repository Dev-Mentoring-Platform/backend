package com.project.mentoridge.modules.inquiry.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

import static com.project.mentoridge.config.init.TestDataBuilder.getInquiryCreateRequestWithInquiryType;
import static org.junit.jupiter.api.Assertions.*;

@ServiceTest
class InquiryServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    EntityManager em;
    @Autowired
    InquiryService inquiryService;
    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    LoginService loginService;

    @BeforeEach
    @Override
    protected void init() {
        initDatabase();
    }

    @Test
    void createInquiry() {

        // Given
        User menteeUser = saveMenteeUser(loginService);

        // When
        InquiryCreateRequest inquiryCreateRequest = getInquiryCreateRequestWithInquiryType(InquiryType.ETC);
        Long inquiryId = inquiryService.createInquiry(menteeUser, inquiryCreateRequest).getId();

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