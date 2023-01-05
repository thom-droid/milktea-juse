package com.example.juse.tester;

import com.example.juse.helper.resolver.uri.RequestURLArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles({"testonly", "plain"})
class UrlTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(UrlTestController.class)
                .setCustomArgumentResolvers(new RequestURLArgumentResolver())
                .build();

    }

    @Test
    void whenRequest() throws Exception {

        String port = "http://localhost:8080";

        String url = port + "/post-board-for-test";

        String returnedUrl = mockMvc.perform(get(url)).andDo(print()).andReturn().getResponse().getForwardedUrl();

        assertEquals(url, returnedUrl);

    }
}