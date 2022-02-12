package com.project.mentoridge.config;

import com.project.mentoridge.modules.account.vo.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

// https://www.baeldung.com/spring-boot-swagger-jwt
// https://www.programcreek.com/java-api-examples/?api=springfox.documentation.builders.ParameterBuilder
@Profile("dev")
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {

        ParameterBuilder parameterBuilder = new ParameterBuilder();
        parameterBuilder.name("Authorization")
                .description("Access Token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(parameterBuilder.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(User.class)
                .apiInfo(ApiInfo.DEFAULT)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
    }
}
