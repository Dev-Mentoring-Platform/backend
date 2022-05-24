package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.mail.EmailMessage;
import com.project.mentoridge.mail.EmailService;
import com.project.mentoridge.modules.account.controller.request.LoginRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpOAuthDetailRequest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.component.LoginLogService;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import static com.project.mentoridge.config.exception.AlreadyExistException.NICKNAME;
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
    private final MentorRepository mentorRepository;

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

    public void signUpOAuthDetail(User user, SignUpOAuthDetailRequest signUpOAuthDetailRequest) {

        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        // TODO - 예외 : OAuth로 가입한 회원이 아닌 경우
        if (user.getProvider() == null || StringUtils.isBlank(user.getProviderId())) {
            throw new RuntimeException("OAuth로 가입한 회원이 아닙니다.");
        }

        if (checkNicknameDuplication(signUpOAuthDetailRequest.getNickname())) {
            throw new AlreadyExistException(NICKNAME);
        }

        User before = user.copy();
        user.updateOAuthDetail(signUpOAuthDetailRequest);
        userLogService.update(user, before, user);
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
        user.verifyEmail();
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

    public JwtTokenManager.JwtResponse loginOAuth(String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", RoleType.MENTEE.getType());
        String accessToken = jwtTokenManager.createToken(username, claims);

        // lastLoginAt
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("username : " + username));
        user.login();

        // refreshToken
        String refreshToken = jwtTokenManager.createRefreshToken();
        user.updateRefreshToken(refreshToken);

        loginLogService.login(user);
        return jwtTokenManager.getJwtTokens(accessToken, refreshToken);
    }

    public JwtTokenManager.JwtResponse login(String username, String password) {

        Authentication authentication = authenticate(username, password);
        if (authentication != null) {

            Object principal = authentication.getPrincipal();
            if (principal instanceof PrincipalDetails) {

                PrincipalDetails principalDetails = (PrincipalDetails) principal;

                Map<String, Object> claims = new HashMap<>();
                claims.put("username", username);
                claims.put("role", RoleType.MENTEE.getType());
                String accessToken = jwtTokenManager.createToken(principalDetails.getUsername(), claims);

                // lastLoginAt
                User user = principalDetails.getUser();
                user.login();

                // refreshToken
                String refreshToken = jwtTokenManager.createRefreshToken();
                user.updateRefreshToken(refreshToken);

                loginLogService.login(user);
                return jwtTokenManager.getJwtTokens(accessToken, refreshToken);
            }
        }
        // TODO - CHECK : 예외 처리
        return null;
    }

    public JwtTokenManager.JwtResponse refreshToken(String _accessToken, String _refreshToken, String role) {

        final String accessToken = _accessToken.replace("Bearer ", "");
        final String refreshToken = _refreshToken.replace("Bearer ", "");

        // accessToken 만료 시
        if (!jwtTokenManager.verifyToken(accessToken)) {

            // refreshToken 유효한지 확인
            // TODO - Refactoring
            return userRepository.findByRefreshToken(refreshToken)
                    .map(user -> {

                        Map<String, Object> claims = new HashMap<>();
                        claims.put("username", user.getUsername());
                        // TODO - Enum Converter
                        if (role.equals(RoleType.MENTOR.getType())) {
                            claims.put("role", RoleType.MENTOR.getType());
                        } else {
                            claims.put("role", RoleType.MENTEE.getType());
                        }

                        String newAccessToken = jwtTokenManager.createToken(user.getUsername(), claims);

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
        String randomPassword = generateRandomPassword(10);
        user.updatePassword(bCryptPasswordEncoder.encode(randomPassword));

        // 랜덤 비밀번호가 담긴 이메일 전송
        // TODO - 상수
        Map<String, Object> variables = new HashMap<>();
        variables.put("host", String.format("http://{}:{}", ip, port));
        variables.put("password", randomPassword);

        String content = templateEngine.process("find-password", getContext(variables));
        sendEmail(user.getUsername(), "Welcome to MENTORIDGE, find your password!", content);
    }

        private String generateRandomPassword(int count) {
        return RandomStringUtils.randomAlphanumeric(count);
    }

/*    public SessionUser getSessionUser() {
        return (SessionUser) httpSession.getAttribute("user");
    }*/

    public JwtTokenManager.JwtResponse changeType(String username, String role) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        if (role.equals(RoleType.MENTEE.getType())) {

            if (!user.getRole().equals(RoleType.MENTOR)) {
                // 멘토 자격이 없는 경우 - "해당 사용자는 멘토가 아닙니다."
                throw new UnauthorizedException(RoleType.MENTOR);
            }
            claims.put("role", RoleType.MENTOR.getType());
        } else if (role.equals(RoleType.MENTOR.getType())) {
            claims.put("role", RoleType.MENTEE.getType());
        }
        String accessToken = jwtTokenManager.createToken(username, claims);
        // TODO - CHECK : refreshToken을 다시 발급받아야 하는가?
        String refreshToken = jwtTokenManager.createRefreshToken();
        user.updateRefreshToken(refreshToken);
        return jwtTokenManager.getJwtTokens(accessToken, refreshToken);
    }
}
