package com.project.mentoridge.modules.upload.service;

import com.project.mentoridge.modules.upload.controller.response.FileResponse;
import com.project.mentoridge.modules.upload.respository.FileRepository;
import com.project.mentoridge.modules.upload.service.request.FileRequest;
import com.project.mentoridge.modules.upload.vo.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public FileResponse createFile(FileRequest fileRequest) {
        File file = fileRequest.toEntity();
        return new FileResponse(fileRepository.save(file));
    }

    @Transactional(readOnly = true)
    @Override
    public FileResponse getFile(String uuid) {
        File file = fileRepository.findByUuid(uuid);
        return new FileResponse(file);
    }
}
