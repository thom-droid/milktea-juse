package com.example.juse.unit;

import com.example.juse.helper.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UriBuildTest {

    @Test
    public void givenMockMultiPartFile_thenFileNameReturned() {

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "image", MediaType.IMAGE_JPEG_VALUE, getRandomByte(5));

        //expected
        String expected = UUID.randomUUID()+"-"+mockMultipartFile.getOriginalFilename();
        String actual = StringUtils.createUniqueAndRegulatedFileName(mockMultipartFile.getOriginalFilename());
        System.out.println(actual);
        assertEquals(expected.length(), actual.length());
        assertEquals(expected.indexOf("image"), actual.indexOf("image"));

    }

    @Test
    public void givenUri_thenLocationReturned() {
        String root = "chicken-milktea-juse.com";
        String fileName = "image";
        String arg1 = "icons";
        String arg2 = "user";

        String expected = "https://chicken-milktea-juse.com/icons/user/image";
        String actual = StringUtils.buildLocation(root, fileName, arg1, arg2);

        assertEquals(expected, actual);
    }

    @Test
    public void givenString_thenRegulatedReturn() {
        String string = "pa@rad-ise (1).png";
        String expected = "paradise1.png";
        String before = StringUtils.createUniqueAndRegulatedFileName(string);
        String actual = before.substring(before.lastIndexOf("-")+1);

        assertEquals(expected, actual);
    }

    private static byte[] getRandomByte(int size) {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return b;
    }
}
