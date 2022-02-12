package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.response.FileResponse;
import com.project.mentoridge.modules.upload.service.request.FileRequest;

public interface FileService {

    FileResponse createFile(FileRequest fileRequest);
    FileResponse getFile(String uuid);
}
