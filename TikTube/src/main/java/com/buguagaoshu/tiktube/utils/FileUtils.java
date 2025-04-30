package com.buguagaoshu.tiktube.utils;

import com.buguagaoshu.tiktube.entity.FileTableEntity;
import com.buguagaoshu.tiktube.enums.FileStatusEnum;
import com.buguagaoshu.tiktube.enums.FileTypeEnum;
import org.springframework.context.annotation.FilterType;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2022-06-07 16:31
 */
public class FileUtils {
    public static FileTableEntity createFileTableEntity(String filename, String suffix,
                                                 String path, long size,
                                                 String originalFilename,
                                                 long userId, int type, int saveLocation) {
        FileTableEntity fileTableEntity = new FileTableEntity();
        fileTableEntity.setUploadTime(System.currentTimeMillis());
        if (saveLocation == 0) {
            fileTableEntity.setFileUrl("/api/upload/" + path + "/" + filename);
        } else {
            fileTableEntity.setFileUrl(FileTypeEnum.ossFileURL(saveLocation, path, filename));
        }

        fileTableEntity.setFileNewName(filename);
        fileTableEntity.setSize(size);
        fileTableEntity.setFileOriginalName(originalFilename);

        fileTableEntity.setType(type);
        fileTableEntity.setSuffixName(suffix);
        fileTableEntity.setUploadUserId(userId);
        fileTableEntity.setStatus(FileStatusEnum.NOT_USE_FILE.getCode());
        fileTableEntity.setSaveLocation(saveLocation);
        return fileTableEntity;
    }
}
