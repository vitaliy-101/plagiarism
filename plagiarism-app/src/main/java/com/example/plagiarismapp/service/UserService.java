package com.example.plagiarismapp.service;

import com.example.plagiarismapp.dto.request.user.UserRequest;
import com.example.plagiarismapp.entity.Project;
import com.example.plagiarismapp.entity.User;
import com.example.plagiarismapp.exception.NotFoundByIdException;
import com.example.plagiarismapp.mapper.UserMapper;
import com.example.plagiarismapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public User createUser(UserRequest request) {
        var user = userMapper.userFromUserRequest(request);

        userRepository.save(user);
        return user;
    }

    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundByIdException(User.class, userId));
        userRepository.delete(user);
    }

    public List<Project> getAllProject(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundByIdException(User.class, userId));

        return user.getProjects();
    }

}
