package ru.practicum.ewmstats.mapper;

import ru.practicum.ewmstats.dto.EndpointHitDto;
import ru.practicum.ewmstats.model.EndpointHit;

public class EndpointHitMapper {
    public static EndpointHit dtotoEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .build();
    }

    public static EndpointHitDto endpointHitToDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}