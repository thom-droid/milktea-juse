package com.example.juse.user.service;

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

    protected boolean validImgFile(InputStream inputStream) {

        try {

            String mimeType = TIKA.detect(inputStream);

            boolean isValid = IMAGE_TYPE_LIST.stream()
                    .anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));

            return isValid;

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    public void imageResize(MultipartFile file, String resizePath, String formatName) throws IOException {
        BufferedImage inputImage = ImageIO.read(file.getInputStream());

        int originWidth = inputImage.getWidth();
        System.out.println("originWidth = " + originWidth);
        int originHeight = inputImage.getHeight();
        System.out.println("originHeight = " + originHeight);

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
        }
        else {
            file.transferTo(new java.io.File(resizePath));
        }

    }
}
