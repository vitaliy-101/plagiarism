package com.example.plagiarismapp.controller;


import com.example.plagiarismapp.dto.request.user.UserRequest;
import com.example.plagiarismapp.dto.response.project.SmallProjectResponse;
import com.example.plagiarismapp.dto.response.user.UserResponse;
import com.example.plagiarismapp.mapper.ProjectMapper;
import com.example.plagiarismapp.mapper.UserMapper;
import com.example.plagiarismapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    @PostMapping("/create")
    public Mono<UserResponse> createUser(@RequestBody UserRequest request) {
        return userMapper.userResponseFromMono(userService.createUser(request));
    }

    @GetMapping("/all/project")
    public Mono<List<SmallProjectResponse>> getAllProject(@RequestParam("userId") Long userId) {
        return projectMapper.listSmallProjectResponseFromMono(userService.getAllProjects(userId));
    }

    @DeleteMapping("/delete/{userId}")
    public Mono<Void> deleteUser(@PathVariable("userId") Long userId) {
        return userService.deleteUser(userId);
    }

}
