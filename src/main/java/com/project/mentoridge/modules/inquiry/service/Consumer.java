package com.project.mentoridge.modules.inquiry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Consumer {

    private final ObjectMapper objectMapper;
    private final InquiryRepository inquiryRepository;

//    @RabbitListener(queues = InquiryService.ROUTING_KEY)
//    public void handler(String message) throws JsonProcessingException {
//        Inquiry inquiry = objectMapper.readValue(message, Inquiry.class);
//        inquiryRepository.save(inquiry);
//    }
}
