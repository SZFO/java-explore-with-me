package ru.practicum.ewmmain.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class UpdateEventRequest {
    Long eventId;

    @NotBlank
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    String description;

    @Future
    @NotNull
    LocalDateTime eventDate;

    Boolean paid;

    Long participantLimit;

    @NotBlank
    String title;
}