package com.example.juse.user.repository;

import com.example.juse.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public abstract class UserRepositoryImpl implements UserRepository {

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return Optional.empty();
    }
}
