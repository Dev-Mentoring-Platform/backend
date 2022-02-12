package com.project.mentoridge.configuration;

import org.junit.jupiter.api.BeforeEach;

import javax.validation.Validation;
import javax.validation.Validator;

public class AbstractRequestTest {

    protected Validator validator;

    @BeforeEach
    public void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

}
