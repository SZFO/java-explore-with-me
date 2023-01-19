package ru.practicum.ewmmain.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmmain.user.dto.NewUserRequest;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.dto.UserShortDto;
import ru.practicum.ewmmain.user.model.User;

@Component
public class UserMapper {
    public User dtoToUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rating(user.getRating() != null ? user.getRating() : 0)
                .build();
    }

    public UserShortDto userToShortDto(User user) {
        return UserShortDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .rating(user.getRating() != null ? user.getRating() : 0)
                .build();
    }
}