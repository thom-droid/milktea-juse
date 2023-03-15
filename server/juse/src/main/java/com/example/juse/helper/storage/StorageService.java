package com.example.juse.helper.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile multipartFile) throws IOException;



}
