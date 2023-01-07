package ru.practicum.ewmmain.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;
import ru.practicum.ewmmain.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(boolean pinned, PageRequest pageRequest);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilation);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventInCompilation(Long compId, Long eventId);

    void unpinCompilation(Long compId);

    void pinCompilation(Long compId);
}