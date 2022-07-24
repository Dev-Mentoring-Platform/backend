package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.mail.EmailMessage;
import com.project.mentoridge.mail.EmailService;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.LoginLogService;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.project.mentoridge.config.exception.AlreadyExistException.ID;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.USER;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    @Value("${server.ip}")
    private String ip;
    @Value("${server.port}")
    private Integer port;

    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenManager jwtTokenManager;

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    private final UserLogService userLogService;
    private final MenteeLogService menteeLogService;

    private final LoginLogService loginLogService;

    public boolean checkUsernameDuplication(String username) {
        boolean duplicated = false;

        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findAllByUsername(username);
        if (user != null) {
            duplicated = true;
        }
        return duplicated;
    }

    public boolean checkNicknameDuplication(String nickname) {
        boolean duplicated = false;

        if (StringUtils.isBlank(nickname)) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findAllByNickname(nickname);
        if (user != null) {
            duplicated = true;
        }

        return duplicated;
    }

    public User signUp(SignUpRequest signUpRequest) {

        String username = signUpRequest.getUsername();
        if (checkUsernameDuplication(username)) {
            throw new AlreadyExistException(ID);
        }

        // TODO - toEntity()
        User user = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                .name(signUpRequest.getName())
                .gender(signUpRequest.getGender())
                .birthYear(signUpRequest.getBirthYear())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .nickname(signUpRequest.getNickname())
                .zone(signUpRequest.getZone())
                .image(signUpRequest.getImage())
                .role(RoleType.MENTEE)
                .provider(null)
                .providerId(null)
                .build();
        User unverified = userRepository.save(user);
        userLogService.insert(user, unverified);

        // TODO - 상수
        Map<String, Object> variables = new HashMap<>();
        variables.put("host", String.format("http://%s:%d", ip, port));
        variables.put("link", "/api/verify-email?email=" + unverified.getUsername() + "&token=" + unverified.getEmailVerifyToken());
        variables.put("content", "Welcome! We recently received a request to create an account. To verify that you made this request, we're sending this confirmation email.");

        String content = templateEngine.process("verify-email", getContext(variables));
        sendEmail(unverified.getUsername(), "Welcome to MENTORIDGE, please verify your email!", content);

        return unverified;
    }

    public Mentee verifyEmail(String email, String token) {

        User user = userRepository.findUnverifiedUserByUsername(email)
                .orElseThrow(() -> new RuntimeException("해당 계정의 미인증 사용자가 존재하지 않습니다."));

        if (!token.equals(user.getEmailVerifyToken())) {
            throw new RuntimeException("인증 실패");
        }
        user.verifyEmail(userLogService);
        Mentee saved = menteeRepository.save(Mentee.builder()
                .user(user)
                .build());
        menteeLogService.insert(user, saved);
        return saved;
    }

        private Context getContext(Map<String, Object> variables) {

            Context context = new Context();

            Iterator<Map.Entry<String, Object>> iterator = variables.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> variable = iterator.next();
                context.setVariable(variable.getKey(), variable.getValue());
            }
            return context;
        }

        private void sendEmail(String to, String subject, String content) {
            EmailMessage emailMessage = EmailMessage.builder()
                    .to(to)
                    .subject(subject)
                    .content(content)
                    .build();
            emailService.send(emailMessage);
        }

        private Authentication authenticate(String username, String password) {

            try {
                // SecurityContextHolder.getContext().setAuthentication(authentication);
                return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            } catch(BadCredentialsException e) {
                throw new BadCredentialsException("BadCredentialsException");
            } catch(DisabledException e) {
                throw new DisabledException("DisabledException");
            } catch(LockedException e) {
                throw new LockedException("LockedException");
            } catch(UsernameNotFoundException e) {
                throw new UsernameNotFoundException("UsernameNotFoundException");
            } catch(AuthenticationException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }

            return null;
        }

        public static Map<String, Object> getClaims(String username, RoleType roleType) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("role", roleType.getType());
            return claims;
        }

        public static Map<String, Object> getMenteeClaims(String username) {
            return getClaims(username, RoleType.MENTEE);
        }

        private static Map<String, Object> getMentorClaims(String username) {
            return getClaims(username, RoleType.MENTOR);
        }

    public JwtTokenManager.JwtResponse login(String username, String password) {

        Authentication authentication = authenticate(username, password);
        if (authentication != null) {

            Object principal = authentication.getPrincipal();
            if (principal instanceof PrincipalDetails) {

                PrincipalDetails principalDetails = (PrincipalDetails) principal;
                User user = principalDetails.getUser();
                // accessToken
                String accessToken = jwtTokenManager.createToken(principalDetails.getUsername(), getMenteeClaims(username));
                // refreshToken
                String refreshToken = jwtTokenManager.createRefreshToken();
                user.updateRefreshToken(refreshToken);

                // lastLoginAt
                user.login(loginLogService);
                return jwtTokenManager.getJwtTokens(accessToken, refreshToken);
            }
        }
        // TODO - CHECK : 예외 처리
        return null;
    }

    public JwtTokenManager.JwtResponse refreshToken(String accessTokenWithPrefix, String refreshTokenWithPrefix, String role) {

        final String accessToken = accessTokenWithPrefix.replace("Bearer ", "");
        final String refreshToken = refreshTokenWithPrefix.replace("Bearer ", "");

        // accessToken 만료 시
        if (!jwtTokenManager.verifyToken(accessToken)) {

            // refreshToken 유효한지 확인
            // TODO - Refactoring
            return userRepository.findByRefreshToken(refreshToken)
                    .map(user -> {

                        String newAccessToken = null;
                        // TODO - Enum Converter
                        if (role.equals(RoleType.MENTOR.getType())) {
                            newAccessToken = jwtTokenManager.createToken(user.getUsername(), getMentorClaims(user.getUsername()));
                        } else {
                            newAccessToken = jwtTokenManager.createToken(user.getUsername(), getMenteeClaims(user.getUsername()));
                        }
                        if (!jwtTokenManager.verifyToken(refreshToken)) {
                            String newRefreshToken = jwtTokenManager.createRefreshToken();
                            user.updateRefreshToken(newRefreshToken);
                            return jwtTokenManager.getJwtTokens(newAccessToken, newRefreshToken);
                        }
                        return jwtTokenManager.getJwtTokens(newAccessToken, refreshToken);
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in Database!"));
        }
        return null;
    }

    public JwtTokenManager.JwtResponse login(LoginRequest request) {
        return this.login(request.getUsername(), request.getPassword());
    }

    public void findPassword(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER));
        // 랜덤 비밀번호로 변경
        String randomPassword = user.findPassword(bCryptPasswordEncoder, userLogService);

        // 랜덤 비밀번호가 담긴 이메일 전송
        // TODO - 상수
        Map<String, Object> variables = new HashMap<>();
        variables.put("host", String.format("http://{}:{}", ip, port));
        variables.put("password", randomPassword);

        String content = templateEngine.process("find-password", getContext(variables));
        sendEmail(user.getUsername(), "Welcome to MENTORIDGE, find your password!", content);
    }

    /**
     * @param username
     * @param role - 현재 Role
     * @return
     */
    public JwtTokenManager.JwtResponse changeType(String username, String role) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER));

        String accessToken = null;
        if (role.equals(RoleType.MENTEE.getType())) {

            if (!user.getRole().equals(RoleType.MENTOR)) {
                // 멘토 자격이 없는 경우 - "해당 사용자는 멘토가 아닙니다."
                throw new UnauthorizedException(RoleType.MENTOR);
            }
            accessToken = jwtTokenManager.createToken(username, getMentorClaims(username));
        } else if (role.equals(RoleType.MENTOR.getType())) {
            accessToken = jwtTokenManager.createToken(username, getMenteeClaims(username));
        }
        // TODO - CHECK : refreshToken을 다시 발급받아야 하는가?
        String refreshToken = jwtTokenManager.createRefreshToken();
        user.updateRefreshToken(refreshToken);
        return jwtTokenManager.getJwtTokens(accessToken, refreshToken);
    }
}
