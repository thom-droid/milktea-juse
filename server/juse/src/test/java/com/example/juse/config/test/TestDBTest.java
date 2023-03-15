package com.example.juse.config.test;

import com.example.juse.config.TestDBInstance;
import com.example.juse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@Import(TestDBInstance.class)
@SpringBootTest
public class TestDBTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void test() {

        assertDoesNotThrow(() -> userRepository.findById(1L).orElseThrow());

    }
}
