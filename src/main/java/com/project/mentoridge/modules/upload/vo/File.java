package com.project.mentoridge.modules.upload.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.upload.enums.FileType;
import lombok.*;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "file_id"))
@Table(name = "mentoridge_file")
@Entity
public class File extends BaseEntity {

    @Column(unique = true)
    private String uuid;

    @Column(length = 50)
    private FileType type;

    private String name;
    private String contentType;
    private Long size;

    @Builder(access = PRIVATE)
    private File(String uuid, FileType type, String name, String contentType, Long size) {
        this.uuid = uuid;
        this.type = type;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
    }

    public static File of(String uuid, FileType type, String name, String contentType, Long size) {
        return File.builder()
                .uuid(uuid)
                .type(type)
                .name(name)
                .contentType(contentType)
                .size(size)
                .build();
    }
}
