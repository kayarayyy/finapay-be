package com.bcaf.bcapay.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bcaf.bcapay.repositories.FileStorageRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageRepository {

    @Value("${base.url}")
    private String baseUrl;

    @Value("${upload.path.ktp}")
    private String ktpDir;

    @Value("${upload.path.selfie}")
    private String selfieDir;

    @Value("${upload.path.house}")
    private String houseDir;

    @Value("${server.servlet.context-path}")
    private String servlet;

    @Override
    public String saveImage(MultipartFile file, String filenamePrefix, String type) {
        String subDir;

        switch (type.toLowerCase()) {
            case "ktp":
                subDir = ktpDir;
                break;
            case "selfie":
                subDir = selfieDir;
                break;
            case "house":
                subDir = houseDir;
                break;
            default:
                throw new IllegalArgumentException("Tipe folder tidak dikenali: " + type);
        }

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File kosong.");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";

            String filename = filenamePrefix + extension;

            Path storagePath = Paths.get(subDir).toAbsolutePath().normalize();
            Files.createDirectories(storagePath);

            Path destination = storagePath.resolve(filename);
            file.transferTo(destination.toFile());

            // URL: http://localhost:8080/uploads/ktp/ktp_123.jpg
            return baseUrl + servlet + subDir.replace("\\", "/") + filename;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage(), e);
        }
    }
}
