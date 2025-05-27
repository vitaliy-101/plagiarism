package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.request.user.UserRequest;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.User;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.mapper.UserMapper;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public Mono<User> createUser(UserRequest request) {
        var user = userMapper.userFromUserRequest(request);
        log.info("Saving user: {}", user);
        return userRepository.save(user);
    }

    public Mono<Void> deleteUser(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundByIdException(User.class, userId)))
                .flatMap(userRepository::delete);
    }

    public Mono<List<Project>> getAllProjects(Long userId) {
        return userRepository.existsById(userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new NotFoundByIdException(User.class, userId));
                    }
                    return projectRepository.findAllByUserId(userId)
                            .collectList()
                            .flatMap(Mono::just);
                });
    }

}
