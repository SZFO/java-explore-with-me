package ru.practicum.ewmmain.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmmain.request.model.StateParticipationRequest;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ParticipationRequestDto {
    Long id;

    LocalDateTime created;

    @NotNull
    Long event;

    @NotNull
    Long requester;

    @NotNull
    StateParticipationRequest status;
}