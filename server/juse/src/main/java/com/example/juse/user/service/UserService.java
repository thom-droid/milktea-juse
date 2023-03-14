package com.example.juse.user.service;

import com.example.juse.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    User getJuse(long userId);

    User getProfile(long userId);

    User update(User mappedObj, MultipartFile profileImg) throws IOException;

    void deleteAccount(long userId);

    User verifyUserById(long userId);

    boolean isNicknameAvailable(String nickname);

    User createUser(User user, MultipartFile profileImg) throws IOException;

    void validateIdentity(long requestUserId, long actualUserId);
}
