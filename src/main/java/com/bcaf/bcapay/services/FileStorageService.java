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
public class FileStorageService implements FileStorageRepository {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveImage(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File kosong.");
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + fileExtension;

            Path storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(storagePath);

            Path destination = storagePath.resolve(filename);
            file.transferTo(destination.toFile());

            return filename; // bisa juga return path lengkap: destination.toString()
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file: " + e.getMessage(), e);
        }
    }
}
