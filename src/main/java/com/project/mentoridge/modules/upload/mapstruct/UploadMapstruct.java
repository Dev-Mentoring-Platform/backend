package com.project.mentoridge.modules.upload.mapstruct;

import com.project.mentoridge.config.mapstruct.MapstructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface UploadMapstruct {

//    @Mappings({
//            @Mapping(target = "result", source = "fileResponse"),
//            @Mapping(target = "url", source = "url")
//    })
//    UploadResponse fileToUploadResponse(FileResponse fileResponse, String url);
}
