package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.response.FileResponse;
import com.project.mentoridge.modules.upload.service.request.FileRequest;
import com.project.mentoridge.modules.upload.vo.File;

public interface FileService {

    File createFile(FileRequest fileRequest);
    FileResponse getFile(String uuid);
}
