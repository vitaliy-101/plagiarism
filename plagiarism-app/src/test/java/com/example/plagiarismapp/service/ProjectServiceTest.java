package com.example.plagiarismapp.service;

import com.example.content.*;
import com.example.plagiarismapp.dto.request.project.ProjectCreateRequest;
import com.example.plagiarismapp.entity.*;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.FileProjectRepository;
import com.example.plagiarismapp.repository.RepositoryProjectRepository;
import org.example.service.CoreServiceReactive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest extends BaseServiceTest {

    @Mock
    private GitService gitService;

    @InjectMocks
    private ProjectService projectService;

    @InjectMocks
    private CoreServiceReactive coreService;

    @Mock
    private RepositoryProjectRepository repositoryProjectRepository;
    
    @Mock
    private FileProjectRepository fileProjectRepository;

    private ProjectCreateRequest createRequest;
    private RepositoryContent repositoryContent;
    private FileContent fileContent;
    private Project testProject;
    private User testUser;

    @BeforeEach
    void setUp() {
        createRequest = new ProjectCreateRequest();
        createRequest.setName("Test Project");
        createRequest.setUserId(TEST_USER_ID);
        createRequest.setLanguage(Language.JAVA);
        createRequest.setRepositoryUrls(List.of("https://github.com/test/repo1"));

        fileContent = new FileContent("src/main/java/Test.java", "Test.java", "public class Test {}");
        repositoryContent = new RepositoryContent(
                "https://github.com/test/repo1",
                "repo1",
                "test",
                List.of(fileContent),
                Language.JAVA
        );

        testProject = createTestProject();
        testProject.setId(TEST_PROJECT_ID);
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setName("testuser");
    }

    @Test
    void downloadProject_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.downloadProject(createRequest))
                .expectError(NotFoundByIdException.class)
                .verify();
    }

    @Test
    void getProject_WhenProjectExistsAndBelongsToUser_ShouldReturnProject() {
        when(projectRepository.findById(TEST_PROJECT_ID)).thenReturn(Mono.just(testProject));

        StepVerifier.create(projectService.getProject(TEST_USER_ID, TEST_PROJECT_ID))
                .expectNext(testProject)
                .verifyComplete();
    }

    @Test
    void getProject_WhenProjectDoesNotExist_ShouldThrowException() {
        when(projectRepository.findById(TEST_PROJECT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(projectService.getProject(TEST_USER_ID, TEST_PROJECT_ID))
                .expectError(NotFoundByIdException.class)
                .verify();
    }

    @Test
    void getProject_WhenProjectBelongsToDifferentUser_ShouldThrowException() {
        when(projectRepository.findById(TEST_PROJECT_ID)).thenReturn(Mono.just(testProject));

        StepVerifier.create(projectService.getProject(999L, TEST_PROJECT_ID))
                .expectError(NotFoundResourceByIdException.class)
                .verify();
    }

}
