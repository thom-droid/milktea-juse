package com.example.juse.tester;

import com.example.juse.helper.resolver.uri.RequestURL;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UrlTestController {

    @GetMapping("/post-board-for-test")
    public String testPosting(@RequestURL String requestUrl) {
        return requestUrl;
    }
}
