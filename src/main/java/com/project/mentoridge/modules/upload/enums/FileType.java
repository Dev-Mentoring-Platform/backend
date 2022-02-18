package com.project.mentoridge.modules.upload.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.project.mentoridge.config.converter.enumConverter.EnumerableConverter;
import com.project.mentoridge.modules.base.Enumerable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType implements Enumerable {

    LECTURE_IMAGE("LECTURE_IMAGE", "강의 이미지");

    private String type;
    private String name;

    public static FileType find(String type) {
        return Enumerable.find(type, values());
    }

    @JsonCreator
    public static FileType findToNull(String type) {
        return Enumerable.findToNull(type, values());
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter extends EnumerableConverter<FileType> {
        public Converter() {
            super(FileType.class);
        }
    }
}
