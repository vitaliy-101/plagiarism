package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.request.user.UserRequest;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.User;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.mapper.UserMapper;
import com.example.plagiarismapp.repository.ProjectRepository;
import com.example.plagiarismapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequest testUserRequest;
    private final Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        testUserRequest = new UserRequest();
        testUserRequest.setName("Test User");
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setPassword("password123");
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        when(userMapper.userFromUserRequest(testUserRequest)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

        Mono<User> result = userService.createUser(testUserRequest);

        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getName()).isEqualTo("Test User");
                    assertThat(user.getEmail()).isEqualTo("test@example.com");
                })
                .verifyComplete();

        verify(userMapper).userFromUserRequest(testUserRequest);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUserWhenExists() {
        when(userRepository.findById(testUserId)).thenReturn(Mono.just(testUser));
        when(userRepository.delete(any(User.class))).thenReturn(Mono.empty());
        Mono<Void> result = userService.deleteUser(testUserId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository).findById(testUserId);
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = userService.deleteUser(999L);

        StepVerifier.create(result)
                .expectError(NotFoundByIdException.class)
                .verify();

        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void getAllProjects_ShouldReturnUserProjects() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");
        project1.setUserId(testUserId);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");
        project2.setUserId(testUserId);

        when(userRepository.existsById(testUserId)).thenReturn(Mono.just(true));
        when(projectRepository.findAllByUserId(testUserId)).thenReturn(Flux.just(project1, project2));

        Mono<List<Project>> result = userService.getAllProjects(testUserId);
        StepVerifier.create(result)
                .assertNext(projects -> {
                    assertThat(projects).hasSize(2);
                    assertThat(projects).extracting(Project::getName)
                            .containsExactlyInAnyOrder("Project 1", "Project 2");
                })
                .verifyComplete();

        verify(userRepository).existsById(testUserId);
        verify(projectRepository).findAllByUserId(testUserId);
    }

    @Test
    void getAllProjects_ShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(Mono.just(false));
        Mono<List<Project>> result = userService.getAllProjects(999L);

        StepVerifier.create(result)
                .expectError(NotFoundByIdException.class)
                .verify();

        verify(userRepository).existsById(999L);
        verify(projectRepository, never()).findAllByUserId(anyLong());
    }
}
