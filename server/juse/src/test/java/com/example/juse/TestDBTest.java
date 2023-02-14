package com.example.juse;

import com.example.juse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestDBInstance.class)
public class TestDBTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void test() {

        assertDoesNotThrow(() -> userRepository.findById(1L).orElseThrow());

    }
}
