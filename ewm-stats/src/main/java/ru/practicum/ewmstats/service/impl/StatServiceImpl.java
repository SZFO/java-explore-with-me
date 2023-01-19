package ru.practicum.ewmstats.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmstats.dto.EndpointHitDto;
import ru.practicum.ewmstats.dto.ViewStatsDto;
import ru.practicum.ewmstats.mapper.EndpointHitMapper;
import ru.practicum.ewmstats.model.EndpointHit;
import ru.practicum.ewmstats.repository.StatRepository;
import ru.practicum.ewmstats.service.StatService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    private final EndpointHitMapper endpointHitMapper;

    @Override
    @Transactional
    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.dtotoEndpointHit(endpointHitDto);
        endpointHit.setTimestamp(LocalDateTime.now());
        log.info("Сохранена информация о том, что к эндпоинту был запрос.");

        return endpointHitMapper.endpointHitToDto(statRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> findStat(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(
                URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        LocalDateTime endTime = LocalDateTime.parse(
                URLDecoder.decode(end, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
        if (Boolean.TRUE.equals(unique)) {
            log.info("Получена статистика по уникальным посещениям.");
            return statRepository.getAllUnique(startTime, endTime, uris);
        } else {
            log.info("Получена статистика по всем посещениям.");
            return statRepository.getAll(startTime, endTime, uris);
        }
    }
}