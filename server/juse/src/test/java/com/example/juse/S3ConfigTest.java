package com.example.juse;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
public class S3ConfigTest {


    @Test
    public void S3ClientConfigTest() {

        // get the default profile as configured in the credentials file.
        ProfileCredentialsProvider provider = ProfileCredentialsProvider.create();

        String accessKey = provider.resolveCredentials().accessKeyId();
        String secretAccessKey = provider.resolveCredentials().secretAccessKey();

        assertNotNull(accessKey);
        assertNotNull(secretAccessKey);

    }
}
