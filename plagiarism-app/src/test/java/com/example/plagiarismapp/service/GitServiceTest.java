package com.example.plagiarismapp.service;

import com.example.content.Language;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GitServiceTest {

    @InjectMocks
    private GitService gitService;

    @Mock
    private Git git;

    @TempDir
    Path tempDir;

    private final String testRepoUrl = "https://github.com/test/test-repo";
    private final Language testLanguage = Language.JAVA;


    
    @Test
    void collectFiles_ShouldReturnOnlyFilesWithMatchingExtension() throws IOException {

        Path srcDir = tempDir.resolve("src/main/java");
        Files.createDirectories(srcDir);

        File javaFile = srcDir.resolve("Test.java").toFile();
        javaFile.createNewFile();

        File textFile = srcDir.resolve("README.txt").toFile();
        textFile.createNewFile();

        Path subDir = srcDir.resolve("subpackage");
        Files.createDirectories(subDir);
        File subJavaFile = subDir.resolve("SubTest.java").toFile();
        subJavaFile.createNewFile();

        GitService gitService = new GitService();

        StepVerifier.create(gitService.collectFiles(tempDir, Language.JAVA).collectList())
                .assertNext(files -> {
                    assertThat(files).hasSize(2);
                })
                .verifyComplete();
    }

}
