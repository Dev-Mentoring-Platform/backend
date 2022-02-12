package com.project.mentoridge.config.messageQueue;//package com.mentor.mentoridge.config.messageQueue;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//public class Producer {
//
//    private final RabbitTemplate rabbitTemplate;
//
//    public void send(String routingKey, String message) {
//        this.rabbitTemplate.convertAndSend(routingKey, message);
//    }
//}
