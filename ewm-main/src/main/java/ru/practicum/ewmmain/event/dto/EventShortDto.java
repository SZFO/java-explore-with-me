package ru.practicum.ewmmain.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmmain.category.dto.CategoryDto;
import ru.practicum.ewmmain.user.dto.UserShortDto;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class EventShortDto {
    Long id;

    String annotation;

    CategoryDto category;

    Long confirmedRequests;

    LocalDateTime eventDate;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Long views;
}