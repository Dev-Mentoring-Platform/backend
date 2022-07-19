package com.project.mentoridge.configuration.annotation;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Disabled
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DataJpaTest(properties = {"spring.config.location=classpath:application-test.yml"})
public @interface RepositoryTest {
}
