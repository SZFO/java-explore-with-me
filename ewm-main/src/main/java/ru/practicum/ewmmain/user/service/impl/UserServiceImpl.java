package ru.practicum.ewmmain.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.exception.ConflictException;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.user.dto.NewUserRequest;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.repository.UserRepository;
import ru.practicum.ewmmain.user.service.UserService;
import ru.practicum.ewmmain.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids, Pageable pageable) {
        List<UserDto> userDtos = userRepository.findUsersByIds(ids, pageable)
                .stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
        log.info("Получение пользователей: {}.", userDtos);

        return userDtos;
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (Boolean.TRUE.equals(userRepository.existsUserByName(newUserRequest.getName()))) {
            throw new ConflictException(String.format("Пользователь с id = %s уже существует.",
                    newUserRequest.getName()));
        }
        User user = userRepository.save(userMapper.dtoToUser(newUserRequest));
        log.debug("Пользователь с id={} создан.", user.getId());

        return userMapper.userToDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", id))));
    }
}