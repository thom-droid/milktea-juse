package com.example.juse.config.test;

import com.example.juse.security.jwt.AppKeyConfig;
import com.example.juse.security.jwt.SecretKeyHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = AppKeyConfig.class)
@SpringBootTest(properties = "jwt.secret=${random.uuid}")
public class AppKeyConfigTest {

    @Autowired
    SecretKeyHolder secretKeyHolder;

    @Test
    public void JwtSecretKeyFinal() {
        String key = secretKeyHolder.getSecretKey();
        String key2 = secretKeyHolder.getSecretKey();

        Assertions.assertEquals(key, key2);
    }
}
