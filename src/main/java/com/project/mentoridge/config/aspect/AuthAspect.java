package com.project.mentoridge.config.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuthAspect {

//    @Pointcut("execution(* com.project.mentoridge.modules.*.controller.*.*(..))")
//    public void pointcut() {}
//
//    @Before("pointcut() && args(user)")
//    public void checkAuth(User user) {
//        if (user == null) {
//            throw new UnauthorizedException();
//        }
//    }
}
