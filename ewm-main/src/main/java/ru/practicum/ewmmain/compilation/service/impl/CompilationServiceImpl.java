package ru.practicum.ewmmain.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.model.Compilation;
import ru.practicum.ewmmain.compilation.repository.CompilationRepository;
import ru.practicum.ewmmain.compilation.service.CompilationService;
import ru.practicum.ewmmain.event.dto.EventShortDto;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.exception.BadRequestException;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.request.model.StateParticipationRequest;
import ru.practicum.ewmmain.request.repository.ParticipationRequestRepository;
import ru.practicum.ewmmain.stat.client.StatsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewmmain.compilation.mapper.CompilationMapper.*;
import static ru.practicum.ewmmain.event.mapper.EventMapper.*;
import static ru.practicum.ewmmain.category.mapper.CategoryMapper.*;
import static ru.practicum.ewmmain.user.mapper.UserMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final ParticipationRequestRepository participationRequestRepository;

    private final StatsClient statsClient;

    private final EventRepository eventRepository;

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = getCompilationById(compId);
        log.info("Получена подборка событий с id = {}.", compId);

        return compilationToDto(compilation, getEventShortDto(compilation));
    }

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = newCompilationDto.getEvents().stream()
                .map(this::getEventById)
                .collect(Collectors.toList());
        Compilation saveCompilation = compilationRepository.save(compilationNewToDto(newCompilationDto, events));
        log.info("Новая подборка событий с id = {} добавлена.", saveCompilation.getId());
        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> eventToShortDto(e, categoryToDto(e.getCategory()),
                        userToShortDto(e.getInitiator()),
                        participationRequestRepository.countAllByEventIdAndStatus(e.getId(), StateParticipationRequest.CONFIRMED),
                        statsClient.getViews(e)))
                .collect(Collectors.toList());

        return compilationToDto(saveCompilation, eventShortDtos);
    }

    @Override
    public List<CompilationDto> getFew(boolean pinned, PageRequest pageRequest) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        log.info("Получены подборки событий.");

        return compilations.stream()
                .map(c -> compilationToDto(c, getEventShortDto(c)))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long compId) {
        Compilation compilation = getCompilationById(compId);
        compilationRepository.delete(compilation);
        log.info("Подборка событий с id = {} удалена.", compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = getEventById(eventId);
        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Удалено событие с id = {} из подборки событий с id = {}.", event, compId);
    }

    @Override
    public void addEventInCompilation(Long compId, Long eventId) {
        Compilation compilation = getCompilationById(compId);
        Event event = getEventById(eventId);
        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Добавлено событие с id = {} в подборку событий с id = {}.", event, compId);
    }

    @Override
    public void unpin(Long compId) {
        Compilation compilation = getCompilationById(compId);
        if (Boolean.FALSE.equals(compilation.getPinned())) {
            throw new BadRequestException(String.format("Подборка событий с id %s не " +
                    "закреплена на главной странице.", compId));
        }
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("Подборка событий с id = {} откреплена с главной страницы.", compId);
    }

    @Override
    public void pin(Long compId) {
        Compilation compilation = getCompilationById(compId);
        if (Boolean.TRUE.equals(compilation.getPinned())) {
            throw new BadRequestException(String.format("Подборка событий с id %s уже " +
                    "закреплена на главной странице.", compId));
        }
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        log.info("Подборка событий с id = {} прикреплена на главную страницу.", compId);
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено.", eventId)));
    }

    private Compilation getCompilationById(Long comId) {
        return compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка событий с id = %s не найдена.", comId)));
    }

    private List<EventShortDto> getEventShortDto(Compilation compilation) {
        return compilation.getEvents().stream()
                .map(e -> eventToShortDto(e, categoryToDto(e.getCategory()),
                        userToShortDto(e.getInitiator()),
                        participationRequestRepository.countAllByEventIdAndStatus(e.getId(), StateParticipationRequest.CONFIRMED),
                        statsClient.getViews(e)))
                .collect(Collectors.toList());
    }
}