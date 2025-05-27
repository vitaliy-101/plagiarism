package com.example.plagiarismapp.mapper;


import com.example.plagiarismapp.dto.request.user.UserRequest;
import com.example.plagiarismapp.dto.response.user.UserResponse;
import com.example.plagiarismapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import reactor.core.publisher.Mono;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse userResponseFromEntity(User user);
    User userFromUserRequest(UserRequest request);

    default Mono<UserResponse> userResponseFromMono(Mono<User> user) {
        return user.map(this::userResponseFromEntity);
    }
}
