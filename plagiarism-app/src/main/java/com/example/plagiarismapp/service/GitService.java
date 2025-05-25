package com.example.plagiarismapp.service;

import com.example.content.FileContent;
import com.example.content.Language;
import com.example.content.RepositoryContent;
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

    public RepositoryContent downloadRepository(String url, Language language) throws Exception {
        var tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX);

        try {
            var git = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(tempDir.toFile())
                    .call();
            git.close();

            var files = collectFiles(tempDir, language);

            return new RepositoryContent(
                    url,
                    extractRepoName(url),
                    extractRepoOwner(url),
                    files,
                    language
            );
        } finally {
            FileUtils.deleteDirectory(tempDir.toFile());
        }
    }

    private List<FileContent> collectFiles(Path rootDir, Language language) throws IOException {
        return Files.walk(rootDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(language.getFileExtension()))
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
