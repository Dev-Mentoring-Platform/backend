package com.project.mentoridge.config.aspect;

import com.project.mentoridge.config.response.RestResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class RestResponseAspect {

    // @Around("execution(* com.project.mentoridge.modules.*.controller.*.*(..))")
    public RestResponse restResponseHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return RestResponse.of(HttpStatus.OK.value(), "API 호출에 성공하였습니다.", proceedingJoinPoint.proceed());
    }

}
