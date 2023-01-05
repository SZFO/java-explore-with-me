package ru.practicum.ewmmain.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmmain.user.dto.NewUserRequest;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.dto.UserShortDto;
import ru.practicum.ewmmain.user.model.User;

@Component
public class UserMapper {
    public static User dtoToUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public static UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto userToShortDto(User user) {
        return UserShortDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}