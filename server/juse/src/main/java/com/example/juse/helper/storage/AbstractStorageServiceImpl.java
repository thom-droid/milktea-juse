package com.example.juse.helper.storage;

import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractStorageServiceImpl implements StorageService {

    private static final Tika TIKA = new Tika();
    private static final List<String> IMAGE_TYPE_LIST = Arrays.asList(
            "image/jpeg", "image/pjpeg", "image/png",
            "image/gif", "image/bmp", "image/x-windows-bmp");


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
