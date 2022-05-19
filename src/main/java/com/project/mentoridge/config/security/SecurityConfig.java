package com.project.mentoridge.config.security;

import com.project.mentoridge.config.security.jwt.JwtRequestFilter;
import com.project.mentoridge.config.security.oauth.CustomOAuth2SuccessHandler;
import com.project.mentoridge.config.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity  // Spring Security 설정 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterAfter(jwtRequestFilter, LogoutFilter.class);
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .authorizeRequests()
                //.antMatchers("/swagger-ui/**", "/swagger-ui.html/**", "/swagger-resources/**", "/v2/**", "/webjars/**").permitAll()
                // TODO - CHECK : 테스트 코드
                //.antMatchers("/login", "/sign-up/**", "/oauth/**").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/addresses/**", "/subjects/**").permitAll()
                .antMatchers("/sign-up/oauth/detail").authenticated()
                .antMatchers(HttpMethod.POST, "/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/**").authenticated();

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");

        http.oauth2Login()
                // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정 담당
                .userInfoEndpoint().userService(customOAuth2UserService)
                .and()
                // 소셜 로그인 성공 시 후속 조치
                // 리소서 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능
                .successHandler(customOAuth2SuccessHandler);
    }
}
