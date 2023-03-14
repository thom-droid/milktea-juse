package com.example.juse.user.service;

import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import com.example.juse.tag.entity.Tag;
import com.example.juse.tag.entity.UserTag;
import com.example.juse.tag.service.TagService;
import com.example.juse.tag.service.UserTagService;
import com.example.juse.user.entity.User;
import com.example.juse.user.mapper.UserMapper;
import com.example.juse.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TagService tagService;
    private final UserTagService userTagService;
    private final UserMapper userMapper;
    private final StorageService storageService;

    @Override
    public User getJuse(long userId) {
        return verifyUserById(userId);
    }

    @Override
    public User getProfile(long userId) {
        return verifyUserById(userId);
        
    }

    @Override
    public User update(User mappedObj, MultipartFile profileImg) throws IOException {

        User user = verifyUserById(mappedObj.getId());
        userMapper.updateEntityFromSource(user, mappedObj);
        uploadImage(user, profileImg);
        updateUserTag(user, mappedObj);

        return userRepository.save(user);
    }

    @Override
    public void deleteAccount(long userId) {

        verifyUserById(userId);

        userRepository.deleteById(userId);

    }

    @Override
    public User createUser(User user, MultipartFile profileImg) throws IOException {

        String socialUserEmail = user.getSocialUser().getEmail();
        checkDuplicated(socialUserEmail);
        setUserTag(user);
        uploadImage(user, profileImg);

        //Todo : compress and set limit to the size of the image

        return userRepository.save(user);
    }

    @Override
    public User verifyUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> {
                    throw new CustomRuntimeException(ExceptionCode.USER_NOT_FOUND);
                }
        );
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    @Override
    public void validateIdentity(long requestUserId, long actualUserId) {
        if(requestUserId != actualUserId)
            throw new CustomRuntimeException(ExceptionCode.USER_NOT_MATCHED);
    }

    public void checkDuplicated(String socialUserEmail) {
        if (isDuplicatedBy(socialUserEmail)) {
            throw new CustomRuntimeException(ExceptionCode.USER_ALREADY_EXIST);
        }
    }

    private boolean isDuplicatedBy(String socialUserEmail) {
        return userRepository.findByEmail(socialUserEmail) != null;
    }

    private void setUserTag(User user) {
        if (!user.getUserTagList().isEmpty()) {
            user.getUserTagList().forEach(
                    userTag -> {
                        String tagName = userTag.getTag().getName();
                        Tag tag = tagService.findByName(tagName);
                        userTag.addUser(user);
                        userTag.addTag(tag);
                    }
            );
        }
    }

    private void updateUserTag(User user, User mappedObj) {

        if (!mappedObj.getUserTagList().isEmpty()) {
            List<UserTag> list = mappedObj.getUserTagList().stream()
                    .map(
                            userTag -> {
                                UserTag userTagEntity =
                                        userTagService.findMappedUserTagOrCreate(userTag, user);

                                return userTagService.create(userTagEntity);
                            }
                    ).collect(Collectors.toList());

            List<UserTag> difference =
                    user.getUserTagList().stream()
                            .filter(userTag -> !list.contains(userTag))
                            .collect(Collectors.toList());

            user.getUserTagList().removeAll(difference);
        }

    }

    private void uploadImage(User user, MultipartFile file) {
        if(file != null) {
            try {
                String savedPath = storageService.store(file);
                user.setImg(savedPath);
            } catch (IOException | S3Exception ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
