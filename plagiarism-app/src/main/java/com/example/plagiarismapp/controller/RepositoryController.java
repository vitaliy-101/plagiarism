package com.example.plagiarismapp.controller;

import com.example.plagiarismapp.dto.response.file.SuspiciousFileResponse;
import com.example.plagiarismapp.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/repository")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;

    @GetMapping("/suspicious/{projectId}/{firstRepositoryId}/{secondRepositoryId}")
    public List<SuspiciousFileResponse> getSuspicious(@PathVariable("projectId") Long projectId,
                                                      @PathVariable("firstRepositoryId") Long firstRepositoryId,
                                                      @PathVariable("secondRepositoryId") Long secondRepositoryId) {
        return repositoryService.getSuspiciousFiles(projectId, firstRepositoryId, secondRepositoryId);
    }

    @DeleteMapping("/delete/{projectId}/{repositoryId}")
    public void deleteRepository(@PathVariable("projectId") Long projectId, @PathVariable("repositoryId") Long repositoryId) {
        repositoryService.deleteRepository(projectId, repositoryId);
    }
}
