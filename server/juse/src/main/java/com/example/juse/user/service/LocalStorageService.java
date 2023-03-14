package com.example.juse.user.service;

import com.example.juse.helper.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

        String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll(" ", "");
        String savedName = StringUtils.createUniqueAndRegulatedFileName(originalFileName);
        String formatName = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

        Path destinationFile = this.rootLocation.resolve(savedName).normalize().toAbsolutePath();

        String resizePath = resizeLocation  + "\\" + savedName;

        try (InputStream inputStream = file.getInputStream()) {
            validateImageExtension(file);

            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            imageResize(file, resizePath, formatName);

            } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
        return savedName;
    }

    protected void imageResize(MultipartFile file, String resizePath, String formatName) {

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage inputImage = ImageIO.read(inputStream);
            int originWidth = inputImage.getWidth();
            int originHeight = inputImage.getHeight();
            int newWidth = 500;

            if (originWidth > newWidth) {
                int newHeight = (originHeight * newWidth) / originWidth;

                Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
                BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                Graphics graphics = newImage.getGraphics();
                graphics.drawImage(resizeImage, 0, 0, null);
                graphics.dispose();

                File newFile = new File(resizePath);
                ImageIO.write(newImage, formatName, newFile);
            } else {
                file.transferTo(new java.io.File(resizePath));
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
