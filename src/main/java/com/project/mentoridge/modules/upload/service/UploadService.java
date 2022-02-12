package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.response.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    UploadResponse uploadImage(String dir, MultipartFile file);
}
