package com.dyw.util.FastDfs;

import com.dyw.util.FastDfs.work.FastDfsClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Devil
 * @create 2022-04-05 23:31
 */
public class FastDfsUtil {
    private final FastDfsClient fastDfsClient;

    public FastDfsUtil(String prefixFilePath) {
        this.fastDfsClient = new FastDfsClient(prefixFilePath);
    }

    /**
     * 上传文件
     * @param file 上传的文件
     * @return 文件路径
     * @throws IOException 异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        return fastDfsClient.uploadFile(file);
    }

    /**
     * 删除文件系统中的文件
     * @param FileURL 文件路径
     */
    public void deleteFile(String FileURL){
        fastDfsClient.deleteFile(FileURL);
    }

}
