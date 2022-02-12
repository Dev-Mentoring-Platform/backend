package com.project.mentoridge.modules.upload.controller.response;

import com.project.mentoridge.modules.upload.vo.File;
import lombok.Data;

@Data
public class FileResponse {

    private Long id;
    private String uuid;
    private String name;
    private String contentType;
    private String type;
    private Long size;

    public FileResponse(File file) {
        this.id = file.getId();
        this.uuid = file.getUuid();
        this.name = file.getName();
        this.contentType = file.getContentType();
        this.type = file.getType().getType();
        this.size = file.getSize();
    }
}
