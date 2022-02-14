package com.project.mentoridge.test;

import com.project.mentoridge.config.init.TestDataBuilder;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IsolationTestService {

    private final UserRepository userRepository;

    @Transactional
    public void get(Long id) {

        System.out.println(userRepository.findById(id));
        System.out.println(userRepository.findAll());

        System.out.println(userRepository.findById(id));
        System.out.println(userRepository.findAll());
    }

    @Transactional
    public void get2(Long id) {
        System.out.println(userRepository.findById(id));
        System.out.println(userRepository.findAll());

        // * JPA 트랜잭션과 MYSQL 트랜잭션을 동시에 실행
        // MYSQL로는 UPDATE user SET NAME = 'yk2' WHERE user_id = id 쿼리 실행
        // 1. IsolationLevel.TRANSACTION_READ_UNCOMMITTED : MYSQL 트랜잭션에서 update한 내용 READ (Dirty-read)
        System.out.println(userRepository.findById(id));
        System.out.println(userRepository.findAll());

        User user = userRepository.findById(id).get();
        // user.setNickname("yk2");
        // JPA 트랜잭션에서 다시 업데이트하면? → lock이 발생해서 MYSQL 트랜잭션을 기다린다.
        // ① 이 때 MYSQL 트랜잭션이 commit을 하면? : NAME, NICKNAME 둘 다 UPDATE
        // ② rollback을 해도 둘 다 UPDATE
        // - MYSQL에서 UPDATE를 한 것이 아니라
        // - JPA에서 Dirty-check(Dirty-read)한 값이 이미 반영되어 JPA에서 둘 다 UPDATE시킨 것
        // → User 엔티티에 @DynamicUpdate

        // Dirty-read로 중간에 데이터가 변경되는 현상 때문에 일반적으로 많이 사용하지는 않는다.

        // 2. IsolationLevel.TRANSACTION_READ_COMMITTED
        // ** COMMIT을 했음에도 test에서 마지막으로 조회를 했을 때 값이 UPDATE되지 않은 채로 나온 이유는
        // 1차 캐시 때문 → entityManager.clear(); 실행
        // - UNREPEATABLE_READ 문제 발생 : 내가 생성한 트랜잭션에서 값을 변경하지 않고 반복적으로 조회만 했는데 중간에 값이 변경되는 현상

        // 3. IsolationLevel.TRANSACTION_REPEATABLE_READ
        // : 트랜잭션 내에서 반복적으로 조회를 할 때 항상 값이 동일함을 보장한다.
        // 다른 트랜잭션에서 update/commit이 발생하더라도
        // 트랜잭션 시작 시의 상태를 스냅샷으로 별도로 저장하고 있고, 트랜잭션이 끝나기 전까지는 그 스냅샷 정보를 return

        // 4. IsolationLevel.TRANSACTION_SERIALIZABLE
        // - 데이터를 변경하려고 하면 무조건 다른 트랜잭션이 끝날 때까지 기다렸다가 처리
        // - 데이터 정합성은 100%
        // - But, waiting이 길어져서 성능은 down
    }

    // * Propagation(전파 방식)
    // 1. Propagation.REQUIRED (default)
    // - 기존에 사용하던 트랜잭션이 있다면 그 트랜잭션을 사용하고
    // 트랜잭션이 없다면 새로 트랜잭션을 생성해서 사용


    // 만일 saveUser()가 아닌 save()를 실행한다면?
    // saveUser()의 @Transactional 무효화
    public void save() {
        this.saveUser();
    }

    @Transactional
    public void saveUser() {

        User user = TestDataBuilder.getUserWithName("yk");
        userRepository.save(user);

        // RuntimeException 발생 시 롤백되어 커밋이 발생하지 않는다.
        throw new RuntimeException("RuntimeException");
    }

//    @Transactional(rollbackFor = Exception.class)
//    public void saveUser() {
//
//        String name = "yk";
//        User user = User.builder()
//                .username(name + "@email.com")
//                .password("password")
//                .name(name)
//                .gender("MALE")
//                .phoneNumber(null)
//                .email(null)
//                .nickname(null)
//                .bio(null)
//                .zone(null)
//                .role(RoleType.ROLE_MENTEE)
//                .provider(null)
//                .providerId(null)
//                .build();
//        userRepository.save(user);
//    }

}
