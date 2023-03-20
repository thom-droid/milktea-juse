package com.example.juse.helper.storage.config;

import software.amazon.awssdk.profiles.ProfileFile;

public interface StorageProfile {

    ProfileFile getProfileFile();

    String getProfilePath();
}
