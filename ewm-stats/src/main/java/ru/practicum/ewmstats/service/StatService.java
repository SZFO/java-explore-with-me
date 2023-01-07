package ru.practicum.ewmstats.service;

import ru.practicum.ewmstats.dto.EndpointHitDto;
import ru.practicum.ewmstats.dto.ViewStatsDto;

import java.util.List;

public interface StatService {

    EndpointHitDto save(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> findStat(String start, String end, List<String> uris, Boolean unique);
}