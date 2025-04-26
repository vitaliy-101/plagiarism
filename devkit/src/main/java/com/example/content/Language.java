package com.example.content;

import lombok.Getter;

@Getter
public enum Language {
    JAVA(".java"),
    PY(".py"),
    CPP(".cpp"),
    GO(".go"),
    JS(".js"),
    KOTLIN(".kt");

    private final String fileExtension;

    Language(String fileExtension) {
        this.fileExtension = fileExtension;
    }

}