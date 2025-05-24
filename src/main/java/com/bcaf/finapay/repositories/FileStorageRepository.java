package com.bcaf.finapay.repositories;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageRepository {
    String saveImage(MultipartFile file, String filenamePrefix, String type);
}
