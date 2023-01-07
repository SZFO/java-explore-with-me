package ru.practicum.ewmmain.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.category.mapper.CategoryMapper;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;
import ru.practicum.ewmmain.compilation.mapper.CompilationMapper;
import ru.practicum.ewmmain.compilation.model.Compilation;
import ru.practicum.ewmmain.compilation.repository.CompilationRepository;
import ru.practicum.ewmmain.compilation.service.CompilationService;
import ru.practicum.ewmmain.event.dto.EventShortDto;
import ru.practicum.ewmmain.event.mapper.EventMapper;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.exception.BadRequestException;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.request.model.StateParticipationRequest;
import ru.practicum.ewmmain.request.repository.ParticipationRequestRepository;
import ru.practicum.ewmmain.stat.client.StatsClient;
import ru.practicum.ewmmain.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final ParticipationRequestRepository participationRequestRepository;

    private final StatsClient statsClient;

    private final EventRepository eventRepository;

    private final CategoryMapper categoryMapper;

    private final CompilationMapper compilationMapper;

    private final EventMapper eventMapper;

    private final UserMapper userMapper;

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = getEntityCompilationById(compId);
        log.info("Получена подборка событий с id = {}.", compId);

        return compilationMapper.compilationToDto(compilation, getEventShortDto(compilation));
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = newCompilationDto.getEvents().stream()
                .map(this::getEventById)
                .collect(Collectors.toList());
        Compilation compilation = compilationMapper.compilationNewToDto(newCompilationDto, events);
        List<Long> eventIds = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
        compilationRepository.save(compilation);
        log.info("Новая подборка событий с id = {} добавлена.", compilation.getId());
        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> eventMapper.eventToShortDto(e, categoryMapper.categoryToDto(e.getCategory()),
                        userMapper.userToShortDto(e.getInitiator()),
                        participationRequestRepository.countAllByEventIdAndStatus(eventIds,
                                StateParticipationRequest.CONFIRMED),
                        statsClient.getViews(e)))
                .collect(Collectors.toList());

        return compilationMapper.compilationToDto(compilation, eventShortDtos);
    }

    @Override
    public List<CompilationDto> getAllCompilations(boolean pinned, PageRequest pageRequest) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        log.info("Получены подборки событий.");

        return compilations.stream()
                .map(c -> compilationMapper.compilationToDto(c, getEventShortDto(c)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = getEntityCompilationById(compId);
        compilationRepository.delete(compilation);
        log.info("Подборка событий с id = {} удалена.", compId);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = getEntityCompilationById(compId);
        Event event = getEventById(eventId);
        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Удалено событие с id = {} из подборки событий с id = {}.", event, compId);
    }

    @Override
    @Transactional
    public void addEventInCompilation(Long compId, Long eventId) {
        Compilation compilation = getEntityCompilationById(compId);
        Event event = getEventById(eventId);
        List<Event> events = new ArrayList<>(compilation.getEvents());
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
        log.info("Добавлено событие с id = {} в подборку событий с id = {}.", event, compId);
    }

    @Override
    @Transactional
    public void unpinCompilation(Long compId) {
        Compilation compilation = getEntityCompilationById(compId);
        if (Boolean.FALSE.equals(compilation.getPinned())) {
            throw new BadRequestException(String.format("Подборка событий с id %s не " +
                    "закреплена на главной странице.", compId));
        }
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("Подборка событий с id = {} откреплена с главной страницы.", compId);
    }

    @Override
    @Transactional
    public void pinCompilation(Long compId) {
        Compilation compilation = getEntityCompilationById(compId);
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

    private Compilation getEntityCompilationById(Long comId) {
        return compilationRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка событий с id = %s не найдена.", comId)));
    }

    private List<EventShortDto> getEventShortDto(Compilation compilation) {
        List<Long> eventIds = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());

        return compilation.getEvents().stream()
                .map(e -> eventMapper.eventToShortDto(e, categoryMapper.categoryToDto(e.getCategory()),
                        userMapper.userToShortDto(e.getInitiator()),
                        participationRequestRepository.countAllByEventIdAndStatus(eventIds,
                                StateParticipationRequest.CONFIRMED),
                        statsClient.getViews(e)))
                .collect(Collectors.toList());
    }
}