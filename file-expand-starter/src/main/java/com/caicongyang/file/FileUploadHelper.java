package com.caicongyang.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 通用文件上传工具
 */
public class FileUploadHelper {

    private static final Logger log = LoggerFactory.getLogger(FileUploadHelper.class);

    private final String baseDir;

    public FileUploadHelper(String baseDir) {
        this.baseDir = baseDir;
    }

    public String upload(MultipartFile file) throws IOException {
        return upload(file, null);
    }

    public String upload(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf('.'));
        }
        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;

        Path targetDir = Paths.get(baseDir);
        if (subDir != null && !subDir.isEmpty()) {
            targetDir = targetDir.resolve(subDir);
        }
        Files.createDirectories(targetDir);

        Path targetPath = targetDir.resolve(storedName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("文件上传成功: {} -> {}", originalName, targetPath);
        return targetPath.toString();
    }

    public boolean delete(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    public byte[] download(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
}
