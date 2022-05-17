package com.project.mentoridge.config;

import com.project.mentoridge.config.converter.enumConverter.EnumerableConverterFactory;
import com.project.mentoridge.config.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER_ACCESS_TOKEN;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER_REFRESH_TOKEN;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Bean
    public EnumerableConverterFactory getEnumerableConverterFactory() {
        return new EnumerableConverterFactory();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(getEnumerableConverterFactory());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }

    /**
     * CORS 이슈 해결.
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //TODO 매핑설정 세분화해야함.
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                //.allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .maxAge(3600L)
                .exposedHeaders(HEADER_ACCESS_TOKEN, HEADER_REFRESH_TOKEN)
                .allowCredentials(true);
    }

}

