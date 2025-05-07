package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.request.user.UserRequest;
import com.example.plagiarismapp.dto.response.user.UserResponse;
import com.example.plagiarismapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse userResponseFromEntity(User user);
    User userFromUserRequest(UserRequest request);
}
