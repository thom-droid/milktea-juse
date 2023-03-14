package com.example.juse.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile multipartFile) throws IOException;



}
