package com.example.plagiarismapp.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GitUtils {
    public static String extractRepoName(String url) {
        var cleanedUrl = url.replace(".git", "");
        return cleanedUrl.substring(cleanedUrl.lastIndexOf('/') + 1);
    }

    public static String extractFilename(String fullFilename) {
        return fullFilename.replaceAll(".*[/\\\\]", "");
    }

    public static String extractRepoOwner(String url) {
        var normalized = url.replace(".git", "");
        String[] parts;

        if (normalized.contains("github.com")) {
            parts = normalized.split("github.com[/:]")[1].split("/");
        } else {
            parts = normalized.split("[/:]");
        }

        return parts.length >= 1 ? parts[parts.length - 2] : "unknown";
    }
}
