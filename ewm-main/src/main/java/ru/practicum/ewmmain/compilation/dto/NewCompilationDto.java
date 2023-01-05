package ru.practicum.ewmmain.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class NewCompilationDto {
    @NotBlank
    private String title;

    @NotNull
    private Boolean pinned;

    private List<Long> events;
}