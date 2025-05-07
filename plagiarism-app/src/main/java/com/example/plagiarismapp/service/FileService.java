package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.response.match.MatchResponse;
import com.example.plagiarismapp.dto.response.match.TileResponse;
import com.example.plagiarismapp.entity.FileProject;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.RepositoryProject;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.exception.NotFoundResourceByIdException;
import com.example.plagiarismapp.repository.FileProjectRepository;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.RepositoryProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final ProjectRepository projectRepository;
    private final FileProjectRepository fileRepository;
    private final RepositoryProjectRepository repositoryRepository;

    private FileProject checkFileExistAndGet(Long repositoryId, Long fileId) {
        var file = fileRepository.findById(fileId).orElseThrow(() -> new NotFoundByIdException(FileProject.class, fileId));
        var repository = repositoryRepository.findById(repositoryId).orElseThrow(
                () -> new NotFoundByIdException(RepositoryProject.class, repositoryId));
        if (file.getRepository().getId().equals(repository.getId())) {
            return file;
        } else {
            throw new NotFoundResourceByIdException(RepositoryProject.class, repositoryId, FileProject.class, repositoryId);
        }
    }

    public List<MatchResponse> getSuspiciousForFile(Long projectId, Long fileId) {
        var project = projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundByIdException(Project.class, projectId));
        fileRepository.findById(fileId).orElseThrow(
                () -> new NotFoundByIdException(File.class, fileId));

        List<MatchResponse> result = new ArrayList<>();

        project.getMatches()
                .stream()
                .filter(x -> x.getPercentage() >= 0.8 && x.getFirstFile().getId().equals(fileId)
                        || x.getSecondFile().getId().equals(fileId))
                .map(x -> {
                    MatchResponse response = new MatchResponse();
                    response.setId(x.getId());
                    response.setPercentage(x.getPercentage());

                    response.setFirstFileId(x.getFirstFile().getId());
                    response.setFirstFileName(x.getFirstFile().getFilename());
                    response.setFirstRepositoryId(x.getFirstFile().getRepository().getId());

                    response.setSecondFileId(x.getSecondFile().getId());
                    response.setSecondFileName(x.getSecondFile().getFilename());
                    response.setSecondRepositoryId(x.getSecondFile().getRepository().getId());

                    response.setTiles(x.getTiles().stream().map(y -> {
                        TileResponse tileResponse = new TileResponse();
                        tileResponse.setPositionInFirstFile(y.getPositionInFirstFile());
                        tileResponse.setPositionInSecondFile(y.getPositionInSecondFile());
                        tileResponse.setTextInFirstFile(y.getTextInFirstFile());
                        tileResponse.setTextInSecondFile(y.getTextInSecondFile());
                        return tileResponse;
                    }).toList());

                    return response;
                })
                .forEach(result::add);

        return result;
    }

    public FileProject getFileProject(Long repositoryId, Long id) {
        return checkFileExistAndGet(repositoryId, id);
    }

    public void deleteFile(Long repositoryId, Long id) {
        var file = checkFileExistAndGet(repositoryId, id);
        fileRepository.delete(file);
    }
}
