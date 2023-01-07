package ru.practicum.ewmmain.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmmain.event.model.Location;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AdminUpdateEventRequest {
    String annotation;

    Long category;

    String description;

    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    Long participantLimit;

    Boolean requestModeration;

    String title;
}
