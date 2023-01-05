package ru.practicum.ewmmain.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.event.dto.EventFullDto;
import ru.practicum.ewmmain.event.dto.EventShortDto;
import ru.practicum.ewmmain.event.service.EventService;
import ru.practicum.ewmmain.stat.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;

    private final StatsClient statsClient;

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable @Positive Long eventId, HttpServletRequest request) {
        statsClient.save(request);
        log.info("Вызван метод getFullPublicEvent() в EventPublicController для события с id = {}.", eventId);
        EventFullDto getById = eventService.getFullPublicEvent(eventId);

        return ResponseEntity.ok().body(getById).getBody();
    }

    @GetMapping
    public List<EventShortDto> getFiltered(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                           @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size,
                                           HttpServletRequest request) {
        statsClient.save(request);
        log.info("Вызван метод getPublicFiltered() в EventPublicController.");
        List<EventShortDto> getFiltered = eventService.getPublicFiltered(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);

        return ResponseEntity.ok().body(getFiltered).getBody();
    }
}