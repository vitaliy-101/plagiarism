package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.response.file.SuspiciousFileResponse;
import com.example.plagiarismapp.entity.*;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.FileProjectRepository;
import com.example.plagiarismapp.repository.MatchRepository;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.RepositoryProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RepositoryProjectRepository repositoryProjectRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private FileProjectRepository fileRepository;

    @InjectMocks
    private RepositoryService repositoryService;

    private final Long testProjectId = 1L;
    private final Long testFirstRepoId = 1L;
    private final Long testSecondRepoId = 2L;
    private final Long testFileId = 1L;
    private Project testProject;
    private RepositoryProject testFirstRepo;
    private RepositoryProject testSecondRepo;
    private FileProject testFile;
    private Match testMatch;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(testProjectId);
        testProject.setName("Test Project");

        testFirstRepo = new RepositoryProject();
        testFirstRepo.setId(testFirstRepoId);
        testFirstRepo.setProjectId(testProjectId);
        testFirstRepo.setName("first-repo");

        testSecondRepo = new RepositoryProject();
        testSecondRepo.setId(testSecondRepoId);
        testSecondRepo.setProjectId(testProjectId);
        testSecondRepo.setName("second-repo");

        testFile = new FileProject();
        testFile.setId(testFileId);
        testFile.setFilename("TestFile.java");
        testFile.setRepositoryId(testFirstRepoId);

        testMatch = new Match();
        testMatch.setId(1L);
        testMatch.setFirstFileId(testFileId);
        testMatch.setFirstRepositoryId(testFirstRepoId);
        testMatch.setSecondRepositoryId(testSecondRepoId);
        testMatch.setProjectId(testProjectId);
    }


    @Test
    void getSuspiciousFiles_ShouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<List<SuspiciousFileResponse>> result = repositoryService
                .getSuspiciousFiles(999L, testFirstRepoId, testSecondRepoId);

        StepVerifier.create(result)
                .expectError(NotFoundByIdException.class)
                .verify();

        verify(projectRepository).findById(999L);
        verifyNoMoreInteractions(repositoryProjectRepository, matchRepository, fileRepository);
    }

    @Test
    void deleteRepository_ShouldDeleteWhenRepositoryExists() {
        when(repositoryProjectRepository.findById(testFirstRepoId)).thenReturn(Mono.just(testFirstRepo));
        when(projectRepository.findById(testProjectId)).thenReturn(Mono.just(testProject));
        when(repositoryProjectRepository.delete(any(RepositoryProject.class))).thenReturn(Mono.empty());

        Mono<Void> result = repositoryService.deleteRepository(testProjectId, testFirstRepoId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(repositoryProjectRepository).findById(testFirstRepoId);
        verify(projectRepository).findById(testProjectId);
        verify(repositoryProjectRepository).delete(testFirstRepo);
    }

    @Test
    void deleteRepository_ShouldThrowWhenRepositoryNotFound() {
        when(repositoryProjectRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = repositoryService.deleteRepository(testProjectId, 999L);

        StepVerifier.create(result)
                .expectError(NotFoundByIdException.class)
                .verify();

        verify(repositoryProjectRepository).findById(999L);
        verifyNoMoreInteractions(projectRepository);
        verify(repositoryProjectRepository, never()).delete(any());
    }

    @Test
    void deleteRepository_ShouldThrowWhenProjectNotOwnsRepository() {
        Long otherProjectId = 999L;
        testFirstRepo.setProjectId(otherProjectId);
        when(repositoryProjectRepository.findById(testFirstRepoId)).thenReturn(Mono.just(testFirstRepo));
        when(projectRepository.findById(testProjectId)).thenReturn(Mono.just(testProject));

        Mono<Void> result = repositoryService.deleteRepository(testProjectId, testFirstRepoId);
        StepVerifier.create(result)
                .expectError(NotFoundResourceByIdException.class)
                .verify();

        verify(repositoryProjectRepository).findById(testFirstRepoId);
        verify(projectRepository).findById(testProjectId);
        verify(repositoryProjectRepository, never()).delete(any());
    }
}
