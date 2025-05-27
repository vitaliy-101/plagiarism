package com.example.plagiarismapp.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.util.Comparator;

@UtilityClass
public class FileUtils {
    public static void deleteDirectory(File directory) throws IOException {
        if (directory == null || !directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    // Делаем файл доступным для записи
                    file.setWritable(true);
                    // Удаляем с задержкой
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
            }
        }
        // Делаем директорию доступной для записи
        directory.setWritable(true);
        if (!directory.delete()) {
            directory.deleteOnExit();
        }
    }
}
