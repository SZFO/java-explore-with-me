package ru.practicum.ewmmain.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.compilation.service.CompilationService;
import ru.practicum.ewmmain.compilation.dto.CompilationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getFew(@RequestParam(required = false) boolean pinned,
                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Вызван метод getFew() в CompilationPublicController.");
        PageRequest pageRequest = PageRequest.of(from, size);
        List<CompilationDto> getFew = compilationService.getFew(pinned, pageRequest);

        return ResponseEntity.ok().body(getFew).getBody();
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @Positive Long compId) {
        log.info("Вызван метод getById() в CompilationPublicController для подборки событий с id {}.", compId);
        CompilationDto getById = compilationService.getById(compId);

        return ResponseEntity.ok().body(getById).getBody();
    }
}