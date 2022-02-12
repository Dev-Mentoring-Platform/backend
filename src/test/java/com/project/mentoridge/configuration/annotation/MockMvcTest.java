package com.project.mentoridge.configuration.annotation;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AutoConfigureMockMvc(addFilters = false)
// @SpringBootTest(properties = {"spring.config.location=classpath:application-test.yml"})
@SpringBootTest
public @interface MockMvcTest {
}
