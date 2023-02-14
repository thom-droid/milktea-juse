package com.example.juse;

import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.security.jwt.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test"})
@Import(TestDBInstance.class)
@SpringBootTest
public class JuseApplicationTests {

    protected String accessToken;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void initJwtToken() {

        TokenDto token = jwtTokenProvider.generateToken("test2@gmail.com", "ROLE_MEMBER");
        accessToken = token.getAccessToken();

    }

    public void setTokenAsBoardWriter() {

        TokenDto token = jwtTokenProvider.generateToken("test1@gmail.com", "ROLE_MEMBER");
        accessToken = token.getAccessToken();
    }
}
