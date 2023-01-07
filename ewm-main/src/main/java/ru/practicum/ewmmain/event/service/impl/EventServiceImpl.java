package ru.practicum.ewmmain.event.service.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.category.mapper.CategoryMapper;
import ru.practicum.ewmmain.category.model.Category;
import ru.practicum.ewmmain.category.repository.CategoryRepository;
import ru.practicum.ewmmain.event.dto.*;
import ru.practicum.ewmmain.event.mapper.EventMapper;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.model.QEvent;
import ru.practicum.ewmmain.event.model.StateEvent;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.event.service.EventService;
import ru.practicum.ewmmain.exception.BadRequestException;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewmmain.request.model.ParticipationRequest;
import ru.practicum.ewmmain.request.model.QParticipationRequest;
import ru.practicum.ewmmain.request.model.StateParticipationRequest;
import ru.practicum.ewmmain.request.repository.ParticipationRequestRepository;
import ru.practicum.ewmmain.stat.client.StatsClient;
import ru.practicum.ewmmain.user.dto.UserShortDto;
import ru.practicum.ewmmain.user.mapper.UserMapper;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final ParticipationRequestRepository participationRequestRepository;

    private final StatsClient statsClient;

    private final CategoryMapper categoryMapper;

    private final EventMapper eventMapper;

    private final ParticipationRequestMapper participationRequestMapper;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента.");
        User user = getUserById(userId);
        Category category = getCategoryById(newEventDto.getCategory());
        Event event = eventMapper.dtoToNewEvent(newEventDto, category, user);
        event.setState(StateEvent.PENDING);
        Event newEvent = eventRepository.save(event);
        log.info("Добавлено новое событие с id = {}.", newEvent.getId());

        return getCompleteFullDto(event);
    }

    @Override
    public EventFullDto getAuthorsEventById(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        throwEventInitiator(event, user);
        log.info("Получена полная информации о событии с id = {}, добавленном текущим пользователем.", eventId);

        return getCompleteFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventByOwner(Long userId, Pageable pageable) {
        List<Event> eventList = eventRepository.findAllByInitiatorId(userId, pageable);
        List<Long> eventIds = eventList.stream().map(Event::getId).collect(Collectors.toList());
        log.info("Получены события, добавленные пользователем с id = {}.", userId);

        return eventList.stream()
                .map(e -> eventMapper.eventToShortDto(e, categoryMapper.categoryToDto(e.getCategory()),
                        userMapper.userToShortDto(e.getInitiator()),
                        participationRequestRepository.countAllByEventIdAndStatus(eventIds,
                                StateParticipationRequest.CONFIRMED),
                        statsClient.getViews(e)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto cancelAuthorsEvent(Long userId, Long eventId) {
        User initiator = getUserById(userId);
        Event event = getEventById(eventId);
        throwEventInitiator(event, initiator);
        if (!event.getState().equals(StateEvent.PENDING)) {
            throw new BadRequestException(String.format("Отменить можно только событие в состоянии ожидания модерации. " +
                    "Текущий статус события: %s.", event.getState()));
        }
        event.setState(StateEvent.CANCELED);
        Event newEvent = eventRepository.save(event);
        log.info("Выполнена отмена события с id = {}, добавленного текущим пользователем.", eventId);

        return getCompleteFullDto(newEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByOwner(Long userId, UpdateEventRequest updateEventRequest) {
        User user = getUserById(userId);
        Event oldEvent = getEventById(updateEventRequest.getEventId());
        throwEventInitiator(oldEvent, user);
        if (oldEvent.getState().equals(StateEvent.PUBLISHED)) {
            throw new BadRequestException("Изменить можно только отмененные события или " +
                    "события в состоянии ожидания модерации.");
        }
        if (LocalDateTime.now().plusHours(2).isAfter(oldEvent.getEventDate())) {
            throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента.");
        }
        oldEvent.setAnnotation(updateEventRequest.getAnnotation());
        oldEvent.setDescription(updateEventRequest.getDescription());
        Category category = getCategoryById(updateEventRequest.getCategory());
        oldEvent.setCategory(category);
        oldEvent.setEventDate(updateEventRequest.getEventDate());
        oldEvent.setPaid(updateEventRequest.getPaid());
        oldEvent.setParticipantLimit(updateEventRequest.getParticipantLimit());
        oldEvent.setTitle(updateEventRequest.getTitle());
        Event updateEvent = eventRepository.save(oldEvent);
        log.info("Выполнено изменение события с id = {}, добавленного текущим пользователем.", updateEvent.getId());

        return getCompleteFullDto(updateEvent);
    }

    @Override
    public List<ParticipationRequestDto> getEventsUsersRequests(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        throwEventInitiator(event, user);
        List<ParticipationRequest> requests = participationRequestRepository.findByEventId(event.getId());
        log.info("Получена информация о запросах на участие в событии с id = {} текущего пользователя.", eventId);

        return requests.stream()
                .map(participationRequestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectUsersRequest(Long userId, Long eventId, Long requestId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        ParticipationRequest participationRequest = getRequestById(requestId);
        throwEventInitiator(event, user);
        participationRequest.setStatus(StateParticipationRequest.REJECTED);
        participationRequestRepository.save(participationRequest);
        log.info("Отклонение чужой заявки с id = {} на участие в событии с id = {} текущего пользователя.",
                requestId, eventId);

        return participationRequestMapper.requestToParticipationRequestDto(participationRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmUsersRequest(Long userId, Long eventId, Long requestId) {
        User initiator = getUserById(userId);
        Event event = getEventById(eventId);
        ParticipationRequest participationRequest = getRequestById(requestId);
        throwEventInitiator(event, initiator);
        if (event.getParticipantLimit() != 0 && Boolean.TRUE.equals(event.getRequestModeration())) {
            long limit = event.getParticipantLimit();
            long participantRequest = participationRequestRepository.countAllByEventIdAndStatus(List.of(event.getId()),
                    StateParticipationRequest.CONFIRMED);
            if (limit < participantRequest) {
                throw new BadRequestException(String.format("Нельзя подтвердить заявку на событие с id = %s. " +
                        "Достигнут лимит по заявкам на данное событие.", event.getId()));
            }
            participationRequest.setStatus(StateParticipationRequest.CONFIRMED);
            participationRequestRepository.save(participationRequest);
            if (limit == participantRequest) {
                participationRequestRepository.findByEventId(event.getId()).stream()
                        .filter(r -> r.getStatus() == StateParticipationRequest.PENDING)
                        .forEach(r -> {
                            r.setStatus(StateParticipationRequest.REJECTED);
                            participationRequestRepository.save(r);
                        });
            }
        }
        log.info("Подтверждение чужой заявки с id = {} на участие в событии с id = {} текущего пользователя.",
                requestId, eventId);

        return participationRequestMapper.requestToParticipationRequestDto(participationRequest);
    }

    @Override
    public List<EventShortDto> getPublicFilteredEvent(String text, List<Long> categories, Boolean paid,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                      Boolean onlyAvailable, String sort, Integer from, Integer size) {
        String sorting;
        switch (sort) {
            case "EVENT_DATE":
                sorting = "eventDate";
                break;
            case "VIEWS":
                sorting = "view";
                break;
            default:
                throw new IllegalStateException(String.format("Неизвестная сортировка %s.", sort));
        }
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(sorting).descending());
        LocalDateTime start = rangeStart == null ? LocalDateTime.now() : rangeStart;
        LocalDateTime end = rangeEnd == null ? LocalDateTime.now().plusMonths(1) : rangeEnd;
        QEvent event = QEvent.event;
        List<Predicate> predicateList = new ArrayList<>();
        if (text != null) {
            predicateList.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            predicateList.add(event.category.id.in(categories));
        }
        if (paid != null) {
            predicateList.add(paid ? event.paid.isTrue() : event.paid.isFalse());
        }
        if (Boolean.TRUE.equals(onlyAvailable)) {
            QParticipationRequest qParticipationRequest = QParticipationRequest.participationRequest;
            BooleanExpression requestLimitZero = event.participantLimit.eq(0L);
            BooleanExpression requestModerationFalse = event.requestModeration.isFalse()
                    .and(event.participantLimit.goe(qParticipationRequest.count()));
            BooleanExpression requestModerationTrue = event.requestModeration.isTrue()
                    .and(event.participantLimit.goe(qParticipationRequest.status.eq(
                            StateParticipationRequest.CONFIRMED).count()));
            predicateList.add(requestLimitZero.or(requestModerationFalse).or(requestModerationTrue));
        }
        predicateList.add(event.eventDate.after(start));
        predicateList.add(event.eventDate.before(end));
        Predicate conditions = allOf(predicateList);
        assert conditions != null;
        Page<Event> filteredEvents = eventRepository.findAll(conditions, pageRequest);
        List<Long> eventIds = filteredEvents.getContent().stream().map(Event::getId).collect(Collectors.toList());
        log.info("Получены события с возможностью фильтрации.");

        return filteredEvents.stream()
                .map(e -> eventMapper.eventToShortDto(e, categoryMapper.categoryToDto(e.getCategory()),
                        userMapper.userToShortDto(e.getInitiator()),
                        participationRequestRepository.countAllByEventIdAndStatus(eventIds,
                                StateParticipationRequest.CONFIRMED),
                        statsClient.getViews(e)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getFullPublicEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new BadRequestException("Событие должно быть опубликовано.");
        }
        CategoryDto category = categoryMapper.categoryToDto(event.getCategory());
        UserShortDto userShortDto = userMapper.userToShortDto(event.getInitiator());
        log.info("Получена подробная информация об опубликованном событии с id = {}.", eventId);

        return eventMapper.eventToFullDto(event, category, userShortDto,
                participationRequestRepository.countAllByEventIdAndStatus(List.of(event.getId()),
                        StateParticipationRequest.CONFIRMED),
                statsClient.getViews(event));
    }

    @Override
    public List<EventFullDto> getEventByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        LocalDateTime start = rangeStart == null ? LocalDateTime.now() : rangeStart;
        LocalDateTime end = rangeEnd == null ? LocalDateTime.now().plusMonths(1) : rangeEnd;

        List<StateEvent> stateEvents = null;
        if (states != null) {
            stateEvents = states.stream()
                    .map(this::getState)
                    .collect(Collectors.toList());
        }
        QEvent event = QEvent.event;
        List<Predicate> predicateList = new ArrayList<>();
        if (users != null) {
            predicateList.add(event.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicateList.add(event.state.in(stateEvents));
        }
        if (categories != null && !categories.isEmpty()) {
            predicateList.add(event.category.id.in(categories));
        }
        predicateList.add(event.eventDate.after(start));
        predicateList.add(event.eventDate.before(end));
        Predicate conditions = allOf(predicateList);
        assert conditions != null;
        Page<Event> events = eventRepository.findAll(conditions, pageRequest);
        log.info("Поиск событий с фильтрацией.");

        return events.stream()
                .map(this::getCompleteFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Event event = getEventById(eventId);
        Optional.ofNullable(adminUpdateEventRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(adminUpdateEventRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(adminUpdateEventRequest.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(adminUpdateEventRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(adminUpdateEventRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(adminUpdateEventRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(adminUpdateEventRequest.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(adminUpdateEventRequest.getRequestModeration()).ifPresent(location -> {
            event.setLocationLat(adminUpdateEventRequest.getLocation().getLat());
            event.setLocationLon(adminUpdateEventRequest.getLocation().getLon());
        });
        Optional.ofNullable(adminUpdateEventRequest.getCategory()).ifPresent(category ->
                event.setCategory(getCategoryById(category)));
        eventRepository.save(event);
        log.info("Отредактировано событие с id = {}.", eventId);

        return getCompleteFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(StateEvent.PENDING)) {
            throw new BadRequestException("Событие должно быть в состоянии ожидания публикации.");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Дата начала события должна быть не ранее чем за час от даты публикации.");
        }
        event.setState(StateEvent.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        eventRepository.save(event);
        log.info("Опубликовано событие с id = {}.", eventId);

        return getCompleteFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(StateEvent.PENDING)) {
            throw new BadRequestException("Событие должно быть в состоянии ожидания публикации.");
        }
        event.setState(StateEvent.CANCELED);
        eventRepository.save(event);
        log.info("Отклонено событие с id = {}.", eventId);

        return getCompleteFullDto(event);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден.", userId)));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено.", eventId)));
    }

    private ParticipationRequest getRequestById(Long requestId) {
        return participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос на участие с id = %s не найден.",
                        requestId)));
    }

    private Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %s не найдена.", catId)));
    }

    private void throwEventInitiator(Event event, User user) {
        if (event.getInitiator() != user) {
            throw new BadRequestException(String.format("Вы не инициатор события c id = %s", event.getId()));
        }
    }

    private StateEvent getState(String state) {
        try {
            return StateEvent.valueOf(state);
        } catch (BadRequestException e) {
            throw new BadRequestException(String.format("Неизвестное состояние %s события.", state));
        }
    }

    private EventFullDto getCompleteFullDto(Event event) {
        return eventMapper.eventToFullDto(event,
                categoryMapper.categoryToDto(event.getCategory()),
                userMapper.userToShortDto(event.getInitiator()),
                participationRequestRepository.countAllByEventIdAndStatus(List.of(event.getId()),
                        StateParticipationRequest.CONFIRMED),
                statsClient.getViews(event));
    }
}