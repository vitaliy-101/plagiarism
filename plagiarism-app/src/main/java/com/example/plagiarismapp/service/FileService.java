package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.response.match.MatchResponse;
import com.example.plagiarismapp.dto.response.match.TileResponse;
import com.example.plagiarismapp.entity.FileProject;
import com.example.plagiarismapp.entity.RepositoryProject;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileProjectRepository fileRepository;
    private final RepositoryProjectRepository repositoryRepository;
    private final MatchRepository matchRepository;
    private final TileRepository tileRepository;

    private Mono<FileProject> checkFileExistAndGet(Long repositoryId, Long fileId) {
        return fileRepository.findById(fileId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(FileProject.class, fileId)))
                .flatMap(file -> repositoryRepository.findById(repositoryId)
                        .switchIfEmpty(Mono.error(new NotFoundByIdException(RepositoryProject.class, repositoryId)))
                        .flatMap(repository -> {
                            if (file.getRepositoryId().equals(repository.getId())) {
                                return Mono.just(file);
                            } else {
                                return Mono.error(
                                        new NotFoundResourceByIdException(
                                                RepositoryProject.class, repositoryId, FileProject.class, repositoryId));
                            }
                        }));
    }


    public Mono<List<MatchResponse>> getSuspiciousForFile(Long fileId, Long firstRepositoryId, Long secondRepositoryId) {
        return fileRepository.findById(fileId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(FileProject.class, fileId)))
                .flatMap(file ->
                        Mono.zip(
                                repositoryRepository.findById(firstRepositoryId)
                                        .switchIfEmpty(Mono.error(
                                                new NotFoundByIdException(RepositoryProject.class, firstRepositoryId))),
                                repositoryRepository.findById(secondRepositoryId)
                                        .switchIfEmpty(Mono.error(
                                                new NotFoundByIdException(RepositoryProject.class, secondRepositoryId)))
                        ).flatMap(tuple -> {
                            RepositoryProject firstRepo = tuple.getT1();
                            RepositoryProject secondRepo = tuple.getT2();

                            if (!file.getRepositoryId().equals(firstRepo.getId())) {
                                return Mono.error(new NotFoundResourceByIdException(
                                        RepositoryProject.class, firstRepositoryId, FileProject.class, fileId));
                            }

                            return matchRepository.findByFirstRepositoryIdAndSecondRepositoryId(
                                            firstRepositoryId, secondRepositoryId)
                                    .filter(match -> match.getFirstFileId().equals(fileId) ||
                                            match.getSecondFileId().equals(fileId))
                                    .filter(match -> match.getPercentage() >= 0.4)
                                    .flatMap(match ->
                                            Mono.zip(
                                                    fileRepository.findById(match.getFirstFileId()),
                                                    fileRepository.findById(match.getSecondFileId()),
                                                    tileRepository.findByMatchId(match.getId())
                                                            .map(tile -> {
                                                                TileResponse tr = new TileResponse();

                                                                tr.setTextInFirstFile(tile.getTextInFirstFile());
                                                                tr.setTextAfterInFirstFile(tile.getTextAfterContextInFirstFile());
                                                                tr.setTextBeforeInFirstFile(tile.getTextBeforeContextInFirstFile());

                                                                tr.setTextInSecondFile(tile.getTextInSecondFile());
                                                                tr.setTextAfterInSecondFile(tile.getTextAfterContextInSecondFile());
                                                                tr.setTextBeforeInSecondFile(tile.getTextBeforeContextInSecondFile());
                                                                return tr;
                                                            })
                                                            .collectList()
                                            ).map(triple -> {
                                                FileProject firstFile = triple.getT1();
                                                FileProject secondFile = triple.getT2();
                                                List<TileResponse> tiles = triple.getT3();

                                                MatchResponse response = new MatchResponse();
                                                response.setId(match.getId());
                                                response.setPercentage(match.getPercentage());
                                                response.setFirstFileId(match.getFirstFileId());
                                                response.setFirstFileName(firstFile.getFullFilename());
                                                response.setFirstRepositoryId(firstFile.getRepositoryId());
                                                response.setSecondFileId(match.getSecondFileId());
                                                response.setSecondFileName(secondFile.getFullFilename());
                                                response.setSecondRepositoryId(secondFile.getRepositoryId());
                                                response.setTiles(tiles);

                                                return response;
                                            })
                                    )
                                    .collectList();
                        })
                );
    }

    public Mono<FileProject> getFileProject(Long repositoryId, Long id) {
        return checkFileExistAndGet(repositoryId, id);
    }

    public Mono<Void> deleteFile(Long repositoryId, Long id) {
        return checkFileExistAndGet(repositoryId, id)
                .flatMap(fileRepository::delete);
    }
}
