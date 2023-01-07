package ru.practicum.ewmstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmstats.dto.ViewStatsDto;
import ru.practicum.ewmstats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "SELECT new ru.practicum.ewmstats.dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit AS e WHERE e.timestamp BETWEEN :start AND :end AND (COALESCE(:uris, null) IS null OR " +
            "e.uri IN :uris) GROUP BY e.uri, e.app")
    List<ViewStatsDto> getAll(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                              @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.ewmstats.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit AS e WHERE e.timestamp BETWEEN :start AND :end AND (COALESCE(:uris, null) IS null OR " +
            "e.uri IN :uris) GROUP BY e.uri, e.app")
    List<ViewStatsDto> getAllUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);
}