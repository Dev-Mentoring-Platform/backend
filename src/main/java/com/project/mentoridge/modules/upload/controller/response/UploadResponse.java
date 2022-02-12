package com.project.mentoridge.modules.upload.controller.response;

import lombok.Data;

@Data
public class UploadResponse {

    private FileResponse file;
    private String url;

    public UploadResponse(FileResponse file, String url) {
        this.file = file;
        this.url = url;
    }
}
