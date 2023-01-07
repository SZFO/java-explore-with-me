package ru.practicum.ewmmain.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.service.ParticipationRequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ParticipationRequestController {
    private final ParticipationRequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable @NotNull Long userId) {
        log.info("Вызван метод getRequestsByUserId() в ParticipationRequestController для " +
                "пользователя с id {}.", userId);
        List<ParticipationRequestDto> getRequestsByUserId = requestService.getRequestsByUserId(userId);

        return ResponseEntity.ok().body(getRequestsByUserId).getBody();
    }

    @PostMapping
    public ParticipationRequestDto createParticipationRequest(@PathVariable @NotNull Long userId,
                                                              @RequestParam Long eventId) {
        log.info("Вызван метод createParticipationRequest() в ParticipationRequestController пользователем с id {} " +
                "для события с id {}.", userId, eventId);
        ParticipationRequestDto createParticipationRequest = requestService.createParticipationRequest(userId, eventId);

        return ResponseEntity.ok().body(createParticipationRequest).getBody();
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable @NotNull Long userId,
                                          @PathVariable @NotNull Long requestId) {
        log.info("Вызван метод cancelParticipationRequest() в ParticipationRequestController пользователем с id {} " +
                "для зароса на участие с id {}.", userId, requestId);
        ParticipationRequestDto cancelParticipationRequest = requestService.cancelParticipationRequest(userId, requestId);

        return ResponseEntity.ok().body(cancelParticipationRequest).getBody();
    }
}