package com.example.juse.config;

import com.example.juse.helper.resolver.uri.RequestURL;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@TestComponent
@Controller
public class UrlTestController {

    @GetMapping("/post-board-for-test")
    public String testPosting(@RequestURL String requestUrl) {
        return requestUrl;
    }

}
