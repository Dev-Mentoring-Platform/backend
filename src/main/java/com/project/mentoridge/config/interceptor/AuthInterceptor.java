package com.project.mentoridge.config.interceptor;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.Nullable;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        // TODO - CHECK : CORS 에러
        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {

                Parameter parameter = methodParameter.getParameter();
                if (parameter.isAnnotationPresent(CurrentUser.class) || parameter.getType().equals(User.class)) {

                    if (existCurrentUser()) {
                        return true;
                    } else {
                        if (request.getRequestURI().equals("/api/chat/rooms/all")) {
                            return true;
                        }
                        if (!parameter.isAnnotationPresent(Nullable.class)) {
                            throw new UnauthorizedException();
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean existCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

            Object principal = authentication.getPrincipal();

            if (principal instanceof PrincipalDetails) {

                PrincipalDetails principalDetails = (PrincipalDetails) principal;
                if (principalDetails.getUser() != null) {
                    return true;
                }
            }
        }

        return false;
    }
}
