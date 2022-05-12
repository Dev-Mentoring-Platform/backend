package com.project.mentoridge.configuration.auth;

import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.PrincipalDetailsService;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndZone;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    // private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;

    private final MenteeLogService menteeLogService;
    private final LoginService loginService;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {

        String name = withAccount.value();
        String username = name + "@email.com";
        if (!userRepository.findByUsername(username).isPresent()) {

            User user = loginService.signUp(getSignUpRequestWithNameAndZone(name, "서울특별시 강서구 화곡동"));
            // loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());
            user.verifyEmail();
            Mentee saved = menteeRepository.save(Mentee.builder()
                    .user(user)
                    .build());
            // menteeLogService.insert(user, saved);
        }

        // JWT Token으로 테스트
        // PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(username);
        // Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, principalDetails.getPassword());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // context.setAuthentication(authentication);

        return context;
    }
}
