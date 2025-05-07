package com.example.plagiarismapp.controller;


import com.example.plagiarismapp.dto.request.user.UserRequest;
import com.example.plagiarismapp.dto.response.project.SmallProjectResponse;
import com.example.plagiarismapp.dto.response.user.UserResponse;
import com.example.plagiarismapp.mapper.ProjectMapper;
import com.example.plagiarismapp.mapper.UserMapper;
import com.example.plagiarismapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ProjectMapper projectMapper;

    @PostMapping("/create")
    public UserResponse createUser(@RequestBody UserRequest request) {
        return userMapper.userResponseFromEntity(userService.createUser(request));
    }

    @GetMapping("/all/project")
    public List<SmallProjectResponse> getAllProject(@RequestParam("userId") Long userId) {
        return projectMapper.listSmallProjectResponseFromEntity(userService.getAllProject(userId));
    }

    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }

}
