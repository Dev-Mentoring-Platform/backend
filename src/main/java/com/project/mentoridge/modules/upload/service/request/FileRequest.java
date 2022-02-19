package com.project.mentoridge.modules.upload.service.request;

import com.project.mentoridge.modules.upload.enums.FileType;
import com.project.mentoridge.modules.upload.vo.File;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileRequest {

    private String uuid;
    private String name;
    private String contentType;
    private FileType type;
    private long size;

    @Builder(access = AccessLevel.PUBLIC)
    private FileRequest(String uuid, String name, String contentType, FileType type, long size) {
        this.uuid = uuid;
        this.name = name;
        this.contentType = contentType;
        this.type = type;
        this.size = size;
    }

    public File toEntity() {
        return File.builder()
                .uuid(uuid)
                .name(name)
                .contentType(contentType)
                .type(type)
                .size(size)
                .build();
    }

}
