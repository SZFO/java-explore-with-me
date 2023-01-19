package ru.practicum.ewmmain.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmmain.event.dto.ReactionDto;
import ru.practicum.ewmmain.event.model.Reaction;

@Component
public class ReactionMapper {
    public ReactionDto reactionToDto(Reaction reaction) {
        return ReactionDto.builder()
                .id(reaction.getId())
                .eventId(reaction.getEvent().getId())
                .userId(reaction.getUser().getId())
                .stateReaction(reaction.getStateReaction())
                .build();
    }
}