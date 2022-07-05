package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.enums.SystemType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SystemTypeResponse {

    private String type;
    private String name;

    public SystemTypeResponse(SystemType systemType) {
        this.type = systemType.getType();
        this.name = systemType.getName();
    }
}
