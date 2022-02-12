package com.project.mentoridge.test;

import com.project.mentoridge.config.init.TestDataBuilder;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MainTestService {

    private final UserRepository userRepository;

    private final IsolationTestService isolationTestService;
    private final PropagationTestService propagationTestService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveMentor() {

        String name = "yk";
        User user = TestDataBuilder.getUser("yk");
        userRepository.save(user);

        try {
            // 둘 다 Propagation.REQUIRED일 때
            // MainTestService.saveMentor()에서 Exception 발생 시 둘 다 rollback
            // PropagationTestService.saveMentor()에서 Exception 발생 시에도 둘 다 rollback
            propagationTestService.saveMentor(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new RuntimeException("RuntimeException");

    }
}
