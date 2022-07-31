package com.project.mentoridge.modules.inquiry.repository;

import com.project.mentoridge.configuration.annotation.RepositoryTest;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.project.mentoridge.modules.base.TestDataBuilder.getInquiryCreateRequestWithInquiryType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@RepositoryTest
class InquiryRepositoryTest {

    @Autowired
    InquiryRepository inquiryRepository;
    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {

        user = userRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);

    }

    // TODO - CHECK
    // Converter
    @Test
    void findAll() {

        // given
        Inquiry inquiry = getInquiryCreateRequestWithInquiryType(InquiryType.LECTURE).toEntity(user);
        inquiryRepository.save(inquiry);

        // when
        List<Inquiry> inquiries = inquiryRepository.findAll();
        // then
        assertAll(
                () -> assertThat(inquiries.size()).isEqualTo(1),
                () -> assertThat(inquiries.get(0)).extracting("type").isEqualTo(inquiry.getType()),
                () -> assertThat(inquiries.get(0)).extracting("title").isEqualTo(inquiry.getTitle()),
                () -> assertThat(inquiries.get(0)).extracting("content").isEqualTo(inquiry.getContent())
        );
    }
}