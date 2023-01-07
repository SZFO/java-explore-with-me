package ru.practicum.ewmmain.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmmain.event.dto.*;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getEventByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventFullDto publishEvent(Long eventId);

    EventFullDto rejectEvent(Long eventId);

    List<EventShortDto> getEventByOwner(Long userId, Pageable pageable);

    EventFullDto updateEventByOwner(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getAuthorsEventById(Long userId, Long eventId);

    EventFullDto cancelAuthorsEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getEventsUsersRequests(Long userId, Long eventId);

    ParticipationRequestDto confirmUsersRequest(Long userId, Long eventId, Long requestId);

    ParticipationRequestDto rejectUsersRequest(Long userId, Long eventId, Long requestId);

    List<EventShortDto> getPublicFilteredEvent(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                          Integer from, Integer size);

    EventFullDto getFullPublicEvent(Long eventId);
}
