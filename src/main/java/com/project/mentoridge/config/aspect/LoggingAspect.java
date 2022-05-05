package com.project.mentoridge.config.aspect;

import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final UrlPathHelper urlPathHelper;
    private final SimpleDateFormat simpleDateFormat;
    private final LogRepository logRepository;

//    // TODO - CHECK : within
//    @Pointcut("execution(* com.project.mentoridge.modules.*.controller.*.*(..))")
//    public void pointcut() {}

    @Around("execution(* com.project.mentoridge.modules.*.controller.*.*(..))")
    public Object log(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = null;
        String username = null;
        LocalDateTime lastLoginAt = null;
        if (authentication != null) {

            // 로그인
            Object principal = authentication.getPrincipal();

            if (principal instanceof PrincipalDetails) {

                PrincipalDetails principalDetails = (PrincipalDetails) principal;
                if (principalDetails.getUser() != null) {

                    User user = principalDetails.getUser();
                    userId = user.getId();
                    username = user.getUsername();
                    lastLoginAt = user.getLastLoginAt();
                }
            }
        }

//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        System.out.println("sessionId : " + session.getId());
//        System.out.println("osType : " + request.getHeader(HttpHeaders.USER_AGENT));
//        System.out.println("accessPath : " + new UrlPathHelper().getOriginatingRequestUri(request));
//        System.out.println("ip : " + request.getRemoteAddr());
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
//        System.out.println("lastAccessAt : " + sdf.format(session.getLastAccessedTime()));
//        System.out.println("userId : " + userId);
//        System.out.println("username : " + username);
//        System.out.println("LoginAt : " + lastLoginAt);
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        Log log = Log.builder()
//                .sessionId(session.getId())
//                .osType(request.getHeader(HttpHeaders.USER_AGENT))
//                .accessPath(urlPathHelper.getOriginatingRequestUri(request))
//                .ip(request.getRemoteAddr())
//                .lastAccessAt(simpleDateFormat.format(session.getLastAccessedTime()))
//                .userId(userId)
//                .username(username)
//                .loginAt(LocalDateTimeUtil.getDateTimeToString(lastLoginAt))
//                .build();
        // logRepository.save(log);

        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }
}
