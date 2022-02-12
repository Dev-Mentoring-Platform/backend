package com.project.mentoridge.modules.account.repository;

import com.project.mentoridge.config.security.oauth.provider.OAuthType;
import com.project.mentoridge.modules.account.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(value = "select * from user where username = :username", nativeQuery = true)
    User findAllByUsername(@Param("username") String username);

    @Query(value = "select * from user where nickname = :nickname", nativeQuery = true)
    User findAllByNickname(@Param("nickname") String nickname);

    User findByProviderAndProviderId(OAuthType provider, String providerId);

    @Query(value = "select * from user where username = :username and deleted = false", nativeQuery = true)
    Optional<User> findUndeletedUserByUsername(@Param("username") String username);

    @Query(value = "select * from user where username = :username and email_verified = false", nativeQuery = true)
    Optional<User> findUnverifiedUserByUsername(@Param("username") String username);

//    User findByName(String name);
//
//    @Query(value = "select * from user where name = :name", nativeQuery = true)
//    User findAllByName(@Param("name") String name);

    Optional<User> findByFcmToken(String fcmToken);
}
