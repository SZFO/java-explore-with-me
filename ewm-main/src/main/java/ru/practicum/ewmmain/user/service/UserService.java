package ru.practicum.ewmmain.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.user.dto.NewUserRequest;
import ru.practicum.ewmmain.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getByIds(List<Long> ids, Pageable pageable);

    UserDto create(NewUserRequest newUserRequest);

    void delete(Long id);
}
