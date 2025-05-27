package com.example.plagiarismapp.service;

import com.example.content.FileContent;
import com.example.content.Language;
import com.example.content.RepositoryContent;
import com.example.plagiarismapp.exception.ProcessGitEcxeption;
import com.example.plagiarismapp.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.plagiarismapp.utils.GitUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitService {

    private static final String TEMP_DIR_PREFIX = "repo_";

    public Mono<RepositoryContent> downloadRepository(String url, Language language) {
        return Mono.fromCallable(() -> Files.createTempDirectory(TEMP_DIR_PREFIX))
                .flatMap(tempDir -> Mono.fromCallable(() -> {
                    var git = Git.cloneRepository()
                            .setURI(url)
                            .setDirectory(tempDir.toFile())
                            .call();

                    git.close();
                    return tempDir;
                }).flatMap(tempDirResult -> collectFiles(tempDirResult, language)
                        .collectList()
                        .map(files -> new RepositoryContent(
                                url,
                                extractRepoName(url),
                                extractRepoOwner(url),
                                files,
                                language
                        ))).doFinally(signalType -> {
                    try {
                        FileUtils.deleteDirectory(tempDir.toFile());
                        log.debug("Deleted temp directory: {}", tempDir);
                    } catch (IOException e) {
                        throw new ProcessGitEcxeption(e.getMessage());
                    }
                }));
    }

    private Flux<FileContent> collectFiles(Path rootDir, Language language) {
        return Flux.fromStream(() -> {
            try {
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
                        });
            } catch (IOException e) {
                throw new RuntimeException("Failed to walk through directory", e);
            }
        });
    }

}
