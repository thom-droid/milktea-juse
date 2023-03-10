package com.example.juse.user.service;

import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
public class LocalStorageService extends AbstractStorageServiceImpl{

    private final Path rootLocation = Paths.get("C:\\images");
    private final Path resizeLocation = Paths.get("C:\\images\\resize\\");


    public String store(MultipartFile file){

        String savedName = createUniqueFileName(file.getOriginalFilename());

        String originFilename = Objects.requireNonNull(file.getOriginalFilename()).replaceAll(" ", "");
        String formatName = originFilename.substring(originFilename.lastIndexOf(".") + 1).toLowerCase();

        Path destinationFile = this.rootLocation.resolve(savedName).normalize().toAbsolutePath();

        String resizePath = resizeLocation  + "\\" + savedName;

        try (InputStream inputStream = file.getInputStream()) {

            InputStream inputStream1 = file.getInputStream();

            System.out.println("업로드한 확장자 타입 : " + file.getContentType());
            boolean isValid = validImgFile(inputStream);

                if(!isValid) {
                    System.out.println("이미지 파일만 업로드 가능합니다.");
                    throw new CustomRuntimeException(ExceptionCode.NOT_VALID_IMAGE_TYPE);
                }

                Files.copy(inputStream1, destinationFile, StandardCopyOption.REPLACE_EXISTING);

                imageResize(file, resizePath, formatName);

            } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
        return savedName;
    }

}
