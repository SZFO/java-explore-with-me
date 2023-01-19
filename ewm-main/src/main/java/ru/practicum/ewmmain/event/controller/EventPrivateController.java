package ru.practicum.ewmmain.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.event.dto.EventFullDto;
import ru.practicum.ewmmain.event.dto.EventShortDto;
import ru.practicum.ewmmain.event.dto.NewEventDto;
import ru.practicum.ewmmain.event.dto.UpdateEventRequest;
import ru.practicum.ewmmain.event.service.EventService;
import ru.practicum.ewmmain.event.dto.ReactionDto;
import ru.practicum.ewmmain.event.model.StateReaction;
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
        EventFullDto createEventDto = eventService.createEvent(userId, newEventDto);

        return ResponseEntity.ok().body(createEventDto).getBody();
    }

    @PatchMapping
    public EventFullDto update(@PathVariable(required = false) @Positive Long userId,
                               @RequestBody(required = false) @Valid UpdateEventRequest updateRequest) {
        log.info("Вызван метод update() в EventPrivateController пользователем с id = {}.", userId);
        EventFullDto updateEventByOwner = eventService.updateEventByOwner(userId, updateRequest);

        return ResponseEntity.ok().body(updateEventByOwner).getBody();
    }

    @GetMapping
    public List<EventShortDto> getByOwner(@PathVariable(required = false) @Positive Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Вызван метод getByOwner() в EventPrivateController пользователем с id = {}.", userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<EventShortDto> getEventByOwner = eventService.getEventByOwner(userId, pageRequest);

        return ResponseEntity.ok().body(getEventByOwner).getBody();
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable(required = false) @Positive Long userId,
                                @PathVariable(required = false) @Positive Long eventId) {
        log.info("Вызван метод getById() в EventPrivateController для события с id = {}.", eventId);
        EventFullDto getEventDtoById = eventService.getAuthorsEventById(userId, eventId);

        return ResponseEntity.ok().body(getEventDtoById).getBody();
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancel(@PathVariable(required = false) @Positive Long userId,
                               @PathVariable(required = false) @Positive Long eventId) {
        log.info("Вызван метод cancel() в EventPrivateController для события с id = {}.", eventId);
        EventFullDto cancelEvent = eventService.cancelAuthorsEvent(userId, eventId);

        return ResponseEntity.ok().body(cancelEvent).getBody();
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

    @PostMapping("/{eventId}/reactions")
    public ReactionDto createReaction(@PathVariable Long eventId, @PathVariable Long userId,
                                      @RequestParam(name = "reaction") StateReaction stateReaction) {
        log.info("Вызван метод createReaction() в EventPrivateController пользователем с id = {} для " +
                "события с id = {}. Тип реакции: {}.", userId, eventId, stateReaction.name());
        ReactionDto createReaction = eventService.createReaction(userId, eventId, stateReaction);

        return ResponseEntity.ok().body(createReaction).getBody();
    }

    @DeleteMapping("/{eventId}/reactions")
    public ResponseEntity<HttpStatus> deleteReaction(@PathVariable Long eventId, @PathVariable Long userId) {
        log.info("Вызван метод deleteReaction() в EventPrivateController пользователем с id = {} для " +
                "события с id = {}.", userId, eventId);
        eventService.deleteReaction(userId, eventId);

        return ResponseEntity.ok().build();
    }
}