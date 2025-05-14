package com.bcaf.bcapay.repositories;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageRepository {
    String saveImage(MultipartFile file);
}
