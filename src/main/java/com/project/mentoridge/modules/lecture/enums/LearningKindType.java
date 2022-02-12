package com.project.mentoridge.modules.lecture.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LearningKindType {

    IT(1L, "IT", "IT"),
    LANGUAGE(2L, "LANGUAGE", "언어");

    private Long id;
    private String type;
    private String name;
}
