package ru.practicum.ewmstats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmstats.dto.EndpointHitDto;
import ru.practicum.ewmstats.dto.ViewStatsDto;
import ru.practicum.ewmstats.service.StatService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatService statService;

    @PostMapping("/hit")
    public EndpointHitDto save(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Вызван метод save() в StatsController.");
        EndpointHitDto save = statService.save(endpointHitDto);

        return ResponseEntity.ok().body(save).getBody();
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findStat(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Вызван метод findStat() в StatsController.");
        List<ViewStatsDto> findStat = statService.findStat(start, end, uris, unique);

        return ResponseEntity.ok().body(findStat).getBody();
    }
}