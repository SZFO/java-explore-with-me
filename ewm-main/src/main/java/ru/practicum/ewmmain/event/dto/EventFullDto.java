package ru.practicum.ewmmain.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.event.model.Location;
import ru.practicum.ewmmain.event.model.StateEvent;
import ru.practicum.ewmmain.user.dto.UserShortDto;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EventFullDto {
    Long id;

    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    LocalDateTime createdOn;

    String description;

    LocalDateTime eventDate;

    UserShortDto initiator;

    Location location;

    Boolean paid;

    Long participantLimit;

    LocalDateTime publishedOn;

    Boolean requestModeration;

    StateEvent state;

    String title;

    Long views;
}