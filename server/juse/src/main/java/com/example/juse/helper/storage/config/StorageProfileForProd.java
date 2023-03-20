package com.example.juse.helper.storage.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.profiles.ProfileFile;

import java.nio.file.Path;

@Profile("prod")
@Component
public class StorageProfileForProd implements StorageProfile {

    private final Path profilePath = Path.of("","home", "ec2-user", ".aws", "credentials");
    @Override
    public ProfileFile getProfileFile() {
        return ProfileFile.builder()
                .content(profilePath)
                .type(ProfileFile.Type.CREDENTIALS)
                .build();
    }

    @Override
    public String getProfilePath() {
        return profilePath.toAbsolutePath().toString();
    }
}
