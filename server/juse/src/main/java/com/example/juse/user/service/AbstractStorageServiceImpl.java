package com.example.juse.user.service;

import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractStorageServiceImpl implements StorageService {

    private static final Tika TIKA = new Tika();
    private static final List<String> IMAGE_TYPE_LIST = Arrays.asList(
            "image/jpeg", "image/pjpeg", "image/png",
            "image/gif", "image/bmp", "image/x-windows-bmp");

    @Override
    public String createUniqueFileName(String fileName) {
        return UUID.randomUUID() + "-" + fileName;
    }

    protected void validateImageExtension(MultipartFile file) {
        String mimeType = detectMimeType(file);
        boolean valid = IMAGE_TYPE_LIST.stream()
                .anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));
        if (!valid) {
            throw new CustomRuntimeException(ExceptionCode.NOT_VALID_IMAGE_TYPE);
        }
    }

    private String detectMimeType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return TIKA.detect(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
