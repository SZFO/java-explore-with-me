package ru.practicum.ewmmain.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.event.model.Event;
import ru.practicum.ewmmain.event.model.StateEvent;
import ru.practicum.ewmmain.event.repository.EventRepository;
import ru.practicum.ewmmain.exception.BadRequestException;
import ru.practicum.ewmmain.exception.NotFoundException;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.model.ParticipationRequest;
import ru.practicum.ewmmain.request.model.StateParticipationRequest;
import ru.practicum.ewmmain.request.repository.ParticipationRequestRepository;
import ru.practicum.ewmmain.request.service.ParticipationRequestService;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.repository.UserRepository;
import ru.practicum.ewmmain.request.mapper.ParticipationRequestMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        return participationRequestRepository.findByRequesterId(userId)
                .stream()
                .map(participationRequestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(StateParticipationRequest.PENDING)
                .build();
        if (user.getId().equals(event.getInitiator().getId()) || !event.getState().equals(StateEvent.PUBLISHED)) {
            throw new BadRequestException("Валидация не пройдена.");
        }
        if (Boolean.TRUE.equals(!event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(StateParticipationRequest.CONFIRMED);
        }
        log.info("Успешная валидадция. Создание запроса на участие.");

        return participationRequestMapper.requestToParticipationRequestDto(
                participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        getUserById(userId);
        ParticipationRequest participationRequest = getRequestById(requestId);
        participationRequest.setStatus(StateParticipationRequest.CANCELED);
        log.info("Успешная валидадция. Отмена запроса на участие.");

        return participationRequestMapper.requestToParticipationRequestDto(
                participationRequestRepository.save(participationRequest));
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
}