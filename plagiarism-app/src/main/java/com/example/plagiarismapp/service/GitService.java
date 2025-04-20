package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.FileContent;
import com.example.plagiarismapp.dto.RepositoryContent;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.example.plagiarismapp.utils.GitUtils.*;

@Service
@RequiredArgsConstructor
public class GitService {
    private static final String TEMP_DIR_PREFIX = "repo_";

    public RepositoryContent downloadRepository(String url, String fileExtension) throws Exception {
        // 1. Создаем временную директорию
        var tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX);

        try {
            // 2. Клонируем репозиторий с помощью JGit
            var git = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(tempDir.toFile())
                    .call();

            // 3. Закрываем Git-ресурс
            git.close();

            // 4. Собираем все файлы с нужным расширением
            List<FileContent> files = collectFiles(tempDir, fileExtension);

            return new RepositoryContent(
                    url,
                    extractRepoName(url),
                    extractRepoOwner(url),
                    files
            );
        } finally {
            // 5. Удаляем временную директорию
            FileUtils.deleteDirectory(tempDir.toFile());
        }
    }

    private List<FileContent> collectFiles(Path rootDir, String fileExtension) throws IOException {
        return Files.walk(rootDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(fileExtension))
                .map(path -> {
                    try {
                        return new FileContent(
                                rootDir.relativize(path).toString(),
                                extractFilename(rootDir.relativize(path).toString()),
                                Files.readString(path)
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read file: " + path, e);
                    }
                })
                .toList();
    }

}
