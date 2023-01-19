package ru.practicum.ewmmain.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.event.dto.AdminUpdateEventRequest;
import ru.practicum.ewmmain.event.dto.EventFullDto;
import ru.practicum.ewmmain.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getByAdmin(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<String> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Вызван метод getByAdmin() в EventAdminController.");
        List<EventFullDto> getEventByAdmin = eventService.getEventByAdmin(users, states, categories,
                rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok().body(getEventByAdmin).getBody();
    }

    @PutMapping("/{eventId}")
    public EventFullDto updateByAdmin(@PathVariable @Positive Long eventId,
                                      @RequestBody AdminUpdateEventRequest adminUpdateEventRequest) {
        log.info("Вызван метод updateByAdmin() в EventAdminController для события с id {}.", eventId);
        EventFullDto updateEventByAdmin = eventService.updateEventByAdmin(eventId, adminUpdateEventRequest);

        return ResponseEntity.ok().body(updateEventByAdmin).getBody();
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publish(@PathVariable @Positive Long eventId) {
        log.info("Вызван метод publish() в EventAdminController для события с id {}.", eventId);
        EventFullDto publishEvent = eventService.publishEvent(eventId);

        return ResponseEntity.ok().body(publishEvent).getBody();
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto reject(@PathVariable @Positive Long eventId) {
        log.info("Вызван метод reject() в EventAdminController для события с id {}.", eventId);
        EventFullDto rejectEvent = eventService.rejectEvent(eventId);

        return ResponseEntity.ok().body(rejectEvent).getBody();
    }
}