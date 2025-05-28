package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.response.match.MatchResponse;
import com.example.plagiarismapp.dto.response.match.TileResponse;
import com.example.plagiarismapp.entity.*;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest extends BaseServiceTest {

    @InjectMocks
    private FileService fileService;

    private FileProject testFile;
    private RepositoryProject testRepository;
    private Match testMatch;
    private Tile testTile;

    @BeforeEach
    void setUp() {
        testFile = createTestFileProject();
        testRepository = createTestRepositoryProject();
        
        testMatch = new Match();
        testMatch.setId(1L);
        testMatch.setFirstFileId(testFile.getId());
        testMatch.setSecondFileId(2L);
        testMatch.setFirstRepositoryId(testRepository.getId());
        testMatch.setSecondRepositoryId(2L);
        testMatch.setPercentage(0.8);
        
        testTile = new Tile();
        testTile.setId(1L);
        testTile.setMatchId(testMatch.getId());
        testTile.setTextInFirstFile("test");
        testTile.setTextInSecondFile("test");
        testTile.setTextBeforeContextInFirstFile("before ");
        testTile.setTextAfterContextInFirstFile(" after");
        testTile.setTextBeforeContextInSecondFile("before ");
        testTile.setTextAfterContextInSecondFile(" after");
    }

    @Test
    void getFileProject_WhenFileAndRepositoryExist_ShouldReturnFile() {
        when(fileRepository.findById(anyLong())).thenReturn(Mono.just(testFile));
        when(repositoryRepository.findById(anyLong())).thenReturn(Mono.just(testRepository));

        StepVerifier.create(fileService.getFileProject(testRepository.getId(), testFile.getId()))
                .expectNext(testFile)
                .verifyComplete();

        verify(fileRepository).findById(testFile.getId());
        verify(repositoryRepository).findById(testRepository.getId());
    }

    @Test
    void getFileProject_WhenFileNotFound_ShouldThrowNotFoundException() {
        when(fileRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(fileService.getFileProject(testRepository.getId(), 999L))
                .expectError(NotFoundByIdException.class)
                .verify();
    }

    @Test
    void getFileProject_WhenRepositoryNotFound_ShouldThrowNotFoundException() {
        when(fileRepository.findById(anyLong())).thenReturn(Mono.just(testFile));
        when(repositoryRepository.findById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(fileService.getFileProject(999L, testFile.getId()))
                .expectError(NotFoundByIdException.class)
                .verify();
    }

    @Test
    void getFileProject_WhenFileNotInRepository_ShouldThrowResourceNotFoundException() {
        FileProject otherFile = createTestFileProject();
        otherFile.setRepositoryId(999L);
        
        when(fileRepository.findById(anyLong())).thenReturn(Mono.just(otherFile));
        when(repositoryRepository.findById(anyLong())).thenReturn(Mono.just(testRepository));

        StepVerifier.create(fileService.getFileProject(testRepository.getId(), otherFile.getId()))
                .expectError(NotFoundResourceByIdException.class)
                .verify();
    }

    @Test
    void getSuspiciousForFile_WhenValidInput_ShouldReturnMatches() {
        Long secondRepositoryId = 2L;
        Long secondFileId = 2L;
        
        when(fileRepository.findById(testFile.getId())).thenReturn(Mono.just(testFile));
        when(repositoryRepository.findById(testRepository.getId())).thenReturn(Mono.just(testRepository));
        when(repositoryRepository.findById(secondRepositoryId)).thenReturn(Mono.just(createSecondRepository()));
        when(matchRepository.findByFirstRepositoryIdAndSecondRepositoryId(testRepository.getId(), secondRepositoryId))
                .thenReturn(Flux.just(testMatch));
        when(fileRepository.findById(secondFileId)).thenReturn(Mono.just(createSecondFile(secondFileId, secondRepositoryId)));
        when(tileRepository.findByMatchId(testMatch.getId())).thenReturn(Flux.just(testTile));

        StepVerifier.create(fileService.getSuspiciousForFile(testFile.getId(), testRepository.getId(), secondRepositoryId))
                .assertNext(matches -> {
                    assertThat(matches).hasSize(1);
                    MatchResponse match = matches.get(0);
                    assertThat(match.getFirstFileId()).isEqualTo(testFile.getId());
                    assertThat(match.getPercentage()).isEqualTo(0.8);
                    assertThat(match.getTiles()).hasSize(1);
                    TileResponse tile = match.getTiles().get(0);
                    assertThat(tile.getTextInFirstFile()).isEqualTo("test");
                })
                .verifyComplete();
    }

    @Test
    void deleteFile_WhenFileExists_ShouldDelete() {
        when(fileRepository.findById(anyLong())).thenReturn(Mono.just(testFile));
        when(repositoryRepository.findById(anyLong())).thenReturn(Mono.just(testRepository));
        when(fileRepository.delete(any(FileProject.class))).thenReturn(Mono.empty());

        StepVerifier.create(fileService.deleteFile(testRepository.getId(), testFile.getId()))
                .verifyComplete();

        verify(fileRepository).delete(testFile);
    }

    private RepositoryProject createSecondRepository() {
        RepositoryProject repo = new RepositoryProject();
        repo.setId(2L);
        repo.setProjectId(2L);
        repo.setName("second-repo");
        repo.setUrl("https://github.com/test/second-repo");
        return repo;
    }

    private FileProject createSecondFile(Long fileId, Long repositoryId) {
        FileProject file = new FileProject();
        file.setId(fileId);
        file.setRepositoryId(repositoryId);
        file.setFullFilename("second/file.txt");
        file.setContent("test content");
        return file;
    }
}
