package ru.practicum.ewmmain.stat.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.exception.BadRequestException;
import ru.practicum.ewmmain.stat.dto.EndpointHit;
import ru.practicum.ewmmain.stat.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsClient {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final WebClient webClient;

    @Autowired
    public StatsClient(@Value("${ewm-stats.url}") String statUrl) {
        final DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(statUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        webClient = WebClient
                .builder()
                .uriBuilderFactory(factory)
                .baseUrl(statUrl)
                .build();
    }

    public void save(HttpServletRequest httpServletRequest) {
        EndpointHit endpointHit = new EndpointHit(
                httpServletRequest.getServerName(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr(),
                LocalDateTime.now());
        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(endpointHit), EndpointHit.class)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new BadRequestException("Ошибка сохранения данных на сервере статистики.");
                })
                .toBodilessEntity()
                .block();
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", dateTimeFormatEncode(start))
                        .queryParam("end", dateTimeFormatEncode(end))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    throw new BadRequestException("Ошибка получения данных статистики.");
                })
                .bodyToMono(new ParameterizedTypeReference<List<ViewStats>>() {
                })
                .block();
    }

    public Long getViews(Event event) {
        List<ViewStats> viewStatsList = getStats(
                event.getCreatedOn(),
                event.getEventDate(),
                List.of("/events/" + event.getId()), false);
        Long result = 0L;
        if (!viewStatsList.isEmpty()) {
            result = viewStatsList.get(0).getHits();
        }
        return result;
    }

    private String dateTimeFormatEncode(LocalDateTime localDateTime) {
        return URLEncoder
                .encode(localDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        StandardCharsets.UTF_8);
    }
}