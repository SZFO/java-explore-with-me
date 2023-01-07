package ru.practicum.ewmstats.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmstats.dto.EndpointHitDto;
import ru.practicum.ewmstats.model.EndpointHit;

@Component
public class EndpointHitMapper {
    public EndpointHit dtotoEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .build();
    }

    public EndpointHitDto endpointHitToDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}