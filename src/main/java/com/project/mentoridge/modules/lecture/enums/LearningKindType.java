package com.project.mentoridge.modules.lecture.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LearningKindType {

    IT("IT", "IT");
    // LANGUAGE("LANGUAGE", "언어");

    // private Long id;
    private String type;
    private String name;
}
