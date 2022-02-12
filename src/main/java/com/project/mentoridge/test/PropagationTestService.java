package com.project.mentoridge.test;

import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropagationTestService {

    private final UserRepository userRepository;
    private final MentorService mentorService;

    // * Propagation(전파 방식)
    // 1. Propagation.REQUIRED (default)
    // - 기존에 사용하던 트랜잭션이 있다면 그 트랜잭션을 사용하고
    // 트랜잭션이 없다면 새로 트랜잭션을 생성해서 사용

    // 2. Propagation.REQUIRES_NEW
    // - 기존에 사용하던 트랜잭션과 상관없이 새로운 트랜잭션을 생성
    // - 호출하는 쪽의 커밋/롤백과 상관없이 자체적으로 커밋/롤백 진행

    // @Transactional(propagation = Propagation.REQUIRED)
    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    // @Transactional(propagation = Propagation.NESTED)
    // @Transactional(propagation = Propagation.SUPPORTS)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveMentor(User user) {

//        MentorSignUpRequest mentorSignUpRequest = TestDataBuilder.getMentorSignUpRequest("java,spring");
//        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);

        // throw new RuntimeException("RuntimeException");
    }



}
