package ru.practicum.ewmmain.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.event.dto.EventFullDto;
import ru.practicum.ewmmain.event.dto.EventShortDto;
import ru.practicum.ewmmain.event.dto.NewEventDto;
import ru.practicum.ewmmain.event.dto.UpdateEventRequest;
import ru.practicum.ewmmain.event.service.EventService;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    public EventFullDto create(@PathVariable(required = false) Long userId,
                               @RequestBody(required = false) @Valid NewEventDto newEventDto) {
        log.info("Вызван метод create() в EventPrivateController пользователем с id = {}.", userId);
        EventFullDto create = eventService.create(userId, newEventDto);

        return ResponseEntity.ok().body(create).getBody();
    }

    @PatchMapping
    public EventFullDto update(@PathVariable(required = false) @Positive Long userId,
                               @RequestBody(required = false) @Valid UpdateEventRequest updateRequest) {
        log.info("Вызван метод updateByOwner() в EventPrivateController пользователем с id = {}.", userId);
        EventFullDto update = eventService.updateByOwner(userId, updateRequest);

        return ResponseEntity.ok().body(update).getBody();
    }

    @GetMapping
    public List<EventShortDto> getByOwner(@PathVariable(required = false) @Positive Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Вызван метод getByOwner() в EventPrivateController пользователем с id = {}.", userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<EventShortDto> getByOwner = eventService.getByOwner(userId, pageRequest);

        return ResponseEntity.ok().body(getByOwner).getBody();
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable(required = false) @Positive Long userId,
                                @PathVariable(required = false) @Positive Long eventId) {
        log.info("Вызван метод getAuthorsEventById() в EventPrivateController для события с id = {}.", eventId);
        EventFullDto getById = eventService.getAuthorsEventById(userId, eventId);

        return ResponseEntity.ok().body(getById).getBody();
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancel(@PathVariable(required = false) @Positive Long userId,
                               @PathVariable(required = false) @Positive Long eventId) {
        log.info("Вызван метод cancelAuthorsEvent() в EventPrivateController для события с id = {}.", eventId);
        EventFullDto cancel = eventService.cancelAuthorsEvent(userId, eventId);

        return ResponseEntity.ok().body(cancel).getBody();
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventsUsersRequests(@PathVariable(required = false) @Positive Long userId,
                                                                @PathVariable(required = false) @Positive Long eventId) {
        log.info("Вызван метод getEventsUsersRequests() в EventPrivateController для события с id = {}.", eventId);
        List<ParticipationRequestDto> getEventsUsersRequests = eventService.getEventsUsersRequests(userId, eventId);

        return ResponseEntity.ok().body(getEventsUsersRequests).getBody();
    }

    @PatchMapping("/{eventId}/requests/{requestId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable(required = false) @Positive Long userId,
                                                  @PathVariable(required = false) @Positive Long eventId,
                                                  @PathVariable(required = false) @Positive Long requestId) {
        log.info("Вызван метод confirmUsersRequest() в EventPrivateController для запроса с id = {} и " +
                "события с id = {}.", requestId, eventId);
        ParticipationRequestDto confirmRequest = eventService.confirmUsersRequest(userId, eventId, requestId);

        return ResponseEntity.ok().body(confirmRequest).getBody();
    }

    @PatchMapping("/{eventId}/requests/{requestId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable(required = false) @Positive Long userId,
                                                 @PathVariable(required = false) @Positive Long eventId,
                                                 @PathVariable(required = false) @Positive Long requestId) {
        log.info("Вызван метод rejectUsersRequest() в EventPrivateController для запроса с id = {} и " +
                "события с id = {}.", requestId, eventId);
        ParticipationRequestDto rejectRequest = eventService.rejectUsersRequest(userId, eventId, requestId);

        return ResponseEntity.ok().body(rejectRequest).getBody();
    }
}