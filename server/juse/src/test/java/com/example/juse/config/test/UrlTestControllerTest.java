package com.example.juse.config.test;

import com.example.juse.config.UrlTestController;
import com.example.juse.helper.resolver.uri.RequestURLArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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