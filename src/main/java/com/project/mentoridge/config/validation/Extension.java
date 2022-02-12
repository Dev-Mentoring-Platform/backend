package com.project.mentoridge.config.validation;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Objects;

@Constraint(validatedBy = Extension.ExtensionValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Extension {
    String message() default "파일 확장자가 기준에 맞지 않습니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String[] allows();

    public static class ExtensionValidator implements ConstraintValidator<Extension, MultipartFile> {

        private String[] allows;

        @Override
        public void initialize(Extension constraintAnnotation) {
            allows = constraintAnnotation.allows();
        }

        @Override
        public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
            if (Objects.isNull(file)) {
                return true;
            }
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            return Arrays.stream(allows).anyMatch(allow -> allow.equalsIgnoreCase(ext));
        }
    }

}
