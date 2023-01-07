package ru.practicum.ewmmain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.user.dto.NewUserRequest;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserAdminController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam(name = "ids") List<Long> ids,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<UserDto> getUsersByIds = userService.getUsersByIds(ids, pageRequest);
        log.info("Вызван метод getUsersByIds() в UserAdminController.");

        return ResponseEntity.ok().body(getUsersByIds).getBody();
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Вызван метод createUser() в UserAdminController.");
        UserDto createUser = userService.createUser(newUserRequest);

        return ResponseEntity.ok().body(createUser).getBody();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userId) {
        log.info("Вызван метод deleteUser() в UserAdminController для пользователя с id {}.", userId);
        userService.deleteUser(userId);

        return ResponseEntity.ok().build();
    }
}