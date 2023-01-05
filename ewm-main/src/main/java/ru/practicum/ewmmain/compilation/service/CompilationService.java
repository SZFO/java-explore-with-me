package ru.practicum.ewmmain.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getFew(boolean pinned, PageRequest pageRequest);

    CompilationDto getById(Long compId);

    CompilationDto create(NewCompilationDto newCompilation);

    void delete(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventInCompilation(Long compId, Long eventId);

    void unpin(Long compId);

    void pin(Long compId);
}