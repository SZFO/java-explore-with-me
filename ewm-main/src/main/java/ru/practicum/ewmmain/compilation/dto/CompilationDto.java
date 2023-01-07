package ru.practicum.ewmmain.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewmmain.event.dto.EventShortDto;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CompilationDto {
    Long id;

    String title;

    Boolean pinned;

    List<EventShortDto> events;
}
