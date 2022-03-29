package com.project.mentoridge.config.security;

import com.project.mentoridge.config.security.jwt.JwtRequestFilter;
import com.project.mentoridge.config.security.oauth.provider.CustomOAuth2UserService;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomOAuth2UserService customOAuth2UserService;

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

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.httpBasic().disable()
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
                .userInfoEndpoint().userService(customOAuth2UserService);
    }
}
