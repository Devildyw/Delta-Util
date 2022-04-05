package com.dyw.util.FastDfs.work;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Devil
 * @create 2022-04-03 23:09
 */
public class FastDfsClient {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    //上传文件文件路径
    private String prefixFilePath;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        StorePath storePath = fastFileStorageClient.uploadFile(multipartFile.getInputStream(), multipartFile.getSize(), FilenameUtils.getExtension(multipartFile.getOriginalFilename()), null);
        return prefixFilePath + storePath.getFullPath();
    }

    public FastDfsClient() {
    }

    public FastDfsClient(String prefixFilePath) {
        this.prefixFilePath = prefixFilePath;
    }

    //删除文件
    public void deleteFile(String FileURL) {
        if (StringUtils.isBlank(FileURL)) {
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(FileURL);
            fastFileStorageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            e.printStackTrace();
            throw new RuntimeException("文件删除失败");
        }
    }
}
