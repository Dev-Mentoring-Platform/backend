package com.project.mentoridge.config.security.jwt;

import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.config.security.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
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

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtHeader.replace("Bearer ", "");
        String username = jwtTokenManager.getClaim(accessToken, "username");
        String role = jwtTokenManager.getClaim(accessToken, "role");

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(username);
            principalDetails.setAuthority(role);

            if (jwtTokenManager.verifyToken(accessToken, principalDetails)) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {

                
            }

        }

        filterChain.doFilter(request, response);

    }
}
