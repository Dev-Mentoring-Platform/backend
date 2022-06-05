package com.project.mentoridge.config.security.jwt;

import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwtHeader = request.getHeader("Authorization");
        log.info(">>>>>>>>>>> Authorization : " + jwtHeader + ", Path : " + request.getRequestURI());
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtHeader.replace("Bearer ", "");

        // if (SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtTokenManager.verifyToken(accessToken)) {

                String username = jwtTokenManager.getClaim(accessToken, "username");
                String role = jwtTokenManager.getClaim(accessToken, "role");
                PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(username);
                principalDetails.setAuthority(role);

                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);

            } else {

                String path = request.getRequestURI();
                if ("/api/refresh-token".equals(path)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                response.setContentType(ContentType.APPLICATION_JSON.toString());   // "application/json; charset=UTF-8"
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401

                JSONObject json = new JSONObject();
                json.put("code", ErrorCode.TOKEN_EXPIRED.getCode());
                json.put("message", ErrorCode.TOKEN_EXPIRED.getMessage());
                response.getWriter().print(json);
                // response.getWriter().print(ErrorResponse.of(ErrorCode.TOKEN_EXPIRED));
            }

//        }

    }
}
