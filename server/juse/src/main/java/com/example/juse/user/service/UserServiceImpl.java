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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

        if(profileImg != null) {
            String savedName = storageService.store(profileImg);

            // profile 이미지를 uri 형식으로 전송
            String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("images/resize/")
                    .path(savedName)
                    .toUriString();

            user.setImg(uri);

            String myUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("images/")
                    .path(savedName)
                    .toUriString();

            user.setMyImg(myUri);
        }

        long userId = mappedObj.getSocialUser().getId();

        if (user.getSocialUser().getId() != userId) {
            throw new CustomRuntimeException(ExceptionCode.USER_NOT_MATCHED);
        }

        userMapper.updateEntityFromSource(user, mappedObj);

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

        if (isDuplicatedBy(socialUserEmail)) {
            throw new CustomRuntimeException(ExceptionCode.USER_ALREADY_EXIST);
        }

        if (!user.getUserTagList().isEmpty()) {
            setUserTag(user);
        }

        if (profileImg != null) {
            String savedPath = storageService.store(profileImg);
            user.setImg(savedPath);

            //Todo : compress and set limit to the size of the image

        }

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

    public boolean isDuplicatedBy(String socialUserEmail) {
        return userRepository.findByEmail(socialUserEmail) != null;
    }

    private void setUserTag(User user) {
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
