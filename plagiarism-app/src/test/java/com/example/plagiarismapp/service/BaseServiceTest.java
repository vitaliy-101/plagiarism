package com.example.plagiarismapp.service;

import com.example.plagiarismapp.entity.*;
import com.example.plagiarismapp.entity.status.ProjectStatus;
import com.example.plagiarismapp.repository.*;
import org.example.service.CoreServiceReactive;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {
    
    @Mock
    protected FileProjectRepository fileRepository;
    
    @Mock
    protected RepositoryProjectRepository repositoryRepository;
    
    @Mock
    protected MatchRepository matchRepository;
    
    @Mock
    protected TileRepository tileRepository;
    
    @Mock
    protected ProjectRepository projectRepository;
    
    @Mock
    protected UserRepository userRepository;
    
    @Mock
    protected StatisticRepository statisticRepository;
    
    @Mock
    protected CoreServiceReactive coreServiceReactive;
    
    protected final Long TEST_REPOSITORY_ID = 1L;
    protected final Long TEST_FILE_ID = 1L;
    protected final Long TEST_USER_ID = 1L;
    protected final Long TEST_PROJECT_ID = 1L;
    
    protected FileProject createTestFileProject() {
        FileProject file = new FileProject();
        file.setId(TEST_FILE_ID);
        file.setRepositoryId(TEST_REPOSITORY_ID);
        file.setFullFilename("test/file.txt");
        file.setContent("test content");
        return file;
    }
    
    protected RepositoryProject createTestRepositoryProject() {
        RepositoryProject repo = new RepositoryProject();
        repo.setId(TEST_REPOSITORY_ID);
        repo.setProjectId(TEST_PROJECT_ID);
        repo.setName("test-repo");
        repo.setUrl("https://github.com/test/test-repo");
        return repo;
    }
    
    protected Project createTestProject() {
        Project project = new Project();
        project.setId(TEST_PROJECT_ID);
        project.setUserId(TEST_USER_ID);
        project.setName("Test Project");
        project.setStatus(ProjectStatus.NOT_ANALYZED);
        return project;
    }
}
