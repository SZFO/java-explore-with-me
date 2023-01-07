package ru.practicum.ewmmain.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmmain.request.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.request.model.ParticipationRequest;

@Component
public class ParticipationRequestMapper {
    public ParticipationRequestDto requestToParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}