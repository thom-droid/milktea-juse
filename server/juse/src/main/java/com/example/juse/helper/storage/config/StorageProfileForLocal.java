package com.example.juse.helper.storage.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileLocation;

@Profile("local")
@Component
public class StorageProfileForLocal implements StorageProfile{

    @Override
    public ProfileFile getProfileFile() {
        return ProfileFile.defaultProfileFile();
    }

    @Override
    public String getProfilePath() {
        return ProfileFileLocation.credentialsFilePath().toString();
    }
}
