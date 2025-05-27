package com.example.plagiarismapp.controller;

import com.example.plagiarismapp.dto.response.file.FileResponse;
import com.example.plagiarismapp.dto.response.match.MatchResponse;
import com.example.plagiarismapp.mapper.FileProjectMapper;
import com.example.plagiarismapp.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("api/v1/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileProjectMapper fileProjectMapper;

    @GetMapping("/suspicious/file/{fileId}/{firstRepositoryId}/{secondRepositoryId}")
    public Mono<List<MatchResponse>> getSuspiciousForFile(@PathVariable("fileId") Long fileId,
                                                    @PathVariable("firstRepositoryId") Long firstRepositoryId,
                                                    @PathVariable("secondRepositoryId") Long secondRepositoryId) {

        return fileService.getSuspiciousForFile(fileId, firstRepositoryId, secondRepositoryId);
    }

    @GetMapping("file/{repositoryId}/{fileId}")
    public Mono<FileResponse> getFile(@PathVariable("repositoryId") Long repositoryId, @PathVariable("fileId") Long fileId) {
        return fileProjectMapper.fileResponseFromMono(fileService.getFileProject(repositoryId, fileId));
    }

    @DeleteMapping("/file/{repositoryId}/{fileId}")
    public Mono<Void> deleteFile(@PathVariable("repositoryId") Long repositoryId, @PathVariable("fileId") Long fileId) {
        return fileService.deleteFile(repositoryId, fileId);
    }

}
